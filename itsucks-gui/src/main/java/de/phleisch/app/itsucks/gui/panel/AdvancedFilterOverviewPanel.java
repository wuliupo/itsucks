/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 13.01.2007
 */

package de.phleisch.app.itsucks.gui.panel;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import de.phleisch.app.itsucks.filter.RegExpJobFilter;
import de.phleisch.app.itsucks.filter.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.gui.AddAdvancedFilterDialog;
import de.phleisch.app.itsucks.gui.AddAdvancedFilterInterface;

public class AdvancedFilterOverviewPanel extends JPanel implements AddAdvancedFilterInterface {

	private static final long serialVersionUID = 1L;

	private JList jAdvancedFilterList = null;
	private ExtendedListModel mAdvancedFilterListModel = null;

	private JLabel jLabelAdvancedFilter = null;

	private JButton jButtonAdvancedFilterAdd = null;

	private JButton jButtonAdvancedFilterRemove = null;
	
	private JScrollPane jAdvancedFilterListScrollPane = null;
	
	private JButton jButtonMoveRuleUp = null;

	private JButton jButtonMoveRuleDown = null;
	
	private Dialog mParentDialog = null;
	
	/**
	 * This is the default constructor
	 */
	public AdvancedFilterOverviewPanel(Dialog pParentDialog) {
		super();
		mParentDialog = pParentDialog;
		
		initialize();
		
		addAdvancedFilterRule(new RegExpJobFilter.RegExpFilterRule(
				"css$|jpg$|gif$|png$|js$", +50
		));
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(394, 226);
		this.setLayout(null);
		jLabelAdvancedFilter = new JLabel();
		jLabelAdvancedFilter.setBounds(new Rectangle(10, 10, 341, 31));
		jLabelAdvancedFilter.setFont(new Font("Dialog", Font.PLAIN, 12));
		jLabelAdvancedFilter.setVerticalAlignment(SwingConstants.TOP);
		jLabelAdvancedFilter.setText("<html>Advanced Filter: <br>(A job without any 'accept' match will be rejected)</html>");
		add(jLabelAdvancedFilter, null);
		this.add(getJAdvancedFilterListScrollPane(), null);
		this.add(getJButtonAdvancedFilterAdd(), null);
		this.add(getJButtonAdvancedFilterRemove(), null);
		this.add(getJButtonMoveRuleUp(), null);
		this.add(getJButtonMoveRuleDown(), null);
	}


	/**
	 * This method initializes mFileFilterListModel1	
	 * 	
	 * @return javax.swing.DefaultListModel	
	 */
	private ExtendedListModel getAdvancedFilterListModel() {
		if (mAdvancedFilterListModel == null) {
			mAdvancedFilterListModel = new ExtendedListModel();
		}
		return mAdvancedFilterListModel;
	}

	/**
	 * This method initializes jButtonAdvancedFilterAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAdvancedFilterAdd() {
		if (jButtonAdvancedFilterAdd == null) {
			jButtonAdvancedFilterAdd = new JButton();
			jButtonAdvancedFilterAdd.setBounds(new Rectangle(90, 190, 91, 21));
			jButtonAdvancedFilterAdd.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButtonAdvancedFilterAdd.setText("Add");
			jButtonAdvancedFilterAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AddAdvancedFilterDialog advancedFilterDialog = 
						new AddAdvancedFilterDialog(mParentDialog, AdvancedFilterOverviewPanel.this);
					advancedFilterDialog.setModal(true);
					advancedFilterDialog.setVisible(true);
				}
			});
		}
		return jButtonAdvancedFilterAdd;
	}

	/**
	 * This method initializes jAdvancedFilterListScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJAdvancedFilterListScrollPane() {
		if (jAdvancedFilterListScrollPane == null) {
			jAdvancedFilterListScrollPane = new JScrollPane();
			jAdvancedFilterListScrollPane.setBounds(new Rectangle(40, 40, 341, 141));
			jAdvancedFilterListScrollPane.setViewportView(getJAdvancedFilterList());
		}
		return jAdvancedFilterListScrollPane;
	}	
	
	/**
	 * This method initializes jAdvancedFilterList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJAdvancedFilterList() {
		if (jAdvancedFilterList == null) {
			jAdvancedFilterList = new JList(getAdvancedFilterListModel());
			jAdvancedFilterList.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jAdvancedFilterList;
	}
	

	/**
	 * This method initializes jButtonAdvancedFilterRemove	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAdvancedFilterRemove() {
		if (jButtonAdvancedFilterRemove == null) {
			jButtonAdvancedFilterRemove = new JButton();
			jButtonAdvancedFilterRemove.setBounds(new Rectangle(230, 190, 91, 21));
			jButtonAdvancedFilterRemove.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButtonAdvancedFilterRemove.setText("Remove");
			jButtonAdvancedFilterRemove.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] selections = jAdvancedFilterList.getSelectedIndices();
					if(selections.length > 0) {
						for (int i = selections.length - 1; i >= 0; i--) {
							mAdvancedFilterListModel.remove(selections[i]);
						}
					}
				}
			});
		}
		return jButtonAdvancedFilterRemove;
	}
	

	/**
	 * This method initializes jButtonMoveRuleUp	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonMoveRuleUp() {
		if (jButtonMoveRuleUp == null) {
			jButtonMoveRuleUp = new JButton();
			jButtonMoveRuleUp.setBounds(new Rectangle(10, 50, 21, 21));
			jButtonMoveRuleUp.setIcon(new ImageIcon(getClass().getResource("/go-up.png")));
			jButtonMoveRuleUp.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					int selection = jAdvancedFilterList.getSelectedIndex();
					Object source = mAdvancedFilterListModel.get(selection);
					
					//move the entry
					mAdvancedFilterListModel.moveEntry(selection, -1);
					
					//move the selection
					jAdvancedFilterList.setSelectedValue(source, true);
					
				}
			});
		}
		return jButtonMoveRuleUp;
	}

	/**
	 * This method initializes jButtonMoveRuleDown	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonMoveRuleDown() {
		if (jButtonMoveRuleDown == null) {
			jButtonMoveRuleDown = new JButton();
			jButtonMoveRuleDown.setBounds(new Rectangle(10, 150, 21, 21));
			jButtonMoveRuleDown.setIcon(new ImageIcon(getClass().getResource("/go-down.png")));
			jButtonMoveRuleDown.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					int selection = jAdvancedFilterList.getSelectedIndex();
					Object source = mAdvancedFilterListModel.get(selection);
					
					//move the entry
					mAdvancedFilterListModel.moveEntry(selection, 1);
					
					//move the selection
					jAdvancedFilterList.setSelectedValue(source, true);
					
				}
			});
		}
		return jButtonMoveRuleDown;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.chaoscrawler.gui.second_try.AddAdvancedFilterInterface#addAdvancedFilterRule(de.phleisch.app.chaoscrawler.filter.RegExpJobFilter.RegExpFilterRule)
	 */
	public void addAdvancedFilterRule(RegExpJobFilter.RegExpFilterRule pRule) {
		mAdvancedFilterListModel.add(mAdvancedFilterListModel.size(), pRule);
		this.jAdvancedFilterList.ensureIndexIsVisible(mAdvancedFilterListModel.size() - 1);
	}

	public RegExpJobFilter buildAdvancedFilter() {
		
		//copy the filter from the model to the filter list
		RegExpJobFilter regExpJobFilter = new RegExpJobFilter();
		Enumeration<?> advancedFilters = mAdvancedFilterListModel.elements();
		
		while(advancedFilters.hasMoreElements()) {
			RegExpFilterRule rule = (RegExpFilterRule) advancedFilters.nextElement();
			regExpJobFilter.addFilterRule(rule);
		}
		
		return regExpJobFilter;
	}

	public void loadAdvancedFilter(RegExpJobFilter pRegExpJobFilter) {
		
		if(pRegExpJobFilter == null) return;
		
		ExtendedListModel filterListModel = getAdvancedFilterListModel();
		filterListModel.clear();
		
		for (RegExpFilterRule rule : pRegExpJobFilter.getFilterRules()) {
			filterListModel.addElement(rule);
		}
	}

}  //  @jve:decl-index=0:visual-constraint="4,-5"
