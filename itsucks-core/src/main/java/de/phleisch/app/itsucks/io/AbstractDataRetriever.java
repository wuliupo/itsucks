/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io;

import java.net.URL;
import java.util.Observable;

import de.phleisch.app.itsucks.Context;
import de.phleisch.app.itsucks.processing.DataProcessorChain;


/**
 * Abstract implementation of an data retriever.
 * 
 * @author olli
 *
 */
public abstract class AbstractDataRetriever extends Observable implements DataRetriever {

	protected URL mUrl;
	protected DataProcessorChain mDataProcessorChain;
	protected Context mContext;
	
	public AbstractDataRetriever() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#getDataProcessorChain()
	 */
	public DataProcessorChain getDataProcessorChain() {
		return mDataProcessorChain;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.DataRetriever#setDataProcessorChain(de.phleisch.app.itsucks.processing.DataProcessorChain)
	 */
	public void setDataProcessorChain(DataProcessorChain pDataProcessorChain) {
		mDataProcessorChain = pDataProcessorChain;
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

}
