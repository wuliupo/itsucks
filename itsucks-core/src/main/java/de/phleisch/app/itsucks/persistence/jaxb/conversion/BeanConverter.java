/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.07.2007
 */

package de.phleisch.app.itsucks.persistence.jaxb.conversion;

import java.util.List;

public interface BeanConverter {

	public Object convertClassToBean(Object pObject) throws Exception;
	
	public Object convertBeanToClass(Object pBean) throws Exception;
	
	public void setBeanConverterManager(BeanConverterManager pConverterManager);

	public List<Class<?>> getSupportedClassConverter();

	public List<Class<?>> getSupportedBeanConverter();
}
