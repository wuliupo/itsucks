/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 06.04.2007
 */

package de.phleisch.app.itsucks.job.download.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.io.impl.DataRetrieverManager;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.processing.impl.DataProcessorManager;


/**
 * 
 * Factory to create new Jobs.
 * Internally spring is used, but this factory also implements the
 * feature to inject dependencies into deserialized jobs and jobs
 * not created over spring.
 * 
 * @author olli
 *
 */
public class DownloadJobFactory implements ApplicationContextAware {

	private ApplicationContext mContext;

	final static String BEAN_REF_DATAPROCESSOR_MANAGER = "DataProcessorManager";

	final static String BEAN_REF_DATARETRIEVER_MANAGER = "DataRetrieverManager";

	public void setApplicationContext(ApplicationContext pContext)
			throws BeansException {
		mContext = pContext;
	}

	public UrlDownloadJob createDownloadJob() {

		UrlDownloadJob downloadJob = new UrlDownloadJob();

		injectDependencies(downloadJob);
		
		return downloadJob;
	}

	public void injectDependencies(Job pJob) {
		
		if(pJob instanceof UrlDownloadJob) {
			injectDependencies((UrlDownloadJob)pJob);
		}
		
	}
	
	public void injectDependencies(UrlDownloadJob pDownloadJob) {

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
