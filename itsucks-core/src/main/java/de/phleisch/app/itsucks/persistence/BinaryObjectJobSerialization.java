/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 07.03.2007
 */

package de.phleisch.app.itsucks.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.JobFactory;

/**
 * This class implements the JobSerialization interface using the 
 * default object.serialize feature.
 * 
 * @author olli
 *
 */
public class BinaryObjectJobSerialization implements ApplicationContextAware, JobSerialization {

	@SuppressWarnings("unused")
	private ApplicationContext mContext;
	
	private JobFactory mJobFactory;
	
	public BinaryObjectJobSerialization() {
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#serialize(de.phleisch.app.itsucks.persistence.SerializableJobList, java.io.File)
	 */
	public void serialize(SerializableJobList pJobList, File pTargetFile) throws IOException {
		
		FileOutputStream output = new FileOutputStream(pTargetFile);
		BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
		
		try {
			serialize(pJobList, bufferedOutput);
		} finally {
			bufferedOutput.close();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#serialize(de.phleisch.app.itsucks.persistence.SerializableJobList, java.io.OutputStream)
	 */
	public void serialize(SerializableJobList pJobList, OutputStream pOutputStream) throws IOException {
		
		ObjectOutputStream objectOutput = new ObjectOutputStream(pOutputStream);
		objectOutput.writeObject(pJobList);
		objectOutput.flush();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#deserialize(java.io.File)
	 */
	public SerializableJobList deserialize(File pTargetFile) throws IOException, ClassNotFoundException {

		FileInputStream input = new FileInputStream(pTargetFile);
		BufferedInputStream bufferedInput = new BufferedInputStream(input);

		return deserialize(bufferedInput);
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
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#setJobFactory(de.phleisch.app.itsucks.JobFactory)
	 */
	public void setJobFactory(JobFactory pJobFactory) {
		mJobFactory = pJobFactory;
	}
}
