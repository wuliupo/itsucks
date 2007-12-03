/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.02.2007
 */

package de.phleisch.app.itsucks.io.impl;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.io.Metadata;


/**
 * Implementation of an data retriever which can read files from 
 * the filesystem.
 * 
 * @author olli
 *
 */
public class FileRetriever extends AbstractDataRetriever {

	private static Log mLog = LogFactory.getLog(FileRetriever.class);
	
	private File mFile;
	private FileInputStream mIn;
	
	private float mProgress = -1;
	private long mFileSize;
	private long mBytesRead;
	
	private boolean mAbort = false;

	private long mByteOffset;

	public FileRetriever(File pFile) {
		mFile = pFile;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#abort()
	 */
	public void abort() {
		mAbort = true;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#connect()
	 */
	public void connect() throws Exception {
		mIn = new FileInputStream(mFile);
		
		mFileSize = mFile.length();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#disconnect()
	 */
	public void disconnect() throws Exception {
		mIn.close();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getBytesRetrieved()
	 */
	public long getBytesRetrieved() {
		return mBytesRead;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getMetadata()
	 */
	public Metadata getMetadata() {
		throw new IllegalStateException("Not implemented yet!");
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getProgress()
	 */
	public float getProgress() {
		return mProgress;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#isDataAvailable()
	 */
	public boolean isDataAvailable() throws Exception {
		return mIn.available() > 0;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#retrieve()
	 */
	public void retrieve() throws Exception {

		//skip bytes in front when given
		if(mByteOffset > 0) {
			mByteOffset= mIn.skip(mByteOffset);
		}
		
		//100k buffer
		byte buffer[] = new byte[102400];
		
		mBytesRead = 0; //reset bytes read
		int bytesRead;
		
		while((bytesRead = mIn.read(buffer)) > 0) {
			
			if(mAbort ) {
				mLog.warn("File retriever aborted: " + this);
				break;
			}
			
			getDataConsumer().process(buffer, bytesRead);

			//update the progress
			mBytesRead += bytesRead;
			updateProgress(((float)mBytesRead / (float)mFileSize));
		}
		
	}

	private void updateProgress(float pProgress) {
		mLog.trace("Update Progress: " + pProgress);
		
		if(mProgress != pProgress) {
			mProgress = pProgress;
			this.setChanged();
		}
		
		this.notifyObservers(NOTIFICATION_PROGRESS);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#setBytesToSkip(long)
	 */
	public void setBytesToSkip(long pBytesToSkip) {
		mByteOffset = pBytesToSkip;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getBytesSkipped()
	 */
	public long getBytesSkipped() {
		return mByteOffset;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getResultCode()
	 */
	public int getResultCode() {
		return RESULT_RETRIEVAL_OK;
	}
	
}
