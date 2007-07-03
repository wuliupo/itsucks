/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.07.2007
 */

package de.phleisch.app.itsucks.persistence.jaxb.conversion;

public class BeanConverterManager {

	public BeanConverter getBeanConverterForClass(Class<?> pClass) {
		return null;
	}
	
	public BeanConverter getConverterForBean(Class<?> pBean) {
		return null;
	}
	
	public void registerConverterForBean(Class<?> pBean) {
		
	}
	
	public void registerBeanConverterForClass(Class<?> pClass) {
		
	}
	
}
