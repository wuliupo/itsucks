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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DataProcessorManager;
import de.phleisch.app.itsucks.io.DataRetrieverManager;
import de.phleisch.app.itsucks.io.DownloadJob;

public class JobSerializationManager implements ApplicationContextAware {

	private ApplicationContext mContext;
	
	public JobSerializationManager() {
	}

	public void serialize(SerializableJobList pJobList, File pTargetFile) throws IOException {
		
		FileOutputStream output = new FileOutputStream(pTargetFile);
		BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
		ObjectOutputStream objectOutput = new ObjectOutputStream(bufferedOutput);
		objectOutput.writeObject(pJobList);
		objectOutput.close();
		
	}
	
	public SerializableJobList deserialize(File pTargetFile) throws IOException, ClassNotFoundException {
		
		FileInputStream input = new FileInputStream(pTargetFile);
		BufferedInputStream bufferedInput = new BufferedInputStream(input);
		ObjectInputStream objectInput = new ObjectInputStream(bufferedInput);
		SerializableJobList jobList = (SerializableJobList) objectInput.readObject();
		objectInput.close();
		
		//TODO FIXME solve this in an better way.
		//restore DataProcessorManager and DataRetrieverManager of the jobs
		
		//Plan: Eine Factory aufziehen die den Job erzeugt.
		//Im Job einen Namen speichern z.B. 'DownloadJob' und das später in der Factory
		// nutzen um die Job nach dem deserialisieren wiederherzustellen. 
		
		for (Job job : jobList.getJobs()) {
			if(job instanceof DownloadJob) {
				DownloadJob downloadJob = (DownloadJob) job;
				downloadJob.setDataProcessorManager(
						(DataProcessorManager) mContext.getBean("DataProcessorManager"));
				downloadJob.setDataRetrieverManager(
						(DataRetrieverManager) mContext.getBean("DataRetrieverManager"));				
			}
		}
		
		return jobList;
	}
	
	public void setApplicationContext(ApplicationContext pContext) {
		mContext = pContext;
	}
}
