/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.02.2008
 */

package de.phleisch.app.itsucks.io.impl;

import java.io.File;
import java.net.URL;

import de.phleisch.app.itsucks.io.UrlDataRetriever;

public class FileResumeUrlRetriever 
		extends FileResumeRetriever 
		implements UrlDataRetriever {

	public FileResumeUrlRetriever(UrlDataRetriever pDataRetriever, File pFile) {
		super(pDataRetriever, pFile);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.UrlDataRetriever#getUrl()
	 */
	public URL getUrl() {
		return ((UrlDataRetriever)this.mDataRetriever).getUrl();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.UrlDataRetriever#setUrl(java.net.URL)
	 */
	public void setUrl(URL pUrl) {
		throw new IllegalArgumentException("Not possible!");
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.UrlDataRetriever#getResultCode()
	 */
	public int getResultCode() {
		return ((UrlDataRetriever)mDataRetriever).getResultCode();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.UrlDataRetriever#getSuggestedTimeToWaitForRetry()
	 */
	public long getSuggestedTimeToWaitForRetry() {
		return ((UrlDataRetriever)mDataRetriever).getSuggestedTimeToWaitForRetry();
	}
	
}
