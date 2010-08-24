/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.job.download.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

/**
 * Manages the data retriever for every protocol.
 * It is initialized by spring.
 * 
 * @author olli
 *
 */
public class DataRetrieverManager {

	private Map<String, DataRetrieverFactory> mRetriever;
	
	public DataRetrieverManager() {
		super();
	}

	/**
	 * Gets an data retriever for the given protocol (http, ftp, etc.).
	 * 
	 * @param pProtocol
	 * @return
	 */
	public DataRetrieverFactory getRetrieverFactoryForProtocol(String pProtocol) {
		DataRetrieverFactory retriever = mRetriever.get(pProtocol);
		return retriever;
	}
	
	/**
	 * Sets the retriever map.
	 * 
	 * @param pRetriever
	 */
	@Inject
	public void setRetriever(Map<String, DataRetrieverFactory> pRetriever) {
		mRetriever = new HashMap<String, DataRetrieverFactory>(pRetriever);
	}

}
