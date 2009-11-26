/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 16.11.2009
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.io.ResumeUrlDataRetriever;
import de.phleisch.app.itsucks.io.UrlDataRetriever;
import de.phleisch.app.itsucks.io.http.HttpResponseCodes;
import de.phleisch.app.itsucks.io.impl.FileRetriever;
import de.phleisch.app.itsucks.io.impl.FilterDataRetriever;
import de.phleisch.app.itsucks.io.impl.SequenceRetriever;

public class HttpFileResumeUrlRetriever extends FilterDataRetriever implements ResumeUrlDataRetriever {

	private static Log mLog = LogFactory.getLog(HttpFileResumeUrlRetriever.class);
	
	protected static enum OperatingState {
		NOT_CONNECTED,
		ABORTED,
		ONLY_DELEGATE,
		RESUME_FILE,
		ONLY_FILE,
	};
	
	protected UrlDataRetriever mWrappedUrlDataRetriever;
	protected File mLocalFile;
	protected OperatingState mOperatingState;
	protected long mResumeOffset; 
	
	public HttpFileResumeUrlRetriever(UrlDataRetriever pDataRetriever, File pFile) {

		mWrappedUrlDataRetriever = pDataRetriever;
		mLocalFile = pFile;
		
		reset();
	}
	
	private void reset() {
		
		mOperatingState = OperatingState.NOT_CONNECTED;
		mResumeOffset = 0;
		
		//set direct delegate when not connected
		mDataRetriever = mWrappedUrlDataRetriever;	
	}

	public void connect() throws IOException {
		
		if(isConnected()) {
			throw new IllegalStateException("Already connected");
		}
		reset();
		
		if(mWrappedUrlDataRetriever.isConnected() && mWrappedUrlDataRetriever.getBytesSkipped() > 0) {
			throw new IllegalStateException("The wrapped data retriever is already connected and resuming.");
		}
		
		if(!mLocalFile.exists() || mLocalFile.length() == 0) {
			mLog.debug("local file is not usable, do an normal download");
			doDelgateDownload();
		
		} else {
			
			long bytesOnDisk = mLocalFile.length();
			
			//optimization: check if the retriever is already connected and sent the content length
			if(mWrappedUrlDataRetriever.isConnected()) {
				long contentLenght = mWrappedUrlDataRetriever.getContentLenght();
				
				if(contentLenght < 1) {
					mLog.debug("Content length not given, do normal download");
					doDelgateDownload();
				} else if(getMetadata().getContentType().startsWith("text")) {
					mLog.debug("File is of type text, not resumed.");
					doDelgateDownload();
				} else if(bytesOnDisk == contentLenght) {
					//file is completely on disk, only read the file
					mLog.debug("File is completely on disk, " + bytesOnDisk + " bytes");
					doReadOnlyFile(bytesOnDisk);
				} else if(bytesOnDisk > contentLenght) {
					//uhoh, do normal download, something isn't ok here
					mLog.debug("File on disk is larger than servers, bytes on disk: " + bytesOnDisk + ", bytes on server: " + contentLenght + ", drop data on disk and redownload file."); 
					doDelgateDownload();
				} else if(contentLenght < 65535) {
					mLog.debug("Server file is too small < 65k, do normal download");
					doDelgateDownload();
				} else if(bytesOnDisk < contentLenght) {
					//do normal resume, local file is smaller than the server one
					mLog.debug("File on disk is smaller than servers, bytes on disk: " + bytesOnDisk + ", bytes on server: " + contentLenght + ", do normal resume.");
					mWrappedUrlDataRetriever.disconnect();
					connectResume();
				}
				
			} else {
				//wrapper is not connected, do normal resume
				connectResume();
			}
		}
	}

	private void connectResume() throws IOException {
		
		long bytesOnDisk = mLocalFile.length();
		
		mWrappedUrlDataRetriever.setBytesToSkip(bytesOnDisk);
		mWrappedUrlDataRetriever.connect();
		
		int statusCode = getMetadata().getStatusCode();
		if(statusCode == HttpResponseCodes.PARTIAL_CONTENT_206) {
			//resume was successful
			mLog.debug("Resume successful, " + bytesOnDisk + " bytes skipped");
			doResumeDownload(bytesOnDisk);
			
		} else if(statusCode == HttpResponseCodes.REQUESTED_RANGE_NOT_SATISFIABLE_416) {
			//file seems completely on disk
			mLog.debug("File is completely on disk, no download needed");
			doReadOnlyFile(bytesOnDisk);
			
		} else {
			//something went wrong with resuming, do a normal download
			mLog.debug("Unknown response code " + statusCode + " received, falling back to normal download");
			
			mWrappedUrlDataRetriever.disconnect();
			mWrappedUrlDataRetriever.setBytesToSkip(0);
			doDelgateDownload();
		}
	}

	protected void doResumeDownload(long bytesOnDisk) throws IOException {
		FileRetriever fileRetriever = new FileRetriever(mLocalFile);
		
		//connect the file retriever
		fileRetriever.connect();
		
		//build resume retriever sequence
		mDataRetriever = new SequenceRetriever(mWrappedUrlDataRetriever, fileRetriever, mWrappedUrlDataRetriever);
		mOperatingState = OperatingState.RESUME_FILE;
		mResumeOffset = bytesOnDisk;
	}

	protected void doReadOnlyFile(long bytesOnDisk) throws IOException {
		//set fileretriever
		FileRetriever fileRetriever = new FileRetriever(mLocalFile);
		
		//connect the file retriever
		fileRetriever.connect();
		
		mDataRetriever = fileRetriever;
		mOperatingState = OperatingState.ONLY_FILE;
		mResumeOffset = bytesOnDisk;
	}	
	
	protected void doDelgateDownload() throws IOException {
		mDataRetriever = mWrappedUrlDataRetriever;
		mOperatingState = OperatingState.ONLY_DELEGATE;
		
		//connect the retriever
		if(!mDataRetriever.isConnected()) {
			mDataRetriever.connect();
		}
	}

	public void disconnect() throws IOException {
		
		if(mOperatingState == OperatingState.NOT_CONNECTED) {
			return;
		}
		
		mDataRetriever.disconnect();
		if(mDataRetriever != mWrappedUrlDataRetriever) {
			mWrappedUrlDataRetriever.disconnect();
		}
		
		mOperatingState = OperatingState.NOT_CONNECTED;
	}

	@Override
	public void abort() {
		
		if(mOperatingState == OperatingState.NOT_CONNECTED) {
			return;
		}
		
		mDataRetriever.abort();
		if(mDataRetriever != mWrappedUrlDataRetriever) {
			mWrappedUrlDataRetriever.abort();
		}
		
		mOperatingState = OperatingState.ABORTED;
	}

	@Override
	public long getResumeOffset() {
		return mResumeOffset;
	}
	
	@Override
	public int getResultCode() {
		if(mOperatingState == OperatingState.NOT_CONNECTED) {
			return RESULT_RETRIEVAL_UNKNOWN;
		} else if(mOperatingState == OperatingState.ONLY_DELEGATE) {
			return mWrappedUrlDataRetriever.getResultCode();
		} else if(mOperatingState == OperatingState.ONLY_FILE 
				|| mOperatingState == OperatingState.RESUME_FILE) {
			return RESULT_RETRIEVAL_OK;
		} else if(mOperatingState == OperatingState.ABORTED) {
			return RESULT_RETRIEVAL_ABORTED;
		}
		throw new IllegalStateException("Unknown retriever state!");
	}

	@Override
	public long getSuggestedTimeToWaitForRetry() {
		return mWrappedUrlDataRetriever.getSuggestedTimeToWaitForRetry();
	}

	@Override
	public URL getUrl() {
		return mWrappedUrlDataRetriever.getUrl();
	}

	@Override
	public void setUrl(URL pUrl) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public HttpMetadata getMetadata() {
		return (HttpMetadata) mWrappedUrlDataRetriever.getMetadata();
	}

	@Override
	public boolean isConnected() throws IOException {
		return mOperatingState != OperatingState.NOT_CONNECTED && mOperatingState != OperatingState.ABORTED;
	}

}
