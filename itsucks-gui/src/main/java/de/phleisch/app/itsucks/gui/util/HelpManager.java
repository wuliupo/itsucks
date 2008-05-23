/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 23.05.2008
 */

package de.phleisch.app.itsucks.gui.util;

import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HelpManager {
	
	private static Log mLog = LogFactory.getLog(HelpManager.class);

	private static HelpManager mSingleton = null;
	
	private HelpSet mHelpSet = null;
	private HelpBroker mHelpBroker = null;

	private HelpManager() {
	}
	
	public static HelpManager getInstance() {
		
		if(mSingleton == null) {
			createInstance();
		}
		
		return mSingleton;
	}
	
	private static synchronized void createInstance() {
		
		if(mSingleton != null) {
			return;
		}
		
		mSingleton = new HelpManager();
		mSingleton.init();
	}

	public HelpSet getHelpSet() {
		return mHelpSet;
	}

	public HelpBroker getHelpBroker() {
		return mHelpBroker;
	}
	
	protected void init() {
		
		ClassLoader cl = HelpManager.class.getClassLoader();
		String helpHS = "main.hs";
		URL hsURL = HelpSet.findHelpSet(cl, helpHS);
		if(hsURL == null) {
			mLog.error("Can't find helpset: " + helpHS);
			throw new RuntimeException("Can't find helpset: " + helpHS);
		}
		
		try {
			mHelpSet = new HelpSet(cl, hsURL);
		} catch (Exception ee) {
			mLog.error("Can't load helpset: " + helpHS, ee);
			throw new RuntimeException("Can't load helpset: " + helpHS, ee);
		}
		
		mHelpBroker = mHelpSet.createHelpBroker();
	}
	
}
