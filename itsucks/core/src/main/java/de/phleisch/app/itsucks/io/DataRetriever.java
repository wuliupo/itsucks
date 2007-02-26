/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 26.02.2007
 */

package de.phleisch.app.itsucks.io;

import java.net.URL;
import java.util.Observer;

public interface DataRetriever {

	/**
	 * This type of notification will be send if the progress changes.
	 */
	public final static Integer NOTIFICATION_PROGRESS = 100;

	public void addDataProcessor(DataProcessor pDataProcessor);

	public URL getUrl();

	public void setUrl(URL pUrl);

	public void connect() throws Exception;

	public boolean isDataAvailable() throws Exception;

	public void retrieve() throws Exception;

	public void disconnect() throws Exception;

	public long getBytesDownloaded();

	public float getProgress();

	public void abort();

	public Metadata getMetadata();

	
	//Observable interface
	
	public void addObserver(Observer o);
	public void deleteObserver(Observer o);
}