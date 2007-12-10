/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui.common;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JPanel;

import de.phleisch.app.itsucks.gui.common.panel.LogPanel;


public class LogDialog extends JDialog {

	private static final long serialVersionUID = -8676938168030470646L;

	private JPanel jContentPane = null;

	private LogPanel logPane = null;

	/**
	 * @param owner
	 */
	public LogDialog(Frame owner) {
		super(owner);
		initialize();
	}
	
	/**
	 * @param owner
	 */
	public LogDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setTitle("Log");
		this.setContentPane(getJContentPane());
		this.addWindowListener(new java.awt.event.WindowAdapter() {   
			public void windowOpened(java.awt.event.WindowEvent e) {    
				logPane.enableLogging();
			}
			public void windowClosing(java.awt.event.WindowEvent e) {
				logPane.disableLogging();
			}
		});
		this.setLocationByPlatform(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getLogPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes logPane	
	 * 	
	 * @return de.phleisch.app.chaoscrawler.gui.second_try.LogPane	
	 */
	private LogPanel getLogPane() {
		if (logPane == null) {
			logPane = new LogPanel();
		}
		return logPane;
	}

}
