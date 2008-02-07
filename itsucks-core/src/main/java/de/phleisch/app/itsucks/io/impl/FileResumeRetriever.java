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
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.context.Context;
import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.DataProcessorChain;
import de.phleisch.app.itsucks.processing.download.impl.PersistenceProcessor;
import de.phleisch.app.itsucks.processing.impl.SeekDataProcessorWrapper;

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

	private FileRetriever mFileRetriever;
	private DataRetriever mDataRetriever;

	protected DataProcessorChain mDataProcessorChain;

	private File mLocalFile;

	private long mResumeOffset;
	private long mOverlap = 512;

	private boolean mConnected;
	private boolean mResumePrepared;
	private boolean mReadFromFile;

	private InputStream mIn;

	public FileResumeRetriever(DataRetriever pDataRetriever, File pFile) {

		mFileRetriever = null;
		mDataRetriever = pDataRetriever;
		mLocalFile = pFile;
		mReadFromFile = false;
		mResumePrepared = false;
		mConnected = false;
		mDataProcessorChain = null;
		mResumeOffset = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#abort()
	 */
	public void abort() {
		if (mReadFromFile) {
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
			mDataRetriever.setBytesToSkip(mResumeOffset - mOverlap);
			mDataRetriever.connect();

			if (mDataRetriever.getBytesSkipped() > 0) {
				mLog.info("Resume of URL successful: "
						+ mDataRetriever.getUrl());

				// skip overlap bytes if defined
				if (mOverlap > 0) {
					mDataRetriever.getDataAsInputStream().skip(mOverlap);
				}

			} else {
				// resume not possible, read everything from the live stream
				mResumeOffset = 0;

				mLog.info("Resume of URL not possible: "
						+ mDataRetriever.getUrl() + ", seeking not allowed.");
			}

		} else {
			// resume not possible, read everything from the live stream
			mResumeOffset = 0;

			// connect without skipping any bytes
			mDataRetriever.connect();

			mLog.info("Resume of url not possible: " + mDataRetriever.getUrl()
					+ ", no local data available.");

		}

		mConnected = true;
	}

	private void prepareResume() throws IOException {

		if(!mConnected) {
			throw new IllegalStateException("Not connected!");
		}
		
		if (mResumePrepared)
			return;

		// check the processor chain
		DataProcessorChain dataProcessorChain = mDataProcessorChain;

		if (dataProcessorChain == null) {
			throw new IllegalArgumentException("Processor Chain not set!");
		}

		if (mResumeOffset > 0) {

			// retriever successfully seeked to the position
			// reorganize the data processors

			if (dataProcessorChain.canResume()) {
				// ok, resume of chain is possible, advise every processor to resume at
				// the given position.

				dataProcessorChain.resumeAt(mResumeOffset);

				mReadFromFile = false;

			} else {
				// resume is not possible, read the data from the file and pipe
				// it through the processors

				mFileRetriever = new FileRetriever(mLocalFile);

				List<DataProcessor> dataProcessors = dataProcessorChain
						.getDataProcessors();

				for (DataProcessor processor : dataProcessors) {

					// skip the persistence processor
					if (processor instanceof PersistenceProcessor) {

						processor.resumeAt(mResumeOffset);
						dataProcessorChain.replaceDataProcessor(processor,
								new SeekDataProcessorWrapper(processor,
										mResumeOffset));

						continue;
					}
				}

				mFileRetriever.connect();

				mReadFromFile = true;
			}

		} else {
			// only pipe through
			mReadFromFile = false;
		}

		//prepare input stream
		if (mReadFromFile) {
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
		
			if (mReadFromFile) {
				mFileRetriever.disconnect();
			}
			mDataRetriever.disconnect();

			mConnected = false;
			mResumePrepared = false;
			mIn = null;
			
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
	 * @see de.phleisch.app.itsucks.io.DataRetriever#setUrl(java.net.URL)
	 */
	public void setUrl(URL pUrl) {
		throw new IllegalArgumentException("Not possible!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getUrl()
	 */
	public URL getUrl() {
		return mDataRetriever.getUrl();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getResultCode()
	 */
	public int getResultCode() {
		return mDataRetriever.getResultCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getContext()
	 */
	public Context getContext() {
		return mDataRetriever.getContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.io.DataRetriever#setContext(de.phleisch.app.itsucks.Context)
	 */
	public void setContext(Context pContext) {
		mDataRetriever.setContext(pContext);
	}

	public DataProcessorChain getDataProcessorChain() {
		return mDataProcessorChain;
	}

	public void setDataProcessorChain(DataProcessorChain pDataProcessorChain) {
		mDataProcessorChain = pDataProcessorChain;
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

}
