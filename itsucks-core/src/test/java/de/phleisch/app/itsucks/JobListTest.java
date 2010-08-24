/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks;

import java.net.URL;

import junit.framework.TestCase;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class JobListTest extends TestCase {

	public JobListTest() {
		super();
	}

	public JobListTest(String pArg0) {
		super(pArg0);
	}

	public void testJobList() throws Exception {
		
	    Injector injector = Guice.createInjector(
	    		new BaseModule(), 
	    		new CoreModule());

	    Dispatcher dispatcher = injector.getInstance(Dispatcher.class);
		assertNotNull(dispatcher);
		
		DownloadJobFilter filter = new DownloadJobFilter();
		dispatcher.addJobFilter(filter);

	    DownloadJobFactory jobFactory = injector.getInstance(DownloadJobFactory.class);
		
		UrlDownloadJob job1 = jobFactory.createDownloadJob();
		UrlDownloadJob job2 = jobFactory.createDownloadJob();
		
		job1.setState(UrlDownloadJob.STATE_OPEN);
		job1.setPriority(8);
		job1.setUrl(new URL("http://test.de"));
		job1.setIgnoreFilter(true);
		
		job2.setState(UrlDownloadJob.STATE_OPEN);
		job2.setPriority(10);
		job2.setUrl(new URL("http://test2.de"));
		job2.setIgnoreFilter(true);
		
		dispatcher.getJobManager().addJob(job1);
		dispatcher.getJobManager().addJob(job2);

		Job openJob;
		openJob = dispatcher.getJobManager().getNextOpenJob();
		assertTrue(openJob == job2);
		
		job1.setPriority(15);
		openJob = dispatcher.getJobManager().getNextOpenJob();
		assertTrue(openJob == job1);
		
		job1.setState(UrlDownloadJob.STATE_IN_PROGRESS);
		openJob = dispatcher.getJobManager().getNextOpenJob();
		assertTrue(openJob == job2);
		
		job2.setState(UrlDownloadJob.STATE_IN_PROGRESS);
		openJob = dispatcher.getJobManager().getNextOpenJob();
		assertTrue(openJob == null);
		
		
	}
	
}
