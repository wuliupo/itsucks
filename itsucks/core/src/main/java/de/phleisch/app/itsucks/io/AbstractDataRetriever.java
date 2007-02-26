/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


public abstract class AbstractDataRetriever extends Observable implements DataRetriever {

	protected List<DataProcessor> mDataProcessors = new ArrayList<DataProcessor>();
	protected URL mUrl;
	
	public AbstractDataRetriever() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#addDataProcessor(de.phleisch.app.itsucks.io.DataProcessor)
	 */
	public void addDataProcessor(DataProcessor pDataProcessor) {
		mDataProcessors.add(pDataProcessor);
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

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#connect()
	 */
	public abstract void connect() throws Exception;
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#isDataAvailable()
	 */
	public abstract boolean isDataAvailable() throws Exception;
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#retrieve()
	 */
	public abstract void retrieve() throws Exception;
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#disconnect()
	 */
	public abstract void disconnect() throws Exception;
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#getBytesDownloaded()
	 */
	public abstract long getBytesDownloaded();
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#getProgress()
	 */
	public abstract float getProgress();
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#abort()
	 */
	public abstract void abort();
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.io.IDataRetriever#getMetadata()
	 */
	public abstract Metadata getMetadata();
}
