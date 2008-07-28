/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.gui.main.DownloadJobOverviewFrame;

public class Main {
	
	/**
	 * Launches this application
	 */
	public static void main(String[] args) {

		Log log = LogFactory.getLog(Main.class);
		
		log.info("Start ItSucks / Version: " + ApplicationConstants.APPLICATION_VERSION);
		log.info("Current JVM memory usage: " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "MB");
		log.info("Max JVM memory usage: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB");
		
		//prepare spring
		SpringContextSingelton.getApplicationContext();
		
		try {
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
//			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
//			UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
//			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DownloadJobOverviewFrame frame = new DownloadJobOverviewFrame();
				frame.setVisible(true);
			}
		});
	}

}
