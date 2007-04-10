/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 25.01.2007
 */

package de.phleisch.app.itsucks.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import de.phleisch.app.itsucks.gui.panel.EditRegularExpressionPanel;

public class EditRegularExpressionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private EditRegularExpressionPanel editRegularExpression = null;

	private JPanel jActionPanel = null;

	private JButton jOkButton = null;

	private JButton jCancelButton = null;

	private boolean mCanceled = false;
	
	/**
	 * @param owner
	 */
	public EditRegularExpressionDialog(Dialog owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(916, 428);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setLocationByPlatform(true);
		this.setTitle("Edit regular expression");
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
			jContentPane.add(getEditRegularExpression(), BorderLayout.CENTER);
			jContentPane.add(getJActionPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes editRegularExpression	
	 * 	
	 * @return de.phleisch.app.itsucks.gui.panel.EditRegularExpression	
	 */
	private EditRegularExpressionPanel getEditRegularExpression() {
		if (editRegularExpression == null) {
			editRegularExpression = new EditRegularExpressionPanel();
		}
		return editRegularExpression;
	}

	/**
	 * This method initializes jActionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJActionPanel() {
		if (jActionPanel == null) {
			jActionPanel = new JPanel();
			jActionPanel.setLayout(new FlowLayout());
			jActionPanel.add(getJOkButton(), null);
			jActionPanel.add(getJCancelButton(), null);
		}
		return jActionPanel;
	}

	/**
	 * This method initializes jOkButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJOkButton() {
		if (jOkButton == null) {
			jOkButton = new JButton();
			jOkButton.setText("Ok");
			jOkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					EditRegularExpressionDialog.this.setVisible(false);
					dispose();
				}
			});
		}
		return jOkButton;
	}

	/**
	 * This method initializes jCancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = new JButton();
			jCancelButton.setText("Cancel");
			jCancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					EditRegularExpressionDialog.this.setVisible(false);
					mCanceled = true;
					dispose();
				}
			});
		}
		return jCancelButton;
	}
	
	public void setRegularExpression(String pExpression) {
		this.editRegularExpression.setRegularExpression(pExpression);
	}
	
	public String getRegularExpression() {
		return this.editRegularExpression.getRegularExpression();
	}

	public boolean isCanceled() {
		return mCanceled;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
