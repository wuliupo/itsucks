/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DataProcessorManager implements ApplicationContextAware {

	private ApplicationContext mContext;
	private List<String> mProcessors; 
	
	public DataProcessorManager() {
		super();
	}
	
	public void setProcessors(List<String> pList) {
		mProcessors = new ArrayList<String>(pList);
	}

	public List<AbstractDataProcessor> getProcessorsForProtocol(String pProtocol, String pMimetype) {
		ArrayList<AbstractDataProcessor> result = new ArrayList<AbstractDataProcessor>();
		
		for (Iterator<String> it = mProcessors.iterator(); it.hasNext();) {
			String beanId = it.next();
			result.add((AbstractDataProcessor) mContext.getBean(beanId));
		}
		return result;
	}

	public void setApplicationContext(ApplicationContext pContext) throws BeansException {
		mContext = pContext;
	}

	public List<AbstractDataProcessor> getProcessorsForJob(DownloadJob pJob) {
		
		ArrayList<AbstractDataProcessor> result = new ArrayList<AbstractDataProcessor>();
		
		for (Iterator<String> it = mProcessors.iterator(); it.hasNext();) {
			String beanId = it.next();
			AbstractDataProcessor processor = (AbstractDataProcessor) mContext.getBean(beanId);
			
			if(processor.supports(pJob)) {
				result.add(processor);
			}
		}
		
		return result;
	}

}
