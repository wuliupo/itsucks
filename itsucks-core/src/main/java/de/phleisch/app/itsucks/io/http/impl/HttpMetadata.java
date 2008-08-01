/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.01.2007
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.util.Map;

import de.phleisch.app.itsucks.io.Metadata;

/**
 * Metadata for an http connection.
 * 
 * @author olli
 *
 */
public class HttpMetadata implements Metadata {

	private String mContentType;
	private long mContentLength;
	private int mStatusCode;
	private String mStatusText;
	private Map<String, String[]> mResponseParameters;
	private String mEncoding;
	
	/**
	 * Gets the content type of the file.
	 * 
	 * @return
	 */
	public String getContentType() {
		return mContentType;
	}
	
	/**
	 * Sets the content type of the file.
	 * 
	 * @param mimetype
	 */
	public void setContentType(String mimetype) {
		mContentType = mimetype;
	}
	
	/**
	 * Gets the content length of an file (if available).
	 * 
	 * @return
	 */
	public long getContentLength() {
		return mContentLength;
	}
	
	/**
	 * Sets the content length of an file.
	 * 
	 * @param contentLength
	 */
	public void setContentLength(long contentLength) {
		mContentLength = contentLength;
	}
	
	/**
	 * Returns the http status code.
	 * 
	 * @return
	 */
	public int getStatusCode() {
		return mStatusCode;
	}
	
	/**
	 * Sets the http status code.
	 * 
	 * @param statusCode
	 */
	public void setStatusCode(int statusCode) {
		mStatusCode = statusCode;
	}
	
	/**
	 * Gets the status text.
	 * 
	 * @return
	 */
	public String getStatusText() {
		return mStatusText;
	}
	
	/**
	 * Sets the status text.
	 * 
	 * @param pStatusText
	 */
	public void setStatusText(String pStatusText) {
		mStatusText = pStatusText;
	}

	/**
	 * Gets the file encoding.
	 * 
	 * @return
	 */
	public String getEncoding() {
		return mEncoding;
	}

	public void setEncoding(String pEncoding) {
		mEncoding = pEncoding;
	}
	
	
	/**
	 * Gets an http repsonse header field.
	 * 
	 * @param pName
	 * @return
	 */
	public String[] getResponseHeaderField(String pName) {
		return mResponseParameters.get(pName);
	}

	public void setResponseHeader(Map<String, String[]> pHeaders) {
		mResponseParameters = pHeaders;
	}

}
