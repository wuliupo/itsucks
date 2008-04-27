/*
 * DownloadJobContentFilterPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.awt.Dialog;
import java.io.Serializable;
import java.util.List;

import de.phleisch.app.itsucks.filter.download.impl.ContentFilter;
import de.phleisch.app.itsucks.filter.download.impl.ContentFilter.ContentFilterConfig;
import de.phleisch.app.itsucks.filter.download.impl.ContentFilter.ContentFilterConfig.Action;
import de.phleisch.app.itsucks.gui.common.EditRegularExpressionDialog;
import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.util.ExtendedListModel;
import de.phleisch.app.itsucks.gui.util.SwingUtils;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  __USER__
 */
public class DownloadJobContentFilterPanel extends javax.swing.JPanel implements EditJobCapable {

	private static final long serialVersionUID = -8393121172912816716L;

	protected ExtendedListModel contentFilterListModel;
	protected ContentFilterConfig mRuleInEditMode;

	/** Creates new form DownloadJobContentFilterPanel */
	public DownloadJobContentFilterPanel() {
		contentFilterListModel = new ExtendedListModel();
		mRuleInEditMode = null;

		initComponents();

		//add elements to combo boxes
		for (ComboBoxEntry entry : mActionList) {
			editContentFilterMatchStatusChangeComboBox.addItem(entry);
			editContentFilterNoMatchStatusChangeComboBox.addItem(entry);
		}

		//disable advanced edit filter panel
		SwingUtils
				.setContainerAndChildrenEnabled(editContentFilterPanel, false);
		
//		final KeyboardFocusManager focusManager =
//            KeyboardFocusManager.getCurrentKeyboardFocusManager();
//        focusManager.addVetoableChangeListener("focusOwner", new VetoableChangeListener() {
//
//			public void vetoableChange(PropertyChangeEvent pEvt)
//					throws PropertyVetoException {
//				
////				Component focusOwner = focusManager.getFocusOwner();
//				
//				Component lastFocusOwner = (Component) pEvt.getOldValue();
//				Component nextFocusOwner = (Component) pEvt.getNewValue();
//				
//				if(nextFocusOwner != null) {
//					System.out.println("Next: " + nextFocusOwner);
//				}
//				
//				if(lastFocusOwner != nextFocusOwner 
//					&& nextFocusOwner == null
//					&& lastFocusOwner == editContentFilterRegExpTextArea) {
//				
////				if(nextFocusOwner == editContentFilterRegExpTextArea) {
//					
//					throw new PropertyVetoException("Not a valid input.", pEvt);
//				}
//				
//			}
//        	
//        });
	}


	public void loadJobPackage(SerializableJobPackage pJobPackage) {
		
		ContentFilter contentFilter = 
			(ContentFilter) pJobPackage.getFilterByType(ContentFilter.class);
		
		if (contentFilter != null) {
			ExtendedListModel model = this.contentFilterListModel;
			
			for (ContentFilterConfig jobFilterRule : contentFilter.getContentFilterConfigList()) {
				model.addElement(this.new ContentFilterRuleListElement(
								jobFilterRule));
			}
			
		}
		
	}

	public void saveJobPackage(SerializableJobPackage pJobPackage) {
		
		ContentFilter contentFilter = 
			(ContentFilter) pJobPackage.getFilterByType(ContentFilter.class);
		
		int contentFilterCount = this.contentFilterListModel.getSize();
		
		if (contentFilterCount > 0) {
			if(contentFilter == null) {
				contentFilter = new ContentFilter();
				pJobPackage.addFilter(contentFilter);
			}
		
			for (int i = 0; i < contentFilterCount; i++) {
				ContentFilterConfig rule = ((DownloadJobContentFilterPanel.ContentFilterRuleListElement) 
						this.contentFilterListModel
							.get(i)).getRule();
				contentFilter.addContentFilterConfig(rule);
			}
		}
	}

	public List<String> validateFields() {
		return null;
	}
	
	
	protected class ContentFilterRuleListElement {

		private ContentFilterConfig mRule;

		public ContentFilterRuleListElement(ContentFilterConfig pRule) {
			mRule = pRule;
		}

		public ContentFilterConfig getRule() {
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
					+ translateAction(mRule.getMatchAction()) 
					+ "<br>\nNo Match: "
					+ translateAction(mRule.getNoMatchAction()) 
					+ "</html>";
		}
		
		private String translateAction(Action action) {
			if(action == null) {
				return "null";
			} else if(Action.ACCEPT.equals(action)) {
				return "'no action'";
			} else if(Action.REJECT.equals(action)) {
				return "'abort download'";
			} else {
				return "<unknown>";
			}
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
	
	protected ComboBoxEntry mActionList[] = new ComboBoxEntry[] {
			new ComboBoxEntry("No Action", Boolean.FALSE),
			new ComboBoxEntry("Abort download", Boolean.TRUE), };

	private void updateContentFilter() {

		Object[] selectedValues = contentFilterList.getSelectedValues();
		if (mRuleInEditMode == null || selectedValues == null
				|| selectedValues.length != 1) {
			return;
		}

		ContentFilterConfig rule = mRuleInEditMode;

		rule.setName(editContentFilterNameField.getText());
		rule.setDescription(editContentFilterDescriptionTextArea.getText());
		rule.setPattern(editContentFilterRegExpTextArea.getText());

		//match Action
		{

			ComboBoxEntry selectedItem = (ComboBoxEntry) editContentFilterMatchStatusChangeComboBox
					.getSelectedItem();
			if (Boolean.TRUE.equals(selectedItem.getValue())) {
				rule.setMatchAction(ContentFilterConfig.Action.REJECT);
			} else {
				rule.setMatchAction(ContentFilterConfig.Action.ACCEPT);
			}

		}

		//no match Action
		{
			ComboBoxEntry selectedItem = (ComboBoxEntry) editContentFilterNoMatchStatusChangeComboBox
					.getSelectedItem();

			if (Boolean.TRUE.equals(selectedItem.getValue())) {
				rule.setNoMatchAction(ContentFilterConfig.Action.REJECT);
			} else {
				rule.setNoMatchAction(ContentFilterConfig.Action.ACCEPT);
			}

		}

		//notify list
		int selectionIndex = contentFilterList.getSelectedIndex();
		contentFilterListModel.fireContentsChanged(selectionIndex,
				selectionIndex);
	}

//	private void displayErrors(List<String> errorsBasicPanel) {
//		StringBuffer buffer = new StringBuffer();
//		for (String string : errorsBasicPanel) {
//			buffer.append(string + '\n');
//		}
//
//		JOptionPane.showMessageDialog(this, buffer.toString(),
//				"Validation errors", JOptionPane.ERROR_MESSAGE);
//	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		contentFilterChainLabel = new javax.swing.JLabel();
		contentFilterChainExplanationLabel = new javax.swing.JLabel();
		contentFilterPane = new javax.swing.JScrollPane();
		contentFilterList = new javax.swing.JList();
		contentFilterAddButton = new javax.swing.JButton();
		contentFilterRemoveButton = new javax.swing.JButton();
		contentFilterMoveUpButton = new javax.swing.JButton();
		contentFilterMoveDownButton = new javax.swing.JButton();
		editContentFilterPanel = new javax.swing.JPanel();
		editContentFilterNameLabel = new javax.swing.JLabel();
		editContentFilterNameField = new javax.swing.JTextField();
		editContentFilterDescriptionLabel = new javax.swing.JLabel();
		editContentFilterDescriptionPane = new javax.swing.JScrollPane();
		editContentFilterDescriptionTextArea = new javax.swing.JTextArea();
		editContentFilterRegExpLabel = new javax.swing.JLabel();
		editContentFilterRegExpPane = new javax.swing.JScrollPane();
		editContentFilterRegExpTextArea = new javax.swing.JTextArea();
		editContentFilterOpenRegExpEditorButton = new javax.swing.JButton();
		editContentFilterMatchPanel = new javax.swing.JPanel();
		editContentFilterMatchStatusChangeLabel = new javax.swing.JLabel();
		editContentFilterMatchStatusChangeComboBox = new javax.swing.JComboBox();
		editContentFilterNoMatchPanel = new javax.swing.JPanel();
		editContentFilterNoMatchStatusChangeLabel = new javax.swing.JLabel();
		editContentFilterNoMatchStatusChangeComboBox = new javax.swing.JComboBox();

		contentFilterChainLabel.setText("Content Filter Chain");

		contentFilterChainExplanationLabel.setFont(new java.awt.Font("Dialog",
				0, 12));
		contentFilterChainExplanationLabel
				.setText("<html>This filter is applied to every text file while it's being downloaded.</html>");

		contentFilterList.setFont(new java.awt.Font("Dialog", 0, 12));
		contentFilterList.setModel(contentFilterListModel);
		contentFilterList
				.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					public void valueChanged(
							javax.swing.event.ListSelectionEvent evt) {
						contentFilterListValueChanged(evt);
					}
				});
		contentFilterPane.setViewportView(contentFilterList);

		contentFilterAddButton.setFont(new java.awt.Font("Dialog", 0, 12));
		contentFilterAddButton.setText("+");
		contentFilterAddButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
		contentFilterAddButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						contentFilterAddButtonActionPerformed(evt);
					}
				});

		contentFilterRemoveButton.setFont(new java.awt.Font("Dialog", 0, 12));
		contentFilterRemoveButton.setText("-");
		contentFilterRemoveButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
		contentFilterRemoveButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						contentFilterRemoveButtonActionPerformed(evt);
					}
				});

		contentFilterMoveUpButton.setFont(new java.awt.Font("Dialog", 0, 12));
		contentFilterMoveUpButton.setText("up");
		contentFilterMoveUpButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
		contentFilterMoveUpButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						contentFilterMoveUpButtonActionPerformed(evt);
					}
				});

		contentFilterMoveDownButton.setFont(new java.awt.Font("Dialog", 0, 12));
		contentFilterMoveDownButton.setText("down");
		contentFilterMoveDownButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
		contentFilterMoveDownButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						contentFilterMoveDownButtonActionPerformed(evt);
					}
				});

		editContentFilterPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Content Filter"));

		editContentFilterNameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		editContentFilterNameLabel.setText("Filter Name:");

		editContentFilterNameField
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						editContentFilterNameFieldFocusLost(evt);
					}
				});

		editContentFilterDescriptionLabel.setFont(new java.awt.Font("Dialog",
				0, 12));
		editContentFilterDescriptionLabel
				.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		editContentFilterDescriptionLabel.setText("Filter Description:");

		editContentFilterDescriptionTextArea.setColumns(20);
		editContentFilterDescriptionTextArea.setLineWrap(true);
		editContentFilterDescriptionTextArea.setRows(2);
		editContentFilterDescriptionTextArea
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						editContentFilterDescriptionTextAreaFocusLost(evt);
					}
				});
		editContentFilterDescriptionPane
				.setViewportView(editContentFilterDescriptionTextArea);

		editContentFilterRegExpLabel
				.setFont(new java.awt.Font("Dialog", 0, 12));
		editContentFilterRegExpLabel
				.setText("Regular Expression, partial match:");

		editContentFilterRegExpTextArea.setColumns(20);
		editContentFilterRegExpTextArea.setLineWrap(true);
		editContentFilterRegExpTextArea.setRows(3);
		editContentFilterRegExpTextArea
				.addFocusListener(new java.awt.event.FocusAdapter() {
					public void focusLost(java.awt.event.FocusEvent evt) {
						editContentFilterRegExpTextAreaFocusLost(evt);
					}
				});
		editContentFilterRegExpPane
				.setViewportView(editContentFilterRegExpTextArea);

		editContentFilterOpenRegExpEditorButton.setFont(new java.awt.Font(
				"Dialog", 0, 10));
		editContentFilterOpenRegExpEditorButton
				.setText("Open Regular Expression Editor");
		editContentFilterOpenRegExpEditorButton.setMargin(new java.awt.Insets(
				2, 4, 2, 4));
		editContentFilterOpenRegExpEditorButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						editContentFilterOpenRegExpEditorButtonActionPerformed(evt);
					}
				});

		editContentFilterMatchPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Action in case of match"));

		editContentFilterMatchStatusChangeLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editContentFilterMatchStatusChangeLabel.setText("Action:");

		editContentFilterMatchStatusChangeComboBox.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editContentFilterMatchStatusChangeComboBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						editContentFilterMatchStatusChangeComboBoxActionPerformed(evt);
					}
				});

		org.jdesktop.layout.GroupLayout editContentFilterMatchPanelLayout = new org.jdesktop.layout.GroupLayout(
				editContentFilterMatchPanel);
		editContentFilterMatchPanel
				.setLayout(editContentFilterMatchPanelLayout);
		editContentFilterMatchPanelLayout
				.setHorizontalGroup(editContentFilterMatchPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editContentFilterMatchPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editContentFilterMatchStatusChangeLabel)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editContentFilterMatchStatusChangeComboBox,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												128,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(104, Short.MAX_VALUE)));
		editContentFilterMatchPanelLayout
				.setVerticalGroup(editContentFilterMatchPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editContentFilterMatchPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editContentFilterMatchPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																editContentFilterMatchStatusChangeLabel)
														.add(
																editContentFilterMatchStatusChangeComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(45, Short.MAX_VALUE)));

		editContentFilterNoMatchPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Action in case of no match"));

		editContentFilterNoMatchStatusChangeLabel.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editContentFilterNoMatchStatusChangeLabel.setText("Action:");

		editContentFilterNoMatchStatusChangeComboBox.setFont(new java.awt.Font(
				"Dialog", 0, 12));
		editContentFilterNoMatchStatusChangeComboBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						editContentFilterNoMatchStatusChangeComboBoxActionPerformed(evt);
					}
				});

		org.jdesktop.layout.GroupLayout editContentFilterNoMatchPanelLayout = new org.jdesktop.layout.GroupLayout(
				editContentFilterNoMatchPanel);
		editContentFilterNoMatchPanel
				.setLayout(editContentFilterNoMatchPanelLayout);
		editContentFilterNoMatchPanelLayout
				.setHorizontalGroup(editContentFilterNoMatchPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editContentFilterNoMatchPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editContentFilterNoMatchStatusChangeLabel)
										.add(16, 16, 16)
										.add(
												editContentFilterNoMatchStatusChangeComboBox,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												128,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(100, Short.MAX_VALUE)));
		editContentFilterNoMatchPanelLayout
				.setVerticalGroup(editContentFilterNoMatchPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editContentFilterNoMatchPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editContentFilterNoMatchPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																editContentFilterNoMatchStatusChangeComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																editContentFilterNoMatchStatusChangeLabel))
										.addContainerGap(45, Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout editContentFilterPanelLayout = new org.jdesktop.layout.GroupLayout(
				editContentFilterPanel);
		editContentFilterPanel.setLayout(editContentFilterPanelLayout);
		editContentFilterPanelLayout
				.setHorizontalGroup(editContentFilterPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editContentFilterPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editContentFilterPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																editContentFilterRegExpPane,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																616,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																editContentFilterPanelLayout
																		.createSequentialGroup()
																		.add(
																				editContentFilterMatchPanel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editContentFilterNoMatchPanel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
														.add(
																editContentFilterPanelLayout
																		.createSequentialGroup()
																		.add(
																				editContentFilterNameLabel)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editContentFilterNameField,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				138,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editContentFilterDescriptionLabel,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				111,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editContentFilterDescriptionPane,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				261,
																				Short.MAX_VALUE))
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																editContentFilterPanelLayout
																		.createSequentialGroup()
																		.add(
																				editContentFilterRegExpLabel,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				441,
																				Short.MAX_VALUE)
																		.add(
																				175,
																				175,
																				175))
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																editContentFilterOpenRegExpEditorButton))
										.addContainerGap()));
		editContentFilterPanelLayout
				.setVerticalGroup(editContentFilterPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								editContentFilterPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												editContentFilterPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																editContentFilterPanelLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.BASELINE)
																		.add(
																				editContentFilterNameLabel)
																		.add(
																				editContentFilterNameField,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.add(
																				editContentFilterDescriptionLabel))
														.add(
																editContentFilterDescriptionPane,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																41,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(editContentFilterRegExpLabel)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editContentFilterRegExpPane,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												58, Short.MAX_VALUE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editContentFilterOpenRegExpEditorButton)
										.add(31, 31, 31)
										.add(
												editContentFilterPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING,
																false)
														.add(
																editContentFilterMatchPanel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																editContentFilterNoMatchPanel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
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
																editContentFilterPanel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				contentFilterPane,
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
																								contentFilterRemoveButton)
																						.add(
																								contentFilterAddButton)
																						.add(
																								contentFilterMoveUpButton)
																						.add(
																								contentFilterMoveDownButton)))
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																contentFilterChainExplanationLabel,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																650,
																Short.MAX_VALUE)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																contentFilterChainLabel))
										.addContainerGap()));

		layout.linkSize(new java.awt.Component[] { contentFilterAddButton,
				contentFilterRemoveButton },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		layout.linkSize(new java.awt.Component[] { contentFilterMoveDownButton,
				contentFilterMoveUpButton },
				org.jdesktop.layout.GroupLayout.HORIZONTAL);

		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(contentFilterChainLabel)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												contentFilterChainExplanationLabel,
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
																				contentFilterAddButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				contentFilterRemoveButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				contentFilterMoveUpButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				contentFilterMoveDownButton))
														.add(contentFilterPane,
																0, 0,
																Short.MAX_VALUE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												editContentFilterPanel,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap()));

		layout.linkSize(new java.awt.Component[] { contentFilterAddButton,
				contentFilterRemoveButton },
				org.jdesktop.layout.GroupLayout.VERTICAL);

		layout.linkSize(new java.awt.Component[] { contentFilterMoveDownButton,
				contentFilterMoveUpButton },
				org.jdesktop.layout.GroupLayout.VERTICAL);

	}// </editor-fold>
	//GEN-END:initComponents

	//GEN-FIRST:event_editContentFilterOpenRegExpEditorButtonActionPerformed
	private void editContentFilterOpenRegExpEditorButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		EditRegularExpressionDialog dialog = new EditRegularExpressionDialog(
				(Dialog) getRootPane().getParent(), true);

		dialog.setRegularExpression(this.editContentFilterRegExpTextArea
				.getText());
		dialog.setVisible(true);

		if (dialog.isOk()) {
			//copy the value from the editor
			this.editContentFilterRegExpTextArea.setText(dialog
					.getRegularExpression());

			updateContentFilter();
		}
	}//GEN-LAST:event_editContentFilterOpenRegExpEditorButtonActionPerformed

	//GEN-FIRST:event_editContentFilterNoMatchStatusChangeComboBoxActionPerformed
	private void editContentFilterNoMatchStatusChangeComboBoxActionPerformed(
			java.awt.event.ActionEvent evt) {

		updateContentFilter();

	}//GEN-LAST:event_editContentFilterNoMatchStatusChangeComboBoxActionPerformed

	//GEN-FIRST:event_editContentFilterMatchStatusChangeComboBoxActionPerformed
	private void editContentFilterMatchStatusChangeComboBoxActionPerformed(
			java.awt.event.ActionEvent evt) {

		updateContentFilter();

	}//GEN-LAST:event_editContentFilterMatchStatusChangeComboBoxActionPerformed

	//GEN-FIRST:event_editContentFilterRegExpTextAreaFocusLost
	private void editContentFilterRegExpTextAreaFocusLost(
			java.awt.event.FocusEvent evt) {

//				FieldValidator validator = new FieldValidator();
//		
//				validator.assertValidRegExp(editContentFilterRegExpTextArea.getText(),
//						"Enter a valid regular expression.");
//				if(validator.getErrors().size() > 0) {
//					displayErrors(validator.getErrors());
//					editContentFilterRegExpTextArea.requestFocus();
//					return;
//				}
		
		updateContentFilter();

	}//GEN-LAST:event_editContentFilterRegExpTextAreaFocusLost

	//GEN-FIRST:event_editContentFilterDescriptionTextAreaFocusLost
	private void editContentFilterDescriptionTextAreaFocusLost(
			java.awt.event.FocusEvent evt) {

		updateContentFilter();

	}//GEN-LAST:event_editContentFilterDescriptionTextAreaFocusLost

	//GEN-FIRST:event_editContentFilterNameFieldFocusLost
	private void editContentFilterNameFieldFocusLost(
			java.awt.event.FocusEvent evt) {

		updateContentFilter();

	}//GEN-LAST:event_editContentFilterNameFieldFocusLost

	//GEN-FIRST:event_contentFilterListValueChanged
	private void contentFilterListValueChanged(
			javax.swing.event.ListSelectionEvent evt) {

		//ignore event when list readjusting
		if (evt.getValueIsAdjusting()) {
			return;
		}

		//remove rule from edit
		mRuleInEditMode = null;

		Object[] selectedValues = contentFilterList.getSelectedValues();
		if (selectedValues != null && selectedValues.length == 1) {
			SwingUtils.setContainerAndChildrenEnabled(editContentFilterPanel,
					true);

			ContentFilterConfig rule = ((ContentFilterRuleListElement) selectedValues[0])
					.getRule();

			editContentFilterNameField.setText(rule.getName());
			editContentFilterDescriptionTextArea.setText(rule.getDescription());
			editContentFilterRegExpTextArea
					.setText(rule.getPattern().pattern());

			//match action
			{
				Action matchAction = rule.getMatchAction();

				if (Action.ACCEPT.equals(matchAction)) {
					editContentFilterMatchStatusChangeComboBox
							.setSelectedIndex(0);
				} else if (Action.REJECT.equals(matchAction)) {
					editContentFilterMatchStatusChangeComboBox
							.setSelectedIndex(1);
				} else {
					throw new IllegalStateException();
				}

			}

			//no match action
			{
				Action noMatchAction = rule.getNoMatchAction();

				if (Action.ACCEPT.equals(noMatchAction)) {
					editContentFilterNoMatchStatusChangeComboBox
							.setSelectedIndex(0);
				} else if (Action.REJECT.equals(noMatchAction)) {
					editContentFilterNoMatchStatusChangeComboBox
							.setSelectedIndex(1);
				} else {
					throw new IllegalStateException();
				}

			}

			mRuleInEditMode = rule;

		} else {

			//empty all fields
			SwingUtils.setContainerAndChildrenEnabled(editContentFilterPanel,
					false);

			editContentFilterNameField.setText(null);
			editContentFilterDescriptionTextArea.setText(null);
			editContentFilterRegExpTextArea.setText(null);

			editContentFilterMatchStatusChangeComboBox.setSelectedIndex(0);

			editContentFilterNoMatchStatusChangeComboBox.setSelectedIndex(0);

		}

	}//GEN-LAST:event_contentFilterListValueChanged

	//GEN-FIRST:event_contentFilterMoveDownButtonActionPerformed
	private void contentFilterMoveDownButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		int selection = contentFilterList.getSelectedIndex();
		if (selection < (contentFilterListModel.getSize() - 1)) {
			Object source = contentFilterListModel.get(selection);

			//move the entry
			contentFilterListModel.moveEntry(selection, 1);

			//move the selection
			contentFilterList.setSelectedValue(source, true);
		}

	}//GEN-LAST:event_contentFilterMoveDownButtonActionPerformed

	//GEN-FIRST:event_contentFilterMoveUpButtonActionPerformed
	private void contentFilterMoveUpButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		int selection = contentFilterList.getSelectedIndex();
		if (selection > 0) {
			Object source = contentFilterListModel.get(selection);

			//move the entry
			contentFilterListModel.moveEntry(selection, -1);

			//move the selection
			contentFilterList.setSelectedValue(source, true);
		}

	}//GEN-LAST:event_contentFilterMoveUpButtonActionPerformed

	//GEN-FIRST:event_contentFilterAddButtonActionPerformed
	private void contentFilterAddButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		Object element = new ContentFilterRuleListElement(
				new ContentFilter.ContentFilterConfig("", Action.ACCEPT,
						Action.ACCEPT));

		contentFilterListModel.addElement(element);

		//move the selection
		contentFilterList.setSelectedValue(element, true);

	}//GEN-LAST:event_contentFilterAddButtonActionPerformed

	//GEN-FIRST:event_contentFilterRemoveButtonActionPerformed
	private void contentFilterRemoveButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		int[] selections = contentFilterList.getSelectedIndices();
		if (selections.length > 0) {
			for (int i = selections.length - 1; i >= 0; i--) {
				contentFilterListModel.remove(selections[i]);
			}
		}

	}//GEN-LAST:event_contentFilterRemoveButtonActionPerformed	

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	protected javax.swing.JButton contentFilterAddButton;
	protected javax.swing.JLabel contentFilterChainExplanationLabel;
	protected javax.swing.JLabel contentFilterChainLabel;
	protected javax.swing.JList contentFilterList;
	protected javax.swing.JButton contentFilterMoveDownButton;
	protected javax.swing.JButton contentFilterMoveUpButton;
	protected javax.swing.JScrollPane contentFilterPane;
	protected javax.swing.JButton contentFilterRemoveButton;
	protected javax.swing.JLabel editContentFilterDescriptionLabel;
	protected javax.swing.JScrollPane editContentFilterDescriptionPane;
	protected javax.swing.JTextArea editContentFilterDescriptionTextArea;
	protected javax.swing.JPanel editContentFilterMatchPanel;
	protected javax.swing.JComboBox editContentFilterMatchStatusChangeComboBox;
	protected javax.swing.JLabel editContentFilterMatchStatusChangeLabel;
	protected javax.swing.JTextField editContentFilterNameField;
	protected javax.swing.JLabel editContentFilterNameLabel;
	protected javax.swing.JPanel editContentFilterNoMatchPanel;
	protected javax.swing.JComboBox editContentFilterNoMatchStatusChangeComboBox;
	protected javax.swing.JLabel editContentFilterNoMatchStatusChangeLabel;
	protected javax.swing.JButton editContentFilterOpenRegExpEditorButton;
	protected javax.swing.JPanel editContentFilterPanel;
	protected javax.swing.JLabel editContentFilterRegExpLabel;
	protected javax.swing.JScrollPane editContentFilterRegExpPane;
	protected javax.swing.JTextArea editContentFilterRegExpTextArea;
	// End of variables declaration//GEN-END:variables

}