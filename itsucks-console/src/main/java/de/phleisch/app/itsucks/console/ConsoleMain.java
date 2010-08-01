package de.phleisch.app.itsucks.console;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.job.JobManagerConfiguration;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;
import de.phleisch.app.itsucks.persistence.util.DispatcherBuilder;

public class ConsoleMain {

	private static Log mLog = LogFactory.getLog(ConsoleMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] pArgs) {

		if(pArgs.length == 0) {
			showUsage();
			System.exit(1);
		}
		
		File serializedJob = new File(pArgs[0]);
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				ApplicationConstants.CORE_SPRING_CONFIG_FILE);

		Dispatcher dispatcher = (Dispatcher) context.getBean("Dispatcher");

		JobSerialization serializationManager = (JobSerialization) context
				.getBean("JobSerialization");

		SerializableJobPackage jobList = null;
		try {
			jobList = serializationManager.deserialize(serializedJob);
		} catch (Exception e1) {
			mLog.error("Error occured while loading job template", e1);
			System.exit(1);
		}

		if (jobList != null) {
			DispatcherBuilder.buildDispatcherFromJobPackage(dispatcher, jobList);
		}
		
		//memory settings
		//drop all finished/ignored links to save memory in console mode
		JobManagerConfiguration jobManagerConfiguration = (JobManagerConfiguration) dispatcher.getContext()
			.getContextParameter(JobManagerConfiguration.CONTEXT_PARAMETER_JOB_MANAGER_CONFIGURATION);
		if(jobManagerConfiguration == null) {
			jobManagerConfiguration = new JobManagerConfiguration();
			dispatcher.getContext().setContextParameter(JobManagerConfiguration.CONTEXT_PARAMETER_JOB_MANAGER_CONFIGURATION, 
					jobManagerConfiguration);
		}
		jobManagerConfiguration.setDropFinishedJobs(true);
		jobManagerConfiguration.setDropIgnoredJobs(true);
		
		//start dispatcher thread
		try {
			dispatcher.processJobs();
		} catch (Exception e) {
			mLog.error("Error starting dispatcher thread", e);
		}
		
	}

	private static void showUsage() {
		
		StringBuilder usage = new StringBuilder();
		
		usage.append("Usage: java -jar itsucks-console.jar <serialized job template>" + '\n');
		usage.append("   Starts processing the jobs in the given job template." + '\n');
		usage.append('\n');
		
		System.out.println(usage);
	}

}
