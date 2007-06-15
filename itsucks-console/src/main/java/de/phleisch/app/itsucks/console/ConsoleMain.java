package de.phleisch.app.itsucks.console;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.ApplicationConstants;
import de.phleisch.app.itsucks.Dispatcher;
import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

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
				.getBean("JobSerializationManager");

		SerializableJobList jobList = null;
		try {
			jobList = serializationManager.deserialize(serializedJob);
		} catch (Exception e1) {
			mLog.error("Error occured while loading job template", e1);
			System.exit(1);
		}

		if (jobList != null) {
			
			dispatcher.addJobFilter(jobList.getFilters());
			for (Job job : jobList.getJobs()) {
				dispatcher.addJob(job);
			}
		}
		
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
