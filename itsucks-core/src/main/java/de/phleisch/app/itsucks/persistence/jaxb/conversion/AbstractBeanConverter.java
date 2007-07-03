/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.07.2007
 */

package de.phleisch.app.itsucks.persistence.jaxb.conversion;


public abstract class AbstractBeanConverter implements BeanConverter {

	private BeanConverterManager mBeanConverterManager;
	
	public BeanConverterManager getBeanConverterManager() {
		return mBeanConverterManager;
	}

	public void setBeanConverterManager(BeanConverterManager pConverterManager) {
		mBeanConverterManager = pConverterManager;
	}

}
