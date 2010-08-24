/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceContextSingelton {

	@SuppressWarnings("unused")
	private static Log mLog = LogFactory.getLog(GuiceContextSingelton.class);
	
	private boolean mInitialized = false;
	private Injector mInjector;
	private static GuiceContextSingelton mMyself = null;
	
	static {
		mMyself = new GuiceContextSingelton();
	}
	
	private GuiceContextSingelton() {
	}

	private synchronized void initialize() {
		
		if(isInitialized()) return;
		
	    mInjector = Guice.createInjector(
	    		new BaseModule(), 
	    		new CoreModule());
		

		mInitialized = true;
	}

	private boolean isInitialized() {
		return mInitialized;
	}
	
	public static Injector getInjector() {
		
		if(!mMyself.isInitialized()) {
			mMyself.initialize();
		}
		
		return mMyself.mInjector;
	}
	
}
