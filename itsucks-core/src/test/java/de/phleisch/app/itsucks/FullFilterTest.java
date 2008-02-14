/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 14.02.2008
 */

package de.phleisch.app.itsucks;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.filter.download.http.impl.ChangeHttpResponseCodeBehaviourFilter;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class FullFilterTest extends TestCase {
	
	private String SERVER_BASE_URL =  "http://itsucks.sourceforge.net";
	
	public void testChangeHttpResponseCodeBehaviourFilter() throws Exception {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");
		assertNotNull(dispatcher);
		
		DownloadJobFilter filter = new DownloadJobFilter();
		filter.setMaxRecursionDepth(0);
		dispatcher.addJobFilter(filter);
		
		ChangeHttpResponseCodeBehaviourFilter behaviourFilter = 
			new ChangeHttpResponseCodeBehaviourFilter();
		HttpRetrieverResponseCodeBehaviour behaviour = new HttpRetrieverResponseCodeBehaviour();
		behaviour.add(200, HttpRetrieverResponseCodeBehaviour.Action.FAILED);
		behaviourFilter.addConfig(".*", behaviour);
		dispatcher.addJobFilter(behaviourFilter);
		
		DownloadJobFactory jobFactory = (DownloadJobFactory) context.getBean("JobFactory");
		assertNotNull(jobFactory);
		
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL(SERVER_BASE_URL + "/test/test.html"));
		job.setSavePath(new File("/tmp/crawl"));
		
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);
		
		dispatcher.processJobs();

		assertTrue(job.getState() == Job.STATE_ERROR);

		
	}

}
