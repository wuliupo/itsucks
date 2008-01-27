/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.io.impl.AbstractDataRetriever;
import de.phleisch.app.itsucks.io.impl.ThrottledInputStream;
import de.phleisch.app.itsucks.job.Context;

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
	
	private static Object STATIC_MUTEX = new Object();
	
	private static Log mLog = LogFactory.getLog(AdvancedHttpRetriever.class);
	
	private GetMethod mGet = null;
	private HttpMetadata mMetadata;
	private String mUserAgent;
	
	private boolean mAbort = false;

	private long mBytesToSkip;
	
	public AdvancedHttpRetriever() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#connect()
	 */
	public void connect() throws IOException {
		
		if(mAbort) {
			throw new IllegalStateException("Retriever is aborted.");
		}

		HttpClient client = getHttpClientFromContext();
		
		mGet = new GetMethod(mUrl.toString());
		mGet.setFollowRedirects(false);

		HttpMethodParams params = mGet.getParams();
		params.setSoTimeout(90 * 1000); //90 seconds
		
		if(getUserAgent() != null) {
			params.setParameter(HttpMethodParams.USER_AGENT, getUserAgent());
		}
		if(mBytesToSkip > 0) { //try to resume
			mGet.addRequestHeader("Range", "bytes=" + mBytesToSkip + "-");
		}
		
		client.executeMethod(mGet);
		mLog.debug("Connected to: " + mUrl + " Status: " + mGet.getStatusCode());
		
		//build metadata
		mMetadata = new HttpMetadata();
		
		Header contentType = mGet.getResponseHeader("Content-Type");
		if(contentType != null && mGet.getStatusCode() < 400) {
			mMetadata.setContentType(contentType.getValue());	
		} else {
			mMetadata.setContentType("undefined");
		}
		
		mMetadata.setContentLength(mGet.getResponseContentLength());
		mMetadata.setStatusCode(mGet.getStatusCode());
		mMetadata.setStatusText(mGet.getStatusText());
		mMetadata.setConnection(mGet);
	}
	
	private HttpClient getHttpClientFromContext() {
		
		Context jobContext = getContext();
		
		HttpClient httpClient = 
			(HttpClient) jobContext.getContextParameter("AdvancedHttpRetriever_HttpClient");
		
		if(httpClient == null) {
			
			synchronized (STATIC_MUTEX) {
			
				//try again, because in the time waiting for the lock, the configuration 
				//could be created by another thread.
				httpClient = 
					(HttpClient) jobContext.getContextParameter("AdvancedHttpRetriever_HttpClient");
				
				if(httpClient == null) {
					HttpRetrieverConfiguration configuration = getHttpRetrieverConfiguration(jobContext);
		     		httpClient = createHttpClient(configuration);
		     		jobContext.setContextParameter("AdvancedHttpRetriever_HttpClient", httpClient);
				}
			}
		}
		
		return httpClient;
	}

	protected HttpRetrieverConfiguration getHttpRetrieverConfiguration(
			Context jobContext) {
		
		HttpRetrieverConfiguration configuration = 
			(HttpRetrieverConfiguration) jobContext.getContextParameter(
					HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION);
		return configuration;
	}

	protected HttpClient createHttpClient(HttpRetrieverConfiguration pConfiguration) {
		
     	MultiThreadedHttpConnectionManager connectionManager = 
      		new MultiThreadedHttpConnectionManager();
     	
     	HttpClient httpClient = new HttpClient(connectionManager);
     	
     	if(pConfiguration != null) {
     		
     		HttpConnectionManagerParams params = connectionManager.getParams();
     		
     		//set max connections per server
     		if(pConfiguration.getMaxConnectionsPerServer() != null) {
     			params.setDefaultMaxConnectionsPerHost(
     					pConfiguration.getMaxConnectionsPerServer() );
     		}

     		//set proxy configuration
     		if(pConfiguration.isProxyEnabled()) {
     			
     			httpClient.getHostConfiguration().setProxy(
     					pConfiguration.getProxyServer(), 
     					pConfiguration.getProxyPort());
     		}
     		if(pConfiguration.isProxyAuthenticationEnabled()) {
     			
     			httpClient.getState().setProxyCredentials(
     					new AuthScope(
     							pConfiguration.getProxyServer(), 
     							AuthScope.ANY_PORT, 
     							pConfiguration.getProxyRealm(), 
     							AuthScope.ANY_SCHEME),
     					new UsernamePasswordCredentials(
     							pConfiguration.getProxyUser(), 
     							pConfiguration.getProxyPassword()));
     		}
     		if(pConfiguration.getUserAgent() != null) {
     			setUserAgent(pConfiguration.getUserAgent());
     		}
     	}
     	
		return httpClient;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#isDataAvailable()
	 */
	public boolean isDataAvailable() throws IOException {
		if(mGet == null) {
			throw new IllegalStateException("Not connected!");
		}
		
		return mGet.getStatusCode() < 300;
	}
	
	public InputStream getDataAsInputStream() throws IOException {
		
		if(mGet == null) {
			throw new IllegalStateException("Not connected");
		}
		
		InputStream in = mGet.getResponseBodyAsStream();
		
		HttpRetrieverConfiguration httpRetrieverConfiguration = 
			getHttpRetrieverConfiguration(getContext());
		
		if(httpRetrieverConfiguration != null) {
			Integer bandwidthLimit = httpRetrieverConfiguration.getBandwidthLimit();
			
			if(bandwidthLimit != null && bandwidthLimit > 0) {
				//build throttled stream
				in = new ThrottledInputStream(in, bandwidthLimit);
			}
		}
		
		return in;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#disconnect()
	 */
	public void disconnect() {
		
		if(mGet != null) {
			mGet.abort();
			mGet.releaseConnection();
			mGet = null;
			
			mLog.debug("Disconnected from: " + mUrl);
		}
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

	public long getContentLenght() {
		if(mMetadata == null) {
			throw new IllegalStateException("Not connected");
		}
		
		return mMetadata.getContentLength();
	}

}
