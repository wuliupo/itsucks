/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.gui.panel.AdvancedFilterOverviewPanel;
import de.phleisch.app.itsucks.gui.panel.DownloadJobMainPanel;


public class AddDownloadJobDialog extends JDialog {

	@SuppressWarnings("unused")
	private static Log mLog = LogFactory.getLog(AddDownloadJobDialog.class);  //  @jve:decl-index=0:
	
	private static final long serialVersionUID = -872103929498473896L;

	private AddDownloadJobInterface mDownloadJobManager = null;
	
	private DownloadJobMainPanel downloadJobMainPanel = null;

	private JPanel jContentPane = null;

	private JButton jButtonDownload = null;

	private JPanel jPanelAction = null;

	private JButton jButtonCancel = null;

	private JTabbedPane jTabbedPane = null;

	private AdvancedFilterOverviewPanel advancedFilterOverviewPanel = null;

	/**
	 * @param owner
	 */
	public AddDownloadJobDialog(Frame pOwner, AddDownloadJobInterface pMainWindow) {
		super(pOwner);
		mDownloadJobManager = pMainWindow;
		
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(407, 566);
		this.setContentPane(getJContentPane());
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle("Add a download...");
		this.setResizable(true);
		this.setLocationByPlatform(true);
		this.setVisible(true);
	}


	protected void startDownload() {
		
		AddDownloadJobBean job = this.downloadJobMainPanel.buildDownloadJob();
		if(job == null) return;
		
		JobFilter advancedFilter = 
			this.advancedFilterOverviewPanel.buildAdvancedFilter();
		
		job.addFilter(advancedFilter);
		
		mDownloadJobManager.addDownload(job.getDownload(), job.getFilterList());
		
		this.dispose();
	}

	/**
	 * This method initializes downloadJobMainPanel	
	 * 	
	 * @return de.phleisch.app.chaoscrawler.gui.second_try.DownloadJobMainPanel	
	 */
	private DownloadJobMainPanel getDownloadJobMainPanel() {
		if (downloadJobMainPanel == null) {
			downloadJobMainPanel = new DownloadJobMainPanel(this);
			downloadJobMainPanel.setName("downloadJobMainPanel");
			
		}
		return downloadJobMainPanel;
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
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
			jContentPane.add(getJPanelAction(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButtonDownload	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonDownload() {
		if (jButtonDownload == null) {
			jButtonDownload = new JButton();
			jButtonDownload.setBounds(new Rectangle(189, 650, 34, 10));
			jButtonDownload.setText("Start download");
			jButtonDownload.setName("downloadButton");
			jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					startDownload();
				}
			});
			getRootPane().setDefaultButton(jButtonDownload);
		}
		return jButtonDownload;
	}

	/**
	 * This method initializes jPanelAction	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelAction() {
		if (jPanelAction == null) {
			jPanelAction = new JPanel();
//			jPanel1.setLayout(new BoxLayout(getJPanel1(), BoxLayout.X_AXIS));
//			jPanel1 = Box.createHorizontalBox();
//			jPanel1.add(Box.createHorizontalGlue());
//			
			jPanelAction.add(getJButtonDownload(), null);
//			jPanel1.add(Box.createHorizontalStrut(5));
			jPanelAction.add(getJButtonCancel(), null);
//			
//			jPanel1.add(Box.createHorizontalGlue());
//			
//			jPanel1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		}
		return jPanelAction;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dispose();
				}
			});
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("Basic", null, getDownloadJobMainPanel(), null);
			jTabbedPane.addTab("Advanced", null, getAdvancedFilterOverviewPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes advancedFilterOverviewPanel	
	 * 	
	 * @return de.phleisch.app.itsucks.gui.AdvancedFilterOverviewPanel	
	 */
	private AdvancedFilterOverviewPanel getAdvancedFilterOverviewPanel() {
		if (advancedFilterOverviewPanel == null) {
			advancedFilterOverviewPanel = new AdvancedFilterOverviewPanel(this);
		}
		return advancedFilterOverviewPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
