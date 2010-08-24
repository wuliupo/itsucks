/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 06.04.2007
 */

package de.phleisch.app.itsucks.job.download.impl;

import com.google.inject.Inject;

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
public class DownloadJobFactory {

	private DataProcessorManager mDataProcessorManager;
	private DataRetrieverManager mDataRetrieverManager;
	
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

		pDownloadJob.setDataProcessorManager(mDataProcessorManager);
		pDownloadJob.setDataRetrieverManager(mDataRetrieverManager);

	}

	@Inject
	public void setDataProcessorManager(DataProcessorManager pDataProcessorManager) {
		mDataProcessorManager = pDataProcessorManager;
	}

	@Inject
	public void setDataRetrieverManager(DataRetrieverManager pDataRetrieverManager) {
		mDataRetrieverManager = pDataRetrieverManager;
	}

}
