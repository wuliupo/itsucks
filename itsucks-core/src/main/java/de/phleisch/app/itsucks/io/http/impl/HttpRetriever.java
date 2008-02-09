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

/**
 * Implentation of an data retriever for the http protocol.
 * 
 * @author olli
 *
 */
public class HttpRetriever extends AbstractDataRetriever {

	private static int HTTP_STATUS_PARTIAL_CONTENT = 206;
	
	private static int HTTP_STATUS_REQUEST_TIMEOUT = 408;
	private static int HTTP_STATUS_RANGE_NOT_SATISFIABLE = 416;
	
	private static int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
	private static int HTTP_STATUS_SERVICE_UNAVAILABLE = 503;
	private static int HTTP_STATUS_GATEWAY_TIMEOUT = 504;
	
	private static Log mLog = LogFactory.getLog(HttpRetriever.class);
	
	protected HttpRetrieverConfiguration mConfiguration = createDefaultConfiguration();
	
	protected GetMethod mGet = null;
	protected HttpMetadata mMetadata;
	
	protected boolean mAbort = false;

	protected long mBytesToSkip;
	
	public HttpRetriever() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#connect()
	 */
	public void connect() throws IOException {
		
		if(mAbort) {
			throw new IllegalStateException("Retriever is aborted.");
		}
		
		HttpClient client = getHttpClientFromConfiguration();
		
		mGet = new GetMethod(mUrl.toString());
		mGet.setFollowRedirects(false);

		HttpMethodParams params = mGet.getParams();
		params.setSoTimeout(90 * 1000); //90 seconds
		
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
	
	protected HttpClient getHttpClientFromConfiguration() {
		
		HttpRetrieverConfiguration configuration = getConfiguration();
		
		//try to get share httpclient from configuration
		HttpClient httpClient = 
			(HttpClient) configuration.getSharedObjects().get("AdvancedHttpRetriever_HttpClient");
		
		if(httpClient == null) {
			//httpclient instance not available, create a new one.
			
			synchronized (configuration) {
			
				//try again, because in the time waiting for the lock, the http client 
				//could be created by another thread.
				httpClient = 
					(HttpClient) configuration.getSharedObjects().get("AdvancedHttpRetriever_HttpClient");
				
				//create and save the http client
				if(httpClient == null) {
		     		httpClient = createHttpClient(configuration);
		     		configuration.getSharedObjects().put(
		     				"AdvancedHttpRetriever_HttpClient", httpClient);
				}
			}
		}
		
		return httpClient;
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
   				params.setParameter(HttpMethodParams.USER_AGENT, 
   						pConfiguration.getUserAgent());
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
		
		HttpRetrieverConfiguration httpRetrieverConfiguration = getConfiguration();
		
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

	public HttpRetrieverConfiguration createDefaultConfiguration() {
		
		HttpRetrieverConfiguration defaultConfiguration = 
			new HttpRetrieverConfiguration();
		
		defaultConfiguration.setUserAgent("Mozilla/5.0");
		
		
		return defaultConfiguration;
	}

	public HttpRetrieverConfiguration getConfiguration() {
		return mConfiguration;
	}

	public void setConfiguration(HttpRetrieverConfiguration pConfiguration) {
		if(pConfiguration == null) {
			throw new NullPointerException("Configuration is null.");
		}
		
		mConfiguration = pConfiguration;
	}
	
}
