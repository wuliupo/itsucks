/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.impl;

import java.net.URL;
import java.util.Observable;

import de.phleisch.app.itsucks.io.DataConsumer;
import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.job.Context;


/**
 * Abstract implementation of an data retriever.
 * 
 * @author olli
 *
 */
public abstract class AbstractDataRetriever extends Observable implements DataRetriever {

	protected URL mUrl;
	public DataConsumer mDataConsumer;
	protected Context mContext;
	
	public AbstractDataRetriever() {
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

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context pContext) {
		mContext = pContext;
	}

	public DataConsumer getDataConsumer() {
		return mDataConsumer;
	}

	public void setDataConsumer(DataConsumer pDataConsumer) {
		mDataConsumer = pDataConsumer;
	}

}
