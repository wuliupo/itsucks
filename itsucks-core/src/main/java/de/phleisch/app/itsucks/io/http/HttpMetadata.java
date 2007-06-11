/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.01.2007
 */

package de.phleisch.app.itsucks.io.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpMethodBase;

import de.phleisch.app.itsucks.io.Metadata;

public class HttpMetadata extends Metadata {

	private String mContentType;
	private long mContentLength;
	private int mStatusCode;
	private String mStatusText;
	private HttpMethodBase mConnection;
	
	public String getContentType() {
		return mContentType;
	}
	
	public void setContentType(String mimetype) {
		mContentType = mimetype;
	}
	
	public long getContentLength() {
		return mContentLength;
	}
	
	public void setContentLength(long contentLength) {
		mContentLength = contentLength;
	}
	
	public int getStatusCode() {
		return mStatusCode;
	}
	
	public void setStatusCode(int statusCode) {
		mStatusCode = statusCode;
	}
	
	public String getStatusText() {
		return mStatusText;
	}
	
	public void setStatusText(String pStatusText) {
		mStatusText = pStatusText;
	}
	
	public void setConnection(HttpMethodBase pConnection) {
		mConnection = pConnection;
	}
	
	public String getEncoding() {
		return mConnection.getResponseCharSet();
	}
	
	public String[] getResponseHeaderField(String pName) {
		List<String> fields = new ArrayList<String>();
		
		HeaderElement[] values = new HeaderElement[0];
		
		Header requestHeader = mConnection.getResponseHeader(pName);
		if(requestHeader != null) {
			values = requestHeader.getElements();
		}

		for (int i = 0; i < values.length; i++) {
			fields.add(values[i].getName());
		}
		
		return (String[]) fields.toArray(new String[fields.size()]);
	}

}
