/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.processing.download.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.phleisch.app.itsucks.io.FileManager;
import de.phleisch.app.itsucks.io.UrlDataRetriever;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.DownloadJob;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.DataProcessorChain;
import de.phleisch.app.itsucks.processing.DataProcessorInfo;
import de.phleisch.app.itsucks.processing.ProcessingException;
import de.phleisch.app.itsucks.processing.impl.AbstractDataProcessor;

/**
 * An data processor which saves the data in an file on the disk.
 * 
 * @author olli
 *
 */
public class PersistenceProcessor extends AbstractDataProcessor implements DataProcessor {

	private static Log mLog = LogFactory.getLog(PersistenceProcessor.class);
	
	private static Object mCreateFolderMutex = new Object();
	
	private File mFile;
	private FileOutputStream mFileOut = null;
	private OutputStream mBufferedOut = null;
	
	private long mResumeAt;
	private boolean mPreserveDataOnRollback = true;
	
	public PersistenceProcessor() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#supports(de.phleisch.app.itsucks.Job)
	 */
	@Override
	public boolean supports(Job pJob) {
		if(pJob instanceof DownloadJob) {
			DownloadJob downloadJob = (DownloadJob) pJob;
			return downloadJob.isSaveToDisk();
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#init()
	 */
	@Override
	public void init() throws ProcessingException {
	
		DataProcessorChain processorChain = getProcessorChain();
		
		DownloadJob downloadJob = (DownloadJob) processorChain.getJob();
		File target_path = downloadJob.getSavePath();
		
		FileManager fileManager = new FileManager(target_path);
		
		UrlDataRetriever dataRetriever = (UrlDataRetriever) downloadJob.getDataRetriever();
		
		mFile = fileManager.buildSavePath(
				dataRetriever.getUrl(), 
				dataRetriever.getMetadata().getFilename());
	}

	private void prepareOutputStream() throws IOException, FileNotFoundException {
		
		//create the parent folder
		createFolders(mFile.getParentFile());
		
		mLog.debug("saving file: " + mFile);
		
		if(mResumeAt > 0) { //skip bytes when resuming
			mFileOut = new FileOutputStream(mFile, true);
			mFileOut.getChannel().position(mResumeAt);
		} else {
			mFileOut = new FileOutputStream(mFile, false);
		}
		
		mBufferedOut = new BufferedOutputStream(mFileOut);
	}

	private void createFolders(File pFolder) throws IOException {
		
		synchronized(mCreateFolderMutex) {
		
			if(pFolder.exists() && pFolder.isDirectory()) {
				return;
			}
			
			DownloadJob downloadJob = (DownloadJob) getProcessorChain().getJob();
			File baseSavePath = downloadJob.getSavePath();
			
			//move away any files which are in the way
			moveBlockingFiles(pFolder, baseSavePath);
			
			if(!pFolder.mkdirs()) {
				throw new IOException("Cannot create folder(s): " + pFolder);
			}
			
		}
	}

	private void moveBlockingFiles(File pFolder, File pBaseSavePath) throws IOException {
		
		if(pFolder.equals(pBaseSavePath)) {
			return;
		}
		
		if(pFolder.exists() && pFolder.isFile()) {
			if(!pFolder.renameTo(new File(pFolder.getParentFile(), pFolder.getName() + ".moved"))) {
				throw new IOException("Cannot rename file which is in the way: " + pFolder);
			}
		}
		
		moveBlockingFiles(pFolder.getParentFile(), pBaseSavePath);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#resumeAt(long)
	 */
	public void resumeAt(long pByteOffset) {
		mResumeAt = pByteOffset;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#process(byte[], int)
	 */
	public DataChunk process(DataChunk pDataChunk) throws ProcessingException {
		
		try {
			
			if(mBufferedOut == null && ((DownloadJob)getProcessorChain().getJob()).isSaveToDisk()) {
				prepareOutputStream();
			}
		
			if(mBufferedOut != null) {
				mBufferedOut.write(pDataChunk.getData(), 0, pDataChunk.getSize());
			}
			
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
		
		return pDataChunk;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#finish()
	 */
	@Override
	public void finish() {
		super.finish();
		if(mBufferedOut != null) {
			try {
				mBufferedOut.close();
			} catch (IOException e) {
				throw new RuntimeException("Error closing file", e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.impl.AbstractDataProcessor#abort()
	 */
	@Override
	public void abort() {
		super.abort();
		
		closeFile(true);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#rollback()
	 */
	@Override
	public void rollback() {
		super.rollback();
		
		closeFile(!mPreserveDataOnRollback);
	}
	
	protected void closeFile(boolean pDeleteFile) {
		if(mBufferedOut != null) {
			try {
				mBufferedOut.close();
			} catch (IOException e) {
				throw new RuntimeException("Error closing file", e);
			}
		}
		mBufferedOut = null;
		
		if(mFile != null && mFile.exists() && pDeleteFile) {
			boolean deleteSucessful = mFile.delete();
			if(!deleteSucessful) {
				mLog.error("Could not delete partial file: " + mFile.getAbsolutePath());
			}
		}
	}

	/**
	 * Gets if the persistence processor should preserve the file in case of an chain error.
	 */
	public boolean isPreserveDataOnRollback() {
		return mPreserveDataOnRollback;
	}

	/**
	 * Sets if the persistence processor should preserve the file in case of an chain error.
	 * 
	 * @param pPreserveDataWhenRollback
	 */
	@Inject
	public void setPreserveDataOnRollback(
			@Named("persistenceProcessor.preservePartialDownloadedFile") boolean pPreserveDataOnRollback) {
		mPreserveDataOnRollback = pPreserveDataOnRollback;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#getInfo()
	 */
	public DataProcessorInfo getInfo() {
		
		return new DataProcessorInfo(
				DataProcessorInfo.ResumeSupport.RESUME_SUPPORTED,
				DataProcessorInfo.ProcessorType.CONSUMER,
				DataProcessorInfo.StreamingSupport.STREAMING_SUPPORTED
		);
	}

}
