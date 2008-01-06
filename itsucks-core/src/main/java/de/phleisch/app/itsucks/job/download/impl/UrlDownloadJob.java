/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: DownloadJob.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks.job.download.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.FileManager;
import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.io.impl.DataRetrieverManager;
import de.phleisch.app.itsucks.io.impl.FileResumeRetriever;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.DownloadJob;
import de.phleisch.app.itsucks.job.impl.AbstractJob;
import de.phleisch.app.itsucks.processing.AbortProcessingException;
import de.phleisch.app.itsucks.processing.DataProcessorChain;
import de.phleisch.app.itsucks.processing.ProcessingException;
import de.phleisch.app.itsucks.processing.impl.DataProcessorManager;


/**
 * This is the implementation of a job for downloading files.
 * It contains a URL to the file or directory to be downloaded.
 * 
 * @author olli
 *
 */
public class UrlDownloadJob extends AbstractJob implements DownloadJob, Cloneable {

	private static final long serialVersionUID = 1714294563019859104L;

	/**
	 * Constant used to determine when the progress property has changed.
	 * The old value in the PropertyChangeEvent will be the old progress
	 * and the new value will be the new progress.
	 * 
	 * @see #getProgress
	 * @see #addPropertyChangeListener
	 */
	public static final String JOB_PROGRESS_PROPERTY = "Progress";
	
	public static final String JOB_PARAMETER_SKIP_DOWNLOADED_FILE = "job.download.skip_when_existing";
	
	private static Log mLog = LogFactory.getLog(UrlDownloadJob.class);
	
	private boolean mSaveToDisk = true;
	private boolean mAbort = false; //indicates if the download has been aborted
	private File mSavePath = null;
	
	private URL mUrl;
	private UrlDownloadJob mParent = null;
	private int mDepth = 0;
	private int mMaxRetryCount = 3;
	private int mTryCount = 0;
	private transient DataProcessorManager mDataProcessorManager;
	private transient DataRetrieverManager mDataRetrieverManager;
	private transient DataRetriever mDataRetriever;
	private transient FileResumeRetriever mFileResumeRetriever;
	
	private float mProgress = -1;
	private long mBytesDownloaded = -1;
	private transient Metadata mMetadata = null;

	private long mWaitUntil = 0;
	private long mMinTimeBetweenRetry = 5000; //5 seconds 

	public UrlDownloadJob() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.chaoscrawler.refactoring.Job#run()
	 */
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#run()
	 */
	@Override
	public void run() throws Exception {
	
		try {
			download();
		} catch (Exception e) {
			
			this.setState(STATE_ERROR);
			
			//check if the exception can be ignored because we aborted the retrieval
			if(mDataRetriever != null 
					&& mDataRetriever.getResultCode() == DataRetriever.RESULT_RETRIEVAL_ABORTED) {
				
				mLog.info("Aborting download caused exception. URL: " + mUrl, e);
				
			} else {
				mLog.error("Error downloading url: " + mUrl, e);
				
				throw e;
			}
		} finally {
			try {
				if(mDataRetriever != null) {
					mDataRetriever.disconnect();
					mDataRetriever = null;
				}
			} catch (Exception e) {
				mLog.warn("Error occured while trying to disconnect", e);
			}
		}
		
	}
	
	protected void download() throws IOException {
		URL url = mUrl;
		String protocol = url.getProtocol();
		
		mDataRetriever = 
			mDataRetrieverManager.getRetrieverForProtocol(protocol);
		
		//check if an data retriever is available
		if(mDataRetriever == null) {
			mLog.warn("Protocol not supported: '" + protocol + "', job aborted. - " + this);
			setState(Job.STATE_IGNORED);
			
			return;
		}
		
		mDataRetriever.setContext(getContext());
		mDataRetriever.setUrl(url);
		
		boolean skip = false;
		
		//check if this file could be resumed
		if(isSaveToDisk()) {
			
			FileManager fileManager = new FileManager(this.getSavePath());
			File file = fileManager.buildSavePath(url);
			
			if(file.exists()) {
			
				JobParameter skipParameter = getParameter(JOB_PARAMETER_SKIP_DOWNLOADED_FILE);
				
				if(skipParameter != null && Boolean.TRUE.equals(skipParameter.getValue())) {
					
					mLog.info("Skip job: " + this);
					skip = true;
					
				} else {
				
					mLog.info("Try to resume job: " + this);
					
					//ok, it seems the file already exists partially/completly
					//try to resume the file
					mFileResumeRetriever = new FileResumeRetriever(mDataRetriever, file);
					mDataRetriever = mFileResumeRetriever;
				}
			}
		}
		
		if(!skip) {
			executeDownload();
		} else  {
			setState(Job.STATE_IGNORED);
		}
		
		mDataRetriever = null;
	}

	protected void executeDownload() throws IOException {
		
		//register an listener to get the progress events
		mDataRetriever.addObserver(new ProgressObserver());
		
		//check if we must wait, important when retrying downloads
		if(mWaitUntil > System.currentTimeMillis()) {
			try {
				Thread.sleep(mWaitUntil - System.currentTimeMillis());
			} catch (InterruptedException e) {
				mLog.info("Aborted while waiting.");
			}
		}
		
		//if the job has been aborted in the meantime, stop here
		if(mAbort) {
			setState(Job.STATE_IGNORED);
			return;
		}
		
		try {
			//connect the retriever
			mDataRetriever.connect();
			
			executeProcessorChain();
		} finally {
			mDataRetriever.disconnect();
		}
		
		//save the metadata
		mMetadata = mDataRetriever.getMetadata();
		
		mTryCount ++;
		int resultCode = mDataRetriever.getResultCode(); 
		
		if(resultCode == DataRetriever.RESULT_RETRIEVAL_OK) {
			
			setState(Job.STATE_FINISHED);
			
		} else if(resultCode == DataRetriever.RESULT_RETRIEVAL_FAILED_BUT_RETRYABLE) {
			
			if(mTryCount <= (mMaxRetryCount + 1)) {
				
				mWaitUntil = System.currentTimeMillis() + mMinTimeBetweenRetry;
				setState(Job.STATE_OPEN);
			} else {
				setState(Job.STATE_ERROR);
			}
			
		} else if(resultCode == DataRetriever.RESULT_RETRIEVAL_FAILED) {
			setState(Job.STATE_ERROR);
		} else if(resultCode == DataRetriever.RESULT_RETRIEVAL_ABORTED) {
			setState(Job.STATE_IGNORED);
		} else {
			setState(Job.STATE_ERROR);
		}
	}

	private void executeProcessorChain() throws IOException {
		
		//build the data processor chain
		DataProcessorChain dataProcessorChain =
			mDataProcessorManager.getProcessorChainForJob(this);
		
		//if resuming from file, set the processor chain to the resumer
		//because it's changing the processing chain
		if(mFileResumeRetriever != null) {
			mFileResumeRetriever.setDataProcessorChain(dataProcessorChain);
		}
		
		//set up processor chain		
		dataProcessorChain.setDataRetriever(mDataRetriever);
		dataProcessorChain.setJobManager(mJobManager);
		
		try {
			dataProcessorChain.init();
			dataProcessorChain.run();
			
		} catch(IOException ioex) {
			throw ioex;
		} catch(AbortProcessingException ex) {
			mLog.info("Chain was aborted.");
		} catch(ProcessingException ex) {
			throw new RuntimeException("Error retrieving/processing data.", ex);
		} finally {
			dataProcessorChain.finish();
		}
	}
	
	private class ProgressObserver implements Observer {

		public void update(Observable pO, Object pArg) {
			if(DataRetriever.NOTIFICATION_PROGRESS.equals(pArg)) {
				float oldProgress = mProgress;
				mProgress = mDataRetriever.getProgress();
				mBytesDownloaded = mDataRetriever.getBytesRetrieved();
				
				firePropertyChange(JOB_PROGRESS_PROPERTY, oldProgress, mProgress);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.AbstractJob#abort()
	 */
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#abort()
	 */
	@Override
	public void abort() {
		mAbort  = true;
		
		if(mDataRetriever != null) {
			mDataRetriever.abort();
		}
		
		mLog.info("Job aborted: " + this);
	}
	
	/**
	 * Gets the URL to be downloaded.
	 * 
	 * @return
	 */
	public URL getUrl() {
		return mUrl;
	}
	
	/**
	 * Sets the url to be downloaded.
	 * 
	 * @param pUrl
	 */
	public void setUrl(URL pUrl) {
		
		//fix the url if no path part exists
		if("".equals(pUrl.getPath()) && pUrl.getQuery() == null) {
			try {
				pUrl = new URL(pUrl.toExternalForm() + "/");
			} catch (MalformedURLException e) {
				throw new IllegalStateException("Error appending slash to url: " + pUrl, e);
			}
		}
		
		mUrl = pUrl;
	}

	/**
	 * Sets the data retriever manager.
	 * @param pDataRetrieverManager
	 */
	public void setDataRetrieverManager(DataRetrieverManager pDataRetrieverManager) {
		mDataRetrieverManager = pDataRetrieverManager;
	}

	/**
	 * Sets the data processor manager.
	 * @param pDataProcessorManager
	 */
	public void setDataProcessorManager(DataProcessorManager pDataProcessorManager) {
		mDataProcessorManager = pDataProcessorManager;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#getDepth()
	 */
	public int getDepth() {
		return mDepth;
	}

	/**
	 * Sets the recursive depth of the job.
	 * @param pDepth
	 */
	public void setDepth(int pDepth) {
		mDepth = pDepth;
	}

	/**
	 * @return the parent of the job, null if none.
	 */
	public Job getParent() {
		return mParent;
	}

	/**
	 * Sets the parent of the job.
	 * This method also copies the configuration 
	 * from the parent to this instance.
	 * 
	 * @param pParent
	 */
	public void setParent(UrlDownloadJob pParent) {
		mParent = pParent;
		mDepth = pParent.getDepth() + 1;
		setSavePath(pParent.getSavePath());
		setMaxRetryCount(pParent.getMaxRetryCount());
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#isSaveToDisk()
	 */
	public boolean isSaveToDisk() {
		return mSaveToDisk;
	}

	/**
	 * Sets if this file should be saved as file.
	 * @param pSaveToFile
	 */
	public void setSaveToDisk(boolean pSaveToDisk) {
		mSaveToDisk = pSaveToDisk;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#getSavePath()
	 */
	public File getSavePath() {
		return mSavePath;
	}

	/**
	 * Sets the base save path for saving downloaded files.
	 * 
	 * @param pSavePath
	 */
	public void setSavePath(File pSavePath) {
		mSavePath = pSavePath;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#getDataRetriever()
	 */
	public DataRetriever getDataRetriever() {
		return mDataRetriever;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#getProgress()
	 */
	public float getProgress() {
		return mProgress;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#getMaxRetryCount()
	 */
	public int getMaxRetryCount() {
		return mMaxRetryCount;
	}

	/**
	 * Sets the maximum count of retries when an retryable error occurs.
	 * @param pMaxRetryCount
	 */
	public void setMaxRetryCount(int pMaxRetryCount) {
		mMaxRetryCount = pMaxRetryCount;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#getMinTimeBetweenRetry()
	 */
	public long getMinTimeBetweenRetry() {
		return mMinTimeBetweenRetry;
	}

	/**
	 * Sets the waiting time between two retries. 
	 * @param pMinTimeBetweenRetry
	 */
	public void setMinTimeBetweenRetry(long pMinTimeBetweenRetry) {
		mMinTimeBetweenRetry = pMinTimeBetweenRetry;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#getBytesDownloaded()
	 */
	public long getBytesDownloaded() {
		return mBytesDownloaded;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.job.download.impl.DownloadJob#getMetadata()
	 */
	public Metadata getMetadata() {
		if(mDataRetriever != null) {
			return mDataRetriever.getMetadata();
		} else {
			return mMetadata;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DownloadJob (State: " + getState() + ", Prio: " + getPriority() + ", URL: '" + getUrl() + "')";
	}

	@Override
	public UrlDownloadJob clone() {
		try {
			return (UrlDownloadJob) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
}
