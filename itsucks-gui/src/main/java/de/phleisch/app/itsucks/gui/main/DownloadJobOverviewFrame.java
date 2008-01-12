/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui.main;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.core.impl.DispatcherList;
import de.phleisch.app.itsucks.core.impl.DispatcherThread;
import de.phleisch.app.itsucks.core.impl.DispatcherList.DispatcherListEvent;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.event.impl.CoreEvents;
import de.phleisch.app.itsucks.gui.common.AboutDialog;
import de.phleisch.app.itsucks.gui.common.LogDialog;
import de.phleisch.app.itsucks.gui.common.TestRegularExpressionDialog;
import de.phleisch.app.itsucks.gui.job.EditDownloadJobHelper;
import de.phleisch.app.itsucks.gui.job.ifc.AddDownloadJobCapable;
import de.phleisch.app.itsucks.gui.job.panel.DownloadJobQueueOverviewPanel;
import de.phleisch.app.itsucks.gui.main.helper.DispatcherHelper;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 * 
 * @author __USER__
 */
public class DownloadJobOverviewFrame extends javax.swing.JFrame implements
		AddDownloadJobCapable, EventObserver {
	
	private static final long serialVersionUID = 6628042574113496207L;
	@SuppressWarnings("unused")
	private static Log mLog = LogFactory.getLog(DownloadJobOverviewFrame.class);

	private Map<Dispatcher, EventObserver> mEventObserver = new HashMap<Dispatcher, EventObserver>();

	private DispatcherList mDispatcherList;

	/** Creates new form DownloadJobOverviewFrame */
	public DownloadJobOverviewFrame() {
		mDispatcherList = new DispatcherList();
		mDispatcherList.registerObserver(this);

		initComponents();
	}

	public void addDownload(SerializableJobPackage pJobList) {

		DispatcherHelper helper = new DispatcherHelper();

		DispatcherThread dispatcher = helper.createDispatcher(pJobList);

		//add the dispatcher to the list, the panel will be added by the event
		mDispatcherList.addDispatcher(dispatcher);

		helper.startDispatcher(dispatcher);

	}

	private void openAddDownloadDialog() {

		EditDownloadJobHelper helper = new EditDownloadJobHelper(this);
		helper.openAddDownloadDialog(this);

	}

	private void loadDownload() {

		EditDownloadJobHelper helper = new EditDownloadJobHelper(this);
		helper.loadAndEditDownload(this);

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
		Dispatcher dispatcher = panel.getDispatcher();

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
		DispatcherThread dispatcher = (DispatcherThread) panel.getDispatcher();

		DispatcherHelper helper = new DispatcherHelper();
		helper.stopDispatcher(dispatcher);

		mDispatcherList.removeDispatcherById(panel.getDispatcherId());

		//inform the gc that it would be a great opportunity to get some memory back
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
			Dispatcher dispatcher = panel.getDispatcher();

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
			Dispatcher dispatcher = panel.getDispatcher();

			if (dispatcher.isPaused()) {
				downloadsTabbedPane
						.setTitleAt(
								downloadsTabbedPane.indexOfComponent(panel),
								panel.getName() + " (paused)");
			} else if (dispatcher.isRunning()) {
				downloadsTabbedPane.setTitleAt(downloadsTabbedPane
						.indexOfComponent(panel), panel.getName());
			} else {
				downloadsTabbedPane.setTitleAt(downloadsTabbedPane
						.indexOfComponent(panel), panel.getName()
						+ " (finished)");
			}

		}

	}

	public void processEvent(Event pEvent) {

		switch (pEvent.getType()) {

		case DispatcherList.EVENT_DISPATCHER_ADDED:
			processDispatcherAdded((DispatcherListEvent) pEvent);
			break;

		case DispatcherList.EVENT_DISPATCHER_REMOVED:
			processDispatcherRemoved((DispatcherListEvent) pEvent);
			break;

		default:
			throw new IllegalStateException("Unknown Event: " + pEvent);

		}

	}

	private void processDispatcherAdded(DispatcherListEvent pEvent) {

		DownloadJobQueueOverviewPanel pane = new DownloadJobQueueOverviewPanel();
		pane.setDispatcher(pEvent.getDispatcher());
		pane.setDispatcherId(pEvent.getDispatcherId());
		pane.setName(pEvent.getDispatcher().getName());

		EventObserver observer = new EventObserver() {
			public void processEvent(Event pEvent) {
				if (pEvent.getCategory() == CoreEvents.EVENT_CATEGORY_DISPATCHER) {

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							updateButtonState();
							updateTabTitles();
						}
					});
				}
			}
		};

		Dispatcher dispatcher = pEvent.getDispatcher();

		mEventObserver.put(dispatcher, observer);
		dispatcher.getEventManager().registerObserver(observer);

		//add pane
		downloadsTabbedPane.add(pane.getName(), pane);
	}

	private void processDispatcherRemoved(DispatcherListEvent pEvent) {

		Component[] components = downloadsTabbedPane.getComponents();

		for (int i = 0; i < components.length; i++) {

			DownloadJobQueueOverviewPanel panel = (DownloadJobQueueOverviewPanel) components[i];

			if (panel.getDispatcherId() == pEvent.getDispatcherId()) {

				Dispatcher dispatcher = pEvent.getDispatcher();

				EventObserver eventObserver = mEventObserver.get(dispatcher);
				mEventObserver.remove(dispatcher);
				dispatcher.getEventManager().unregisterObserver(eventObserver);

				downloadsTabbedPane.remove(panel);
				panel.removeDispatcher();
			}

		}

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
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
		batchMenuItem = new javax.swing.JMenuItem();
		regExpTesterMenuItem = new javax.swing.JMenuItem();
		logWindowMenuItem = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		contentsMenuItem = new javax.swing.JMenuItem();
		aboutMenuItem = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle(ApplicationConstants.APPLICATION_TITLE);
		setIconImage(new javax.swing.ImageIcon(getClass().getResource(
				"/whirl_icon.png")).getImage());
		setLocationByPlatform(true);
		setName("mainFrame");

		downloadsTabbedPane
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						downloadsTabbedPaneStateChanged(evt);
					}
				});

		toolBar.setFloatable(false);

		newDownloadButton.setFont(new java.awt.Font("Dialog", 0, 12));
		newDownloadButton.setIcon(new javax.swing.ImageIcon(getClass()
				.getResource("/document-new.png"))); // NOI18N
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
				.getResource("/pause.png"))); // NOI18N
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
				.getResource("/edit-delete.png"))); // NOI18N
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

		batchMenuItem.setText("Batch Processing");
		batchMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				batchMenuItemActionPerformed(evt);
			}
		});
		toolsMenu.add(batchMenuItem);

		regExpTesterMenuItem.setText("Regular Expression Tester");
		regExpTesterMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						regExpTesterMenuItemActionPerformed(evt);
					}
				});
		toolsMenu.add(regExpTesterMenuItem);

		logWindowMenuItem.setText("Log window");
		logWindowMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						logWindowMenuItemActionPerformed(evt);
					}
				});
		toolsMenu.add(logWindowMenuItem);

		menuBar.add(toolsMenu);

		helpMenu.setText("Help");

		contentsMenuItem.setText("Contents");
		contentsMenuItem.setEnabled(false);
		helpMenu.add(contentsMenuItem);

		aboutMenuItem.setText("About");
		aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				aboutMenuItemActionPerformed(evt);
			}
		});
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(toolBar,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 677,
				Short.MAX_VALUE).add(downloadsTabbedPane,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 677,
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
								353, Short.MAX_VALUE)));

		downloadsTabbedPane.getAccessibleContext().setAccessibleName(
				"Download 1");

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	//GEN-FIRST:event_batchMenuItemActionPerformed
	private void batchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

		BatchProcessingDialog batchDialog = new BatchProcessingDialog(null,
				false);
		batchDialog.setDispatcherList(mDispatcherList);
		batchDialog.setVisible(true);

	}//GEN-LAST:event_batchMenuItemActionPerformed

	//GEN-FIRST:event_downloadsTabbedPaneStateChanged
	private void downloadsTabbedPaneStateChanged(
			javax.swing.event.ChangeEvent evt) {
		updateButtonState();
	}//GEN-LAST:event_downloadsTabbedPaneStateChanged

	//GEN-FIRST:event_aboutMenuItemActionPerformed
	private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {

		AboutDialog about = new AboutDialog(this, false);
		about.setVisible(true);

	}//GEN-LAST:event_aboutMenuItemActionPerformed

	//GEN-FIRST:event_regExpTesterMenuItemActionPerformed
	private void regExpTesterMenuItemActionPerformed(
			java.awt.event.ActionEvent evt) {

		TestRegularExpressionDialog tester = new TestRegularExpressionDialog(
				null, false);
		tester.setVisible(true);

	}//GEN-LAST:event_regExpTesterMenuItemActionPerformed

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

	// GEN-FIRST:event_exitMenuItemActionPerformed
	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(0);
	}// GEN-LAST:event_exitMenuItemActionPerformed

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JMenuItem aboutMenuItem;
	private javax.swing.JMenuItem batchMenuItem;
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