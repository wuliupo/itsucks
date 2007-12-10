/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui;

import javax.swing.SwingUtilities;

import de.phleisch.app.itsucks.gui.main.DownloadJobOverviewFrame;

public class Main {

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		
		try {
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
			//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
			//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
			//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			
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
