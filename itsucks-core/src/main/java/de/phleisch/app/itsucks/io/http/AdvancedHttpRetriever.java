/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.io.AbstractDataRetriever;

/**
 * Implentation of an data retriever for the http protocol.
 * 
 * @author olli
 *
 */
public class AdvancedHttpRetriever extends AbstractDataRetriever {

	private static int HTTP_STATUS_PARTIAL_CONTENT = 206;
	
	private static int HTTP_STATUS_REQUEST_TIMEOUT = 408;
	private static int HTTP_STATUS_RANGE_NOT_SATISFIABLE = 416;
	
	private static int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
	private static int HTTP_STATUS_SERVICE_UNAVAILABLE = 503;
	private static int HTTP_STATUS_GATEWAY_TIMEOUT = 504;
	
	
	private static Log mLog = LogFactory.getLog(AdvancedHttpRetriever.class);
	
	private static HttpClient mClient = null;
	private GetMethod mGet = null;
	private HttpMetadata mMetadata;
	private String mUserAgent;
	
	private float mProgress = -1;
	private long mBytesDownloaded = -1;

	private boolean mAbort = false;

	private long mBytesToSkip;
	
	static {
     	MultiThreadedHttpConnectionManager connectionManager = 
      		new MultiThreadedHttpConnectionManager();
     	//connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxHostConnections); TODO
     	
     	mClient = new HttpClient(connectionManager);
     	
	}
	
	public AdvancedHttpRetriever() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#connect()
	 */
	public void connect() throws IOException {
		
		if(mAbort) return;
		
		mGet = new GetMethod(mUrl.toString());
		mGet.setFollowRedirects(false);
		
		if(getUserAgent() != null) {
			HttpMethodParams params = mGet.getParams();
			
			params.setSoTimeout(90 * 1000); //90 seconds
			params.setParameter(HttpMethodParams.USER_AGENT, getUserAgent());
			//mGet.addRequestHeader("User-Agent", getUserAgent());
		}
		if(mBytesToSkip > 0) { //try to resume
			mGet.addRequestHeader("Range", "bytes=" + mBytesToSkip + "-");
		}
		
		mClient.executeMethod(mGet);
		mLog.debug("Connected to: " + mUrl + " / " + mGet.getStatusCode());
		
		//build metadata
		mMetadata = new HttpMetadata();
		
		Header contentType = mGet.getResponseHeader("Content-Type");
		if(contentType != null) {
			mMetadata.setContentType(contentType.getValue());	
		} else {
			mMetadata.setContentType("undefined");
		}
		
		mMetadata.setContentLength(mGet.getResponseContentLength());
		mMetadata.setStatusCode(mGet.getStatusCode());
		mMetadata.setStatusText(mGet.getStatusText());
		mMetadata.setConnection(mGet);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#isDataAvailable()
	 */
	public boolean isDataAvailable() throws Exception {
		if(mGet == null) {
			throw new IllegalStateException("Not connected!");
		}
		
		return mGet.getStatusCode() < 300;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#retrieve()
	 */
	public void retrieve() throws Exception {
	
		try {
			download();
		} catch (Exception e) {
			if(mAbort) {
				mLog.info("Exception occured while aborting retrival. URL: " + mUrl, e);
			} else {
				mLog.error("Error downloading url: " + mUrl, e);
			}
			throw e;
		} finally {
			disconnect();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#disconnect()
	 */
	public void disconnect() {
		if(mGet != null) {
			mGet.releaseConnection();
			mGet = null;
		}
	}
	
	private void download() throws Exception {
		
		InputStream input = mGet.getResponseBodyAsStream(); 

		if(input == null) {
			return;
		}
		
		//100k buffer
		byte buffer[] = new byte[102400];
		
		mBytesDownloaded = 0; //reset bytes downloaded
		int bytesRead;
		long completeContentLenght = mMetadata.getContentLength();
		
		while((bytesRead = input.read(buffer)) > 0) {
			
			if(mAbort ) {
				mLog.warn("DownloadJob aborted: " + this);
				break;
			}
			
			//mLog.error("Bytes read: " + allBytesRead + " from " + mMetadata.getContentLength() + " Progress: " + ((float)allBytesRead / (float)mMetadata.getContentLength()));

			mDataProcessorChain.process(buffer, bytesRead);


			//update the progress
			mBytesDownloaded += bytesRead;
			if(completeContentLenght > 0) {
				updateProgress(((float)mBytesDownloaded / (float)completeContentLenght));
			}
		}
		
		//set progress to 100 % if content length was not available
		if(completeContentLenght <= 0) {
			updateProgress(1);
		}
		
	}

	/**
	 * @param pProgress
	 */
	private void updateProgress(float pProgress) {
		mLog.trace("Update Progress: " + pProgress);
		
		if(mProgress != pProgress) {
			mProgress = pProgress;
			this.setChanged();
		}
		
		this.notifyObservers(NOTIFICATION_PROGRESS);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getMetadata()
	 */
	public HttpMetadata getMetadata() {
		return mMetadata;
	}

	/**
	 * Gets the http user agent.
	 * 
	 * @return
	 */
	public String getUserAgent() {
		return mUserAgent;
	}

	/**
	 * Sets the http user agent.
	 * 
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent) {
		mUserAgent = userAgent;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getBytesRetrieved()
	 */
	public long getBytesRetrieved() {
		return mBytesDownloaded;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getProgress()
	 */
	public float getProgress() {
		return mProgress;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#abort()
	 */
	public void abort() {
		mAbort = true;
		if(mGet != null) {
			mGet.abort();
		}
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#setBytesToSkip(long)
	 */
	public void setBytesToSkip(long pBytesToSkip) {
		if(mGet != null && mGet.isRequestSent()) {
			throw new IllegalStateException("Request already sent!");
		}
		
		mBytesToSkip = pBytesToSkip;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getBytesSkipped()
	 */
	public long getBytesSkipped() {
		
		if(mGet == null || !mGet.isRequestSent()) {
			throw new IllegalStateException("Request not sent!");
		}
		
		if(mMetadata.getStatusCode() == HTTP_STATUS_PARTIAL_CONTENT ||
				mMetadata.getStatusCode() == HTTP_STATUS_RANGE_NOT_SATISFIABLE) {
			return mBytesToSkip;
		} else {
			return 0;
		}
		
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getResultCode()
	 */
	public int getResultCode() {
		
		if(mAbort) {
			return RESULT_RETRIEVAL_ABORTED;
		}
		
		if(mMetadata == null) {
			return RESULT_RETRIEVAL_NOT_STARTED_YET;
		} 
		
		int statusCode = mMetadata.getStatusCode();
		int result;
		
		if(statusCode < 400) {
			result = RESULT_RETRIEVAL_OK;
		} else {
			
			if(statusCode == HTTP_STATUS_RANGE_NOT_SATISFIABLE) {
				//hm, maybe it's better to return failed and let the FileResumeRetriever handle this... 
				result = RESULT_RETRIEVAL_OK;
			} else if(statusCode == HTTP_STATUS_REQUEST_TIMEOUT) {
				result = RESULT_RETRIEVAL_FAILED_BUT_RETRYABLE; 
			} else if(statusCode == HTTP_STATUS_INTERNAL_SERVER_ERROR) {
				result = RESULT_RETRIEVAL_FAILED_BUT_RETRYABLE;
			} else if(statusCode == HTTP_STATUS_SERVICE_UNAVAILABLE) {
				result = RESULT_RETRIEVAL_FAILED_BUT_RETRYABLE; 
			} else if(statusCode == HTTP_STATUS_GATEWAY_TIMEOUT) {
				result = RESULT_RETRIEVAL_FAILED_BUT_RETRYABLE; 
			} else {
				result = RESULT_RETRIEVAL_FAILED;
			}
		}
		
		return result;
	}

}
