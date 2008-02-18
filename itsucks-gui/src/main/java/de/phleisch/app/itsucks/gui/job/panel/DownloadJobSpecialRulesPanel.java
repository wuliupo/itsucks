/*
 * DownloadJobSpecialRulesPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.util.List;

import de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel;
import de.phleisch.app.itsucks.gui.common.panel.EditListPanel;
import de.phleisch.app.itsucks.gui.common.panel.EditListPanel.ListElement;
import de.phleisch.app.itsucks.gui.util.FieldValidator;
import de.phleisch.app.itsucks.gui.util.SwingUtils;

/**
 *
 * @author  __USER__
 */
public class DownloadJobSpecialRulesPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = -2550810599331718712L;

	/** Creates new form DownloadJobSpecialRulesPanel */
	public DownloadJobSpecialRulesPanel() {
		initComponents();
		this.httpStatusCodeBehaviourEditListPanel
				.setLogic(new EditListCallback());
		
		this.httpStatusCodeBehaviourEditListPanel.registerDataField(
				httpStatusCodeBehaviourHostnameTextField);
		this.httpStatusCodeBehaviourEditListPanel.registerDataField(
				httpStatusCodeBehaviourStatusCodeFromTextField);
		this.httpStatusCodeBehaviourEditListPanel.registerDataField(
				httpStatusCodeBehaviourStatusCodeToTextField);
		this.httpStatusCodeBehaviourEditListPanel.registerDataField(
				httpStatusCodeBehaviourActionComboBox);
		this.httpStatusCodeBehaviourEditListPanel.registerDataField(
				httpStatusCodeBehaviourWaitTextField);
		this.httpStatusCodeBehaviourEditListPanel.registerDataField(
				httpStatusCodeBehaviourQueueBehaviourComboBox);
		
		
	}

	public List<String> validateFields() {

		FieldValidator validator = new FieldValidator();

		if (fileSizeEnableCheckBox.isSelected()) {

			String regExp = "^([-]?[0-9]{1,})[ ]*(KB|kb|MB|mb|GB|gb|$)$";

			validator.assertRegExpResult(regExp, this.fileSizeMinField
					.getText().trim(),
					"Enter a valid value for the minimum file size.");

			validator.assertRegExpResult(regExp, this.fileSizeMaxField
					.getText().trim(),
					"Enter a valid value for the maximum file size.");
		}

		return validator.getErrors();
	}

	protected class HttpStatusCodeBehaviourListElement implements
			EditListPanel.ListElement {

	}

	protected class EditListCallback implements
			EditListCallbackPanel.EditListCallbackInterface {

		public ListElement createNewElement() {
			return new HttpStatusCodeBehaviourListElement();
		}

		public void emptyEditArea() {
			loadEditArea(new HttpStatusCodeBehaviourListElement());
		}

		public void enableEditArea(boolean pEnable) {
			SwingUtils.setContainerAndChildrenEnabled(
					httpStatusCodeBehaviourSubPanel, pEnable);
		}

		public void loadEditArea(ListElement pElement) {

			httpStatusCodeBehaviourHostnameTextField.setText(".*hostname.de");
			httpStatusCodeBehaviourStatusCodeFromTextField.setText("100");
			httpStatusCodeBehaviourStatusCodeToTextField.setText("200");
			httpStatusCodeBehaviourActionComboBox.setSelectedIndex(0);
			httpStatusCodeBehaviourWaitTextField.setText("3000");
			httpStatusCodeBehaviourQueueBehaviourComboBox.setSelectedIndex(1);

		}

		public void updateListElement() {
			
		}
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		fileSizePanel = new javax.swing.JPanel();
		fileSizeLabel = new javax.swing.JLabel();
		fileSizeEnableCheckBox = new javax.swing.JCheckBox();
		fileSizeMinLabel = new javax.swing.JLabel();
		fileSizeMinField = new javax.swing.JTextField();
		fileSizeMaxLabel = new javax.swing.JLabel();
		fileSizeMaxField = new javax.swing.JTextField();
		fileSizeUnitExplanationLabel = new javax.swing.JLabel();
		fileSizeNotKnownLabel = new javax.swing.JLabel();
		fileSizeNotKnownComboBox = new javax.swing.JComboBox();
		httpStatusCodeBehaviourPanel = new javax.swing.JPanel();
		httpStatusCodeBehaviourLabel = new javax.swing.JLabel();
		httpStatusCodeBehaviourCheckBox = new javax.swing.JCheckBox();
		httpStatusCodeBehaviourEditListPanel = new de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel();
		httpStatusCodeBehaviourSubPanel = new javax.swing.JPanel();
		httpStatusCodeBehaviourHostnameLabel = new javax.swing.JLabel();
		httpStatusCodeBehaviourHostnameTextField = new javax.swing.JTextField();
		httpStatusCodeBehaviourHostnameDescLabel = new javax.swing.JLabel();
		httpStatusCodeBehaviourStatusCodeLabel = new javax.swing.JLabel();
		httpStatusCodeBehaviourStatusCodeFromTextField = new javax.swing.JTextField();
		httpStatusCodeBehaviourStatusCodeToPanel = new javax.swing.JLabel();
		httpStatusCodeBehaviourStatusCodeToTextField = new javax.swing.JTextField();
		httpStatusCodeBehaviourActionLabel = new javax.swing.JLabel();
		httpStatusCodeBehaviourActionComboBox = new javax.swing.JComboBox();
		httpStatusCodeBehaviourWaitLabel = new javax.swing.JLabel();
		httpStatusCodeBehaviourWaitTextField = new javax.swing.JTextField();
		httpStatusCodeBehaviourWaitMsLabel = new javax.swing.JLabel();
		httpStatusCodeBehaviourQueueBehaviourLabel = new javax.swing.JLabel();
		httpStatusCodeBehaviourQueueBehaviourComboBox = new javax.swing.JComboBox();

		fileSizePanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("File Size Filter"));

		fileSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		fileSizeLabel
				.setText("<html>Set file size limits to prevent ItSucks from saving certain files on the disk. To get the file size, a connection has to be opened to the server. This can be slow when the server response time is bad. It is not guranteed that the server can send the file size (eg. dynamic sites).</html>");

		fileSizeEnableCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
		fileSizeEnableCheckBox.setText("Enable File Size Filter");
		fileSizeEnableCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		fileSizeEnableCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		fileSizeEnableCheckBox
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						fileSizeEnableCheckBoxStateChanged(evt);
					}
				});

		fileSizeMinLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		fileSizeMinLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		fileSizeMinLabel.setText("Min. file size:");
		fileSizeMinLabel.setEnabled(false);
		fileSizeMinLabel
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

		fileSizeMinField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		fileSizeMinField.setText("-1");
		fileSizeMinField.setEnabled(false);

		fileSizeMaxLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		fileSizeMaxLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		fileSizeMaxLabel.setText("Max. file size:");
		fileSizeMaxLabel.setEnabled(false);
		fileSizeMaxLabel
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

		fileSizeMaxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		fileSizeMaxField.setText("-1");
		fileSizeMaxField.setEnabled(false);

		fileSizeUnitExplanationLabel
				.setFont(new java.awt.Font("Dialog", 0, 12));
		fileSizeUnitExplanationLabel
				.setText("<html>To disable a limit, enter -1 as value. <br>Possible units are: KB (1024 byte), MB (1024 KB) and GB (1024 MB). If no unit is given, the value is interpreted as bytes. </html>");

		fileSizeNotKnownLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		fileSizeNotKnownLabel
				.setText("In case the file size is not sent by server:");
		fileSizeNotKnownLabel.setEnabled(false);

		fileSizeNotKnownComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		fileSizeNotKnownComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Save file", "Reject File" }));
		fileSizeNotKnownComboBox.setEnabled(false);

		org.jdesktop.layout.GroupLayout fileSizePanelLayout = new org.jdesktop.layout.GroupLayout(
				fileSizePanel);
		fileSizePanel.setLayout(fileSizePanelLayout);
		fileSizePanelLayout
				.setHorizontalGroup(fileSizePanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								fileSizePanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												fileSizePanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																fileSizeLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																579,
																Short.MAX_VALUE)
														.add(
																fileSizePanelLayout
																		.createSequentialGroup()
																		.add(
																				fileSizePanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								false)
																						.add(
																								fileSizeMinLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								fileSizeMaxLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				fileSizePanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								false)
																						.add(
																								fileSizeMinField)
																						.add(
																								fileSizeMaxField,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								96,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				fileSizeUnitExplanationLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				379,
																				Short.MAX_VALUE))
														.add(
																fileSizePanelLayout
																		.createSequentialGroup()
																		.add(
																				fileSizeEnableCheckBox)
																		.add(
																				62,
																				62,
																				62))
														.add(
																fileSizePanelLayout
																		.createSequentialGroup()
																		.add(
																				fileSizeNotKnownLabel)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				fileSizeNotKnownComboBox,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				110,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		fileSizePanelLayout
				.setVerticalGroup(fileSizePanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								fileSizePanelLayout
										.createSequentialGroup()
										.add(fileSizeLabel)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												fileSizePanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING)
														.add(
																fileSizePanelLayout
																		.createSequentialGroup()
																		.add(
																				fileSizeEnableCheckBox)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				fileSizePanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								fileSizeMinLabel)
																						.add(
																								fileSizeMinField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				fileSizePanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.TRAILING)
																						.add(
																								fileSizeMaxLabel)
																						.add(
																								fileSizeMaxField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
														.add(
																fileSizeUnitExplanationLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												fileSizePanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																fileSizeNotKnownLabel)
														.add(
																fileSizeNotKnownComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		httpStatusCodeBehaviourPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("HTTP Status Code Behaviour"));

		httpStatusCodeBehaviourLabel
				.setFont(new java.awt.Font("Dialog", 0, 12));
		httpStatusCodeBehaviourLabel
				.setText("<html>Define the behaviour of ItSucks for certain HTTP Status Codes. For instance if an server sends 403 (Forbidden) after to many downloads, a retry + waiting time can be defined here.</html>");

		httpStatusCodeBehaviourCheckBox.setFont(new java.awt.Font("Dialog", 0,
				12));
		httpStatusCodeBehaviourCheckBox
				.setText("Enable HTTP Status Code Filter");
		httpStatusCodeBehaviourCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		httpStatusCodeBehaviourCheckBox.setMargin(new java.awt.Insets(0, 0, 0,
				0));
		httpStatusCodeBehaviourCheckBox
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						httpStatusCodeBehaviourCheckBoxStateChanged(evt);
					}
				});

		httpStatusCodeBehaviourEditListPanel.setEnabled(false);

		httpStatusCodeBehaviourSubPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("HTTP Status Filter"));

		httpStatusCodeBehaviourHostnameLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		httpStatusCodeBehaviourHostnameLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		httpStatusCodeBehaviourHostnameLabel.setText("Hostname:");
		httpStatusCodeBehaviourHostnameLabel.setEnabled(false);

		httpStatusCodeBehaviourHostnameTextField.setEnabled(false);

		httpStatusCodeBehaviourHostnameDescLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		httpStatusCodeBehaviourHostnameDescLabel
				.setText("(regular expression, partial match)");
		httpStatusCodeBehaviourHostnameDescLabel.setEnabled(false);

		httpStatusCodeBehaviourStatusCodeLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		httpStatusCodeBehaviourStatusCodeLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		httpStatusCodeBehaviourStatusCodeLabel.setText("Status Code:");
		httpStatusCodeBehaviourStatusCodeLabel.setEnabled(false);

		httpStatusCodeBehaviourStatusCodeFromTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		httpStatusCodeBehaviourStatusCodeFromTextField.setEnabled(false);

		httpStatusCodeBehaviourStatusCodeToPanel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		httpStatusCodeBehaviourStatusCodeToPanel.setText("to");
		httpStatusCodeBehaviourStatusCodeToPanel.setEnabled(false);

		httpStatusCodeBehaviourStatusCodeToTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		httpStatusCodeBehaviourStatusCodeToTextField.setEnabled(false);

		httpStatusCodeBehaviourActionLabel.setFont(new java.awt.Font("Dialog",
				0, 12));
		httpStatusCodeBehaviourActionLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		httpStatusCodeBehaviourActionLabel.setText("Action:");
		httpStatusCodeBehaviourActionLabel.setEnabled(false);

		httpStatusCodeBehaviourActionComboBox.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		httpStatusCodeBehaviourActionComboBox
				.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
						"Retry", "Ok ", "Error" }));
		httpStatusCodeBehaviourActionComboBox.setEnabled(false);
		httpStatusCodeBehaviourActionComboBox
				.addItemListener(new java.awt.event.ItemListener() {
					public void itemStateChanged(java.awt.event.ItemEvent evt) {
						httpStatusCodeBehaviourActionComboBoxItemStateChanged(evt);
					}
				});

		httpStatusCodeBehaviourWaitLabel.setFont(new java.awt.Font("Dialog", 0,
				12));
		httpStatusCodeBehaviourWaitLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		httpStatusCodeBehaviourWaitLabel.setText("Wait between retry:");
		httpStatusCodeBehaviourWaitLabel.setEnabled(false);

		httpStatusCodeBehaviourWaitTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		httpStatusCodeBehaviourWaitTextField.setEnabled(false);

		httpStatusCodeBehaviourWaitMsLabel.setFont(new java.awt.Font("Dialog",
				0, 12));
		httpStatusCodeBehaviourWaitMsLabel.setText("ms");

		httpStatusCodeBehaviourQueueBehaviourLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		httpStatusCodeBehaviourQueueBehaviourLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		httpStatusCodeBehaviourQueueBehaviourLabel.setText("Queue behaviour:");
		httpStatusCodeBehaviourQueueBehaviourLabel.setEnabled(false);

		httpStatusCodeBehaviourQueueBehaviourComboBox
				.setFont(new java.awt.Font("Dialog", 0, 12));
		httpStatusCodeBehaviourQueueBehaviourComboBox
				.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
						"Directly wait for retry timeout",
						"Move Job back into queue" }));
		httpStatusCodeBehaviourQueueBehaviourComboBox.setEnabled(false);

		org.jdesktop.layout.GroupLayout httpStatusCodeBehaviourSubPanelLayout = new org.jdesktop.layout.GroupLayout(
				httpStatusCodeBehaviourSubPanel);
		httpStatusCodeBehaviourSubPanel
				.setLayout(httpStatusCodeBehaviourSubPanelLayout);
		httpStatusCodeBehaviourSubPanelLayout
				.setHorizontalGroup(httpStatusCodeBehaviourSubPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								httpStatusCodeBehaviourSubPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												httpStatusCodeBehaviourSubPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING,
																false)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																httpStatusCodeBehaviourActionLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																116,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																httpStatusCodeBehaviourStatusCodeLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																116,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																httpStatusCodeBehaviourWaitLabel,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																116,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																httpStatusCodeBehaviourQueueBehaviourLabel,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																116,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																httpStatusCodeBehaviourHostnameLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																116,
																Short.MAX_VALUE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												httpStatusCodeBehaviourSubPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																httpStatusCodeBehaviourQueueBehaviourComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																httpStatusCodeBehaviourSubPanelLayout
																		.createSequentialGroup()
																		.add(
																				httpStatusCodeBehaviourHostnameTextField,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				205,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				httpStatusCodeBehaviourHostnameDescLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				212,
																				Short.MAX_VALUE))
														.add(
																httpStatusCodeBehaviourSubPanelLayout
																		.createSequentialGroup()
																		.add(
																				httpStatusCodeBehaviourActionComboBox,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED,
																				371,
																				Short.MAX_VALUE))
														.add(
																httpStatusCodeBehaviourSubPanelLayout
																		.createSequentialGroup()
																		.add(
																				httpStatusCodeBehaviourSubPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								false)
																						.add(
																								httpStatusCodeBehaviourSubPanelLayout
																										.createSequentialGroup()
																										.add(
																												httpStatusCodeBehaviourStatusCodeFromTextField,
																												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																												45,
																												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED,
																												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE)
																										.add(
																												httpStatusCodeBehaviourStatusCodeToPanel))
																						.add(
																								httpStatusCodeBehaviourWaitTextField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								68,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				httpStatusCodeBehaviourSubPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								false)
																						.add(
																								httpStatusCodeBehaviourWaitMsLabel)
																						.add(
																								httpStatusCodeBehaviourStatusCodeToTextField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								45,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.add(
																				292,
																				292,
																				292)))
										.add(0, 0, 0)));
		httpStatusCodeBehaviourSubPanelLayout
				.setVerticalGroup(httpStatusCodeBehaviourSubPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								httpStatusCodeBehaviourSubPanelLayout
										.createSequentialGroup()
										.add(
												httpStatusCodeBehaviourSubPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																httpStatusCodeBehaviourHostnameTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																httpStatusCodeBehaviourHostnameDescLabel)
														.add(
																httpStatusCodeBehaviourHostnameLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												httpStatusCodeBehaviourSubPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																httpStatusCodeBehaviourStatusCodeLabel)
														.add(
																httpStatusCodeBehaviourStatusCodeFromTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																httpStatusCodeBehaviourStatusCodeToPanel)
														.add(
																httpStatusCodeBehaviourStatusCodeToTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.add(10, 10, 10)
										.add(
												httpStatusCodeBehaviourSubPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																httpStatusCodeBehaviourActionComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																24,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																httpStatusCodeBehaviourActionLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												httpStatusCodeBehaviourSubPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																httpStatusCodeBehaviourWaitMsLabel)
														.add(
																httpStatusCodeBehaviourWaitTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																httpStatusCodeBehaviourWaitLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												httpStatusCodeBehaviourSubPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																httpStatusCodeBehaviourQueueBehaviourComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																httpStatusCodeBehaviourQueueBehaviourLabel))
										.addContainerGap()));

		org.jdesktop.layout.GroupLayout httpStatusCodeBehaviourPanelLayout = new org.jdesktop.layout.GroupLayout(
				httpStatusCodeBehaviourPanel);
		httpStatusCodeBehaviourPanel
				.setLayout(httpStatusCodeBehaviourPanelLayout);
		httpStatusCodeBehaviourPanelLayout
				.setHorizontalGroup(httpStatusCodeBehaviourPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								httpStatusCodeBehaviourPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												httpStatusCodeBehaviourPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																httpStatusCodeBehaviourLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																579,
																Short.MAX_VALUE)
														.add(
																httpStatusCodeBehaviourCheckBox)
														.add(
																httpStatusCodeBehaviourEditListPanel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																579,
																Short.MAX_VALUE)
														.add(
																httpStatusCodeBehaviourSubPanel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));
		httpStatusCodeBehaviourPanelLayout
				.setVerticalGroup(httpStatusCodeBehaviourPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								httpStatusCodeBehaviourPanelLayout
										.createSequentialGroup()
										.add(
												httpStatusCodeBehaviourLabel,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												43,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(httpStatusCodeBehaviourCheckBox)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.UNRELATED)
										.add(
												httpStatusCodeBehaviourEditListPanel,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												httpStatusCodeBehaviourSubPanel,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));

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
								httpStatusCodeBehaviourPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).add(
								org.jdesktop.layout.GroupLayout.LEADING,
								fileSizePanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						fileSizePanel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								httpStatusCodeBehaviourPanel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents

	private void httpStatusCodeBehaviourActionComboBoxItemStateChanged(
			java.awt.event.ItemEvent evt) {

		boolean enabled = false;

		if (this.httpStatusCodeBehaviourActionComboBox.getSelectedIndex() == 0
				&& this.httpStatusCodeBehaviourActionComboBox.isEnabled()) {

			enabled = true;
		}

		this.httpStatusCodeBehaviourWaitLabel.setEnabled(enabled);
		this.httpStatusCodeBehaviourWaitTextField.setEnabled(enabled);
		this.httpStatusCodeBehaviourWaitMsLabel.setEnabled(enabled);

		this.httpStatusCodeBehaviourQueueBehaviourLabel.setEnabled(enabled);
		this.httpStatusCodeBehaviourQueueBehaviourComboBox.setEnabled(enabled);
	}

	private void httpStatusCodeBehaviourCheckBoxStateChanged(
			javax.swing.event.ChangeEvent evt) {

		boolean enabled = this.httpStatusCodeBehaviourCheckBox.isSelected();

		this.httpStatusCodeBehaviourEditListPanel.setEnabled(enabled);
	}

	//GEN-FIRST:event_fileSizeEnableCheckBoxStateChanged
	private void fileSizeEnableCheckBoxStateChanged(
			javax.swing.event.ChangeEvent evt) {

		boolean enabled = fileSizeEnableCheckBox.isSelected();

		fileSizeMinLabel.setEnabled(enabled);
		fileSizeMinField.setEnabled(enabled);
		fileSizeMaxLabel.setEnabled(enabled);
		fileSizeMaxField.setEnabled(enabled);
		fileSizeNotKnownLabel.setEnabled(enabled);
		fileSizeNotKnownComboBox.setEnabled(enabled);

	}//GEN-LAST:event_fileSizeEnableCheckBoxStateChanged

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	protected javax.swing.JCheckBox fileSizeEnableCheckBox;
	protected javax.swing.JLabel fileSizeLabel;
	protected javax.swing.JTextField fileSizeMaxField;
	protected javax.swing.JLabel fileSizeMaxLabel;
	protected javax.swing.JTextField fileSizeMinField;
	protected javax.swing.JLabel fileSizeMinLabel;
	protected javax.swing.JComboBox fileSizeNotKnownComboBox;
	protected javax.swing.JLabel fileSizeNotKnownLabel;
	protected javax.swing.JPanel fileSizePanel;
	protected javax.swing.JLabel fileSizeUnitExplanationLabel;
	protected javax.swing.JComboBox httpStatusCodeBehaviourActionComboBox;
	protected javax.swing.JLabel httpStatusCodeBehaviourActionLabel;
	protected javax.swing.JCheckBox httpStatusCodeBehaviourCheckBox;
	protected de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel httpStatusCodeBehaviourEditListPanel;
	protected javax.swing.JLabel httpStatusCodeBehaviourHostnameDescLabel;
	protected javax.swing.JLabel httpStatusCodeBehaviourHostnameLabel;
	protected javax.swing.JTextField httpStatusCodeBehaviourHostnameTextField;
	protected javax.swing.JLabel httpStatusCodeBehaviourLabel;
	protected javax.swing.JPanel httpStatusCodeBehaviourPanel;
	protected javax.swing.JComboBox httpStatusCodeBehaviourQueueBehaviourComboBox;
	protected javax.swing.JLabel httpStatusCodeBehaviourQueueBehaviourLabel;
	protected javax.swing.JTextField httpStatusCodeBehaviourStatusCodeFromTextField;
	protected javax.swing.JLabel httpStatusCodeBehaviourStatusCodeLabel;
	protected javax.swing.JLabel httpStatusCodeBehaviourStatusCodeToPanel;
	protected javax.swing.JTextField httpStatusCodeBehaviourStatusCodeToTextField;
	protected javax.swing.JPanel httpStatusCodeBehaviourSubPanel;
	protected javax.swing.JLabel httpStatusCodeBehaviourWaitLabel;
	protected javax.swing.JLabel httpStatusCodeBehaviourWaitMsLabel;
	protected javax.swing.JTextField httpStatusCodeBehaviourWaitTextField;
	// End of variables declaration//GEN-END:variables

}