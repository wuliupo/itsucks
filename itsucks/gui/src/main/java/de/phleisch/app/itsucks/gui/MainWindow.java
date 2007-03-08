/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.ApplicationConstants;
import de.phleisch.app.itsucks.DispatcherThread;
import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.gui.panel.DownloadStatusPanel;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.persistence.JobSerializationManager;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

public class MainWindow implements AddDownloadJobInterface {

	private static Log mLog = LogFactory.getLog(MainWindow.class);  //  @jve:decl-index=0:
	
	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,81"

	private JPanel jContentPane = null;

	private JMenuBar jJMenuBar = null;

	private JMenu fileMenu = null;

	private JMenu editMenu = null;

	private JMenu toolsMenu = null;
	
	private JMenu helpMenu = null;

	private JMenuItem exitMenuItem = null;
	
	private JMenuItem addDownloadMenuItem = null;
	
	private JMenuItem loadDownloadMenuItem = null;
	
	private JMenuItem logDialogMenuItem = null;

	private JMenuItem aboutMenuItem = null;

	private JMenuItem toolsMenuItem = null;
	
	private JMenuItem cutMenuItem = null;

	private JMenuItem copyMenuItem = null;

	private JMenuItem pasteMenuItem = null;

	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="331,551"

	private JPanel aboutContentPane = null;

	private JLabel aboutVersionLabel = null;

	private JToolBar jToolBarBar = null;

	private JButton jNewDownload = null;

	private JTabbedPane jTabbedPane = null;

	private JButton jCloseDownload = null;



	private MainWindow() {
		super();
		initialize();
	}
	
	private void initialize() {
		
		//startup spring
		SpringContextSingelton.getApplicationContext();
		getJFrame();
	}
	
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setJMenuBar(getJJMenuBar());
			jFrame.setSize(702, 460);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle(ApplicationConstants.APPLICATION_TITLE);
			jFrame.setLocationByPlatform(true);
			jFrame.setVisible(true);
			
		}
		return jFrame;
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
			jContentPane.add(getJToolBarBar(), BorderLayout.NORTH);
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getEditMenu());
			jJMenuBar.add(getToolsMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getAddDownloadMenuItem());
			fileMenu.add(getLoadDownloadMenuItem());
			fileMenu.add(getLogDialogItem());
			fileMenu.add(getExitMenuItem());
			
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.add(getCutMenuItem());
			editMenu.add(getCopyMenuItem());
			editMenu.add(getPasteMenuItem());
		}
		return editMenu;
	}

	/**
	 * This method initializes jTools
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getToolsMenu() {
		if (toolsMenu == null) {
			toolsMenu = new JMenu();
			toolsMenu.setText("Tools");
			toolsMenu.add(getToolsMenuItem());
		}
		return toolsMenu;
	}
	
	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getLogDialogItem() {
		if (logDialogMenuItem == null) {
			logDialogMenuItem = new JMenuItem();
			logDialogMenuItem.setText("Show log window");
			logDialogMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openLogDialog();
				}

			});
		}
		return logDialogMenuItem;
	}
	
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAddDownloadMenuItem() {
		if (addDownloadMenuItem == null) {
			addDownloadMenuItem = new JMenuItem();
			addDownloadMenuItem.setText("New download");
			addDownloadMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openAddDownloadDialog();
				}
			});
		}
		return addDownloadMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getLoadDownloadMenuItem() {
		if (loadDownloadMenuItem == null) {
			loadDownloadMenuItem = new JMenuItem();
			loadDownloadMenuItem.setText("Load download template");
			loadDownloadMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadDownload();
				}

			});
		}
		return loadDownloadMenuItem;
	}
	
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getToolsMenuItem() {
		if (toolsMenuItem == null) {
			toolsMenuItem = new JMenuItem();
			toolsMenuItem.setText("Regular Expression Tester");
			toolsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TestRegularExpressionDialog tester = 
						new TestRegularExpressionDialog(MainWindow.this.getJFrame());
					tester.setVisible(true);
				}
			});
		}
		return toolsMenuItem;
	}
	
	/**
	 * This method initializes aboutDialog	
	 * 	
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(getJFrame(), true);
			aboutDialog.setTitle("About " + ApplicationConstants.APPLICATION_TITLE);
			aboutDialog.setSize(new Dimension(298, 126));
			aboutDialog.setContentPane(getAboutContentPane());
			aboutDialog.setLocationByPlatform(true);
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new FlowLayout());
			aboutContentPane.setSize(new Dimension(298, 126));
			aboutContentPane.add(getAboutVersionLabel(), null);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getAboutVersionLabel() {
		if (aboutVersionLabel == null) {
			aboutVersionLabel = new JLabel();
			aboutVersionLabel.setText(
					"<html>" +
					"<b>" + ApplicationConstants.APPLICATION_TITLE + "</b><br><br>" +
					"Created by Oliver Mihatsch<br>" +
					"URL: http://itsucks.sf.net<br>" +
					"Contact: banishedknight@users.sf.net<br>" +
					"</html>");
			aboutVersionLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			aboutVersionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return aboutVersionLabel;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCutMenuItem() {
		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setText("Cut");
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					Event.CTRL_MASK, true));
		}
		return cutMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setText("Copy");
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					Event.CTRL_MASK, true));
		}
		return copyMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					Event.CTRL_MASK, true));
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes jToolBarBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJToolBarBar() {
		if (jToolBarBar == null) {
			jToolBarBar = new JToolBar();
			jToolBarBar.setOpaque(false);
			jToolBarBar.add(getJNewDownload());
			jToolBarBar.add(getJCloseDownload());
		}
		return jToolBarBar;
	}

	/**
	 * This method initializes jNewDownload	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJNewDownload() {
		if (jNewDownload == null) {
			jNewDownload = new JButton();
			jNewDownload.setIcon(new ImageIcon(getClass().getResource("/document-new.png")));
			//jNewDownload.setPreferredSize(new Dimension(53, 22));
			jNewDownload.setBorderPainted(false);
			jNewDownload.setText("New download");
			jNewDownload.setToolTipText("New download");
			jNewDownload.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					openAddDownloadDialog();
				}
			});
		}
		return jNewDownload;
	}

	private void openAddDownloadDialog() {
		new AddDownloadJobDialog(jFrame, this);
	}
	
	private void loadDownload() {
		//open dialog
		JFileChooser fc = new JFileChooser();
		
		//Show load dialog; this method does not return until the dialog is closed
		int result = fc.showOpenDialog(jFrame);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			JobSerializationManager serializationManager = (JobSerializationManager) 
				SpringContextSingelton.getApplicationContext().getBean("JobSerializationManager");
		
			SerializableJobList jobList = null;
			try {
				jobList = serializationManager.deserialize(fc.getSelectedFile());
			} catch (Exception e1) {
				
				mLog.error("Error occured while loading download template", e1);
				
				JOptionPane.showMessageDialog(jFrame, 
						"Error occured while loading download template.\n" + e1.getMessage(), 
						"Error occured", JOptionPane.ERROR_MESSAGE );
			} 
			
			if(jobList != null) {
				addDownload((DownloadJob)jobList.getJobs().get(0), jobList.getFilters());
			}
			
		}
		
	}
	
	private void openLogDialog() {
		LogDialog newLogDialog = new LogDialog();
		newLogDialog.setVisible(true);
	}
	
	private void closeDownloadStatusPane() {
		
		Component selectedComponent = jTabbedPane.getSelectedComponent();
		if(selectedComponent == null) return;
		
		DownloadStatusPanel pane = (DownloadStatusPanel) selectedComponent;
		DispatcherThread dispatcher = pane.getDispatcher();
		
		dispatcher.stop();
		try {
			dispatcher.join();
		} catch (InterruptedException e) {
			mLog.error(e, e);
		}
		
		pane.removeDispatcher();
		jTabbedPane.remove(pane);
		
		//inform the gc that it would be a great oppurtinity to get some memory back
		System.gc();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.chaoscrawler.gui.AddDownloadJobInterface#addDownload(de.phleisch.app.chaoscrawler.io.DownloadJob, java.util.List)
	 */
	public void addDownload(DownloadJob pDownload, List<JobFilter> pFilterList) {
		
		DownloadStatusPanel pane = new DownloadStatusPanel();
		
		DispatcherThread dispatcher = (DispatcherThread) 
			SpringContextSingelton.getApplicationContext().getBean("DispatcherThread");
		
		if(dispatcher == null) {
			throw new RuntimeException("Can't instatiate dispatcher!");
		}
		pane.setDispatcher(dispatcher);
		jTabbedPane.add(pDownload.getName(), pane);
		
		dispatcher.addJobFilter(pFilterList);
		dispatcher.addJob(pDownload);
		
		//start dispatcher thread
		try {
			dispatcher.processJobs();
		} catch (Exception e) {
			mLog.error("Error starting dispatcher thread", e);
		}
	}
	
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jCloseDownload	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCloseDownload() {
		if (jCloseDownload == null) {
			jCloseDownload = new JButton();
			jCloseDownload.setIcon(new ImageIcon(getClass().getResource("/edit-delete.png")));
			//jCloseDownload.setPreferredSize(new Dimension(23, 22));
			jCloseDownload.setBorderPainted(false);
			jCloseDownload.setText("Stop download");
			jCloseDownload.setToolTipText("Close/Stop download");
			jCloseDownload.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeDownloadStatusPane();
				}
			});
			
			
		}
		return jCloseDownload;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				@SuppressWarnings("unused")
				MainWindow application = new MainWindow();
				//application.getJFrame().setVisible(true);
			}
		});
	}

}
