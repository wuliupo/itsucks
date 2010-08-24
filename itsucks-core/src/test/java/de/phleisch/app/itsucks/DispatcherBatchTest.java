/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.12.2007
 */

package de.phleisch.app.itsucks;

import junit.framework.TestCase;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.phleisch.app.itsucks.core.impl.DispatcherBatch;
import de.phleisch.app.itsucks.core.impl.DispatcherThread;

public class DispatcherBatchTest extends TestCase {
	
	
	public void testBatch() throws InterruptedException {
		
	    Injector injector = Guice.createInjector(
	    		new BaseModule(), 
	    		new CoreModule());

	    DispatcherThread dispatcher = injector.getInstance(DispatcherThread.class);
		assertNotNull(dispatcher);
		
		DispatcherThread dispatcher2 = injector.getInstance(DispatcherThread.class);
		assertNotNull(dispatcher2);
		
		DispatcherThread dispatcher3 = injector.getInstance(DispatcherThread.class);
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