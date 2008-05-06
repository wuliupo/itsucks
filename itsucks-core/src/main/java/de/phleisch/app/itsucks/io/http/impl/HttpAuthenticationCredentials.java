/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 06.05.2008
 */

package de.phleisch.app.itsucks.io.http.impl;

public class HttpAuthenticationCredentials {

	private String mServer;
	private String mUser;
	private String mPassword;
	
	public String getServer() {
		return mServer;
	}
	public void setServer(String pServer) {
		mServer = pServer;
	}
	public String getUser() {
		return mUser;
	}
	public void setUser(String pUser) {
		mUser = pUser;
	}
	public String getPassword() {
		return mPassword;
	}
	public void setPassword(String pPassword) {
		mPassword = pPassword;
	}
	
}
