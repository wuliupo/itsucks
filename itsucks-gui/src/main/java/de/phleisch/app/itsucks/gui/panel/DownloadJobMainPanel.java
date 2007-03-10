/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui.panel;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.filter.DownloadJobFilter;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.gui.AddDownloadJobBean;
import de.phleisch.app.itsucks.io.DownloadJob;

/**
 * @author olli
 *
 */
public class DownloadJobMainPanel extends JPanel {

	private static final long serialVersionUID = -3276764294240160785L;

	private static Log mLog = LogFactory.getLog(DownloadJobMainPanel.class);
	
	private JLabel jLabelURL = null;

	private JTextField jURL = null;

	private JLabel jLabelName = null;

	private JTextField jName = null;

	private JLabel jLabelMaxRecursionDepth = null;

	private JTextField jRecursionDepth = null;

	private JLabel jLabelSavePath = null;

	private JTextField jSavePath = null;

	private static int mCounter = 0;

	private JCheckBox jFollowOnlyRelativeLinks = null;

	private JLabel jLabelBaseURL = null;

	private JTextField jBaseURL = null;

	private JList jFileFilterList = null;
	private DefaultListModel mFileFilterListModel = null;

	private JLabel jLabelFileExtensions = null;

	private JTextField jAddFileFilter = null;

	private JButton jAddFileFilterButton = null;

	private JButton jRemoveFileFilterButton = null;

	private JLabel jLabelAllowedHostnames = null;

	private JList jHostnameFilterList = null;
	private DefaultListModel mHostnameFilterListModel = null;

	private JButton jAddHostnameFilterButton = null;

	private JButton jRemoveHostnameFilterButton = null;

	private JTextField jAddHostnameFilter = null;

	@SuppressWarnings("unused")
	private Dialog mParentDialog = null;

	private JScrollPane jFileFilterListScrollPane = null;

	private JScrollPane jHostnameFilterListScrollPane = null;

	/**
	 * This is the default constructor
	 */
	public DownloadJobMainPanel(Dialog pParentDialog) {
		super();
		
		mParentDialog = pParentDialog;
		
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(402, 472);
		
		jLabelAllowedHostnames = new JLabel();
		jLabelAllowedHostnames.setBounds(new Rectangle(20, 190, 338, 21));
		jLabelAllowedHostnames.setText("Allowed Hostnames (Regular expression, full match):");
		jLabelAllowedHostnames.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelFileExtensions = new JLabel();
		jLabelFileExtensions.setBounds(new Rectangle(20, 290, 341, 21));
		jLabelFileExtensions.setText("Files to download (Regular expression, full match):");
		jLabelFileExtensions.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelBaseURL = new JLabel();
		jLabelBaseURL.setBounds(new Rectangle(20, 420, 341, 21));
		jLabelBaseURL.setText("Base URL (needed only for stay on base URL)");
		jLabelBaseURL.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelSavePath = new JLabel();
		jLabelSavePath.setBounds(new Rectangle(20, 90, 251, 21));
		jLabelSavePath.setText("Save path (absolute):");
		jLabelSavePath.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelMaxRecursionDepth = new JLabel();
		jLabelMaxRecursionDepth.setBounds(new Rectangle(20, 140, 251, 21));
		jLabelMaxRecursionDepth.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelMaxRecursionDepth.setText("Max recursion depth: (-1 = unlimited):");
		jLabelName = new JLabel();
		jLabelName.setBounds(new Rectangle(20, 10, 41, 21));
		jLabelName.setText("Name:");
		jLabelName.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelURL = new JLabel();
		jLabelURL.setBounds(new Rectangle(20, 40, 251, 21));
		jLabelURL.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelURL.setText("Start URL:");
		setLayout(null);
		this.add(jLabelName, null);
		this.add(getJName(), null);
		add(jLabelURL, null);
		add(getJURL(), null);
		this.add(jLabelSavePath, null);
		this.add(getJSavePath(), null);
		add(jLabelMaxRecursionDepth, null);
		add(getJRecursionDepth(), null);
		this.add(jLabelAllowedHostnames, null);
		this.add(getJHostnameFilterListScrollPane(), null);
		this.add(getJAddHostnameFilter(), null);
		this.add(getJAddHostnameFilterButton(), null);
		this.add(getJRemoveHostnameFilterButton(), null);
		this.add(jLabelFileExtensions, null);
		this.add(getJFileFilterListScrollPane(), null);
		add(getJAddFileFilter(), null);
		add(getJAddFileFilterButton(), null);
		add(getJRemoveFileFilterButton(), null);
		this.add(jLabelBaseURL, null);
		this.add(getJBaseURL(), null);
		this.add(getJFollowOnlyRelativeLinks(), null);
	}


	/**
	 * This method initializes jURL	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJURL() {
		if (jURL == null) {
			jURL = new JTextField();
			jURL.setBounds(new Rectangle(20, 60, 361, 21));
			jURL.setFont(new Font("Dialog", Font.PLAIN, 12));
			jURL.setText("http://");
		}
		return jURL;
	}

	/**
	 * This method initializes jName	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJName() {
		if (jName == null) {
			jName = new JTextField();
			jName.setBounds(new Rectangle(70, 10, 171, 21));
			jName.setText("Download" + ++mCounter);
			jName.setFont(new Font("Dialog", Font.PLAIN, 12));
			jName.setName("jName");
		}
		return jName;
	}

	/**
	 * This method initializes jRecursionDepth	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJRecursionDepth() {
		if (jRecursionDepth == null) {
			jRecursionDepth = new JTextField();
			jRecursionDepth.setBounds(new Rectangle(20, 160, 41, 21));
			jRecursionDepth.setHorizontalAlignment(JTextField.RIGHT);
			jRecursionDepth.setFont(new Font("Dialog", Font.PLAIN, 12));
			jRecursionDepth.setText("-1");
		}
		return jRecursionDepth;
	}

	/**
	 * This method initializes jSavePath	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJSavePath() {
		if (jSavePath == null) {
			jSavePath = new JTextField();
			jSavePath.setBounds(new Rectangle(20, 110, 361, 21));
			jSavePath.setFont(new Font("Dialog", Font.PLAIN, 12));
			jSavePath.setText(System.getProperty("user.home") 
					+ File.separatorChar 
					+ "itsucks" 
					+ File.separatorChar);
		}
		return jSavePath;
	}

	/**
	 * This method initializes jFollowOnlyRelativeLinks	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJFollowOnlyRelativeLinks() {
		if (jFollowOnlyRelativeLinks == null) {
			jFollowOnlyRelativeLinks = new JCheckBox();
			jFollowOnlyRelativeLinks.setBounds(new Rectangle(20, 400, 251, 21));
			jFollowOnlyRelativeLinks.setFont(new Font("Dialog", Font.PLAIN, 12));
			jFollowOnlyRelativeLinks.setText("Stay on base URL");
			jFollowOnlyRelativeLinks.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					jBaseURL.setEnabled(jFollowOnlyRelativeLinks.isSelected());
				}
			});
		}
		return jFollowOnlyRelativeLinks;
	}

	/**
	 * This method initializes jBaseURL	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJBaseURL() {
		if (jBaseURL == null) {
			jBaseURL = new JTextField();
			jBaseURL.setBounds(new Rectangle(20, 440, 361, 21));
			jBaseURL.setEnabled(false);
			jBaseURL.setText("http://");
		}
		return jBaseURL;
	}

	/**
	 * This method initializes jFileFilterList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJFileFilterList() {
		if (jFileFilterList == null) {
			mFileFilterListModel = new DefaultListModel();
			mFileFilterListModel.add(0, ".*");
			
			jFileFilterList = new JList(mFileFilterListModel);
			jFileFilterList.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jFileFilterList;
	}

	/**
	 * This method initializes jAddFileFilter	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJAddFileFilter() {
		if (jAddFileFilter == null) {
			jAddFileFilter = new JTextField();
			jAddFileFilter.setBounds(new Rectangle(260, 320, 121, 21));
			jAddFileFilter.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jAddFileFilter;
	}

	/**
	 * This method initializes jAddFileFilterButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJAddFileFilterButton() {
		if (jAddFileFilterButton == null) {
			jAddFileFilterButton = new JButton();
			jAddFileFilterButton.setBounds(new Rectangle(220, 320, 31, 21));
			jAddFileFilterButton.setActionCommand("Add");
			jAddFileFilterButton.setIcon(new ImageIcon(getClass().getResource("/go-previous.png")));
			jAddFileFilterButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(jAddFileFilter.getText().length() > 0) {
						mFileFilterListModel.add(0, jAddFileFilter.getText());
					}
				}
			});
		}
		return jAddFileFilterButton;
	}

	/**
	 * This method initializes jRemoveFileFilterButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJRemoveFileFilterButton() {
		if (jRemoveFileFilterButton == null) {
			jRemoveFileFilterButton = new JButton();
			jRemoveFileFilterButton.setBounds(new Rectangle(220, 350, 31, 21));
			jRemoveFileFilterButton.setIcon(new ImageIcon(getClass().getResource("/go-next.png")));
			jRemoveFileFilterButton.setActionCommand("Remove");
			jRemoveFileFilterButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] selections = jFileFilterList.getSelectedIndices();
					if(selections.length > 0) {
						for (int i = selections.length - 1; i >= 0; i--) {
							mFileFilterListModel.remove(selections[i]);
						}
					}
				}
			});
			
		}
		return jRemoveFileFilterButton;
	}

	/**
	 * This method initializes jHostnameFilterList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJHostnameFilterList() {
		if (jHostnameFilterList == null) {
			mHostnameFilterListModel = new DefaultListModel();
			mHostnameFilterListModel.add(0, ".*");
			
			jHostnameFilterList = new JList(mHostnameFilterListModel);
			jHostnameFilterList.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jHostnameFilterList;
	}

	/**
	 * This method initializes jAddHostnameFilterButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJAddHostnameFilterButton() {
		if (jAddHostnameFilterButton == null) {
			jAddHostnameFilterButton = new JButton();
			jAddHostnameFilterButton.setBounds(new Rectangle(220, 220, 31, 21));
			jAddHostnameFilterButton.setIcon(new ImageIcon(getClass().getResource("/go-previous.png")));
			jAddHostnameFilterButton.setActionCommand("Add");
			jAddHostnameFilterButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(jAddHostnameFilter.getText().length() > 0) {
						mHostnameFilterListModel.add(0, jAddHostnameFilter.getText());
					}
				}
			});
		}
		return jAddHostnameFilterButton;
	}

	/**
	 * This method initializes jRemoveHostnameFilterButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJRemoveHostnameFilterButton() {
		if (jRemoveHostnameFilterButton == null) {
			jRemoveHostnameFilterButton = new JButton();
			jRemoveHostnameFilterButton.setBounds(new Rectangle(220, 250, 31, 21));
			jRemoveHostnameFilterButton.setIcon(new ImageIcon(getClass().getResource("/go-next.png")));
			jRemoveHostnameFilterButton.setActionCommand("Remove");
			jRemoveHostnameFilterButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] selections = jHostnameFilterList.getSelectedIndices();
					if(selections.length > 0) {
						for (int i = selections.length - 1; i >= 0; i--) {
							mHostnameFilterListModel.remove(selections[i]);
						}
					}
				}
			});
		}
		return jRemoveHostnameFilterButton;
	}

	/**
	 * This method initializes jAddHostnameFilter	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJAddHostnameFilter() {
		if (jAddHostnameFilter == null) {
			jAddHostnameFilter = new JTextField();
			jAddHostnameFilter.setBounds(new Rectangle(260, 220, 121, 21));
			jAddHostnameFilter.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jAddHostnameFilter;
	}

	private boolean checkFields() {
		
		List<String> errorMessages = new ArrayList<String>();
		
		//check fields
		try {
			new URL(jURL.getText()).toURI();
		} catch(Exception e) {
			errorMessages.add("URL is malformed.");
		}
		
		try {
			if(jFollowOnlyRelativeLinks.isSelected()) {
				new URL(jBaseURL.getText()).toURI();
			}
		} catch(Exception e) {
			errorMessages.add("Base URL is malformed.");
		}
		
		try {
			Integer.parseInt(jRecursionDepth.getText());
		} catch (NumberFormatException e) {
			errorMessages.add("Recursion Depth is not a number.");
		}
		
		try {
			File target_path = new File(jSavePath.getText());
			if(!target_path.isDirectory()) {
				errorMessages.add("Save path does not exists.");
			}
		} catch (Exception e) {
			errorMessages.add("Save path is not valid.");
		}
		
		if(errorMessages.size() > 0) {
			String messageText = "";
			
			for (String message : errorMessages) {
				messageText += message + "\n";
			}
			
			JOptionPane.showMessageDialog(this, messageText, "Validation error", JOptionPane.ERROR_MESSAGE );
			
			return false;
		} else {
			return true;
		}
	}
	
	public AddDownloadJobBean buildDownloadJob() {
		
		if(!checkFields()) return null;
		
		//build download job
		DownloadJob job = (DownloadJob) 
			SpringContextSingelton.getApplicationContext().getBean("DownloadJob");
		job.setIgnoreFilter(true);
		job.setState(DownloadJob.STATE_OPEN);
		job.setName(jName.getText());
		try {
			job.setUrl(new URL(jURL.getText()));
		} catch (MalformedURLException e) {
			mLog.error("Bad URL", e);
			throw new RuntimeException("Bad URL", e);
		}
		
		job.setSavePath(new File(jSavePath.getText()));
		
		DownloadJobFilter jobFilter = (DownloadJobFilter) 
			SpringContextSingelton.getApplicationContext().getBean("DownloadJobFilter");
		
		jobFilter.setMaxRecursionDepth(Integer.parseInt(jRecursionDepth.getText()));
		
		ArrayList<String> hostFilter = new ArrayList<String>();
		ListModel model = jHostnameFilterList.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			hostFilter.add((String) model.getElementAt(i));
		}
		jobFilter.setAllowedHostNames(hostFilter.toArray(new String[hostFilter.size()]));
		
		ArrayList<String> fileFilter = new ArrayList<String>();
		ListModel fileModel = jFileFilterList.getModel();
		for (int i = 0; i < fileModel.getSize(); i++) {
			fileFilter.add((String) fileModel.getElementAt(i));
		}
		jobFilter.setSaveToFileFilter(fileFilter.toArray(new String[fileFilter.size()]));
		
		if(jFollowOnlyRelativeLinks.isSelected()) {
			jobFilter.setAllowOnlyRelativeReferences(true);
			try {
				jobFilter.setBaseURL(new URL(jBaseURL.getText()));
			} catch (MalformedURLException e) {
				mLog.error("Bad Base URL", e);
				throw new RuntimeException("Bad Base URL", e);
			}
		}
		
		//build result
		AddDownloadJobBean result = new AddDownloadJobBean();
		result.setDownload(job);
		result.setFilterList(Arrays.asList(new JobFilter[] {
				jobFilter/*, regExpJobFilter*/}));
		
		return result;
	}
	
	public void loadDownloadJob(DownloadJob pDownload, DownloadJobFilter pFilter) {
		
		jName.setText(pDownload.getName());
		jURL.setText(pDownload.getUrl().toString());
		jSavePath.setText(pDownload.getSavePath().toString());
		
		if(pFilter != null) {
			
			jRecursionDepth.setText(Integer.toString(pFilter.getMaxRecursionDepth()));
			
			mHostnameFilterListModel.clear();
			for (String allowedHostname : pFilter.getAllowedHostNames()) {
				mHostnameFilterListModel.addElement(allowedHostname);
			}
			
			mFileFilterListModel.clear();
			for (String fileFilter : pFilter.getSaveToFileFilter()) {
				mFileFilterListModel.addElement(fileFilter);
			}
			
			jFollowOnlyRelativeLinks.setSelected(pFilter.isAllowOnlyRelativeReferences());
			if(pFilter.isAllowOnlyRelativeReferences()) {
				jBaseURL.setText(pFilter.getBaseURL().toString());
			}
			
		}
	}


	/**
	 * This method initializes jFileFilterListScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJFileFilterListScrollPane() {
		if (jFileFilterListScrollPane == null) {
			jFileFilterListScrollPane = new JScrollPane();
			jFileFilterListScrollPane.setBounds(new Rectangle(20, 310, 191, 71));
			jFileFilterListScrollPane.setViewportView(getJFileFilterList());
		}
		return jFileFilterListScrollPane;
	}

	/**
	 * This method initializes jHostnameFilterListScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJHostnameFilterListScrollPane() {
		if (jHostnameFilterListScrollPane == null) {
			jHostnameFilterListScrollPane = new JScrollPane();
			jHostnameFilterListScrollPane.setBounds(new Rectangle(20, 210, 191, 71));
			jHostnameFilterListScrollPane.setViewportView(getJHostnameFilterList());
		}
		return jHostnameFilterListScrollPane;
	}

	
}  //  @jve:decl-index=0:visual-constraint="10,10"
