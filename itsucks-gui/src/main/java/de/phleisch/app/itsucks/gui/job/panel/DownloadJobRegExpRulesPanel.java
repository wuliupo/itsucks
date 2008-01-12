/*
 * DownloadJobAdvancedRulesPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.awt.Dialog;
import java.io.Serializable;

import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter.RegExpFilterAction;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.gui.common.EditRegularExpressionDialog;
import de.phleisch.app.itsucks.gui.util.ExtendedListModel;
import de.phleisch.app.itsucks.gui.util.SwingUtils;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

/**
 *
 * @author  __USER__
 */
public class DownloadJobRegExpRulesPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 9062521650244140654L;

	protected ExtendedListModel regExpFilterListModel;
	protected RegExpFilterRule mRuleInEditMode;

	/** Creates new form DownloadJobAdvancedRulesPanel */
	public DownloadJobRegExpRulesPanel() {
		regExpFilterListModel = new ExtendedListModel();
		mRuleInEditMode = null;

		initComponents();

		//add elements to combo boxes
		for (ComboBoxEntry entry : mStatusChangeList) {
			editRegExpFilterMatchStatusChangeComboBox.addItem(entry);
			editRegExpFilterNoMatchStatusChangeComboBox.addItem(entry);
		}

		//disable advanced edit filter panel
		SwingUtils.setContainerAndChildrenEnabled(editRegExpFilterPanel, false);
	}

	protected class RegExpFilterRuleListElement {

		private RegExpFilterRule mRule;

		public RegExpFilterRuleListElement(RegExpFilterRule pRule) {
			mRule = pRule;
		}

		public RegExpFilterRule getRule() {
			return mRule;
		}

		@Override
		public String toString() {
			return toHtmlString();
		}

		/**
		 * Returns a string containing all information about the filter.
		 * HTML format.
		 * 
		 * @return
		 */
		public String toHtmlString() {
			return "<html>"
					+ (mRule.getName() != null ? "<b>Name: '" + mRule.getName()
							+ "'</b><br>\n" : "") + "Pattern: '"
					+ mRule.getPattern() + "' <br>\n" + "Match: "
					+ mRule.getMatchAction() + "<br>\nNo Match: "
					+ mRule.getNoMatchAction() + "</html>";
		}
	}

	protected static class ComboBoxEntry implements Serializable {

		private static final long serialVersionUID = 7129073467970212773L;

		private String mName;
		private Object mValue;

		public ComboBoxEntry(String pName, Object pValue) {
			mName = pName;
			mValue = pValue;
		}

		public String getName() {
			return mName;
		}

		public Object getValue() {
			return mValue;
		}

		@Override
		public String toString() {
			return mName;
		}
	}

	protected ComboBoxEntry mStatusChangeList[] = new ComboBoxEntry[] {
			new ComboBoxEntry("No change", null),
			new ComboBoxEntry("Accept", Boolean.TRUE),
			new ComboBoxEntry("Reject", Boolean.FALSE), };

	private void updateAdvancedFilter() {

		Object[] selectedValues = regExpFilterList.getSelectedValues();
		if (mRuleInEditMode == null || selectedValues == null
				|| selectedValues.length != 1) {
			return;
		}

		RegExpFilterRule rule = mRuleInEditMode;

		rule.setName(editRegExpFilterNameField.getText());
		rule.setDescription(editRegExpFilterDescriptionTextArea.getText());
		rule.setPattern(editRegExpFilterRegExpTextArea.getText());

		//match Action
		{
			RegExpFilterAction matchAction = rule.getMatchAction();

			ComboBoxEntry selectedItem = (ComboBoxEntry) editRegExpFilterMatchStatusChangeComboBox
					.getSelectedItem();
			matchAction.setAccept((Boolean) selectedItem.getValue());

			try {
				matchAction.setPriorityChange(Integer
						.parseInt(editRegExpFilterMatchPrioChangeTextField
								.getText()));
			} catch (NumberFormatException ex) {
				editRegExpFilterMatchPrioChangeTextField.setText(String
						.valueOf(matchAction.getPriorityChange()));
			}

			matchAction
					.addJobParameter(new JobParameter(
							UrlDownloadJob.JOB_PARAMETER_SKIP_DOWNLOADED_FILE,
							Boolean
									.valueOf(editRegExpFilterMatchAssumeFinishedFileCheckBox
											.isSelected())));
		}

		//no match Action
		{
			RegExpFilterAction noMatchAction = rule.getNoMatchAction();

			ComboBoxEntry selectedItem = (ComboBoxEntry) editRegExpFilterNoMatchStatusChangeComboBox
					.getSelectedItem();
			noMatchAction.setAccept((Boolean) selectedItem.getValue());

			try {
				noMatchAction.setPriorityChange(Integer
						.parseInt(editRegExpFilterNoMatchPrioChangeTextField
								.getText()));
			} catch (NumberFormatException ex) {
				editRegExpFilterNoMatchPrioChangeTextField.setText(String
						.valueOf(noMatchAction.getPriorityChange()));
			}

			noMatchAction
					.addJobParameter(new JobParameter(
							UrlDownloadJob.JOB_PARAMETER_SKIP_DOWNLOADED_FILE,
							Boolean
									.valueOf(editRegExpFilterNoMatchAssumeFinishedFileCheckBox
											.isSelected())));
		}

		//notify list
		int selectionIndex = regExpFilterList.getSelectedIndex();
		regExpFilterListModel.fireContentsChanged(selectionIndex,
				selectionIndex);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		regExpFilterChainLabel = new javax.swing.JLabel();
		regExpFilterChainExplanationLabel = new javax.swing.JLabel();
		regExpFilterPane = new javax.swing.JScrollPane();
		regExpFilterList = new javax.swing.JList();
		regExpFilterAddButton = new javax.swing.JButton();
		regExpFilterRemoveButton = new javax.swing.JButton();
		regExpFilterMoveUpButton = new javax.swing.JButton();
		regExpFilterMoveDownButton = new javax.swing.JButton();
		editRegExpFilterPanel = new javax.swing.JPanel();
		editRegExpFilterNameLabel = new javax.swing.JLabel();
		editRegExpFilterNameField = new javax.swing.JTextField();
		editRegExpFilterDescriptionLabel = new javax.swing.JLabel();
		editRegExpFilterDescriptionPane = new javax.swing.JScrollPane();
		editRegExpFilterDescriptionTextArea = new javax.swing.JTextArea();
		editRegExpFilterRegExpLabel = new javax.swing.JLabel();
		editRegExpFilterRegExpPane = new javax.swing.JScrollPane();
		editRegExpFilterRegExpTextArea = new javax.swing.JTextArea();
		editRegExpFilterOpenRegExpEditorButton = new javax.swing.JButton();
		editRegExpFilterMatchPanel = new javax.swing.JPanel();
		editRegExpFilterMatchStatusChangeLabel = new javax.swing.JLabel();
		editRegExpFilterMatchStatusChangeComboBox = new javax.swing.JComboBox();
		editRegExpFilterMatchPrioChangeLabel = new javax.swing.JLabel();
		editRegExpFilterMatchPrioChangeTextField = new javax.swing.JTextField();
		editRegExpFilterMatchAssumeFinishedFileCheckBox = new javax.swing.JCheckBox();
		editRegExpFilterNoMatchPanel = new javax.swing.JPanel();
		editRegExpFilterNoMatchStatusChangeLabel = new javax.swing.JLabel();
		editRegExpFilterNoMatchStatusChangeComboBox = new javax.swing.JComboBox();
		editRegExpFilterNoMatchPrioChangeLabel = new javax.swing.JLabel();
		editRegExpFilterNoMatchPrioChangeTextField = new javax.swing.JTextField();
		editRegExpFilterNoMatchAssumeFinishedFileCheckBox = new javax.swing.JCheckBox();

		regExpFilterChainLabel.setText("Regular Expression Filter Chain");

		regExpFilterChainExplanationLabel.setFont(new java.awt.Font("Dialog",
				0, 12));
		regExpFilterChainExplanationLabel
				.setText("<html>Every found URL will be filtered through the chain. Filtering starts with the first entry in the list and ends with the last entry.</html>");

		regExpFilterList.setFont(new java.awt.Font("Dialog", 0, 12));
		regExpFilterList.setModel(regExpFilterListModel);
		regExpFilterList
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						regExpFilterListValueChanged(evt);
					}
				});

		regExpFilterPane.setViewportView(regExpFilterList);

		regExpFilterAddButton.setFont(new java.awt.Font("Dialog", 0, 12));
		regExpFilterAddButton.setText("+");
		regExpFilterAddButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
		regExpFilterAddButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						regExpFilterAddButtonActionPerformed(evt);
					}
				});

		regExpFilterRemoveButton.setFont(new java.awt.Font("Dialog", 0, 12));
		regExpFilterRemoveButton.setText("-");
		regExpFilterRemoveButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
		regExpFilterRemoveButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						regExpFilterRemoveButtonActionPerformed(evt);
					}
				});

		regExpFilterMoveUpButton.setFont(new java.awt.Font("Dialog", 0, 12));
		regExpFilterMoveUpButton.setText("up");
		regExpFilterMoveUpButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
		regExpFilterMoveUpButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						regExpFilterMoveUpButtonActionPerformed(evt);
					}
				});

		regExpFilterMoveDownButton.setFont(new java.awt.Font("Dialog", 0, 12));
		regExpFilterMoveDownButton.setText("down");
		regExpFilterMoveDownButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
		regExpFilterMoveDownButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						regExpFilterMoveDownButtonActionPerformed(evt);
					}
				});

		editRegExpFilterPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Regular Expression Filter"));
		editRegExpFilterNameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		editRegExpFilterNameLabel.setText("Filter Name:");

		editRegExpFilterNameField
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						editRegExpFilterNameFieldFocusLost(evt);
					}
				});

		editRegExpFilterDescriptionLabel.setFont(new java.awt.Font("Dialog", 0,
				12));
		editRegExpFilterDescriptionLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		editRegExpFilterDescriptionLabel.setText("Filter Description:");

		editRegExpFilterDescriptionTextArea.setColumns(20);
		editRegExpFilterDescriptionTextArea.setLineWrap(true);
		editRegExpFilterDescriptionTextArea.setRows(2);
		editRegExpFilterDescriptionTextArea
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						editRegExpFilterDescriptionTextAreaFocusLost(evt);
					}
				});

		editRegExpFilterDescriptionPane
				.setViewportView(editRegExpFilterDescriptionTextArea);

		editRegExpFilterRegExpLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		editRegExpFilterRegExpLabel
				.setText("Regular Expression, partial match:");

		editRegExpFilterRegExpTextArea.setColumns(20);
		editRegExpFilterRegExpTextArea.setLineWrap(true);
		editRegExpFilterRegExpTextArea.setRows(3);
		editRegExpFilterRegExpTextArea
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						editRegExpFilterRegExpTextAreaFocusLost(evt);
					}
				});

		editRegExpFilterRegExpPane
				.setViewportView(editRegExpFilterRegExpTextArea);

		editRegExpFilterOpenRegExpEditorButton.setFont(new java.awt.Font(
				"Dialog", 0, 10));
		editRegExpFilterOpenRegExpEditorButton
				.setText("Open Regular Expression Editor");
		editRegExpFilterOpenRegExpEditorButton.setMargin(new java.awt.Insets(2,
				4, 2, 4));
		editRegExpFilterOpenRegExpEditorButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						editRegExpFilterOpenRegExpEditorButtonActionPerformed(evt);
					}
				});

		editRegExpFilterMatchPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Action in case of match"));
		editRegExpFilterMatchStatusChangeLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editRegExpFilterMatchStatusChangeLabel.setText("Change status:");

		editRegExpFilterMatchStatusChangeComboBox.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editRegExpFilterMatchStatusChangeComboBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						editRegExpFilterMatchStatusChangeComboBoxActionPerformed(evt);
					}
				});

		editRegExpFilterMatchPrioChangeLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editRegExpFilterMatchPrioChangeLabel.setText("Priority change:");

		editRegExpFilterMatchPrioChangeTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		editRegExpFilterMatchPrioChangeTextField
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						editRegExpFilterMatchPrioChangeTextFieldFocusLost(evt);
					}
				});

		editRegExpFilterMatchAssumeFinishedFileCheckBox
				.setFont(new java.awt.Font("Dialog", 0, 12));
		editRegExpFilterMatchAssumeFinishedFileCheckBox
				.setText("<html>Assume file is already downloaded completely when found on disk.</html>");
		editRegExpFilterMatchAssumeFinishedFileCheckBox
				.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0,
						0));
		editRegExpFilterMatchAssumeFinishedFileCheckBox
				.setMargin(new java.awt.Insets(0, 0, 0, 0));
		editRegExpFilterMatchAssumeFinishedFileCheckBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						editRegExpFilterMatchAssumeFinishedFileCheckBoxActionPerformed(evt);
					}
				});

		org.jdesktop.layout.GroupLayout editRegExpFilterMatchPanelLayout = new org.jdesktop.layout.GroupLayout(
				editRegExpFilterMatchPanel);
		editRegExpFilterMatchPanel.setLayout(editRegExpFilterMatchPanelLayout);
		editRegExpFilterMatchPanelLayout
				.setHorizontalGroup(editRegExpFilterMatchPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editRegExpFilterMatchPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editRegExpFilterMatchPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																editRegExpFilterMatchAssumeFinishedFileCheckBox,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																265,
																Short.MAX_VALUE)
														.add(
																editRegExpFilterMatchPanelLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.TRAILING,
																				false)
																		.add(
																				org.jdesktop.layout.GroupLayout.LEADING,
																				editRegExpFilterMatchPanelLayout
																						.createSequentialGroup()
																						.add(
																								editRegExpFilterMatchStatusChangeLabel)
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED)
																						.add(
																								editRegExpFilterMatchStatusChangeComboBox,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								128,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.add(
																				org.jdesktop.layout.GroupLayout.LEADING,
																				editRegExpFilterMatchPanelLayout
																						.createSequentialGroup()
																						.add(
																								editRegExpFilterMatchPrioChangeLabel)
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED)
																						.add(
																								editRegExpFilterMatchPrioChangeTextField))))
										.addContainerGap()));

		editRegExpFilterMatchPanelLayout.linkSize(new java.awt.Component[] {
				editRegExpFilterMatchPrioChangeLabel,
				editRegExpFilterMatchStatusChangeLabel },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		editRegExpFilterMatchPanelLayout
				.setVerticalGroup(editRegExpFilterMatchPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editRegExpFilterMatchPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editRegExpFilterMatchPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																editRegExpFilterMatchStatusChangeLabel)
														.add(
																editRegExpFilterMatchStatusChangeComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editRegExpFilterMatchPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																editRegExpFilterMatchPrioChangeLabel)
														.add(
																editRegExpFilterMatchPrioChangeTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editRegExpFilterMatchAssumeFinishedFileCheckBox)
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		editRegExpFilterNoMatchPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Action in case of no match"));
		editRegExpFilterNoMatchStatusChangeLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editRegExpFilterNoMatchStatusChangeLabel.setText("Change status:");

		editRegExpFilterNoMatchStatusChangeComboBox.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editRegExpFilterNoMatchStatusChangeComboBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						editRegExpFilterNoMatchStatusChangeComboBoxActionPerformed(evt);
					}
				});

		editRegExpFilterNoMatchPrioChangeLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editRegExpFilterNoMatchPrioChangeLabel.setText("Priority change:");

		editRegExpFilterNoMatchPrioChangeTextField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		editRegExpFilterNoMatchPrioChangeTextField
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						editRegExpFilterNoMatchPrioChangeTextFieldFocusLost(evt);
					}
				});

		editRegExpFilterNoMatchAssumeFinishedFileCheckBox
				.setFont(new java.awt.Font("Dialog", 0, 12));
		editRegExpFilterNoMatchAssumeFinishedFileCheckBox
				.setText("<html>Assume file is already downloaded completely when found on disk.</html>");
		editRegExpFilterNoMatchAssumeFinishedFileCheckBox
				.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0,
						0));
		editRegExpFilterNoMatchAssumeFinishedFileCheckBox
				.setMargin(new java.awt.Insets(0, 0, 0, 0));
		editRegExpFilterNoMatchAssumeFinishedFileCheckBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						editRegExpFilterNoMatchAssumeFinishedFileCheckBoxActionPerformed(evt);
					}
				});

		org.jdesktop.layout.GroupLayout editRegExpFilterNoMatchPanelLayout = new org.jdesktop.layout.GroupLayout(
				editRegExpFilterNoMatchPanel);
		editRegExpFilterNoMatchPanel
				.setLayout(editRegExpFilterNoMatchPanelLayout);
		editRegExpFilterNoMatchPanelLayout
				.setHorizontalGroup(editRegExpFilterNoMatchPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editRegExpFilterNoMatchPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editRegExpFilterNoMatchPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																editRegExpFilterNoMatchAssumeFinishedFileCheckBox,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																277,
																Short.MAX_VALUE)
														.add(
																editRegExpFilterNoMatchPanelLayout
																		.createSequentialGroup()
																		.add(
																				editRegExpFilterNoMatchPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING)
																						.add(
																								editRegExpFilterNoMatchPrioChangeLabel)
																						.add(
																								editRegExpFilterNoMatchStatusChangeLabel))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editRegExpFilterNoMatchPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								false)
																						.add(
																								editRegExpFilterNoMatchPrioChangeTextField)
																						.add(
																								editRegExpFilterNoMatchStatusChangeComboBox,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								128,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
										.addContainerGap()));
		editRegExpFilterNoMatchPanelLayout
				.setVerticalGroup(editRegExpFilterNoMatchPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editRegExpFilterNoMatchPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editRegExpFilterNoMatchPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																editRegExpFilterNoMatchStatusChangeComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																editRegExpFilterNoMatchStatusChangeLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editRegExpFilterNoMatchPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																editRegExpFilterNoMatchPrioChangeLabel)
														.add(
																editRegExpFilterNoMatchPrioChangeTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editRegExpFilterNoMatchAssumeFinishedFileCheckBox)
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout editRegExpFilterPanelLayout = new org.jdesktop.layout.GroupLayout(
				editRegExpFilterPanel);
		editRegExpFilterPanel.setLayout(editRegExpFilterPanelLayout);
		editRegExpFilterPanelLayout
				.setHorizontalGroup(editRegExpFilterPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editRegExpFilterPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editRegExpFilterPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																editRegExpFilterRegExpPane,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																616,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																editRegExpFilterPanelLayout
																		.createSequentialGroup()
																		.add(
																				editRegExpFilterMatchPanel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editRegExpFilterNoMatchPanel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
														.add(
																editRegExpFilterPanelLayout
																		.createSequentialGroup()
																		.add(
																				editRegExpFilterNameLabel)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editRegExpFilterNameField,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				138,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editRegExpFilterDescriptionLabel,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				111,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editRegExpFilterDescriptionPane,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				261,
																				Short.MAX_VALUE))
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																editRegExpFilterPanelLayout
																		.createSequentialGroup()
																		.add(
																				editRegExpFilterRegExpLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				441,
																				Short.MAX_VALUE)
																		.add(
																				175,
																				175,
																				175))
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																editRegExpFilterOpenRegExpEditorButton))
										.addContainerGap()));
		editRegExpFilterPanelLayout
				.setVerticalGroup(editRegExpFilterPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editRegExpFilterPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editRegExpFilterPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																editRegExpFilterPanelLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.BASELINE)
																		.add(
																				editRegExpFilterNameLabel)
																		.add(
																				editRegExpFilterNameField,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.add(
																				editRegExpFilterDescriptionLabel))
														.add(
																editRegExpFilterDescriptionPane,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																41,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(editRegExpFilterRegExpLabel)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editRegExpFilterRegExpPane,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												58, Short.MAX_VALUE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editRegExpFilterOpenRegExpEditorButton)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editRegExpFilterPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING,
																false)
														.add(
																editRegExpFilterMatchPanel,
																0, 131,
																Short.MAX_VALUE)
														.add(
																editRegExpFilterNoMatchPanel,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																131,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																editRegExpFilterPanel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				regExpFilterPane,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				599,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING)
																						.add(
																								regExpFilterRemoveButton)
																						.add(
																								regExpFilterAddButton)
																						.add(
																								regExpFilterMoveUpButton)
																						.add(
																								regExpFilterMoveDownButton)))
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																regExpFilterChainExplanationLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																650,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																regExpFilterChainLabel))
										.addContainerGap()));

		layout.linkSize(new java.awt.Component[] { regExpFilterAddButton,
				regExpFilterRemoveButton },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		layout.linkSize(new java.awt.Component[] { regExpFilterMoveDownButton,
				regExpFilterMoveUpButton },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(regExpFilterChainLabel)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												regExpFilterChainExplanationLabel,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												35,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				regExpFilterAddButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				regExpFilterRemoveButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				regExpFilterMoveUpButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				regExpFilterMoveDownButton))
														.add(regExpFilterPane,
																0, 0,
																Short.MAX_VALUE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editRegExpFilterPanel,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												341, Short.MAX_VALUE)
										.addContainerGap()));

		layout.linkSize(new java.awt.Component[] { regExpFilterAddButton,
				regExpFilterRemoveButton },
				org.jdesktop.layout.GroupLayout.VERTICAL);

		layout.linkSize(new java.awt.Component[] { regExpFilterMoveDownButton,
				regExpFilterMoveUpButton },
				org.jdesktop.layout.GroupLayout.VERTICAL);

	}// </editor-fold>//GEN-END:initComponents

	//GEN-FIRST:event_editRegExpFilterOpenRegExpEditorButtonActionPerformed
	private void editRegExpFilterOpenRegExpEditorButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		EditRegularExpressionDialog dialog = new EditRegularExpressionDialog(
				(Dialog) getRootPane().getParent(), true);

		dialog.setRegularExpression(this.editRegExpFilterRegExpTextArea
				.getText());
		dialog.setVisible(true);

		if (dialog.isOk()) {
			//copy the value from the editor
			this.editRegExpFilterRegExpTextArea.setText(dialog
					.getRegularExpression());

			updateAdvancedFilter();
		}
	}//GEN-LAST:event_editRegExpFilterOpenRegExpEditorButtonActionPerformed

	//GEN-FIRST:event_editRegExpFilterNoMatchAssumeFinishedFileCheckBoxActionPerformed
	private void editRegExpFilterNoMatchAssumeFinishedFileCheckBoxActionPerformed(
			java.awt.event.ActionEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterNoMatchAssumeFinishedFileCheckBoxActionPerformed

	//GEN-FIRST:event_editRegExpFilterNoMatchPrioChangeTextFieldFocusLost
	private void editRegExpFilterNoMatchPrioChangeTextFieldFocusLost(
			java.awt.event.FocusEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterNoMatchPrioChangeTextFieldFocusLost

	//GEN-FIRST:event_editRegExpFilterNoMatchStatusChangeComboBoxActionPerformed
	private void editRegExpFilterNoMatchStatusChangeComboBoxActionPerformed(
			java.awt.event.ActionEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterNoMatchStatusChangeComboBoxActionPerformed

	//GEN-FIRST:event_editRegExpFilterMatchAssumeFinishedFileCheckBoxActionPerformed
	private void editRegExpFilterMatchAssumeFinishedFileCheckBoxActionPerformed(
			java.awt.event.ActionEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterMatchAssumeFinishedFileCheckBoxActionPerformed

	//GEN-FIRST:event_editRegExpFilterMatchPrioChangeTextFieldFocusLost
	private void editRegExpFilterMatchPrioChangeTextFieldFocusLost(
			java.awt.event.FocusEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterMatchPrioChangeTextFieldFocusLost

	//GEN-FIRST:event_editRegExpFilterMatchStatusChangeComboBoxActionPerformed
	private void editRegExpFilterMatchStatusChangeComboBoxActionPerformed(
			java.awt.event.ActionEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterMatchStatusChangeComboBoxActionPerformed

	//GEN-FIRST:event_editRegExpFilterRegExpTextAreaFocusLost
	private void editRegExpFilterRegExpTextAreaFocusLost(
			java.awt.event.FocusEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterRegExpTextAreaFocusLost

	//GEN-FIRST:event_editRegExpFilterDescriptionTextAreaFocusLost
	private void editRegExpFilterDescriptionTextAreaFocusLost(
			java.awt.event.FocusEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterDescriptionTextAreaFocusLost

	//GEN-FIRST:event_editRegExpFilterNameFieldFocusLost
	private void editRegExpFilterNameFieldFocusLost(
			java.awt.event.FocusEvent evt) {

		updateAdvancedFilter();

	}//GEN-LAST:event_editRegExpFilterNameFieldFocusLost

	//GEN-FIRST:event_regExpFilterListValueChanged
	private void regExpFilterListValueChanged(
			javax.swing.event.ListSelectionEvent evt) {

		//ignore event when list readjusting
		if (evt.getValueIsAdjusting()) {
			return;
		}

		//remove rule from edit
		mRuleInEditMode = null;

		Object[] selectedValues = regExpFilterList.getSelectedValues();
		if (selectedValues != null && selectedValues.length == 1) {
			SwingUtils.setContainerAndChildrenEnabled(editRegExpFilterPanel,
					true);

			RegExpFilterRule rule = ((RegExpFilterRuleListElement) selectedValues[0])
					.getRule();

			editRegExpFilterNameField.setText(rule.getName());
			editRegExpFilterDescriptionTextArea.setText(rule.getDescription());
			editRegExpFilterRegExpTextArea.setText(rule.getPattern().pattern());

			//match action
			{
				RegExpFilterAction matchAction = rule.getMatchAction();

				if (matchAction.getAccept() == null) {
					editRegExpFilterMatchStatusChangeComboBox
							.setSelectedIndex(0);
				} else if (matchAction.getAccept().booleanValue()) {
					editRegExpFilterMatchStatusChangeComboBox
							.setSelectedIndex(1);
				} else {
					editRegExpFilterMatchStatusChangeComboBox
							.setSelectedIndex(2);
				}

				editRegExpFilterMatchPrioChangeTextField.setText(String
						.valueOf(matchAction.getPriorityChange()));

				JobParameter assumeCompleteMatchParameter = matchAction
						.getJobParameter(UrlDownloadJob.JOB_PARAMETER_SKIP_DOWNLOADED_FILE);
				editRegExpFilterMatchAssumeFinishedFileCheckBox
						.setSelected(assumeCompleteMatchParameter != null
								&& assumeCompleteMatchParameter.getValue()
										.equals(Boolean.TRUE));
			}

			//no match action
			{
				RegExpFilterAction noMatchAction = rule.getNoMatchAction();

				if (noMatchAction.getAccept() == null) {
					editRegExpFilterNoMatchStatusChangeComboBox
							.setSelectedIndex(0);
				} else if (noMatchAction.getAccept().booleanValue()) {
					editRegExpFilterNoMatchStatusChangeComboBox
							.setSelectedIndex(1);
				} else {
					editRegExpFilterNoMatchStatusChangeComboBox
							.setSelectedIndex(2);
				}

				editRegExpFilterNoMatchPrioChangeTextField.setText(String
						.valueOf(noMatchAction.getPriorityChange()));

				JobParameter assumeCompleteNoMatchParameter = noMatchAction
						.getJobParameter(UrlDownloadJob.JOB_PARAMETER_SKIP_DOWNLOADED_FILE);
				editRegExpFilterNoMatchAssumeFinishedFileCheckBox
						.setSelected(assumeCompleteNoMatchParameter != null
								&& assumeCompleteNoMatchParameter.getValue()
										.equals(Boolean.TRUE));
			}

			mRuleInEditMode = rule;

		} else {

			//empty all fields
			SwingUtils.setContainerAndChildrenEnabled(editRegExpFilterPanel,
					false);

			editRegExpFilterNameField.setText(null);
			editRegExpFilterDescriptionTextArea.setText(null);
			editRegExpFilterRegExpTextArea.setText(null);

			editRegExpFilterMatchStatusChangeComboBox.setSelectedIndex(0);
			editRegExpFilterMatchPrioChangeTextField.setText(null);
			editRegExpFilterMatchAssumeFinishedFileCheckBox.setSelected(false);

			editRegExpFilterNoMatchStatusChangeComboBox.setSelectedIndex(0);
			editRegExpFilterNoMatchPrioChangeTextField.setText(null);
			editRegExpFilterNoMatchAssumeFinishedFileCheckBox
					.setSelected(false);

		}

	}//GEN-LAST:event_regExpFilterListValueChanged

	//GEN-FIRST:event_regExpFilterMoveDownButtonActionPerformed
	private void regExpFilterMoveDownButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		int selection = regExpFilterList.getSelectedIndex();
		if (selection < (regExpFilterListModel.getSize() - 1)) {
			Object source = regExpFilterListModel.get(selection);

			//move the entry
			regExpFilterListModel.moveEntry(selection, 1);

			//move the selection
			regExpFilterList.setSelectedValue(source, true);
		}

	}//GEN-LAST:event_regExpFilterMoveDownButtonActionPerformed

	//GEN-FIRST:event_regExpFilterMoveUpButtonActionPerformed
	private void regExpFilterMoveUpButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		int selection = regExpFilterList.getSelectedIndex();
		if (selection > 0) {
			Object source = regExpFilterListModel.get(selection);

			//move the entry
			regExpFilterListModel.moveEntry(selection, -1);

			//move the selection
			regExpFilterList.setSelectedValue(source, true);
		}

	}//GEN-LAST:event_regExpFilterMoveUpButtonActionPerformed

	//GEN-FIRST:event_regExpFilterAddButtonActionPerformed
	private void regExpFilterAddButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		Object element = new RegExpFilterRuleListElement(new RegExpFilterRule());

		regExpFilterListModel.addElement(element);

		//move the selection
		regExpFilterList.setSelectedValue(element, true);

	}//GEN-LAST:event_regExpFilterAddButtonActionPerformed

	//GEN-FIRST:event_regExpFilterRemoveButtonActionPerformed
	private void regExpFilterRemoveButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		int[] selections = regExpFilterList.getSelectedIndices();
		if (selections.length > 0) {
			for (int i = selections.length - 1; i >= 0; i--) {
				regExpFilterListModel.remove(selections[i]);
			}
		}

	}//GEN-LAST:event_regExpFilterRemoveButtonActionPerformed	

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	protected javax.swing.JLabel editRegExpFilterDescriptionLabel;
	protected javax.swing.JScrollPane editRegExpFilterDescriptionPane;
	protected javax.swing.JTextArea editRegExpFilterDescriptionTextArea;
	protected javax.swing.JCheckBox editRegExpFilterMatchAssumeFinishedFileCheckBox;
	protected javax.swing.JPanel editRegExpFilterMatchPanel;
	protected javax.swing.JLabel editRegExpFilterMatchPrioChangeLabel;
	protected javax.swing.JTextField editRegExpFilterMatchPrioChangeTextField;
	protected javax.swing.JComboBox editRegExpFilterMatchStatusChangeComboBox;
	protected javax.swing.JLabel editRegExpFilterMatchStatusChangeLabel;
	protected javax.swing.JTextField editRegExpFilterNameField;
	protected javax.swing.JLabel editRegExpFilterNameLabel;
	protected javax.swing.JCheckBox editRegExpFilterNoMatchAssumeFinishedFileCheckBox;
	protected javax.swing.JPanel editRegExpFilterNoMatchPanel;
	protected javax.swing.JLabel editRegExpFilterNoMatchPrioChangeLabel;
	protected javax.swing.JTextField editRegExpFilterNoMatchPrioChangeTextField;
	protected javax.swing.JComboBox editRegExpFilterNoMatchStatusChangeComboBox;
	protected javax.swing.JLabel editRegExpFilterNoMatchStatusChangeLabel;
	protected javax.swing.JButton editRegExpFilterOpenRegExpEditorButton;
	protected javax.swing.JPanel editRegExpFilterPanel;
	protected javax.swing.JLabel editRegExpFilterRegExpLabel;
	protected javax.swing.JScrollPane editRegExpFilterRegExpPane;
	protected javax.swing.JTextArea editRegExpFilterRegExpTextArea;
	protected javax.swing.JButton regExpFilterAddButton;
	protected javax.swing.JLabel regExpFilterChainExplanationLabel;
	protected javax.swing.JLabel regExpFilterChainLabel;
	protected javax.swing.JList regExpFilterList;
	protected javax.swing.JButton regExpFilterMoveDownButton;
	protected javax.swing.JButton regExpFilterMoveUpButton;
	protected javax.swing.JScrollPane regExpFilterPane;
	protected javax.swing.JButton regExpFilterRemoveButton;
	// End of variables declaration//GEN-END:variables

}