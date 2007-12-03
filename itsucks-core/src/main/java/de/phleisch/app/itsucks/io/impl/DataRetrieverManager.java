/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.io.DataRetriever;

/**
 * Manages the data retriever for every protocol.
 * It is initialized by spring.
 * 
 * @author olli
 *
 */
public class DataRetrieverManager implements ApplicationContextAware {

	private ApplicationContext mContext;
	private Map<String, String> mRetriever;
	
	public DataRetrieverManager() {
		super();
	}

	/**
	 * Gets an data retriever for the given protocol (http, ftp, etc.).
	 * 
	 * @param pProtocol
	 * @return
	 */
	public DataRetriever getRetrieverForProtocol(String pProtocol) {
		DataRetriever retriever = null;
		
		String retrieverBeanName = mRetriever.get(pProtocol);
		if(retrieverBeanName != null) {
			retriever = (DataRetriever) mContext.getBean(retrieverBeanName);
		}
		
		return retriever;
	}
	
	/**
	 * Sets the retriever map.
	 * 
	 * @param pRetriever
	 */
	public void setRetriever(Map<String, String> pRetriever) {
		mRetriever = new HashMap<String, String>(pRetriever);
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext pContext) throws BeansException {
		mContext = pContext;
	}

}
