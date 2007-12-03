/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 07.03.2007
 */

package de.phleisch.app.itsucks;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

public class SerializationTest extends TestCase {
	
	public void testSerialization() throws Exception {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		DownloadJobFilter filter = new DownloadJobFilter();
		filter.setAllowedHostNames(new String[] {".*"});
		filter.setMaxRecursionDepth(1);
		filter.setSaveToDisk(new String[] {".*[Jj][Pp][Gg]", ".*[Pp][Nn][Gg]", ".*[Gg][Ii][Ff]"});
		
		DownloadJobFactory jobFactory = (DownloadJobFactory) context.getBean("JobFactory");
		
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL("http://itsucks.sourceforge.net/"));
		job.setSavePath(new File("/tmp/itsucks"));
		
		SerializableJobList serializedObject = new SerializableJobList();
		serializedObject.addFilter(filter);
		serializedObject.addJob(job);
		
		File file = File.createTempFile("itsucks_junit_", "_test");
		
		JobSerialization serializator = (JobSerialization) context.getBean("JobSerialization");
		serializator.serialize(serializedObject, file);
		
		SerializableJobList deserializedList = serializator.deserialize(file);
		assertTrue(((UrlDownloadJob)deserializedList.getJobs().get(0)).getUrl().sameFile(job.getUrl()));
		assertTrue(((DownloadJobFilter)deserializedList.getFilters().get(0)).getSaveToDisk()[0].equals(filter.getSaveToDisk()[0]));

		boolean delete = file.delete();
		assertTrue(delete);
	}
}
