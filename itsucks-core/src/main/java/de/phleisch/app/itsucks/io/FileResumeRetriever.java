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
import java.util.List;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileResumeRetriever implements DataRetriever {

	private static Log mLog = LogFactory.getLog(FileResumeRetriever.class);
	
	private FileRetriever mFileRetriever;
	private DataRetriever mDataRetriever;

	private File mLocalFile;
	
	private long mResumeOffset;
	
	private boolean mReadFromFile;
	private boolean mFileFinished;
	
	public FileResumeRetriever(DataRetriever pDataRetriever, File pFile) {
		
		mFileRetriever = null;
		mDataRetriever = pDataRetriever;
		mLocalFile = pFile;
		mFileFinished = false;
		mReadFromFile = false;
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
			
		} else {
			//resume not really possible, read everything from the live stream
			mReadFromFile = false;
			mResumeOffset = 0;
			
			mDataRetriever.connect();
		}

	}

	private void prepareResume() {

		//check the processor chain
		List<DataProcessor> dataProcessors = mDataRetriever.getDataProcessors();
		
		boolean resumePossible = true;
		for (DataProcessor processor : dataProcessors) {
			
			//this processor can't resume, abort
			if(!processor.canResume()) {
				resumePossible = false;
				break;
			}
		}
		
		if(resumePossible) {
			//ok, resume is possible, advise every processor to resume at the given position.
			
			for (DataProcessor processor : dataProcessors) {
				processor.resumeAt(mResumeOffset);
			}
			mReadFromFile = false;
			
		} else {
			//resume is not possible, read the data from the disc and pipe it through the processors
			
			mFileRetriever = new FileRetriever(mLocalFile);
			for (DataProcessor processor : dataProcessors) {
				
				//skip the persistence processor
				if(processor instanceof PersistenceProcessor) {
					processor.resumeAt(mResumeOffset);
					continue;
				}
				
				mFileRetriever.addDataProcessor(processor);
			}
			
			mReadFromFile = true;
		}
	}

	public void retrieve() throws Exception {
		
		if(mDataRetriever.getBytesSkipped() > 0) {

			//retriever successfully seeked to the position
			//reorganize the data processors
			prepareResume();
			
		} else {
			
			mLog.info("Resuming of url failed: " 
					+ mDataRetriever.getUrl() + ", redownload the data.");
			
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

	public URL getUrl() {
		return mDataRetriever.getUrl();
	}

	public boolean isDataAvailable() throws Exception {
		
		if(mReadFromFile && !mFileFinished) { 
			return mFileRetriever.isDataAvailable();
		} else {
			return mDataRetriever.isDataAvailable();
		}
	}

	public void setUrl(URL pUrl) {
		throw new IllegalArgumentException("Not possible!");
	}

	public void addDataProcessor(DataProcessor pDataProcessor) {
		mDataRetriever.addDataProcessor(pDataProcessor);
		if(mFileRetriever != null) {
			mFileRetriever.addDataProcessor(pDataProcessor);
		}
	}
	
	public List<DataProcessor> getDataProcessors() {
		return mDataRetriever.getDataProcessors();
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
	
}
