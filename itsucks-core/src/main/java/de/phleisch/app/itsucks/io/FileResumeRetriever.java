/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 26.02.2007
 */

package de.phleisch.app.itsucks.io;

import java.io.File;
import java.net.URL;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.processing.DataProcessorChain;

public class FileResumeRetriever implements DataRetriever {

	private static Log mLog = LogFactory.getLog(FileResumeRetriever.class);
	
	private FileRetriever mFileRetriever;
	private DataRetriever mDataRetriever;

	private File mLocalFile;
	
	private long mResumeOffset;
	
	private boolean mReadFromFile;
	private boolean mFileFinished;
	private boolean mResumePrepared;
	
	public FileResumeRetriever(DataRetriever pDataRetriever, File pFile) {
		
		mFileRetriever = null;
		mDataRetriever = pDataRetriever;
		mLocalFile = pFile;
		mFileFinished = false;
		mReadFromFile = false;
		mResumePrepared = false;
	}

	public void abort() {
		if(mReadFromFile) {
			mFileRetriever.abort();
		}
		mDataRetriever.abort();
	}

	public void connect() throws Exception {
		
		//first check if the file exists
		if(mLocalFile.exists() && mLocalFile.length() > 0) {
			mResumeOffset = mLocalFile.length();
		
			//try to resume the data stream
			mDataRetriever.setBytesToSkip(mResumeOffset);
			mDataRetriever.connect();
			
			if(mDataRetriever.getBytesSkipped() > 0) {
				mLog.info("Resume of URL successful: " 
						+ mDataRetriever.getUrl());
			} else {
				mLog.info("Resume of URL not possible: " 
						+ mDataRetriever.getUrl() + ", seeking not allowed.");
			}
			
		} else {
			//resume not really possible, read everything from the live stream
			mReadFromFile = false;
			mResumeOffset = 0;
			
			mDataRetriever.connect();
			
			mLog.info("Resume of url not possible: " 
					+ mDataRetriever.getUrl() + ", no local data available.");
			
		}

	}

	private void prepareResume() {

		if(mResumePrepared) return;
		mResumePrepared = true;
		
		//check the processor chain
		DataProcessorChain dataProcessorChain = mDataRetriever.getDataProcessorChain();
		
		if(dataProcessorChain.canResume()) {
			//ok, resume is possible, advise every processor to resume at the given position.
			
			dataProcessorChain.resumeAt(mResumeOffset);
			
			mReadFromFile = false;
			
		} else {
			//resume is not possible, read the data from the disc and pipe it through the processors
			
			//FIXME: Implement resume 
			throw new RuntimeException("Not implemented yet!");
//			mFileRetriever = new FileRetriever(mLocalFile);
//			for (AbstractDataProcessor processor : dataProcessors) {
//				
//				//skip the persistence processor
//				if(processor instanceof PersistenceProcessor) {
//					processor.resumeAt(mResumeOffset);
//					continue;
//				}
//				
//				mFileRetriever.addDataProcessor(processor);
//			}
//			try {
//				mFileRetriever.connect();
//			} catch (Exception e) {
//				throw new RuntimeException("Error creating file retriever", e);
//			}
//			
//			mReadFromFile = true;
		}
	}

	public void retrieve() throws Exception {
		
		if(mDataRetriever.getBytesSkipped() > 0) {

			//retriever successfully seeked to the position
			//reorganize the data processors
			prepareResume();
			
		} else {
			
			//abort resuming
			mReadFromFile = false;
			mResumeOffset = 0;
		}
		
		if(mReadFromFile) {
			//open the old file for the data processor chain
			mFileRetriever.connect();
		
			//load the old file through the data processor chain
			mFileRetriever.retrieve();
			mFileFinished = true;
		}
		
		mDataRetriever.retrieve();
	}
	
	public void disconnect() throws Exception {
		if(mReadFromFile) {
			mFileRetriever.disconnect();
		}
		mDataRetriever.disconnect();
	}

	public long getBytesRetrieved() {
		
		long bytes = 0;
		if(mReadFromFile) {
			bytes += mFileRetriever.getBytesRetrieved();
		} else {
			bytes += mResumeOffset;
		}
		bytes += mDataRetriever.getBytesRetrieved();
		
		return bytes;
	}

	public Metadata getMetadata() {
		return mDataRetriever.getMetadata();
	}

	public float getProgress() {
		return mDataRetriever.getProgress();
	}
	
	public void setUrl(URL pUrl) {
		throw new IllegalArgumentException("Not possible!");
	}
	
	public URL getUrl() {
		return mDataRetriever.getUrl();
	}

	public boolean isDataAvailable() throws Exception {
		
		prepareResume();
		
		if(mReadFromFile && !mFileFinished) { 
			return mFileRetriever.isDataAvailable();
		} else {
			return mDataRetriever.isDataAvailable();
		}
	}
	
	public DataProcessorChain getDataProcessorChain() {
		return mDataRetriever.getDataProcessorChain();
	}

	public void setDataProcessorChain(DataProcessorChain pDataProcessorChain) {

		if(mResumePrepared) {
			throw new RuntimeException("Resuming already prepared, " +
					"changing the data processor chain not allowed at this point.");
		}
		
		mDataRetriever.setDataProcessorChain(pDataProcessorChain);
		if(mFileRetriever != null) {
			mFileRetriever.setDataProcessorChain(pDataProcessorChain);
		}
	}
	

	public void addObserver(Observer pO) {
		mDataRetriever.addObserver(pO);
		if(mFileRetriever != null) {
			mFileRetriever.addObserver(pO);
		}
	}
	
	public void deleteObserver(Observer pO) {
		mDataRetriever.deleteObserver(pO);
		if(mFileRetriever != null) {
			mFileRetriever.deleteObserver(pO);
		}
	}

	public void setBytesToSkip(long pBytesToSkip) {
		throw new IllegalStateException("Not supported.");
	}
	
	public long getBytesSkipped() {
		return 0;
	}

	public int getResultCode() {
		return mDataRetriever.getResultCode();
	}

}
