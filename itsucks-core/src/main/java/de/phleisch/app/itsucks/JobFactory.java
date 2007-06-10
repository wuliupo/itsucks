/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 06.04.2007
 */

package de.phleisch.app.itsucks;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.io.DataRetrieverManager;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.processing.DataProcessorManager;

public class JobFactory implements ApplicationContextAware {

	private ApplicationContext mContext;

	final static String BEAN_REF_DATAPROCESSOR_MANAGER = "DataProcessorManager";

	final static String BEAN_REF_DATARETRIEVER_MANAGER = "DataRetrieverManager";

	public void setApplicationContext(ApplicationContext pContext)
			throws BeansException {
		mContext = pContext;
	}

	public DownloadJob createDownloadJob() {

		DownloadJob downloadJob = new DownloadJob();

		injectDependencies(downloadJob);
		
		return downloadJob;
	}

	public void injectDependencies(Job pJob) {
		
		if(pJob instanceof DownloadJob) {
			injectDependencies((DownloadJob)pJob);
		}
		
	}
	
	public void injectDependencies(DownloadJob pDownloadJob) {

		DataProcessorManager dataProcessorMgr = (DataProcessorManager) mContext
				.getBean(BEAN_REF_DATAPROCESSOR_MANAGER);
		DataRetrieverManager dataRetrieverMgr = (DataRetrieverManager) mContext
				.getBean(BEAN_REF_DATARETRIEVER_MANAGER);

		if (dataProcessorMgr == null) {
			throw new RuntimeException("Could not find bean: "
					+ BEAN_REF_DATAPROCESSOR_MANAGER);
		}
		if (dataRetrieverMgr == null) {
			throw new RuntimeException("Could not find bean: "
					+ BEAN_REF_DATARETRIEVER_MANAGER);
		}

		pDownloadJob.setDataProcessorManager(dataProcessorMgr);
		pDownloadJob.setDataRetrieverManager(dataRetrieverMgr);

	}



}
