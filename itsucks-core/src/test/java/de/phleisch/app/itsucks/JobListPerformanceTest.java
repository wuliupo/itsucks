/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 31.01.2007
 */

package de.phleisch.app.itsucks;

import java.util.Random;

import junit.framework.TestCase;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobList;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;

public class JobListPerformanceTest extends TestCase {

	public JobListPerformanceTest() {
		super();
	}

	public JobListPerformanceTest(String pArg0) {
		super(pArg0);
	}
	
	public void testJobListPerformance() throws Exception {
		
	    Injector injector = Guice.createInjector(
	    		new BaseModule(), 
	    		new CoreModule());

		String beans[] = new String[] { "SimpleJobListImpl", "SimpleJobListImpl", "SimpleJobListImpl"};
		
		for (String jobListName : beans) {
			
			long startUltraShort = System.currentTimeMillis();
			testJobList(injector, jobListName, 1000, 100000);
			long endUltraShort = System.currentTimeMillis();
			
			System.out.println("Ultra Short Test for '" + jobListName + "' took: " + (endUltraShort - startUltraShort) + " ms");
			
			/*
			long startShort = System.currentTimeMillis();
			testJobList(context, jobListName, 10000, 100000);
			long endShort = System.currentTimeMillis();
			
			System.out.println("Short Test for '" + jobListName + "' took: " + (endShort - startShort) + " ms");
			
			long startMedium = System.currentTimeMillis();
			testJobList(context, jobListName, 50000, 500000);
			long endMedium = System.currentTimeMillis();
			
			System.out.println("Medium Test for '" + jobListName + "' took: " + (endMedium - startMedium) + " ms");
			
			long startLong = System.currentTimeMillis();
			testJobList(context, jobListName, 100000, 1000000);
			long endLong = System.currentTimeMillis();
			
			System.out.println("Long Test for '" + jobListName + "' took: " + (endLong - startLong) + " ms");
			*/
		}
		
	}

	private void testJobList(Injector context, String pBeanName, int JOB_AMOUNT, int CHANGE_COUNT) {
		JobList jobList = context.getInstance(JobList.class);
		
		Random random = new Random(7777777);
		
		Job[] allJobs = new Job[JOB_AMOUNT];
		Job testJob;
		
		for (int i = 0; i < JOB_AMOUNT; i++) {
			
			DownloadJobFactory jobFactory = context.getInstance(DownloadJobFactory.class);
			
			testJob = jobFactory.createDownloadJob();
			testJob.setState(random.nextInt(999));
			testJob.setPriority(random.nextInt(Job.MAX_PRIORITY));
		
			allJobs[i] = testJob;
			jobList.addJob(testJob);
		}
		
		
		for (int i = 0; i < CHANGE_COUNT; i++) {
			
			testJob = allJobs[random.nextInt(JOB_AMOUNT - 1)]; 
			
			testJob.setState(random.nextInt(999));
			testJob.setPriority(random.nextInt(Job.MAX_PRIORITY));
		}
	}
	
	
}
