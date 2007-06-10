/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io;

import java.net.URL;
import java.util.Observable;

import de.phleisch.app.itsucks.processing.DataProcessorChain;


public abstract class AbstractDataRetriever extends Observable implements DataRetriever {

	protected URL mUrl;
	protected DataProcessorChain mDataProcessorChain;
	
	public AbstractDataRetriever() {
		super();
	}

	public DataProcessorChain getDataProcessorChain() {
		return mDataProcessorChain;
	}

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

}
