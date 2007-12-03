/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 07.03.2007
 */

package de.phleisch.app.itsucks.persistence.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

/**
 * This class implements the JobSerialization interface using the 
 * default object.serialize feature.
 * 
 * @author olli
 *
 */
public class BinaryObjectJobSerialization
		extends AbstractJobSerialization
		implements ApplicationContextAware, JobSerialization {

	@SuppressWarnings("unused")
	private ApplicationContext mContext;
	
	public BinaryObjectJobSerialization() {
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#serialize(de.phleisch.app.itsucks.persistence.SerializableJobList, java.io.OutputStream)
	 */
	public void serialize(SerializableJobList pJobList, OutputStream pOutputStream) throws IOException {
		
		ObjectOutputStream objectOutput = new ObjectOutputStream(pOutputStream);
		objectOutput.writeObject(pJobList);
		objectOutput.close();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#deserialize(java.io.InputStream)
	 */
	public SerializableJobList deserialize(InputStream pInputStream) throws IOException, ClassNotFoundException {	
		
		ObjectInputStream objectInput = new ObjectInputStream(pInputStream);
		SerializableJobList jobList = (SerializableJobList) objectInput.readObject();
		objectInput.close();
		
		for (Job job : jobList.getJobs()) {
			mJobFactory.injectDependencies(job);
		}
		
		
		return jobList;
	}
	
	public void setApplicationContext(ApplicationContext pContext) {
		mContext = pContext;
	}

}
