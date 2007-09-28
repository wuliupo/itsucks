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

/**
 * Wrapper around the dispatcher to run the job processing in an new thread.
 * Use the join method to wait for the dispatcher thread to finish. 
 * 
 * @author olli
 */
public class DispatcherThread extends DispatcherImpl {

	private static Log mLog = LogFactory.getLog(DispatcherThread.class);
	private static int mCount = 0;
	
	private Thread mOwnThread = null;
	private InternalDispatcherThread mInternalDispatcherThread = null;
	
	private class InternalDispatcherThread implements Runnable {

		public void run() {
			try {
				DispatcherThread.super.processJobs();
			} catch (Exception e) {
				mLog.error("Error running dispatcher", e);
			}
			
		}
		
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.Dispatcher#processJobs()
	 */
	@Override
	public synchronized void processJobs() throws Exception {
		if(mOwnThread != null && mOwnThread.isAlive()) {
			throw new IllegalArgumentException("Dispatcher Thread already running!");
		}
		
		mInternalDispatcherThread = new InternalDispatcherThread();
		mOwnThread = new Thread(mInternalDispatcherThread);
		mOwnThread.setName("DispatcherThread-" + ++mCount);
		mOwnThread.start();
	}

	public void join() throws InterruptedException {
		mOwnThread.join();
	}

}
