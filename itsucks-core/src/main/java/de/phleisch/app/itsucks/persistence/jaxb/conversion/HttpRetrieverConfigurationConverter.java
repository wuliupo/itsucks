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

import de.phleisch.app.itsucks.io.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.persistence.jaxb.ObjectFactory;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedHttpRetrieverConfiguration;

public class HttpRetrieverConfigurationConverter extends AbstractBeanConverter {

	private ObjectFactory mBeanFactory = new ObjectFactory();
	
	public Object convertBeanToClass(Object pBean) throws Exception {
		
		if(pBean instanceof SerializedHttpRetrieverConfiguration) {
			return convertHttpRetrieverConfigurationToClass(
					(SerializedHttpRetrieverConfiguration)pBean);
		}
		
		throw new IllegalArgumentException("Unsupported bean type given: " + pBean.getClass());
	}

	private HttpRetrieverConfiguration convertHttpRetrieverConfigurationToClass(
			SerializedHttpRetrieverConfiguration pBean) {

		HttpRetrieverConfiguration configuration = new HttpRetrieverConfiguration();
		
		configuration.setMaxConnectionsPerServer(pBean.getMaxConnectionsPerServer());
		
		configuration.setProxyEnabled(pBean.isProxyEnabled());
		configuration.setProxyServer(pBean.getProxyServer());
		configuration.setProxyPort(pBean.getProxyPort());
		
		configuration.setProxyAuthenticationEnabled(pBean.isProxyAuthenticationEnabled());
		configuration.setProxyUser(pBean.getProxyUser());
		configuration.setProxyPassword(pBean.getProxyPassword());
		
		return configuration;
	}

	public Object convertClassToBean(Object pObject) throws Exception {
		
		if(pObject instanceof HttpRetrieverConfiguration) {
			return convertSerializedHttpRetrieverConfigurationToBean(
					(HttpRetrieverConfiguration)pObject);
		}
		
		throw new IllegalArgumentException("Unsupported bean type given: " + pObject.getClass());
	}

	private SerializedHttpRetrieverConfiguration 
		convertSerializedHttpRetrieverConfigurationToBean(
				HttpRetrieverConfiguration pClass) {
	
		SerializedHttpRetrieverConfiguration serializedConfiguration = 
			mBeanFactory.createSerializedHttpRetrieverConfiguration();
		
		serializedConfiguration.setMaxConnectionsPerServer(
				pClass.getMaxConnectionsPerServer());
		
		serializedConfiguration.setProxyEnabled(pClass.isProxyEnabled());
		serializedConfiguration.setProxyServer(pClass.getProxyServer());
		serializedConfiguration.setProxyPort(pClass.getProxyPort());
		
		serializedConfiguration.setProxyAuthenticationEnabled(pClass.isProxyAuthenticationEnabled());
		serializedConfiguration.setProxyUser(pClass.getProxyUser());
		serializedConfiguration.setProxyPassword(pClass.getProxyPassword());
		
		return serializedConfiguration;
	}

	public List<Class<?>> getSupportedBeanConverter() {
		
		Class<?>[] supportedBeanConvertClasses = new Class<?>[] {
			SerializedHttpRetrieverConfiguration.class,
		};
		
		return Arrays.asList(supportedBeanConvertClasses);
	}

	public List<Class<?>> getSupportedClassConverter() {
		
		Class<?>[] supportedClassConvertClasses = new Class<?>[] {
			HttpRetrieverConfiguration.class,
		};
		
		return Arrays.asList(supportedClassConvertClasses);
	}


}

