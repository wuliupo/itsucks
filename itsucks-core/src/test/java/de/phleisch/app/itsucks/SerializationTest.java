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

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

public class SerializationTest extends TestCase {
	
	public void testSerialization() throws Exception {
		
	    Injector injector = Guice.createInjector(
	    		new BaseModule(), 
	    		new CoreModule());
	    
		DownloadJobFilter filter = new DownloadJobFilter();
		filter.setAllowedHostNames(new String[] {".*"});
		filter.setMaxRecursionDepth(1);
		filter.setSaveToDisk(new String[] {".*[Jj][Pp][Gg]", ".*[Pp][Nn][Gg]", ".*[Gg][Ii][Ff]"});
		
		DownloadJobFactory jobFactory = injector.getInstance(DownloadJobFactory.class);
		
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL("http://itsucks.sourceforge.net/"));
		job.setSavePath(new File("/tmp/itsucks"));
		
		SerializableJobPackage serializedObject = new SerializableJobPackage();
		serializedObject.addFilter(filter);
		serializedObject.addJob(job);
		
		File file = File.createTempFile("itsucks_junit_", "_test");

		JobSerialization serializator = injector.getInstance(JobSerialization.class);
		serializator.serialize(serializedObject, file);
		
		SerializableJobPackage deserializedList = serializator.deserialize(file);
		assertTrue(((UrlDownloadJob)deserializedList.getJobs().get(0)).getUrl().sameFile(job.getUrl()));
		assertTrue(((DownloadJobFilter)deserializedList.getFilters().get(0)).getSaveToDisk()[0].equals(filter.getSaveToDisk()[0]));

		boolean delete = file.delete();
		assertTrue(delete);
	}
}
