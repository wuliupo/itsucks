package de.phleisch.app.itsucks.example;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.context.Context;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class ExampleMain {

	private static Log mLog = LogFactory.getLog(ExampleMain.class);

	/**
	 * 
	 * Demo application which sucks all images from 
	 * http://itsucks.sourceforge.net
	 * 
	 */
	public static void main(String[] pArgs) throws Exception {

		//load spring application context 
		ClassPathXmlApplicationContext context = 
			new ClassPathXmlApplicationContext(
					ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		//load dispatcher from spring
		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");

		//set default user agent and send referer (optional)
		HttpRetrieverConfiguration retrieverConfiguration = new HttpRetrieverConfiguration();
		retrieverConfiguration.setSendReferer(true);
		retrieverConfiguration.setUserAgent("ItSucks Example");
		
		Context dispatcherContext = dispatcher.getContext();
		dispatcherContext.setContextParameter(HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION,
				retrieverConfiguration);
		
		//configure an download job filter which downloads all images from the website
		DownloadJobFilter filter = new DownloadJobFilter();
		filter.setAllowedHostNames(new String[] {".*"});
		filter.setMaxRecursionDepth(1);
		filter.setSaveToDisk(new String[] {
				".*jpg", 
				".*png", 
				".*gif"});
		
		//add the filter to the dispatcher
		dispatcher.addJobFilter(filter);		
		
		//create an job factory
		DownloadJobFactory jobFactory = (DownloadJobFactory) 
			context.getBean("JobFactory");
		
		//create an initial job
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL("http://itsucks.sourceforge.net/"));
		
		//create an temporary directory
		File tempDir = File.createTempFile("itsucks-example", null);
		if(!tempDir.delete() && !tempDir.mkdir()) {
			throw new RuntimeException("Cannot create tmp directory: " + tempDir);
		}
		
		job.setSavePath(tempDir); //change this for windows
		job.setIgnoreFilter(true);

		dispatcher.addJob(job);
		
		mLog.info("Start demo dispatcher");
		
		//start the dispatcher
		dispatcher.processJobs();
		
		mLog.info("Demo dispatcher finished");
		
		//dump all found urls
		Collection<Job> content = 
			dispatcher.getJobManager().getJobList().getContent();
		
		for (Job finishedJob : content) {
			mLog.info(finishedJob);
		}
		
		mLog.info("Saved data under: " + tempDir);
		
	}

}
