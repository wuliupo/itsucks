/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.12.2007
 */

package de.phleisch.app.itsucks;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.core.impl.DispatcherBatch;
import de.phleisch.app.itsucks.core.impl.DispatcherThread;

public class DispatcherBatchTest extends TestCase {
	
	
	public void testBatch() throws InterruptedException {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ApplicationConstants.CORE_SPRING_CONFIG_FILE);
		
		DispatcherThread dispatcher = (DispatcherThread) context.getBean("DispatcherThread");
		assertNotNull(dispatcher);
		
		DispatcherThread dispatcher2 = (DispatcherThread) context.getBean("DispatcherThread");
		assertNotNull(dispatcher2);
		
		DispatcherThread dispatcher3 = (DispatcherThread) context.getBean("DispatcherThread");
		assertNotNull(dispatcher3);
		
		DispatcherBatch batch = new DispatcherBatch();
		batch.setMaxConcurrentDispatcher(2);
		
		batch.addDispatcher(dispatcher);
		batch.addDispatcher(dispatcher2);
		batch.addDispatcher(dispatcher3);
		
		batch.start();
		batch.join();
		
		batch.reset();
		
		batch.start();
		batch.join();
	}
	
	
}