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
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;

import de.phleisch.app.itsucks.io.Metadata;

public class HttpMetadata extends Metadata {

	private String mContentType;
	private int mContentLength;
	private int mStatusCode;
	private HttpMethodBase mConnection;
	
	public String getContentType() {
		return mContentType;
	}
	public void setContentType(String mimetype) {
		mContentType = mimetype;
	}
	public int getContentLength() {
		return mContentLength;
	}
	public void setContentLength(int contentLength) {
		mContentLength = contentLength;
	}
	public int getStatusCode() {
		return mStatusCode;
	}
	public void setStatusCode(int statusCode) {
		mStatusCode = statusCode;
	}
	public void setConnection(HttpMethodBase pConnection) {
		mConnection = pConnection;
	}
	public String[] getHeaderField(String pName) {
		List<String> fields = new ArrayList<String>();
		
		HeaderElement[] values = new HeaderElement[0];
		try {
			Header requestHeader = mConnection.getRequestHeader(pName);
			if(requestHeader != null) {
				values = requestHeader.getValues();
			}
		} catch (HttpException e) {
			throw new RuntimeException(e);
		}
		for (int i = 0; i < values.length; i++) {
			fields.add(values[i].getValue());
		}
		
		return (String[]) fields.toArray(new String[fields.size()]);
	}
	

}
