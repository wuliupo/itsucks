/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.05.2008
 */

package de.phleisch.app.itsucks.io.http.impl;

public class Cookie {

	private String mName;
	private String mValue;
	private String mDomain;
	private String mPath;
	
	public Cookie() {
	}
	
	public Cookie(final Cookie pCookie) {
		setName(pCookie.getName());
		setValue(pCookie.getValue());
		setDomain(pCookie.getDomain());
		setPath(pCookie.getPath());
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String pName) {
		mName = pName;
	}
	public String getValue() {
		return mValue;
	}
	public void setValue(String pValue) {
		mValue = pValue;
	}
	public String getDomain() {
		return mDomain;
	}
	public void setDomain(String pDomain) {
		mDomain = pDomain;
	}
	public String getPath() {
		return mPath;
	}
	public void setPath(String pPath) {
		mPath = pPath;
	}
}