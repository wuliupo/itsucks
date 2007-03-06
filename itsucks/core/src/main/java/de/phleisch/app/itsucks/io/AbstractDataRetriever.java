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
	
	public List<DataProcessor> getDataProcessors() {
		return new ArrayList<DataProcessor>(mDataProcessors);
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
