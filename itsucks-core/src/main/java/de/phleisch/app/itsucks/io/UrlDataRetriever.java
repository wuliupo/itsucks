/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.02.2008
 */

package de.phleisch.app.itsucks.io;

import java.net.URL;

public interface UrlDataRetriever extends DataRetriever {

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
	
	
	/**
	 * Returns the URL which is retrieved.
	 * 
	 * @return
	 */
	public URL getUrl();
	
	/**
	 * Sets the URL to be retrieved.
	 * 
	 * @param pUrl
	 */
	public void setUrl(URL pUrl);
	
	/**
	 * Returns the internal state and result code after connecting.
	 * Check RESULT_RETRIEVAL_* constants for possible values.
	 * 
	 * @return
	 */
	public int getResultCode();
	
	/**
	 * Returns the suggested time to wait befory retry the retrieval in ms.
	 * 
	 * @return
	 */
	public long getSuggestedTimeToWaitForRetry();
	
	
}
