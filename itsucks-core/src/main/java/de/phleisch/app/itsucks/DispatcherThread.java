/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 * Created on 03.03.2006
 *
 */

package de.phleisch.app.itsucks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DispatcherThread extends Dispatcher implements Runnable {

	private static Log mLog = LogFactory.getLog(DispatcherThread.class);
	private static int mCount = 0;
	
	private Thread ownThread = null; 
	
	public void run() {
		try {
			super.processJobs();
		} catch (Exception e) {
			mLog.error("Error running dispatcher", e);
		}
	}

	@Override
	public synchronized void processJobs() throws Exception {
		if(ownThread != null && ownThread.isAlive()) {
			throw new IllegalArgumentException("Dispatcher Thread already running!");
		}
		
		ownThread = new Thread(this);
		ownThread.setName("DispatcherThread-" + ++mCount);
		ownThread.start();
	}

	public void join() throws InterruptedException {
		ownThread.join();
	}

}
