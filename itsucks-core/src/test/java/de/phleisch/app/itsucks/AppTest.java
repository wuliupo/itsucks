/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks;

import java.io.File;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.event.impl.CoreEvents;
import de.phleisch.app.itsucks.event.impl.DefaultEventFilter;
import de.phleisch.app.itsucks.filter.download.impl.ContentFilter;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	
	private String SERVER_BASE_URL =  "http://itsucks.sourceforge.net";
	//private String SERVER_BASE_URL =  "http://localhost/~olli/itsucks-website";
	
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testContentFilter() throws Exception {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");
		DownloadJobFactory jobFactory = (DownloadJobFactory) context.getBean("JobFactory");

		DownloadJobFilter filter = new DownloadJobFilter();
		filter.setMaxRecursionDepth(1);
		dispatcher.addJobFilter(filter);
		
		ContentFilter contentFilter = new ContentFilter();
		contentFilter.addContentFilterConfig(
				new ContentFilter.ContentFilterConfig(".*test.*", 
						ContentFilter.ContentFilterConfig.Action.REJECT, 
						ContentFilter.ContentFilterConfig.Action.NO_ACTION));
		dispatcher.addJobFilter(contentFilter);
		
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL(SERVER_BASE_URL + "/test/test.html"));
		job.setSavePath(new File("/tmp/crawl"));
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);
		
		dispatcher.processJobs();

		assertTrue(job.getState() == Job.STATE_FINISHED);
		assertTrue(dispatcher.getJobManager().getJobList().size() == 1);
	}
	
	public void testSimpleApp() throws Exception {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");
		DownloadJobFactory jobFactory = (DownloadJobFactory) context.getBean("JobFactory");

		DownloadJobFilter filter = new DownloadJobFilter();
		filter.setMaxRecursionDepth(1);
		dispatcher.addJobFilter(filter);		
		
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL(SERVER_BASE_URL + "/test/test.html"));
		job.setSavePath(new File("/tmp/crawl"));
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);
		
		dispatcher.processJobs();

		assertTrue(job.getState() == Job.STATE_FINISHED);
	}
	
	public void testFullApp() throws Exception {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");
		assertNotNull(dispatcher);

		DefaultEventFilter eventFilter = new DefaultEventFilter();
		eventFilter.addAllowedCategory(CoreEvents.EVENT_CATEGORY_JOBMANAGER);
		
		EventManagerObserver observer = new EventManagerObserver();
		dispatcher.getEventManager().registerObserver(observer, eventFilter);
		
		DownloadJobFilter filter = new DownloadJobFilter();
		//filter.setBaseURL(new URL("http://..."));
		//filter.setAllowOnlyRelativeReferences(true);
		filter.setAllowedHostNames(new String[] {".*"});
		filter.setMaxRecursionDepth(1);
		filter.setSaveToDisk(new String[] {".*[Jj][Pp][Gg]", ".*[Pp][Nn][Gg]", ".*[Gg][Ii][Ff]"});
		dispatcher.addJobFilter(filter);		
		
		DownloadJobFactory jobFactory = (DownloadJobFactory) context.getBean("JobFactory");
		assertNotNull(jobFactory);
		
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL(SERVER_BASE_URL + "/test/test.html"));
		job.setSavePath(new File("/tmp/crawl"));
		
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);
		
		dispatcher.processJobs();

		assertTrue(job.getState() == Job.STATE_FINISHED);
		
		assertTrue(observer.mEventCountType3001 == 3);
		assertTrue(observer.mEventCountType3002 == 3);
		assertTrue(observer.mEventCountType3003 == 2);
		assertTrue(observer.mEventCountType3004 == 9);
		
	}
	
	private static class EventManagerObserver implements EventObserver {

		int mEventCountType3001 = 0; 
		int mEventCountType3002 = 0;
		int mEventCountType3003 = 0;
		int mEventCountType3004 = 0;
		
		public void processEvent(Event pEvent) {
			
			switch(pEvent.getType()) {
			
				case 3001: 
					mEventCountType3001 ++;
					break;
				
				case 3002: 
					mEventCountType3002 ++;
					break;			

				case 3003: 
					mEventCountType3003 ++;
					break;
					
				case 3004: 
					mEventCountType3004 ++;
					break;					
				
				default: 
					AppTest.fail("Received unknown event");
			}
		}
	}
	

	
}
