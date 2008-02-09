/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 30.09.2007
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRetrieverConfiguration {

	public static final String CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION = 
		"HttpRetrieverConfiguration";
	
	private Integer mMaxConnectionsPerServer = null;
	private Integer mBandwidthLimit = null;
	
	private boolean mProxyEnabled = false;
	private String mProxyServer;
	private Integer mProxyPort;
	
	private boolean mProxyAuthenticationEnabled = false;
	private String mProxyUser;
	private String mProxyPassword;
	private String mProxyRealm;
	
	private String mUserAgent;
	
	private transient Map<String, Object> mSharedObjects = 
		Collections.synchronizedMap(new HashMap<String, Object>());

	
	public Integer getMaxConnectionsPerServer() {
		return mMaxConnectionsPerServer;
	}
	public void setMaxConnectionsPerServer(Integer pMaxConnectionsPerServer) {
		mMaxConnectionsPerServer = pMaxConnectionsPerServer;
	}
	public boolean isProxyEnabled() {
		return mProxyEnabled;
	}
	public void setProxyEnabled(boolean pProxyEnabled) {
		mProxyEnabled = pProxyEnabled;
	}
	public String getProxyServer() {
		return mProxyServer;
	}
	public void setProxyServer(String pProxyServer) {
		mProxyServer = pProxyServer;
	}
	public Integer getProxyPort() {
		return mProxyPort;
	}
	public void setProxyPort(Integer pProxyPort) {
		mProxyPort = pProxyPort;
	}
	public boolean isProxyAuthenticationEnabled() {
		return mProxyAuthenticationEnabled;
	}
	public void setProxyAuthenticationEnabled(boolean pProxyAuthenticationEnabled) {
		mProxyAuthenticationEnabled = pProxyAuthenticationEnabled;
	}
	public String getProxyUser() {
		return mProxyUser;
	}
	public void setProxyUser(String pProxyUser) {
		mProxyUser = pProxyUser;
	}
	public String getProxyPassword() {
		return mProxyPassword;
	}
	public void setProxyPassword(String pProxyPassword) {
		mProxyPassword = pProxyPassword;
	}
	public String getProxyRealm() {
		return mProxyRealm;
	}
	public void setProxyRealm(String pProxyRealm) {
		mProxyRealm = pProxyRealm;
	}
	public String getUserAgent() {
		return mUserAgent;
	}
	public void setUserAgent(String pUserAgent) {
		mUserAgent = pUserAgent;
	}
	public Integer getBandwidthLimit() {
		return mBandwidthLimit;
	}
	public void setBandwidthLimit(Integer pBandwidthLimit) {
		mBandwidthLimit = pBandwidthLimit;
	}
	public Map<String, Object> getSharedObjects() {
		return mSharedObjects;
	}
	
}
