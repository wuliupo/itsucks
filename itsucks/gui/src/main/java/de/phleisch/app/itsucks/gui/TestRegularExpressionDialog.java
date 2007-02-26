/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.01.2007
 */

package de.phleisch.app.itsucks.gui;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import de.phleisch.app.itsucks.gui.panel.EditRegularExpressionPanel;
import java.awt.Dimension;
import javax.swing.WindowConstants;

public class TestRegularExpressionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private EditRegularExpressionPanel editRegularExpressionPanel = null;

	/**
	 * @param owner
	 */
	public TestRegularExpressionDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(910, 396);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setLocationByPlatform(true);
		this.setTitle("Test your RegExp");
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
			jContentPane.add(getEditRegularExpressionPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes editRegularExpressionPanel	
	 * 	
	 * @return de.phleisch.app.itsucks.gui.panel.EditRegularExpressionPanel	
	 */
	private EditRegularExpressionPanel getEditRegularExpressionPanel() {
		if (editRegularExpressionPanel == null) {
			editRegularExpressionPanel = new EditRegularExpressionPanel();
		}
		return editRegularExpressionPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="52,11"
