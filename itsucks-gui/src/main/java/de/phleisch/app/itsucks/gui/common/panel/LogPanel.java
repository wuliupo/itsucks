/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui.common.panel;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class LogPanel extends JPanel {

	private static final long serialVersionUID = 2677351171855817165L;

	private JScrollPane jScrollPane = null;

	private transient LogDialogAppender mAppender = null;

	private JList jList = null;
	private DefaultListModel mListModel = null;
	
	private class LogDialogAppender extends AppenderSkeleton {

		@Override
		protected void append(LoggingEvent pEvent) {
			mListModel.add(0, pEvent.getMessage());
		}

		@Override
		public void close() {
		}

		@Override
		public boolean requiresLayout() {
			return true;
		}
	}
	
	/**
	 * @param owner
	 */
	public LogPanel() {
		
		//enable double buffering
		super(true);
		
		mAppender = new LogDialogAppender();
		
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		add(getJScrollPane(), BorderLayout.CENTER);
	}

	public void enableLogging() {
		//log4j specific code
		Logger.getRootLogger().addAppender(mAppender);
	}

	public void disableLogging() {
		//log4j specific code
		Logger.getRootLogger().removeAppender(mAppender);
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
			
			mListModel = new DefaultListModel();
			jList = new JList(mListModel);
		}
		return jList;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
