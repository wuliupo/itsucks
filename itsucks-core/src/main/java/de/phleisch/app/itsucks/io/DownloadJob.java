/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: DownloadJob.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 * Created on 03.03.2006
 */

package de.phleisch.app.itsucks.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.AbstractJob;
import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.JobParameter;
import de.phleisch.app.itsucks.processing.DataProcessorChain;
import de.phleisch.app.itsucks.processing.DataProcessorManager;


/**
 * This is the implementation of a job for downloading files.
 * It contains a URL to the file or directory to be downloaded.
 * 
 * @author olli
 *
 */
public class DownloadJob extends AbstractJob {

	private static final long serialVersionUID = 5609621387348634985L;

	public static final String PARAMETER_SKIP_DOWNLOADED_FILE = "job.download.skip_when_existing";
	
	private static Log mLog = LogFactory.getLog(DownloadJob.class);
	
	private boolean mSaveToFile = true;
	private File mSavePath = null;
	
	private URL mUrl;
	private DownloadJob mParent = null;
	private int mDepth = 0;
	private int mMaxRetryCount = 3;
	private int mTryCount = 0;
	private transient DataProcessorManager mDataProcessorManager;
	private transient DataRetrieverManager mDataRetrieverManager;
	private transient DataRetriever mDataRetriever;
	
	private float mProgress = -1;
	private long mBytesDownloaded = -1;
	private Metadata mMetadata = null;

	private long mWaitUntil = 0;
	private long mMinTimeBetweenRetry = 5000; //5 seconds 
	
	public DownloadJob() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.chaoscrawler.refactoring.Job#run()
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
				mLog.warn("Problem in disconnecting after error", e);
			}
		}
		
	}
	
	protected void download() throws Exception {
		String protocol = mUrl.getProtocol();
		
		mDataRetriever = 
			mDataRetrieverManager.getRetrieverForProtocol(protocol);
		
		//check if an data retriever is available
		if(mDataRetriever == null) {
			mLog.warn("Protocol not supported: '" + protocol + "', job aborted. - " + this);
			setState(Job.STATE_IGNORED);
			
			return;
		}
		
		mDataRetriever.setUrl(mUrl);
		
		boolean skip = false;
		
		//check if this file should be saved
		if(isSaveToFile()) {
			
			FileManager fileManager = new FileManager(this.getSavePath());
			File file = fileManager.buildSavePath(getUrl());
			
			if(file.exists()) {
			
				JobParameter skipParameter = getParameter(PARAMETER_SKIP_DOWNLOADED_FILE);
				
				if(skipParameter != null && Boolean.TRUE.equals(skipParameter.getValue())) {
					
					mLog.info("Skip job: " + this);
					skip = true;
					
				} else {
				
					mLog.info("Try to resume job: " + this);
					
					//ok, it seems the file already exists partially/completly
					//try to resume the file
					mDataRetriever = new FileResumeRetriever(mDataRetriever, file);
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

	protected void executeDownload() throws InterruptedException, Exception {
		
		//register an listener to get the progress events
		mDataRetriever.addObserver(new ProgressObserver());
		
		//check if we must wait, important when retrying downloads
		if(mWaitUntil > System.currentTimeMillis()) {
			Thread.sleep(mWaitUntil - System.currentTimeMillis());
		}
		
		//connect the retriever
		mDataRetriever.connect();
		
		//build the data processor chain
		DataProcessorChain dataProcessorChain =
			mDataProcessorManager.getProcessorChainForJob(this);
		
		//set up processor chain
		dataProcessorChain.setDataRetriever(mDataRetriever);
		dataProcessorChain.setJobManager(mJobManager);
		
		mDataRetriever.setDataProcessorChain(dataProcessorChain);
		
		dataProcessorChain.init();
		
		if(dataProcessorChain.size() > 0 && mDataRetriever.isDataAvailable()) {
			
			mDataRetriever.retrieve();
		}
		
		dataProcessorChain.finish();
		
		mDataRetriever.disconnect();
		
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
			
		} else if(resultCode == DataRetriever.RESULT_RETRIEVAL_FAILED 
				|| resultCode == DataRetriever.RESULT_RETRIEVAL_ABORTED) {
			
			setState(Job.STATE_ERROR);
		}
	}
	
	private class ProgressObserver implements Observer {

		public void update(Observable pO, Object pArg) {
			if(pArg == DataRetriever.NOTIFICATION_PROGRESS) {
				mProgress = mDataRetriever.getProgress();
				mBytesDownloaded = mDataRetriever.getBytesRetrieved();
				
				DownloadJob.this.setChanged();
				DownloadJob.this.notifyObservers(NOTIFICATION_PROGRESS); // notify observers 
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.AbstractJob#abort()
	 */
	@Override
	public void abort() {
		if(mDataRetriever != null) {
			mDataRetriever.abort();
		}
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
				mUrl = new URL(pUrl.toExternalForm() + "/");
			} catch (MalformedURLException e) {
				throw new IllegalStateException("Error appending slash to url: " + pUrl, e);
			}
		} else {
			mUrl = pUrl;
		}
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

	/**
	 * Gets the recursive depth of the job.
	 * @return
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
	public void setParent(DownloadJob pParent) {
		mParent = pParent;
		mDepth = pParent.getDepth() + 1;
		setSavePath(pParent.getSavePath());
		setMaxRetryCount(pParent.getMaxRetryCount());
	}

	/**
	 * @return true if this file should be saved as file.
	 */
	public boolean isSaveToFile() {
		return mSaveToFile;
	}

	/**
	 * Sets if this file should be saved as file.
	 * @param pSaveToFile
	 */
	public void setSaveToFile(boolean pSaveToFile) {
		mSaveToFile = pSaveToFile;
	}
	
	/**
	 * Returns the base save path for saving downloaded files.
	 * 
	 * @return
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

	/**
	 * @return the data retriever used to download from the url.
	 */
	public DataRetriever getDataRetriever() {
		return mDataRetriever;
	}

	/**
	 * Returns the current download progress.
	 * @return
	 */
	public float getProgress() {
		return mProgress;
	}

	/**
	 * Returns the maximum count of retries when an retryable error occurs.
	 * @return
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

	/**
	 * Gets the waiting time between two retries.
	 * @return 
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
	
	/**
	 * Returns the count of bytes which are downloaded
	 * @return
	 */
	public long getBytesDownloaded() {
		return mBytesDownloaded;
	}
	
	/**
	 * Gets the metadata of the data retriever.
	 * @return
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

}
