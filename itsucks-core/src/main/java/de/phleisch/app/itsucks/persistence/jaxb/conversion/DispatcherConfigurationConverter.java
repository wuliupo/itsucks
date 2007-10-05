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

import de.phleisch.app.itsucks.persistence.SerializableDispatcherConfiguration;
import de.phleisch.app.itsucks.persistence.jaxb.ObjectFactory;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedDispatcherConfiguration;

public class DispatcherConfigurationConverter extends AbstractBeanConverter {

	private ObjectFactory mBeanFactory = new ObjectFactory();
	
	public Object convertBeanToClass(Object pBean) throws Exception {
		
		if(pBean instanceof SerializedDispatcherConfiguration) {
			return convertSerializedDispatcherConfigurationToClass(
					(SerializedDispatcherConfiguration)pBean);
		}
		
		throw new IllegalArgumentException("Unsupported bean type given: " + pBean.getClass());
	}

	private SerializableDispatcherConfiguration 
		convertSerializedDispatcherConfigurationToClass(
			SerializedDispatcherConfiguration pBean) {
		
		SerializableDispatcherConfiguration serializableDispatcherConfiguration = 
			new SerializableDispatcherConfiguration();
		
		serializableDispatcherConfiguration.setDispatchDelay(pBean.getDispatchDelay());
		serializableDispatcherConfiguration.setWorkerThreads(pBean.getWorkerThreads());
		
		return serializableDispatcherConfiguration;
	}

	public Object convertClassToBean(Object pObject) throws Exception {
		
		if(pObject instanceof SerializableDispatcherConfiguration) {
			return convertSerializableDispatcherConfigurationToBean(
					(SerializableDispatcherConfiguration)pObject);
		}
		
		throw new IllegalArgumentException("Unsupported bean type given: " + pObject.getClass());
	}

	private SerializedDispatcherConfiguration convertSerializableDispatcherConfigurationToBean(
			SerializableDispatcherConfiguration pObject) {

		SerializedDispatcherConfiguration serializedDispatcherConfiguration = 
			mBeanFactory.createSerializedDispatcherConfiguration();
		
		serializedDispatcherConfiguration.setDispatchDelay(pObject.getDispatchDelay());
		serializedDispatcherConfiguration.setWorkerThreads(pObject.getWorkerThreads());
		
		return serializedDispatcherConfiguration;
	}

	public List<Class<?>> getSupportedBeanConverter() {
		
		Class<?>[] supportedBeanConvertClasses = new Class<?>[] {
			SerializableDispatcherConfiguration.class,
		};
		
		return Arrays.asList(supportedBeanConvertClasses);
	}

	public List<Class<?>> getSupportedClassConverter() {
		
		Class<?>[] supportedClassConvertClasses = new Class<?>[] {
			SerializableDispatcherConfiguration.class,
		};
		
		return Arrays.asList(supportedClassConvertClasses);
	}


}

