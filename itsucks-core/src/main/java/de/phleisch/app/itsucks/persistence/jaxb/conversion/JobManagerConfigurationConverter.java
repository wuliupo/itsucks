/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.07.2007
 */

package de.phleisch.app.itsucks.persistence.jaxb.conversion;

import java.util.Arrays;
import java.util.List;

import de.phleisch.app.itsucks.job.JobManagerConfiguration;
import de.phleisch.app.itsucks.persistence.jaxb.ObjectFactory;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedJobManagerConfiguration;

public class JobManagerConfigurationConverter extends AbstractBeanConverter {

	private ObjectFactory mBeanFactory = new ObjectFactory();
	
	public Object convertBeanToClass(Object pBean) throws Exception {
		
		if(pBean instanceof SerializedJobManagerConfiguration) {
			return convertJobManagerConfigurationToClass(
					(SerializedJobManagerConfiguration)pBean);
		}
		
		throw new IllegalArgumentException("Unsupported bean type given: " + pBean.getClass());
	}

	private JobManagerConfiguration convertJobManagerConfigurationToClass(
			SerializedJobManagerConfiguration pBean) {

		JobManagerConfiguration configuration = new JobManagerConfiguration();
		
		configuration.setDropIgnoredJobs(pBean.isDropIgnoredJobs());
		configuration.setDropFinishedJobs(pBean.isDropFinishedJobs());
		
		return configuration;
	}

	public Object convertClassToBean(Object pObject) throws Exception {
		
		if(pObject instanceof JobManagerConfiguration) {
			return convertSerializedJobManagerConfigurationToBean(
					(JobManagerConfiguration)pObject);
		}
		
		throw new IllegalArgumentException("Unsupported bean type given: " + pObject.getClass());
	}

	private SerializedJobManagerConfiguration 
		convertSerializedJobManagerConfigurationToBean(
				JobManagerConfiguration pClass) {
	
		SerializedJobManagerConfiguration serializedConfiguration = 
			mBeanFactory.createSerializedJobManagerConfiguration();
		
		serializedConfiguration.setDropIgnoredJobs(pClass.isDropIgnoredJobs());
		serializedConfiguration.setDropFinishedJobs(pClass.isDropFinishedJobs());
		
		return serializedConfiguration;
	}

	public List<Class<?>> getSupportedBeanConverter() {
		
		Class<?>[] supportedBeanConvertClasses = new Class<?>[] {
			SerializedJobManagerConfiguration.class,
		};
		
		return Arrays.asList(supportedBeanConvertClasses);
	}

	public List<Class<?>> getSupportedClassConverter() {
		
		Class<?>[] supportedClassConvertClasses = new Class<?>[] {
			JobManagerConfiguration.class,
		};
		
		return Arrays.asList(supportedClassConvertClasses);
	}


}

