/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.06.2007
 */

package de.phleisch.app.itsucks.persistence.impl;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.Introspector;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

public class XMLJobSerialization
		extends AbstractJobSerialization
		implements JobSerialization {

	public SerializableJobList deserialize(InputStream pInputStream)
			throws IOException, ClassNotFoundException {
		
		XMLDecoder objectInput = new XMLDecoder(pInputStream);
		
		SerializableJobList jobList = null;
		try {
			registerSearchPath("de.phleisch.app.itsucks.persistence.beaninfo");
			jobList = (SerializableJobList) objectInput.readObject();
		} finally {
			deregisterSearchPath("de.phleisch.app.itsucks.persistence.beaninfo");
		}
		objectInput.close();
		
		for (Job job : jobList.getJobs()) {
			mJobFactory.injectDependencies(job);
		}
		
		return jobList;

	}

	public void serialize(SerializableJobList pJobList,
			OutputStream pOutputStream) throws IOException {

		XMLEncoder objectOutput = new XMLEncoder(pOutputStream);
		objectOutput.setPersistenceDelegate(URL.class,
                new PersistenceDelegate(){

					@Override
					protected Expression instantiate(Object pOldInstance, Encoder pOut) {
						
						URL url = (URL) pOldInstance;
						
						return new Expression(pOldInstance, pOldInstance.getClass(), "new", new String[] {url.toExternalForm()});
					}
			
		});
		
		objectOutput.setPersistenceDelegate(File.class,
                new PersistenceDelegate(){

					@Override
					protected Expression instantiate(Object pOldInstance, Encoder pOut) {
						
						File file = (File) pOldInstance;
						
						return new Expression(pOldInstance, pOldInstance.getClass(), "new", new String[] {file.getPath()});
					}
			
		});
		
		try {
			registerSearchPath("de.phleisch.app.itsucks.persistence.beaninfo");
			objectOutput.writeObject(pJobList);
		} finally {
			deregisterSearchPath("de.phleisch.app.itsucks.persistence.beaninfo");
		}
		
		objectOutput.close();
	}

	protected void registerSearchPath(String pPackage) {
		
		List<String> searchPath = new ArrayList<String>(Arrays.asList(Introspector.getBeanInfoSearchPath()));
		searchPath.add(pPackage);
		Introspector.setBeanInfoSearchPath(searchPath.toArray(new String[searchPath.size()]));
	}
	
	protected void deregisterSearchPath(String pPackage) {
		
		List<String> searchPath = new ArrayList<String>(Arrays.asList(Introspector.getBeanInfoSearchPath()));
		searchPath.remove(pPackage);
		Introspector.setBeanInfoSearchPath(searchPath.toArray(new String[searchPath.size()]));
	}

}
