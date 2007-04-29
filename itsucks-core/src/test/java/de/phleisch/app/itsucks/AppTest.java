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

import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.filter.DownloadJobFilter;
import de.phleisch.app.itsucks.io.DownloadJob;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
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

	public void testApp() throws Exception {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");
		assertNotNull(dispatcher);

		EventManagerObserver observer = new EventManagerObserver();
		dispatcher.getEventManager().registerObserver(observer);
		
		DownloadJobFilter filter = new DownloadJobFilter();
		//filter.setBaseURL(new URL("http://..."));
		//filter.setAllowOnlyRelativeReferences(true);
		filter.setAllowedHostNames(new String[] {".*"});
		filter.setMaxRecursionDepth(1);
		filter.setSaveToFileFilter(new String[] {".*[Jj][Pp][Gg]", ".*[Pp][Nn][Gg]", ".*[Gg][Ii][Ff]"});
		dispatcher.addJobFilter(filter);		
		
		JobFactory jobFactory = (JobFactory) context.getBean("JobFactory");
		assertNotNull(jobFactory);
		
		DownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL("http://itsucks.sourceforge.net/test/test.html"));
		job.setSavePath(new File("/tmp/crawl"));
		
		job.setIgnoreFilter(true);
		dispatcher.addJob(job);
		
		dispatcher.processJobs();
		
		assertTrue(observer.mEventCountType2001 == 2);
		assertTrue(observer.mEventCountType2002 == 1);
		assertTrue(observer.mEventCountType2003 == 0);
		assertTrue(observer.mEventCountType3001 == 2);
		assertTrue(observer.mEventCountType3003 == 2);
		
	}
	
	private class EventManagerObserver implements EventObserver {

		int mEventCountType2001 = 0; 
		int mEventCountType2002 = 0;
		int mEventCountType2003 = 0;
		
		int mEventCountType3001 = 0;
		int mEventCountType3003 = 0;
		
		public void processEvent(Event pEvent) {
			
			switch(pEvent.getType()) {
			
				case 2001: 
					mEventCountType2001 ++;
					break;
				
				case 2002: 
					mEventCountType2002 ++;
					break;			

				case 2003: 
					mEventCountType2003 ++;
					break;

				case 3001: 
					mEventCountType3001 ++;
					break;					
					
				case 3003: 
					mEventCountType3003 ++;
					break;									
			}
		}
	}
}
