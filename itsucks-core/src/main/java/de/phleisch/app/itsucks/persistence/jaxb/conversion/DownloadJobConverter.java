/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.07.2007
 */

package de.phleisch.app.itsucks.persistence.jaxb.conversion;

import java.math.BigInteger;

import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.persistence.jaxb.ObjectFactory;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedDownloadJob;

public class DownloadJobConverter extends AbstractBeanConverter {

	private ObjectFactory mBeanFactory = null;
	
	public Object convertBeanToClass(Object pBean) {
		return null;
	}

	public Object convertClassToBean(Object pObject) {
		
		if(pObject instanceof DownloadJob) {
			return convertDownloadJobToBean((DownloadJob)pObject);
		} else {
			throw new IllegalArgumentException("Unsupported bean type given: " + pObject.getClass());
		}
	}

	private Object convertDownloadJobToBean(DownloadJob pJob) {
		
		SerializedDownloadJob serializedJob = mBeanFactory.createSerializedDownloadJob();
		
		serializedJob.setId(BigInteger.valueOf(pJob.getId()));
		serializedJob.setName(pJob.getName());
		serializedJob.setPriority(BigInteger.valueOf(pJob.getPriority()));
		serializedJob.setState(BigInteger.valueOf(pJob.getState()));
		
		return serializedJob;
	}

	public void setBeanFactory(ObjectFactory pBeanFactory) {
		mBeanFactory = pBeanFactory;
	}

}

