/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 26.02.2007
 */

package de.phleisch.app.itsucks.io;

import java.net.URL;
import java.util.List;
import java.util.Observer;

public interface DataRetriever {

	/**
	 * This type of notification will be send if the progress changes.
	 */
	public final static Integer NOTIFICATION_PROGRESS = 100;

	
	/**
	 * This value is returned if the download was not started yet.
	 */
	public final static Integer RESULT_RETRIEVAL_NOT_STARTED_YET = 1;
	
	/**
	 * This value is returned if the retrieval finished without errors.
	 */
	public final static Integer RESULT_RETRIEVAL_OK = 2;
	
	/**
	 * This value is returned if the retrieval failed.
	 */
	public final static Integer RESULT_RETRIEVAL_FAILED = 10;

	/**
	 * This value is returned if the retrieval failed but it's retryable.
	 */
	public final static Integer RESULT_RETRIEVAL_FAILED_BUT_RETRYABLE = 11;

	/**
	 * This value is returned if the retrieval was aborted.
	 */
	public final static Integer RESULT_RETRIEVAL_ABORTED = 12;

	
	public void addDataProcessor(DataProcessor pDataProcessor);

	public List<DataProcessor> getDataProcessors();
	
	public URL getUrl();
	public void setUrl(URL pUrl);

	//control retriever
	public void connect() throws Exception;
	public boolean isDataAvailable() throws Exception;
	public void retrieve() throws Exception;
	public void disconnect() throws Exception;
	public void abort();
	
	//get status of retriever
	public long getBytesRetrieved();
	public float getProgress();
	public int getResultCode();
	
	public Metadata getMetadata();

	//resume interface
	public void setBytesToSkip(long pBytesToSkip);
	public long getBytesSkipped();

	//Observable interface
	public void addObserver(Observer o);
	public void deleteObserver(Observer o);

}