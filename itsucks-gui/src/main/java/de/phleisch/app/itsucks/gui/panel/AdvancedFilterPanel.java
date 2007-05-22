/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui.panel;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.phleisch.app.itsucks.JobParameter;
import de.phleisch.app.itsucks.filter.RegExpJobFilter.RegExpFilterAction;
import de.phleisch.app.itsucks.filter.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.gui.EditRegularExpressionDialog;
import de.phleisch.app.itsucks.io.DownloadJob;

public class AdvancedFilterPanel extends JPanel {

	private static final long serialVersionUID = -8188216132610940036L;

	private JTextArea jAddAdvancedFilter = null;

	private JRadioButton jRadioAFilterNoChange = null;

	private JRadioButton jRadioAFilterAccept = null;

	private JRadioButton jRadioAFilterReject = null;

	private JTextField jPriorityChange = null;

	private JCheckBox jChangePriorityMatch = null;

	private JLabel jAdvancedFilterHelp = null;

	private JLabel jLabelAdvancedFilterChangeStatus = null;

	private JLabel jLabel = null;

	private JScrollPane jAddAdvancedFilterScrollablePane = null;

	private JButton jButtonRegExpEditor = null;

	private JLabel jLabelAdvancedFilterChangeStatus1 = null;

	private JRadioButton jRadioAFilterNoChange1 = null;

	private JRadioButton jRadioAFilterAccept1 = null;

	private JRadioButton jRadioAFilterReject1 = null;

	private JCheckBox jChangePriorityNoMatch = null;

	private JTextField jPriorityChange1 = null;
	
	private boolean mUpdateRule = false;
	
	/**
	 * This is the default constructor
	 */
	public AdvancedFilterPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jLabelAdvancedFilterChangeStatus1 = new JLabel();
		jLabelAdvancedFilterChangeStatus1.setBounds(new Rectangle(10, 250, 351, 21));
		jLabelAdvancedFilterChangeStatus1.setText("<html>Change the status of the download in case of <b>no match</b>:</html>");
		jLabelAdvancedFilterChangeStatus1.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabel = new JLabel();
		jLabel.setBounds(new Rectangle(10, 10, 351, 21));
		jLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabel.setText("Advanced filter (Regular expression, partial match):");
		jLabelAdvancedFilterChangeStatus = new JLabel();
		jLabelAdvancedFilterChangeStatus.setBounds(new Rectangle(10, 120, 351, 21));
		jLabelAdvancedFilterChangeStatus.setText("<html>Change the status of the download in case of <b>match</b>:</html>");
		jLabelAdvancedFilterChangeStatus.setFont(new Font("Dialog", Font.PLAIN, 12));
		jAdvancedFilterHelp = new JLabel();
		jAdvancedFilterHelp.setBounds(new Rectangle(370, 10, 181, 381));
		jAdvancedFilterHelp.setFont(new Font("Dialog", Font.PLAIN, 12));
		jAdvancedFilterHelp.setVerticalAlignment(SwingConstants.TOP);
		jAdvancedFilterHelp.setText(
				"<html>" +
				"<b>Hint:</b><br>" +
				"The advanced filter will be used to filter every found url. " +
				"In the Field 'Change Status' you can define what should happen " +
				"with this download. In the field 'Change priority' you can enter " +
				"a difference which will be applied to the priority of the download. " +
				"If you enter zero, nothing will happen (old prio. + 0), if you " +
				"enter a positive value or a negative one, this will change the priority " +
				"of the download." +
				"</html>"
				);
		setLayout(null);
		this.setSize(new Dimension(551, 377));
		this.add(jLabel, null);
		this.add(getJAddAdvancedFilterScrollablePane(), null);
		this.add(jLabelAdvancedFilterChangeStatus, null);
		add(getJRadioAFilterReject(), null);
		add(getJRadioAFilterAccept(), null);
		add(getJRadioAFilterNoChange(), null);
		add(getJChangePriorityMatch(), null);
		this.add(getJPriorityChange(), null);
		add(jAdvancedFilterHelp, null);
		this.add(getJButtonRegExpEditor(), null);
		this.add(jLabelAdvancedFilterChangeStatus1, null);
		this.add(getJRadioAFilterNoChange1(), null);
		this.add(getJRadioAFilterAccept1(), null);
		this.add(getJRadioAFilterReject1(), null);
		this.add(getJChangePriorityNoMatch(), null);
		this.add(getJPriorityChange1(), null);
		
		ButtonGroup group = new ButtonGroup();
		group.add(getJRadioAFilterReject());
		group.add(getJRadioAFilterAccept());
		group.add(getJRadioAFilterNoChange());
		
		ButtonGroup group2 = new ButtonGroup();
		group2.add(getJRadioAFilterReject1());
		group2.add(getJRadioAFilterAccept1());
		group2.add(getJRadioAFilterNoChange1());
	}


	/**
	 * This method initializes jRadioAFilterNoChange	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioAFilterNoChange() {
		if (jRadioAFilterNoChange == null) {
			jRadioAFilterNoChange = new JRadioButton();
			jRadioAFilterNoChange.setFont(new Font("Dialog", Font.PLAIN, 12));
			jRadioAFilterNoChange.setSelected(true);
			jRadioAFilterNoChange.setBounds(new Rectangle(10, 140, 161, 21));
			jRadioAFilterNoChange.setText("Do not change status");
		}
		return jRadioAFilterNoChange;
	}

	/**
	 * This method initializes jRadioAFilterAccept	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioAFilterAccept() {
		if (jRadioAFilterAccept == null) {
			jRadioAFilterAccept = new JRadioButton();
			jRadioAFilterAccept.setFont(new Font("Dialog", Font.PLAIN, 12));
			jRadioAFilterAccept.setBounds(new Rectangle(10, 160, 161, 21));
			jRadioAFilterAccept.setText("Accept URL");
		}
		return jRadioAFilterAccept;
	}

	/**
	 * This method initializes jRadioAFilterReject	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioAFilterReject() {
		if (jRadioAFilterReject == null) {
			jRadioAFilterReject = new JRadioButton();
			jRadioAFilterReject.setFont(new Font("Dialog", Font.PLAIN, 12));
			jRadioAFilterReject.setBounds(new Rectangle(10, 180, 161, 21));
			jRadioAFilterReject.setText("Reject URL");
		}
		return jRadioAFilterReject;
	}

	
	/**
	 * This method initializes jPriorityChange	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJPriorityChange() {
		if (jPriorityChange == null) {
			jPriorityChange = new JTextField();
			jPriorityChange.setBounds(new Rectangle(160, 210, 81, 21));
			jPriorityChange.setEnabled(false);
			jPriorityChange.setText("0");
		}
		return jPriorityChange;
	}

	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJChangePriorityMatch() {
		if (jChangePriorityMatch == null) {
			jChangePriorityMatch = new JCheckBox();
			jChangePriorityMatch.setBounds(new Rectangle(10, 210, 141, 21));
			jChangePriorityMatch.setFont(new Font("Dialog", Font.PLAIN, 12));
			jChangePriorityMatch.setSelected(false);
			jChangePriorityMatch.setText("Change the priority");
			jChangePriorityMatch.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					jPriorityChange.setEnabled(jChangePriorityMatch.isSelected());
				}
			});
		}
		return jChangePriorityMatch;
	}

	/**
	 * This method initializes jAddAdvancedFilter	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextArea getJAddAdvancedFilter() {
		if (jAddAdvancedFilter == null) {
			jAddAdvancedFilter = new JTextArea();
			jAddAdvancedFilter.setText("");
			jAddAdvancedFilter.setLineWrap(true);
		}
		return jAddAdvancedFilter;
	}

	/**
	 * This method initializes jAddAdvancedFilterScrollablePane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJAddAdvancedFilterScrollablePane() {
		if (jAddAdvancedFilterScrollablePane == null) {
			jAddAdvancedFilterScrollablePane = new JScrollPane();
			jAddAdvancedFilterScrollablePane.setBounds(new Rectangle(10, 30, 351, 61));
			jAddAdvancedFilterScrollablePane.setViewportView(getJAddAdvancedFilter());
		}
		return jAddAdvancedFilterScrollablePane;
	}

	public RegExpFilterRule buildRule() {

		String pattern = getJAddAdvancedFilter().getText();
		
		Boolean matchAction = null;
		if(getJRadioAFilterNoChange().isSelected()) matchAction = null;
		if(getJRadioAFilterAccept().isSelected()) matchAction = Boolean.TRUE;
		if(getJRadioAFilterReject().isSelected()) matchAction = Boolean.FALSE;
		
		int matchPrioChange = 0;
		if(getJChangePriorityMatch().isSelected()) {
			matchPrioChange = Integer.parseInt(getJPriorityChange().getText());
		}

		Boolean noMatchAction = null;
		if(getJRadioAFilterNoChange().isSelected()) noMatchAction = null;
		if(getJRadioAFilterAccept1().isSelected()) noMatchAction = Boolean.TRUE;
		if(getJRadioAFilterReject1().isSelected()) noMatchAction = Boolean.FALSE;
		
		int noMatchPrioChange = 0;
		if(getJChangePriorityMatch().isSelected()) {
			noMatchPrioChange = Integer.parseInt(getJPriorityChange1().getText());
		}
		
		RegExpFilterRule rule = new RegExpFilterRule(pattern);
		
		RegExpFilterAction ruleMatchAction = new RegExpFilterAction(matchAction, matchPrioChange);
		ruleMatchAction.addJobParameter(new JobParameter(DownloadJob.PARAMETER_SKIP_DOWNLOADED_FILE, Boolean.TRUE));
		
		RegExpFilterAction ruleNoMatchAction = new RegExpFilterAction(noMatchAction, noMatchPrioChange);
		ruleNoMatchAction.addJobParameter(new JobParameter(DownloadJob.PARAMETER_SKIP_DOWNLOADED_FILE, Boolean.FALSE));
		
		rule.setMatchAction(ruleMatchAction);
		rule.setNoMatchAction(ruleNoMatchAction);
		
		return rule;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonRegExpEditor() {
		if (jButtonRegExpEditor == null) {
			jButtonRegExpEditor = new JButton();
			jButtonRegExpEditor.setBounds(new Rectangle(229, 95, 131, 21));
			jButtonRegExpEditor.setText("RegExp Editor");
			jButtonRegExpEditor.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					JRootPane rootPane = AdvancedFilterPanel.this.getRootPane();
					
					EditRegularExpressionDialog dialog = 
						new EditRegularExpressionDialog(
								(Dialog) rootPane.getParent());
					
					dialog.setRegularExpression(
							AdvancedFilterPanel.this.jAddAdvancedFilter.getText());
					dialog.setModal(true);
					dialog.setVisible(true);
					
					if(!dialog.isCanceled()) {
						//copy the value from the editor
						AdvancedFilterPanel.this.jAddAdvancedFilter.setText(
								dialog.getRegularExpression());
					}
				}
			});
		}
		return jButtonRegExpEditor;
	}

	/**
	 * This method initializes jRadioAFilterNoChange1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioAFilterNoChange1() {
		if (jRadioAFilterNoChange1 == null) {
			jRadioAFilterNoChange1 = new JRadioButton();
			jRadioAFilterNoChange1.setBounds(new Rectangle(10, 270, 171, 23));
			jRadioAFilterNoChange1.setSelected(true);
			jRadioAFilterNoChange1.setText("Do not change status");
			jRadioAFilterNoChange1.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jRadioAFilterNoChange1;
	}

	/**
	 * This method initializes jRadioAFilterAccept1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioAFilterAccept1() {
		if (jRadioAFilterAccept1 == null) {
			jRadioAFilterAccept1 = new JRadioButton();
			jRadioAFilterAccept1.setBounds(new Rectangle(10, 290, 171, 23));
			jRadioAFilterAccept1.setText("Accept URL");
			jRadioAFilterAccept1.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jRadioAFilterAccept1;
	}

	/**
	 * This method initializes jRadioAFilterReject1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioAFilterReject1() {
		if (jRadioAFilterReject1 == null) {
			jRadioAFilterReject1 = new JRadioButton();
			jRadioAFilterReject1.setBounds(new Rectangle(10, 310, 171, 23));
			jRadioAFilterReject1.setText("Reject URL");
			jRadioAFilterReject1.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jRadioAFilterReject1;
	}

	/**
	 * This method initializes jCheckBox1	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJChangePriorityNoMatch() {
		if (jChangePriorityNoMatch == null) {
			jChangePriorityNoMatch = new JCheckBox();
			jChangePriorityNoMatch.setBounds(new Rectangle(10, 340, 141, 21));
			jChangePriorityNoMatch.setSelected(false);
			jChangePriorityNoMatch.setText("Change the priority");
			jChangePriorityNoMatch.setFont(new Font("Dialog", Font.PLAIN, 12));
			jChangePriorityNoMatch.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					jPriorityChange1.setEnabled(jChangePriorityNoMatch.isSelected());
				}
			});
		}
		return jChangePriorityNoMatch;
	}

	/**
	 * This method initializes jPriorityChange1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJPriorityChange1() {
		if (jPriorityChange1 == null) {
			jPriorityChange1 = new JTextField();
			jPriorityChange1.setBounds(new Rectangle(160, 340, 81, 21));
			jPriorityChange1.setText("0");
			jPriorityChange1.setEnabled(false);
		}
		return jPriorityChange1;
	}

	public void fill(RegExpFilterRule pRule) {
		
		this.getJAddAdvancedFilter().setText(pRule.getPattern().toString());
		
		RegExpFilterAction matchAction = pRule.getMatchAction();
		
		if(matchAction.getAccept() == null) {
			this.getJRadioAFilterNoChange().setSelected(true);
		} else if(matchAction.getAccept().booleanValue()) {
			this.getJRadioAFilterAccept().setSelected(true);
		} else if(!matchAction.getAccept().booleanValue()) {
			this.getJRadioAFilterReject().setSelected(true);
		}
		
		if(matchAction.getPriorityChange() > 0) {
			getJChangePriorityMatch().setSelected(true);
			getJPriorityChange().setText(String.valueOf(matchAction.getPriorityChange()));
		}
		
		RegExpFilterAction noMatchAction = pRule.getNoMatchAction();
		
		if(noMatchAction.getAccept() == null) {
			this.getJRadioAFilterNoChange1().setSelected(true);
		} else if(noMatchAction.getAccept().booleanValue()) {
			this.getJRadioAFilterAccept1().setSelected(true);
		} else if(!noMatchAction.getAccept().booleanValue()) {
			this.getJRadioAFilterReject1().setSelected(true);
		}
		
		if(noMatchAction.getPriorityChange() > 0) {
			getJChangePriorityNoMatch().setSelected(true);
			getJPriorityChange1().setText(String.valueOf(noMatchAction.getPriorityChange()));
		}
		
		mUpdateRule = true;
		
	}

	public boolean isUpdateRule() {
		return mUpdateRule;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
