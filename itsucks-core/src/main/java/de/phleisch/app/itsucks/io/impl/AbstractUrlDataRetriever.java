/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.impl;

import java.net.URL;

import de.phleisch.app.itsucks.io.UrlDataRetriever;


/**
 * Abstract implementation of an data retriever.
 * 
 * @author olli
 *
 */
public abstract class AbstractUrlDataRetriever implements UrlDataRetriever {

	protected URL mUrl;
	
	public AbstractUrlDataRetriever() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#getUrl()
	 */
	public URL getUrl() {
		return mUrl;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#setUrl(java.net.URL)
	 */
	public void setUrl(URL pUrl) {
		mUrl = pUrl;
	}
	
	public String toString() {
		return getClass().getName() + " Url:" + getUrl();
	}
	
}
