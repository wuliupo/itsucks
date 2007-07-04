/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 07.03.2007
 */

package de.phleisch.app.itsucks.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.persistence.jaxb.ObjectFactory;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedDownloadJobs;
import de.phleisch.app.itsucks.persistence.jaxb.conversion.BeanConverterManager;

/**
 * This class implements the JobSerialization interface using the 
 * default object.serialize feature.
 * 
 * @author olli
 *
 */
public class JAXBJobSerialization
		extends AbstractJobSerialization
		implements ApplicationContextAware, JobSerialization {

	@SuppressWarnings("unused")
	private ApplicationContext mContext;
	
	public JAXBJobSerialization() {
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#serialize(de.phleisch.app.itsucks.persistence.SerializableJobList, java.io.OutputStream)
	 */
	public void serialize(SerializableJobList pJobList, OutputStream pOutputStream) throws Exception {
		
		JAXBContext jc = JAXBContext.newInstance("de.phleisch.app.itsucks.persistence.jaxb");
		
		Marshaller marshaller = jc.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				   new Boolean(true));

		BeanConverterManager manager = new BeanConverterManager();
		ObjectFactory beanFactory = new ObjectFactory();
		SerializedDownloadJobs jobs = beanFactory.createSerializedDownloadJobs();
		jobs.setVersion("1.0");
		
		for (Job job : pJobList.getJobs()) {
			
			
			//jobs.getSerializedDownloadJob().add
			
		}
		
		marshaller.marshal(jobs,
				   pOutputStream);
		
		pOutputStream.close();
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
