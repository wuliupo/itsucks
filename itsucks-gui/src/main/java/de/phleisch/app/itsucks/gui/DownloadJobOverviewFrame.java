/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.ApplicationConstants;
import de.phleisch.app.itsucks.DispatcherImpl;
import de.phleisch.app.itsucks.DispatcherThread;
import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.event.CoreEvents;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.gui.ifc.AddDownloadJobCapable;
import de.phleisch.app.itsucks.gui.panel.DownloadJobQueueOverviewPanel;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

/**
 * 
 * @author __USER__
 */
public class DownloadJobOverviewFrame extends javax.swing.JFrame implements
		AddDownloadJobCapable {

	private static final long serialVersionUID = 6628042574113496207L;
	private static Log mLog = LogFactory.getLog(DownloadJobOverviewFrame.class);

	private Map<DispatcherImpl, EventObserver> mEventObserver = 
		new HashMap<DispatcherImpl, EventObserver>();
	
	/** Creates new form DownloadJobOverviewFrame */
	public DownloadJobOverviewFrame() {
		initComponents();
	}

	public void addDownload(DownloadJob pDownload, List<JobFilter> pFilterList) {

		//DownloadStatusPanel pane = new DownloadStatusPanel();
		DownloadJobQueueOverviewPanel pane = new DownloadJobQueueOverviewPanel();
		
		DispatcherThread dispatcher = (DispatcherThread) SpringContextSingelton
				.getApplicationContext().getBean("DispatcherThread");

		if (dispatcher == null) {
			throw new RuntimeException("Can't instatiate dispatcher!");
		}
		pane.setDispatcher(dispatcher);
		pane.setName(pDownload.getName());

		//add pane
		downloadsTabbedPane.add(pane.getName(), pane);
		
		//configure dispatcher
		dispatcher.addJobFilter(pFilterList);
		dispatcher.addJob(pDownload);

		EventObserver observer = new EventObserver() {
			public void processEvent(Event pEvent) {
				if (pEvent.getCategory() == CoreEvents.EVENT_CATEGORY_CORE) {
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							updateButtonState();
							updateTabTitles();
						}
					});
				}
			}
		};
		
		mEventObserver.put(dispatcher, observer);
		dispatcher.getEventManager().registerObserver(observer);

		// start dispatcher thread
		try {
			dispatcher.processJobs();

		} catch (Exception e) {
			mLog.error("Error starting dispatcher thread", e);
		}

		// wait till dispatcher is starting
		for (int i = 0; i < 10 && !dispatcher.isRunning(); i++) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

	}

	private void openAddDownloadDialog() {
		EditDownloadJobDialog frame = new EditDownloadJobDialog(this, this);
		frame.setVisible(true);
	}

	private void loadDownload() {
		//open dialog
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter(
				"ItSucks Download Templates (*.xml)", new String[] { "xml" }));

		//Show load dialog; this method does not return until the dialog is closed
		int result = fc.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			JobSerialization serializationManager = (JobSerialization) SpringContextSingelton
					.getApplicationContext().getBean("JobSerialization");

			SerializableJobList jobList = null;
			try {
				jobList = serializationManager
						.deserialize(fc.getSelectedFile());
			} catch (Exception e1) {

				mLog.error("Error occured while loading download template", e1);

				JOptionPane.showMessageDialog(this,
						"Error occured while loading download template.\n"
								+ e1.getMessage(), "Error occured",
						JOptionPane.ERROR_MESSAGE);
			}

			if (jobList != null) {
				//addDownload((DownloadJob)jobList.getJobs().get(0), jobList.getFilters());

				DownloadJob job = (DownloadJob) jobList.getJobs().get(0);

				EditDownloadJobDialog dialog = new EditDownloadJobDialog(this,
						this);
				dialog.loadJob(job, jobList.getFilters());
				dialog.setVisible(true);
			}

		}

	}

	private void openLogDialog() {
		LogDialog newLogDialog = new LogDialog();
		newLogDialog.setVisible(true);
	}

	private void pauseDownloadStatusPane() {

		Component selectedComponent = downloadsTabbedPane
				.getSelectedComponent();
		if (selectedComponent == null)
			return;

		DownloadJobQueueOverviewPanel panel = (DownloadJobQueueOverviewPanel) selectedComponent;
		DispatcherThread dispatcher = panel.getDispatcher();

		if (dispatcher.isPaused()) {
			dispatcher.unpause();
		} else {
			dispatcher.pause();
		}
	}

	private void closeDownloadStatusPane() {

		Component selectedComponent = downloadsTabbedPane
				.getSelectedComponent();
		if (selectedComponent == null)
			return;

		DownloadJobQueueOverviewPanel panel = (DownloadJobQueueOverviewPanel) selectedComponent;
		DispatcherThread dispatcher = panel.getDispatcher();

		dispatcher.stop();
		try {
			dispatcher.join();
		} catch (InterruptedException e) {
			mLog.error(e, e);
		}

		EventObserver eventObserver = mEventObserver.get(dispatcher);
		mEventObserver.remove(dispatcher);
		dispatcher.getEventManager().unregisterObserver(eventObserver);
		
		downloadsTabbedPane.remove(panel);
		panel.removeDispatcher();

		//inform the gc that it would be a great oppurtinity to get some memory back
		System.gc();
	}

	private void updateButtonState() {

		Component selectedComponent = downloadsTabbedPane
				.getSelectedComponent();
		if (selectedComponent == null) {
			pauseDownloadButton.setEnabled(false);
			pauseDownloadButton.setText("Pause download");
			stopDownloadButton.setEnabled(false);
		} else {
			DownloadJobQueueOverviewPanel panel = (DownloadJobQueueOverviewPanel) selectedComponent;
			DispatcherThread dispatcher = panel.getDispatcher();

			if (dispatcher.isPaused()) {
				pauseDownloadButton.setText("Unpause download");
			} else {
				pauseDownloadButton.setText("Pause download");
			}

			if (dispatcher.isRunning()) {
				pauseDownloadButton.setEnabled(true);
			} else {
				pauseDownloadButton.setEnabled(false);
			}

			stopDownloadButton.setEnabled(true);
		}
	}

	private void updateTabTitles() {

		Component[] components = downloadsTabbedPane.getComponents();
		for (int i = 0; i < components.length; i++) {

			DownloadJobQueueOverviewPanel panel = (DownloadJobQueueOverviewPanel) components[i];
			DispatcherThread dispatcher = panel.getDispatcher();

			if (dispatcher.isPaused()) {
				downloadsTabbedPane.setTitleAt(downloadsTabbedPane
						.indexOfComponent(panel), panel.getName() + " (paused)");
			} else if (dispatcher.isRunning()) {
				downloadsTabbedPane.setTitleAt(downloadsTabbedPane
						.indexOfComponent(panel), panel.getName());
			} else {
				downloadsTabbedPane
						.setTitleAt(downloadsTabbedPane.indexOfComponent(panel),
								panel.getName() + " (finished)");
			}

		}

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		downloadsTabbedPane = new javax.swing.JTabbedPane();
		toolBar = new javax.swing.JToolBar();
		newDownloadButton = new javax.swing.JButton();
		pauseDownloadButton = new javax.swing.JButton();
		stopDownloadButton = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		createNewJobMenuItem = new javax.swing.JMenuItem();
		openDownloadTemplateMenuItem = new javax.swing.JMenuItem();
		exitMenuItem = new javax.swing.JMenuItem();
		toolsMenu = new javax.swing.JMenu();
		regExpTesterMenuItem = new javax.swing.JMenuItem();
		logWindowMenuItem = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		contentsMenuItem = new javax.swing.JMenuItem();
		aboutMenuItem = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle(ApplicationConstants.APPLICATION_TITLE);
		setLocationByPlatform(true);
		downloadsTabbedPane.getAccessibleContext().setAccessibleName(
				"Download 1");

		toolBar.setFloatable(false);
		newDownloadButton.setFont(new java.awt.Font("Dialog", 0, 12));
		newDownloadButton.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/document-new.png")));
		newDownloadButton.setText("New download");
		newDownloadButton.setBorderPainted(false);
		newDownloadButton.setOpaque(false);
		newDownloadButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						newDownloadButtonActionPerformed(evt);
					}
				});

		toolBar.add(newDownloadButton);

		pauseDownloadButton.setFont(new java.awt.Font("Dialog", 0, 12));
		pauseDownloadButton.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/pause.png")));
		pauseDownloadButton.setText("Pause download");
		pauseDownloadButton.setBorderPainted(false);
		pauseDownloadButton.setEnabled(false);
		pauseDownloadButton.setOpaque(false);
		pauseDownloadButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						pauseDownloadButtonActionPerformed(evt);
					}
				});

		toolBar.add(pauseDownloadButton);

		stopDownloadButton.setFont(new java.awt.Font("Dialog", 0, 12));
		stopDownloadButton.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/edit-delete.png")));
		stopDownloadButton.setText("Stop download");
		stopDownloadButton.setBorderPainted(false);
		stopDownloadButton.setEnabled(false);
		stopDownloadButton.setOpaque(false);
		stopDownloadButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						stopDownloadButtonActionPerformed(evt);
					}
				});

		toolBar.add(stopDownloadButton);

		fileMenu.setText("File");
		createNewJobMenuItem.setText("Create new download");
		createNewJobMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						createNewJobMenuItemActionPerformed(evt);
					}
				});

		fileMenu.add(createNewJobMenuItem);

		openDownloadTemplateMenuItem.setText("Open download template");
		openDownloadTemplateMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						openDownloadTemplateMenuItemActionPerformed(evt);
					}
				});

		fileMenu.add(openDownloadTemplateMenuItem);

		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});

		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		toolsMenu.setText("Tools");
		regExpTesterMenuItem.setText("Regular Expression Tester");
		regExpTesterMenuItem.setEnabled(false);
		toolsMenu.add(regExpTesterMenuItem);

		logWindowMenuItem.setText("Open log window");
		logWindowMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						logWindowMenuItemActionPerformed(evt);
					}
				});

		toolsMenu.add(logWindowMenuItem);

		menuBar.add(toolsMenu);

		helpMenu.setText("Help");
		helpMenu.setEnabled(false);
		contentsMenuItem.setText("Contents");
		helpMenu.add(contentsMenuItem);

		aboutMenuItem.setText("About");
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(toolBar,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 673,
				Short.MAX_VALUE).add(downloadsTabbedPane,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 673,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().add(toolBar,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								downloadsTabbedPane,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								338, Short.MAX_VALUE)));
		pack();
	}// </editor-fold>//GEN-END:initComponents

	//GEN-FIRST:event_logWindowMenuItemActionPerformed
	private void logWindowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		openLogDialog();
	}//GEN-LAST:event_logWindowMenuItemActionPerformed

	//GEN-FIRST:event_stopDownloadButtonActionPerformed
	private void stopDownloadButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		closeDownloadStatusPane();
	}//GEN-LAST:event_stopDownloadButtonActionPerformed

	//GEN-FIRST:event_pauseDownloadButtonActionPerformed
	private void pauseDownloadButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		pauseDownloadStatusPane();
	}//GEN-LAST:event_pauseDownloadButtonActionPerformed

	// GEN-FIRST:event_newDownloadButtonActionPerformed
	private void newDownloadButtonActionPerformed(java.awt.event.ActionEvent evt) {
		createNewJobMenuItemActionPerformed(evt);
	}// GEN-LAST:event_newDownloadButtonActionPerformed

	// GEN-FIRST:event_openDownloadTemplateMenuItemActionPerformed
	private void openDownloadTemplateMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		loadDownload();
	}// GEN-LAST:event_openDownloadTemplateMenuItemActionPerformed

	// GEN-FIRST:event_createNewJobMenuItemActionPerformed
	private void createNewJobMenuItemActionPerformed(ActionEvent evt) {
		openAddDownloadDialog();
	}// GEN-LAST:event_createNewJobMenuItemActionPerformed

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exitMenuItemActionPerformed
		System.exit(0);
	}// GEN-LAST:event_exitMenuItemActionPerformed

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JMenuItem aboutMenuItem;
	private javax.swing.JMenuItem contentsMenuItem;
	private javax.swing.JMenuItem createNewJobMenuItem;
	private javax.swing.JTabbedPane downloadsTabbedPane;
	private javax.swing.JMenuItem exitMenuItem;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenuItem logWindowMenuItem;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JButton newDownloadButton;
	private javax.swing.JMenuItem openDownloadTemplateMenuItem;
	private javax.swing.JButton pauseDownloadButton;
	private javax.swing.JMenuItem regExpTesterMenuItem;
	private javax.swing.JButton stopDownloadButton;
	private javax.swing.JToolBar toolBar;
	private javax.swing.JMenu toolsMenu;
	// End of variables declaration//GEN-END:variables

}