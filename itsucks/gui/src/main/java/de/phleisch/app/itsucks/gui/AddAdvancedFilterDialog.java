/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import de.phleisch.app.itsucks.filter.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.gui.panel.AdvancedFilterPanel;

public class AddAdvancedFilterDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private AdvancedFilterPanel advancedFilterPanel = null;

	private JPanel jPanelAction = null;

	private JButton jButtonAdd = null;

	private JButton jButtonCancel = null;

	private AddAdvancedFilterInterface mAdvancedFilterManager;
	
	/**
	 * @param owner
	 */
	public AddAdvancedFilterDialog(Dialog owner, 
			AddAdvancedFilterInterface pAdvancedFilterManager) {
		
		super(owner);
		mAdvancedFilterManager = pAdvancedFilterManager;
		
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(572, 444);
		this.setTitle("Add advanced filter");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationByPlatform(true);
		this.setContentPane(getJContentPane());
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
			jContentPane.add(getAdvancedFilterPanel(), BorderLayout.CENTER);
			jContentPane.add(getJPanelAction(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes advancedFilterPanel	
	 * 	
	 * @return de.phleisch.app.chaoscrawler.gui.second_try.AdvancedFilterPanel	
	 */
	private AdvancedFilterPanel getAdvancedFilterPanel() {
		if (advancedFilterPanel == null) {
			advancedFilterPanel = new AdvancedFilterPanel();
		}
		return advancedFilterPanel;
	}

	/**
	 * This method initializes jPanelAction	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelAction() {
		if (jPanelAction == null) {
			jPanelAction = new JPanel();
			jPanelAction.setLayout(new FlowLayout());
			jPanelAction.add(getJButtonAdd(), null);
			jPanelAction.add(getJButtonCancel(), null);
		}
		return jPanelAction;
	}

	/**
	 * This method initializes jButtonAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText("Add");
			jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					RegExpFilterRule rule = advancedFilterPanel.buildRule();
					if(rule != null) {
						mAdvancedFilterManager.addAdvancedFilterRule(rule);
						dispose();
					}
					
				}
			});
		}
		return jButtonAdd;
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

}  //  @jve:decl-index=0:visual-constraint="10,10"
