/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 26.02.2007
 */

package de.phleisch.app.itsucks.io.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.Metadata;

/**
 * This retriever is used to resume partial downloaded files. It creates an
 * additional retriever, the file retriever, and combine it with the original
 * data retriever. So it can send the complete data through the processing chain
 * without receiving it completly from the data retriever.
 * 
 * @author olli
 */
public class FileResumeRetriever implements DataRetriever {

	private static Log mLog = LogFactory.getLog(FileResumeRetriever.class);

	protected FileRetriever mFileRetriever;
	protected DataRetriever mDataRetriever;

	protected File mLocalFile;

	protected long mResumeOffset;
	protected long mOverlap;

	protected boolean mConnected;
	protected boolean mResumePrepared;
	protected boolean mReadFromFile;

	protected InputStream mIn;

	public FileResumeRetriever(DataRetriever pDataRetriever, File pFile) {

		mDataRetriever = pDataRetriever;
		mLocalFile = pFile;

		reset();
	}

	protected void reset() {
		mFileRetriever = null;
		mReadFromFile = true;
		mResumePrepared = false;
		mConnected = false;
		mResumeOffset = 0;
		mOverlap = 128;
		mIn = null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#abort()
	 */
	public void abort() {
		if (mReadFromFile && mFileRetriever != null) {
			mFileRetriever.abort();
		}
		mDataRetriever.abort();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#connect()
	 */
	public void connect() throws IOException {

		if(mConnected) {
			throw new IllegalStateException("Already connected!");
		}
		
		if (mResumePrepared) {
			throw new IllegalStateException("Resume already prepared!");
		}

		// first check if the file exists
		if (mLocalFile.exists() && mLocalFile.length() > 0) {
			mResumeOffset = mLocalFile.length();

			// when overlap is larger than resume offset, do not resume
			if (mOverlap > mResumeOffset) {
				mResumeOffset = 0;
				mOverlap = 0;
			}

			// try to resume the data stream
			long bytesToSkip = mResumeOffset - mOverlap;
			if(bytesToSkip > 0) {
				mDataRetriever.setBytesToSkip(bytesToSkip);
			}
			mDataRetriever.connect();

			if(bytesToSkip <= 0) {
				mLog.info("Resume is smaller than overlap or resumeOffset <= 0 set, " 
					+ "stopping resume and load the file normally: " 
					+ mDataRetriever);
				
			} else if (mDataRetriever.getBytesSkipped() > 0) {
				mLog.info("Resume of retriever successful: "
						+ mDataRetriever);

				// skip overlap bytes if defined
				if (mOverlap > 0) {
					mDataRetriever.getDataAsInputStream().skip(mOverlap);
				}

			} else {
				// resume not possible, read everything from the live stream
				mResumeOffset = 0;

				mLog.info("Resume of retriever not possible, seeking not allowed: "
						+ mDataRetriever);
			}

		} else {
			// resume not possible, read everything from the live stream
			mResumeOffset = 0;

			// connect without skipping any bytes
			mDataRetriever.connect();

			mLog.info("Resume of retriever not possible: " + mDataRetriever
					+ ", no local data available.");

		}

		mConnected = true;
	}

	protected void prepareResume() throws IOException {

		if(!mConnected) {
			throw new IllegalStateException("Not connected!");
		}
		
		if (mResumePrepared) {
			return;
		}

		if (mResumeOffset == 0) {
			// only pipe through
			setReadFromFile(false);
		}
		
		//prepare input stream
		if (mReadFromFile) {
			
			mFileRetriever = new FileRetriever(mLocalFile);
			mFileRetriever.connect();
			
			mIn = new SequenceInputStream(
					mFileRetriever.getDataAsInputStream(), mDataRetriever
							.getDataAsInputStream());
		} else {
			mIn = mDataRetriever.getDataAsInputStream();
		}

		mResumePrepared = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getDataAsInputStream()
	 */
	public InputStream getDataAsInputStream() throws IOException {

		prepareResume();

		return mIn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#disconnect()
	 */
	public void disconnect() throws IOException {

		if(mConnected) {
		
			if (mReadFromFile && mFileRetriever != null) {
				mFileRetriever.disconnect();
			}
			mDataRetriever.disconnect();
			
			reset();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getMetadata()
	 */
	public Metadata getMetadata() {
		return mDataRetriever.getMetadata();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#isDataAvailable()
	 */
	public boolean isDataAvailable() throws IOException {

		prepareResume();

		boolean result;

		if (mReadFromFile) {
			result = mFileRetriever.isDataAvailable();
			if (!result) {
				result = mDataRetriever.isDataAvailable();
			}
		} else {
			result = mDataRetriever.isDataAvailable();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#setBytesToSkip(long)
	 */
	public void setBytesToSkip(long pBytesToSkip) {
		throw new IllegalStateException("Not supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getBytesSkipped()
	 */
	public long getBytesSkipped() {
		if(mReadFromFile) {
			return 0; // we have to read all data from disk, so we did'nt skip any bytes
		} else {
			return mResumeOffset;
		}
	}
	
	/**
	 * Returns count of bytes which are available on disk.
	 * @return
	 */
	public long getResumeOffset() {
		return mResumeOffset;
	}

	public long getContentLenght() throws IOException {

		prepareResume();

		long length;

		if (mReadFromFile) {
			length = mFileRetriever.getContentLenght();
			if (length > -1) {
				length += mDataRetriever.getContentLenght() - mOverlap;
			}
		} else {
			length = mDataRetriever.getContentLenght() - mOverlap + mResumeOffset;
		}

		return length;
	}

	public boolean isReadFromFile() {
		return mReadFromFile;
	}

	public void setReadFromFile(boolean pReadFromFile) {
		if(mResumePrepared) {
			throw new IllegalStateException("Resume already prepared!");
		}
		
		mReadFromFile = pReadFromFile;
	}

}
