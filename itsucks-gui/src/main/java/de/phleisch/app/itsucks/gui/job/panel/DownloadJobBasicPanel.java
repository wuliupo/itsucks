/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * olli
 * Created on 26.08.2007
 */
package de.phleisch.app.itsucks.gui.job.panel;

import java.awt.Dialog;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.phleisch.app.itsucks.gui.common.EditUrlListDialog;
import de.phleisch.app.itsucks.gui.util.FieldValidator;

/**
 *
 * @author  olli
 */
public class DownloadJobBasicPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 6676129664345121404L;

	private List<URL> mUrlList = null;

	/** Creates new form DownloadJobMainPanel */
	public DownloadJobBasicPanel() {
		initComponents();
	}

	public List<String> validateFields() {

		FieldValidator validator = new FieldValidator();

		validator.assertNotEmpty(this.nameTextField.getText(),
				"Enter a valid name.");

		if (mUrlList != null && mUrlList.size() == 0) {
			validator.addError("Enter a valid URL.");
		}
		if (mUrlList == null) {
			validator.assertURL(this.urlTextField.getText(),
					"Enter a valid URL.");
		}

		if (validator.assertNotEmpty(this.savePathTextField.getText(),
				"Enter a valid path to save files.")) {

			File path = new File(this.savePathTextField.getText());
			if (!path.exists()) {
				validator.addError("Path to save files does not exist.");
			} else if (!path.canWrite()) {
				validator.addError("Path to save files is not writable.");
			}
		}

		validator.assertInteger(this.workingThreadsTextField.getText(),
				"Enter a valid number of working threads.");

		validator.assertInteger(this.maxConnectionsTextField.getText(),
				"Enter a valid number of max. connections per server.");

		validator.assertInteger(this.maxRetriesTextField.getText(),
				"Enter a valid number of max. retries.");

		if (this.enableProxyCheckBox.isSelected()) {

			validator.assertNotEmpty(this.proxyServerTextField.getText(),
					"Enter a valid proxy server.");

			validator.assertInteger(this.proxyPortTextField.getText(),
					"Enter a valid proxy port.");
		}

		if (this.enableAuthenticationCheckBox.isSelected()) {

			validator.assertNotEmpty(
					this.authenticationUserTextField.getText(),
					"Enter a valid proxy user.");

			validator.assertNotEmpty(this.authenticationPasswordTextField
					.getText(), "Enter a valid proxy password.");
		}

		return validator.getErrors();
	}

	public List<URL> getUrlList() throws MalformedURLException {

		List<URL> urlList = null;

		if (mUrlList != null) {
			urlList = mUrlList;
		} else {
			urlList = new ArrayList<URL>();
			String url = urlTextField.getText();
			if (url.trim().length() > 0) {
				urlList.add(new URL(url));
			}
		}

		return urlList;
	}

	public void setUrlList(List<URL> pUrls) {

		List<URL> urlList = pUrls;
		if (urlList.size() == 0) {
			urlTextField.setEditable(true);
			urlTextField.setText(null);
			mUrlList = null;
		}
		if (urlList.size() == 1) {
			urlTextField.setEditable(true);
			urlTextField.setText(urlList.get(0).toExternalForm());
			mUrlList = null;
		}
		if (urlList.size() > 1) {
			urlTextField.setEditable(false);
			urlTextField.setText("< " + urlList.size() + " URL's >");
			mUrlList = urlList;
		}

	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		basicParametersPanel = new javax.swing.JPanel();
		nameLabel = new javax.swing.JLabel();
		nameTextField = new javax.swing.JTextField();
		urlLabel = new javax.swing.JLabel();
		urlTextField = new javax.swing.JTextField();
		moreUrlsButton = new javax.swing.JButton();
		savePathLabel = new javax.swing.JLabel();
		savePathTextField = new javax.swing.JTextField();
		savePathButton = new javax.swing.JButton();
		connectionSettingsPanel = new javax.swing.JPanel();
		workingThreadsLabel = new javax.swing.JLabel();
		workingThreadsTextField = new javax.swing.JTextField();
		maxConnectionsLabel = new javax.swing.JLabel();
		maxConnectionsTextField = new javax.swing.JTextField();
		maxRetriesLabel = new javax.swing.JLabel();
		maxRetriesTextField = new javax.swing.JTextField();
		proxySettingsPanel = new javax.swing.JPanel();
		enableProxyCheckBox = new javax.swing.JCheckBox();
		proxyServerLabel = new javax.swing.JLabel();
		proxyServerTextField = new javax.swing.JTextField();
		proxyPortLabel = new javax.swing.JLabel();
		proxyPortTextField = new javax.swing.JTextField();
		enableAuthenticationCheckBox = new javax.swing.JCheckBox();
		authenticationUserLabel = new javax.swing.JLabel();
		authenticationUserTextField = new javax.swing.JTextField();
		authenticationPasswordLabel = new javax.swing.JLabel();
		authenticationPasswordTextField = new javax.swing.JTextField();
		userAgentPanel = new javax.swing.JPanel();
		userAgentCheckBox = new javax.swing.JCheckBox();
		userAgentLabel = new javax.swing.JLabel();
		userAgentTextField = new javax.swing.JTextField();
		bandwidthLimitPanel = new javax.swing.JPanel();
		bandwidthLimitLabel = new javax.swing.JLabel();
		bandwidthLimitTextField = new javax.swing.JTextField();
		bandwidthLimitComboBox = new javax.swing.JComboBox();
		enableBandwidthLimitCheckBox = new javax.swing.JCheckBox();

		basicParametersPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Basic Parameters"));

		nameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		nameLabel.setText("Name:");

		urlLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		urlLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		urlLabel.setText("Start URL:");

		moreUrlsButton.setText("More");
		moreUrlsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				moreUrlsButtonActionPerformed(evt);
			}
		});

		savePathLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		savePathLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		savePathLabel.setText("Save path:");

		savePathButton.setText("Browse");
		savePathButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				savePathButtonActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout basicParametersPanelLayout = new org.jdesktop.layout.GroupLayout(
				basicParametersPanel);
		basicParametersPanel.setLayout(basicParametersPanelLayout);
		basicParametersPanelLayout
				.setHorizontalGroup(basicParametersPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								basicParametersPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												basicParametersPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																savePathLabel)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																urlLabel)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																nameLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												basicParametersPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																basicParametersPanelLayout
																		.createSequentialGroup()
																		.add(
																				nameTextField,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				174,
																				Short.MAX_VALUE)
																		.add(
																				204,
																				204,
																				204))
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																basicParametersPanelLayout
																		.createSequentialGroup()
																		.add(
																				basicParametersPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.TRAILING)
																						.add(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								urlTextField,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								292,
																								Short.MAX_VALUE)
																						.add(
																								savePathTextField,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								292,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				basicParametersPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.TRAILING,
																								false)
																						.add(
																								moreUrlsButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								savePathButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))))
										.addContainerGap()));

		basicParametersPanelLayout.linkSize(new java.awt.Component[] {
				nameLabel, savePathLabel, urlLabel },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		basicParametersPanelLayout
				.setVerticalGroup(basicParametersPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								basicParametersPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												basicParametersPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(nameLabel)
														.add(
																nameTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												basicParametersPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(urlLabel)
														.add(
																urlTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(moreUrlsButton))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												basicParametersPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(savePathLabel)
														.add(
																savePathTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(savePathButton))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		connectionSettingsPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Connection Settings"));

		workingThreadsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		workingThreadsLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		workingThreadsLabel.setText("Working Threads:");

		workingThreadsTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

		maxConnectionsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		maxConnectionsLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		maxConnectionsLabel.setText("Max. connections per server:");

		maxConnectionsTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

		maxRetriesLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		maxRetriesLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		maxRetriesLabel.setText("Max. retries before giving up:");

		maxRetriesTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

		org.jdesktop.layout.GroupLayout connectionSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(
				connectionSettingsPanel);
		connectionSettingsPanel.setLayout(connectionSettingsPanelLayout);
		connectionSettingsPanelLayout
				.setHorizontalGroup(connectionSettingsPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								connectionSettingsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												connectionSettingsPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING)
														.add(
																maxConnectionsLabel)
														.add(maxRetriesLabel)
														.add(
																workingThreadsLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												connectionSettingsPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																maxRetriesTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																46,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																workingThreadsTextField,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																46,
																Short.MAX_VALUE)
														.add(
																maxConnectionsTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));

		connectionSettingsPanelLayout.linkSize(new java.awt.Component[] {
				maxConnectionsLabel, workingThreadsLabel },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		connectionSettingsPanelLayout.linkSize(new java.awt.Component[] {
				maxConnectionsTextField, maxRetriesTextField,
				workingThreadsTextField },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		connectionSettingsPanelLayout
				.setVerticalGroup(connectionSettingsPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								connectionSettingsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												connectionSettingsPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																workingThreadsTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																workingThreadsLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												connectionSettingsPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																maxConnectionsLabel)
														.add(
																maxConnectionsTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												connectionSettingsPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(maxRetriesLabel)
														.add(
																maxRetriesTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		proxySettingsPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Proxy Settings"));
		proxySettingsPanel.setEnabled(false);

		enableProxyCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
		enableProxyCheckBox.setText("Enable Proxy");
		enableProxyCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		enableProxyCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		enableProxyCheckBox
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						enableProxyCheckBoxStateChanged(evt);
					}
				});

		proxyServerLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		proxyServerLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		proxyServerLabel.setText("Server:");
		proxyServerLabel.setEnabled(false);

		proxyServerTextField.setEnabled(false);

		proxyPortLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		proxyPortLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		proxyPortLabel.setText("Port:");
		proxyPortLabel.setEnabled(false);

		proxyPortTextField.setEnabled(false);

		enableAuthenticationCheckBox
				.setFont(new java.awt.Font("Dialog", 0, 12));
		enableAuthenticationCheckBox.setText("Enable proxy authentication");
		enableAuthenticationCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		enableAuthenticationCheckBox.setEnabled(false);
		enableAuthenticationCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		enableAuthenticationCheckBox
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						enableAuthenticationCheckBoxStateChanged(evt);
					}
				});

		authenticationUserLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		authenticationUserLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		authenticationUserLabel.setText("User:");
		authenticationUserLabel.setEnabled(false);

		authenticationUserTextField.setEnabled(false);

		authenticationPasswordLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		authenticationPasswordLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		authenticationPasswordLabel.setText("Password:");
		authenticationPasswordLabel.setEnabled(false);

		authenticationPasswordTextField.setEnabled(false);

		org.jdesktop.layout.GroupLayout proxySettingsPanelLayout = new org.jdesktop.layout.GroupLayout(
				proxySettingsPanel);
		proxySettingsPanel.setLayout(proxySettingsPanelLayout);
		proxySettingsPanelLayout
				.setHorizontalGroup(proxySettingsPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								proxySettingsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												proxySettingsPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																proxySettingsPanelLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.LEADING)
																		.add(
																				proxySettingsPanelLayout
																						.createSequentialGroup()
																						.add(
																								authenticationUserLabel)
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED)
																						.add(
																								authenticationUserTextField,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								130,
																								Short.MAX_VALUE)
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED)
																						.add(
																								authenticationPasswordLabel)
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED)
																						.add(
																								authenticationPasswordTextField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								117,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.add(
																				proxySettingsPanelLayout
																						.createSequentialGroup()
																						.add(
																								enableAuthenticationCheckBox)
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED,
																								218,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
														.add(
																proxySettingsPanelLayout
																		.createSequentialGroup()
																		.add(
																				proxySettingsPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.TRAILING,
																								false)
																						.add(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								enableProxyCheckBox,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								proxySettingsPanelLayout
																										.createSequentialGroup()
																										.add(
																												proxyServerLabel)
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(
																												proxyServerTextField,
																												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																												130,
																												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				proxyPortLabel)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				proxyPortTextField,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				122,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
										.add(67, 67, 67)));

		proxySettingsPanelLayout.linkSize(new java.awt.Component[] {
				authenticationPasswordTextField, authenticationUserTextField,
				proxyPortTextField, proxyServerTextField },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		proxySettingsPanelLayout.linkSize(new java.awt.Component[] {
				authenticationPasswordLabel, proxyPortLabel },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		proxySettingsPanelLayout.linkSize(new java.awt.Component[] {
				authenticationUserLabel, proxyServerLabel },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		proxySettingsPanelLayout
				.setVerticalGroup(proxySettingsPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								proxySettingsPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(enableProxyCheckBox)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												proxySettingsPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(proxyServerLabel)
														.add(
																proxyServerTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(proxyPortLabel)
														.add(
																proxyPortTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(enableAuthenticationCheckBox)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												proxySettingsPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																authenticationUserLabel)
														.add(
																authenticationPasswordLabel)
														.add(
																authenticationPasswordTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																authenticationUserTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		userAgentPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("User Agent"));

		userAgentCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
		userAgentCheckBox.setText("Override User Agent");
		userAgentCheckBox
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						userAgentCheckBoxStateChanged(evt);
					}
				});

		userAgentLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		userAgentLabel.setText("User Agent:");
		userAgentLabel.setEnabled(false);

		userAgentTextField.setText("Mozilla/5.0");
		userAgentTextField.setEnabled(false);

		org.jdesktop.layout.GroupLayout userAgentPanelLayout = new org.jdesktop.layout.GroupLayout(
				userAgentPanel);
		userAgentPanel.setLayout(userAgentPanelLayout);
		userAgentPanelLayout
				.setHorizontalGroup(userAgentPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								userAgentPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												userAgentPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(userAgentCheckBox)
														.add(
																userAgentPanelLayout
																		.createSequentialGroup()
																		.add(
																				userAgentLabel)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				userAgentTextField,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				371,
																				Short.MAX_VALUE)))
										.addContainerGap()));
		userAgentPanelLayout
				.setVerticalGroup(userAgentPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								userAgentPanelLayout
										.createSequentialGroup()
										.add(userAgentCheckBox)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												userAgentPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(userAgentLabel)
														.add(
																userAgentTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		bandwidthLimitPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Bandwidth Limit"));

		bandwidthLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		bandwidthLimitLabel
				.setText("<html>Set a bandwidth limitiation per download thread.</html>");

		bandwidthLimitTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		bandwidthLimitTextField.setEnabled(false);

		bandwidthLimitComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		bandwidthLimitComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "byte/s", "kbyte/s", "mbyte/s" }));
		bandwidthLimitComboBox.setSelectedIndex(1);
		bandwidthLimitComboBox.setEnabled(false);

		enableBandwidthLimitCheckBox
				.setFont(new java.awt.Font("Dialog", 0, 12));
		enableBandwidthLimitCheckBox.setText("Enable Bandwidth Limitation");
		enableBandwidthLimitCheckBox
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						enableBandwidthLimitCheckBoxStateChanged(evt);
					}
				});

		org.jdesktop.layout.GroupLayout bandwidthLimitPanelLayout = new org.jdesktop.layout.GroupLayout(
				bandwidthLimitPanel);
		bandwidthLimitPanel.setLayout(bandwidthLimitPanelLayout);
		bandwidthLimitPanelLayout
				.setHorizontalGroup(bandwidthLimitPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								bandwidthLimitPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												bandwidthLimitPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																bandwidthLimitLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																450,
																Short.MAX_VALUE)
														.add(
																enableBandwidthLimitCheckBox)
														.add(
																bandwidthLimitPanelLayout
																		.createSequentialGroup()
																		.add(
																				bandwidthLimitTextField,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				84,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				bandwidthLimitComboBox,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		bandwidthLimitPanelLayout
				.setVerticalGroup(bandwidthLimitPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								bandwidthLimitPanelLayout
										.createSequentialGroup()
										.add(bandwidthLimitLabel)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.UNRELATED)
										.add(enableBandwidthLimitCheckBox)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												bandwidthLimitPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																bandwidthLimitTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																20,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																bandwidthLimitComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																22,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				layout.createSequentialGroup().addContainerGap().add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.TRAILING).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								bandwidthLimitPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								userAgentPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								connectionSettingsPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								basicParametersPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(proxySettingsPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						basicParametersPanel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								connectionSettingsPanel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								proxySettingsPanel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								userAgentPanel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								bandwidthLimitPanel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents

	private void enableBandwidthLimitCheckBoxStateChanged(
			javax.swing.event.ChangeEvent evt) {

		boolean enabled = enableBandwidthLimitCheckBox.isSelected();

		bandwidthLimitTextField.setEnabled(enabled);
		bandwidthLimitComboBox.setEnabled(enabled);
	}

	private void userAgentCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {

		boolean enabled = userAgentCheckBox.isSelected();

		userAgentLabel.setEnabled(enabled);
		userAgentTextField.setEnabled(enabled);
	}

	//GEN-FIRST:event_moreUrlsButtonActionPerformed
	private void moreUrlsButtonActionPerformed(java.awt.event.ActionEvent evt) {

		EditUrlListDialog urlEditDialog = new EditUrlListDialog(
				(Dialog) getRootPane().getParent(), true);

		try {
			urlEditDialog.setUrlList(getUrlList());
		} catch (MalformedURLException e) {

			JOptionPane.showMessageDialog(this, "Malformed URL: "
					+ urlTextField.getText(), "Validation error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		urlEditDialog.setVisible(true);

		if (urlEditDialog.isOk()) {
			setUrlList(urlEditDialog.getUrlList());
		}

	}//GEN-LAST:event_moreUrlsButtonActionPerformed

	//GEN-FIRST:event_savePathButtonActionPerformed
	private void savePathButtonActionPerformed(java.awt.event.ActionEvent evt) {

		//open dialog
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fc.setSelectedFile(new File(this.savePathTextField.getText()));

		//Show load dialog; this method does not return until the dialog is closed
		int result = fc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			this.savePathTextField.setText(fc.getSelectedFile()
					.getAbsolutePath());
		}

	}//GEN-LAST:event_savePathButtonActionPerformed

	//GEN-FIRST:event_enableProxyCheckBoxStateChanged
	private void enableProxyCheckBoxStateChanged(
			javax.swing.event.ChangeEvent evt) {

		boolean enabled = enableProxyCheckBox.isSelected();

		proxyServerLabel.setEnabled(enabled);
		proxyServerTextField.setEnabled(enabled);
		proxyPortLabel.setEnabled(enabled);
		proxyPortTextField.setEnabled(enabled);

		if (!enabled) {
			enableAuthenticationCheckBox.setSelected(false);
		}
		enableAuthenticationCheckBox.setEnabled(enabled);

	}//GEN-LAST:event_enableProxyCheckBoxStateChanged

	//GEN-FIRST:event_enableAuthenticationCheckBoxStateChanged
	private void enableAuthenticationCheckBoxStateChanged(
			javax.swing.event.ChangeEvent evt) {

		boolean enabled = enableAuthenticationCheckBox.isSelected();

		authenticationUserLabel.setEnabled(enabled);
		authenticationUserTextField.setEnabled(enabled);
		authenticationPasswordLabel.setEnabled(enabled);
		authenticationPasswordTextField.setEnabled(enabled);

	}//GEN-LAST:event_enableAuthenticationCheckBoxStateChanged

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	protected javax.swing.JLabel authenticationPasswordLabel;
	protected javax.swing.JTextField authenticationPasswordTextField;
	protected javax.swing.JLabel authenticationUserLabel;
	protected javax.swing.JTextField authenticationUserTextField;
	protected javax.swing.JComboBox bandwidthLimitComboBox;
	protected javax.swing.JLabel bandwidthLimitLabel;
	protected javax.swing.JPanel bandwidthLimitPanel;
	protected javax.swing.JTextField bandwidthLimitTextField;
	protected javax.swing.JPanel basicParametersPanel;
	protected javax.swing.JPanel connectionSettingsPanel;
	protected javax.swing.JCheckBox enableAuthenticationCheckBox;
	protected javax.swing.JCheckBox enableBandwidthLimitCheckBox;
	protected javax.swing.JCheckBox enableProxyCheckBox;
	protected javax.swing.JLabel maxConnectionsLabel;
	protected javax.swing.JTextField maxConnectionsTextField;
	protected javax.swing.JLabel maxRetriesLabel;
	protected javax.swing.JTextField maxRetriesTextField;
	protected javax.swing.JButton moreUrlsButton;
	protected javax.swing.JLabel nameLabel;
	protected javax.swing.JTextField nameTextField;
	protected javax.swing.JLabel proxyPortLabel;
	protected javax.swing.JTextField proxyPortTextField;
	protected javax.swing.JLabel proxyServerLabel;
	protected javax.swing.JTextField proxyServerTextField;
	protected javax.swing.JPanel proxySettingsPanel;
	protected javax.swing.JButton savePathButton;
	protected javax.swing.JLabel savePathLabel;
	protected javax.swing.JTextField savePathTextField;
	protected javax.swing.JLabel urlLabel;
	protected javax.swing.JTextField urlTextField;
	protected javax.swing.JCheckBox userAgentCheckBox;
	protected javax.swing.JLabel userAgentLabel;
	protected javax.swing.JPanel userAgentPanel;
	protected javax.swing.JTextField userAgentTextField;
	protected javax.swing.JLabel workingThreadsLabel;
	protected javax.swing.JTextField workingThreadsTextField;
	// End of variables declaration//GEN-END:variables

}