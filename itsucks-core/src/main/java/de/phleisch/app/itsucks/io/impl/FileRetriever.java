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
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.Metadata;


/**
 * Implementation of an data retriever which can read files from 
 * the filesystem.
 * 
 * @author olli
 *
 */
public class FileRetriever implements DataRetriever {

	private static Log mLog = LogFactory.getLog(FileRetriever.class);
	
	private File mFile;
	private FileInputStream mIn;
	
	private long mFileSize;
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

		if(mIn != null) {
			try {
				mIn.close();
			} catch (IOException e) {
				mLog.warn("Error closing file to abort transfer", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#connect()
	 */
	public void connect() throws IOException {
		if(mIn != null) {
			throw new IllegalStateException("Already connected!");
		}
		if(mAbort) {
			throw new IllegalStateException("Retriever aborted");
		}
		
		mLog.debug("Open file for retrieval: " + mFile);
		mIn = new FileInputStream(mFile);
		
		mFileSize = mFile.length();
		
		//skip bytes in front when given
		if(mByteOffset > 0) {
			mLog.debug("Seek to position: " + mByteOffset);
			mByteOffset= mIn.skip(mByteOffset);
		}
		
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#disconnect()
	 */
	public void disconnect() throws IOException {
		if(mIn != null) {
			mLog.debug("Close file");
			mIn.close();
		}
		
		mIn = null;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getMetadata()
	 */
	public Metadata getMetadata() {
		throw new IllegalStateException("Not implemented yet!");
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#isDataAvailable()
	 */
	public boolean isDataAvailable() throws IOException {
		return mIn.available() > 0;
	}
	
	public InputStream getDataAsInputStream() {
		
		if(mIn == null) {
			throw new IllegalStateException("Not connected");
		}
		
		return mIn;
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
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getContentLenght()
	 */
	public long getContentLenght() {
		if(mIn == null) {
			throw new IllegalStateException("Not connected");
		}
		
		return mFileSize;
	}
}
