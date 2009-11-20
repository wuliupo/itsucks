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

import de.phleisch.app.itsucks.io.Metadata;
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
		
		if(mOperatingState != OperatingState.NOT_CONNECTED && mOperatingState != OperatingState.ABORTED) {
			throw new IllegalStateException("Already connected");
		}
		reset();
		
		if(!mLocalFile.exists() || mLocalFile.length() == 0) {
			mLog.debug("local file is not usable, do an normal download");
			mDataRetriever = mWrappedUrlDataRetriever;
			mOperatingState = OperatingState.ONLY_DELEGATE;
			
			//connect the retriever
			mDataRetriever.connect();
			
		} else {
			
			long bytesOnDisk = mLocalFile.length();
			mWrappedUrlDataRetriever.setBytesToSkip(bytesOnDisk);
			
			mWrappedUrlDataRetriever.connect();
			
			int statusCode = ((HttpMetadata)mWrappedUrlDataRetriever.getMetadata()).getStatusCode();
			if(statusCode == HttpResponseCodes.PARTIAL_CONTENT_206) {
				//resume was successful
				mLog.debug("Resume successful, " + bytesOnDisk + " bytes skipped");
				
				FileRetriever fileRetriever = new FileRetriever(mLocalFile);
				
				//connect the file retriever
				fileRetriever.connect();
				
				//build resume retriever sequence
				mDataRetriever = new SequenceRetriever(mWrappedUrlDataRetriever, fileRetriever, mWrappedUrlDataRetriever);
				mOperatingState = OperatingState.RESUME_FILE;
				mResumeOffset = bytesOnDisk;
				
			} else if(statusCode == HttpResponseCodes.REQUESTED_RANGE_NOT_SATISFIABLE_416) {
				//file seems completely on disk
				mLog.debug("File is completely on disk, no download needed");
				
				//set fileretriever
				FileRetriever fileRetriever = new FileRetriever(mLocalFile);
				
				//connect the file retriever
				fileRetriever.connect();
				
				mDataRetriever = fileRetriever;
				mOperatingState = OperatingState.ONLY_FILE;
				mResumeOffset = bytesOnDisk;
				
			} else {
				//something went wrong with resuming, do a normal download
				mLog.debug("Unknown response code " + statusCode + " received, falling back to normal download");
				
				mWrappedUrlDataRetriever.disconnect();
				mWrappedUrlDataRetriever.setBytesToSkip(0);
				mWrappedUrlDataRetriever.connect(); //reconnect
				
				mDataRetriever = mWrappedUrlDataRetriever;
				mOperatingState = OperatingState.ONLY_DELEGATE;
				mResumeOffset = 0;
			}
			
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
	public Metadata getMetadata() {
		return mWrappedUrlDataRetriever.getMetadata();
	}

	
	private void assertConnected() {
		if(mOperatingState == OperatingState.NOT_CONNECTED) {
			throw new IllegalStateException("Not connected");
		}
	}

}
