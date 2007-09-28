/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui;

import javax.swing.SwingUtilities;

import de.phleisch.app.itsucks.gui2.DownloadJobOverviewFrame;

public class MainWindow {

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
			//UIManager.setLookAndFeel("javax.swing.plaf.synth.SynthLookAndFeel");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				@SuppressWarnings("unused")
				
				DownloadJobOverviewFrame frame2 = new DownloadJobOverviewFrame();
				frame2.setVisible(true);
				
			}
		});
	}

}
