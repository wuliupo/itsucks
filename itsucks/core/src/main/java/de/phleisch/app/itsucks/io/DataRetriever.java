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


public abstract class DataRetriever extends Observable {

	protected List<DataProcessor> mDataProcessors = new ArrayList<DataProcessor>();
	protected URL mUrl;
	
	/**
	 * This type of notification will be send if the progress changes.
	 */
	public final static Integer NOTIFICATION_PROGRESS = 100;
	
	public DataRetriever() {
		super();
	}
	
	public void addDataProcessor(DataProcessor pDataProcessor) {
		mDataProcessors.add(pDataProcessor);
	}

	public URL getUrl() {
		return mUrl;
	}

	public void setUrl(URL pUrl) {
		mUrl = pUrl;
	}

	protected abstract void connect() throws Exception;
	protected abstract boolean isDataAvailable() throws Exception;
	protected abstract void retrieve() throws Exception;
	protected abstract void disconnect() throws Exception;
	
	protected abstract long getBytesDownloaded();
	protected abstract float getProgress();
	
	public abstract Metadata getMetadata();
}
