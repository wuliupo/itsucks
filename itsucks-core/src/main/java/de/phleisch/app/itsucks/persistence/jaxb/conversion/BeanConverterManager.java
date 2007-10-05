/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.07.2007
 */

package de.phleisch.app.itsucks.persistence.jaxb.conversion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanConverterManager {

	private Map<Class<?>, BeanConverter> mBeanConverter;
	private Map<Class<?>, BeanConverter> mClassConverter;
	
	public BeanConverterManager() {
		mBeanConverter = new HashMap<Class<?>, BeanConverter>();
		mClassConverter = new HashMap<Class<?>, BeanConverter>();
	}

	/**
	 * Gets an Converter which can convert the given type of class
	 * to an bean.
	 * @param pBean
	 * @param pConverter
	 */
	public BeanConverter getClassConverter(Class<?> pClass) 
			throws ConversionNotSupportedException {
		
		BeanConverter converter = mClassConverter.get(pClass);
		if(converter == null) {
			throw new ConversionNotSupportedException("Conversion not supported: " + pClass.getName());
		}
		
		return converter;
	}

	/**
	 * Gets an Converter which can convert the given type of class
	 * to an bean.
	 * @param pBean
	 * @param pConverter
	 */
	public BeanConverter getBeanConverter(Class<?> pBean) 
			throws ConversionNotSupportedException {
		
		BeanConverter converter = mBeanConverter.get(pBean);
		
		if(converter == null) {
			throw new ConversionNotSupportedException("Conversion not supported: " + pBean.getName());
		}
		
		return converter;
	}
	
	/**
	 * Register all supported convert abilities from the converter.
	 * @param pConverer
	 */
	public void setConverters(List<BeanConverter> pConverterList) {
		for (BeanConverter beanConverter : pConverterList) {
			registerConverter(beanConverter);
		}
	}
	
	/**
	 * Register all supported convert abilities from the converter.
	 * @param pConverer
	 */
	public void registerConverter(BeanConverter pConverter) {
		
		List<Class<?>> supportedClassConverter = 
			pConverter.getSupportedClassConverter();
		
		for (Class<?> class1 : supportedClassConverter) {
			registerClassConverter(class1, pConverter);
		}
		
		List<Class<?>> supportedBeanConverter = 
			pConverter.getSupportedBeanConverter();
		
		for (Class<?> class1 : supportedBeanConverter) {
			registerBeanConverter(class1, pConverter);
		}
	}
	
	/**
	 * Registers an Converter which can convert the given type of class
	 * to an bean.
	 * @param pBean
	 * @param pConverter
	 */
	public void registerBeanConverter(Class<?> pBean, BeanConverter pConverter) {
		mBeanConverter.put(pBean, pConverter);
	}

	/**
	 * Registers an Converter which can convert the given type of bean class
	 * back to an normal object.
	 * @param pBean
	 * @param pConverter
	 */
	public void registerClassConverter(Class<?> pClass, BeanConverter pConverter) {
		pConverter.setBeanConverterManager(this);
		mClassConverter.put(pClass, pConverter);
	}
	
	private class ConversionNotSupportedException extends Exception {

		private static final long serialVersionUID = -5316702756221123193L;

		public ConversionNotSupportedException() {
			super();
		}

		public ConversionNotSupportedException(String pMessage, Throwable pCause) {
			super(pMessage, pCause);
		}

		public ConversionNotSupportedException(String pMessage) {
			super(pMessage);
		}

		public ConversionNotSupportedException(Throwable pCause) {
			super(pCause);
		}
	}
}
