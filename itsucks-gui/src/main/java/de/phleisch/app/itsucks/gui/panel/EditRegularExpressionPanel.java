/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 21.01.2007
 */

package de.phleisch.app.itsucks.gui.panel;

import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class EditRegularExpressionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JScrollPane jRegExpPane = null;
	private JScrollPane jTestTextPane = null;
	private JTextArea jTestTextField = null;
	private JTextArea jRegExpField = null;
	private JLabel jTestTextLabel = null;
	private JLabel jRegExpLabel = null;
	private JRadioButton jPartialRadioButton = null;
	private JRadioButton jFullRadioButton = null;
	private JButton jTestButton = null;
	private JLabel jResultLabel = null;
	private JLabel jMatchLabel = null;
	private JScrollPane jHelpTextPane = null;
	private JLabel jHelpText = null;
	
	/**
	 * This is the default constructor
	 */
	public EditRegularExpressionPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		jMatchLabel = new JLabel();
		jMatchLabel.setBounds(new Rectangle(380, 300, 81, 21));
		jMatchLabel.setText("");
		jResultLabel = new JLabel();
		jResultLabel.setBounds(new Rectangle(300, 300, 71, 21));
		jResultLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		jResultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		jResultLabel.setText("Result:");
		jRegExpLabel = new JLabel();
		jRegExpLabel.setBounds(new Rectangle(20, 20, 271, 21));
		jRegExpLabel.setText("Regular Expression:");
		jTestTextLabel = new JLabel();
		jTestTextLabel.setBounds(new Rectangle(20, 200, 261, 21));
		jTestTextLabel.setText("Test text for regular expressions:");
		this.setLayout(null);
		this.setSize(898, 359);
		this.add(getJHelpTextPane(), null);
		this.add(jRegExpLabel, null);
		this.add(getJRegExpPane(), null);
		this.add(jTestTextLabel, null);
		this.add(getJTestTextPane(), null);
		this.add(getJPartialRadioButton(), null);
		this.add(getJFullRadioButton(), null);
		this.add(getJTestButton(), null);
		this.add(jResultLabel, null);
		this.add(jMatchLabel, null);
		
		ButtonGroup group = new ButtonGroup();
		group.add(getJPartialRadioButton());
		group.add(getJFullRadioButton());
	}

	/**
	 * This method initializes jRegExpPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJRegExpPane() {
		if (jRegExpPane == null) {
			jRegExpPane = new JScrollPane();
			jRegExpPane.setBounds(new Rectangle(20, 40, 441, 131));
			jRegExpPane.setViewportView(getJRegExpField());
		}
		return jRegExpPane;
	}

	/**
	 * This method initializes jTestTextPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJTestTextPane() {
		if (jTestTextPane == null) {
			jTestTextPane = new JScrollPane();
			jTestTextPane.setBounds(new Rectangle(20, 220, 441, 61));
			jTestTextPane.setViewportView(getJTestTextField());
		}
		return jTestTextPane;
	}

	/**
	 * This method initializes jTestTextField	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTestTextField() {
		if (jTestTextField == null) {
			jTestTextField = new JTextArea();
		}
		return jTestTextField;
	}

	/**
	 * This method initializes jRegExpField	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJRegExpField() {
		if (jRegExpField == null) {
			jRegExpField = new JTextArea();
		}
		return jRegExpField;
	}

	/**
	 * This method initializes jPartialRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJPartialRadioButton() {
		if (jPartialRadioButton == null) {
			jPartialRadioButton = new JRadioButton();
			jPartialRadioButton.setBounds(new Rectangle(20, 290, 111, 21));
			jPartialRadioButton.setSelected(true);
			jPartialRadioButton.setText("Partial match");
		}
		return jPartialRadioButton;
	}

	/**
	 * This method initializes jFullRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJFullRadioButton() {
		if (jFullRadioButton == null) {
			jFullRadioButton = new JRadioButton();
			jFullRadioButton.setBounds(new Rectangle(20, 310, 101, 21));
			jFullRadioButton.setText("Full match");
		}
		return jFullRadioButton;
	}

	/**
	 * This method initializes jTestButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJTestButton() {
		if (jTestButton == null) {
			jTestButton = new JButton();
			jTestButton.setBounds(new Rectangle(160, 300, 121, 21));
			jTestButton.setText("Test it");
			jTestButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//<html><font color=\"green\">match</font></html>
					String regExp = jRegExpField.getText();
					String testText = jTestTextField.getText();
					
					Pattern pattern = null;
					try {
						pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
					} catch(PatternSyntaxException ex) {
						JOptionPane.showMessageDialog(null, ex.getMessage(), 
								"Regular expression error", JOptionPane.ERROR_MESSAGE );
						return;
					}
					
					Matcher m = pattern.matcher(testText);
					
					boolean result = false;
					if(jPartialRadioButton.isSelected()) {
						result = m.find();
					} else if(jFullRadioButton.isSelected()) {
						result = m.matches();
					}
					
					if(result) {
						jMatchLabel.setText("<html><font color=\"green\">match</font></html>");
					} else {
						jMatchLabel.setText("<html><font color=\"red\">no match</font></html>");
					}
				}
			});
		}
		return jTestButton;
	}

	/**
	 * This method initializes jHelpTextPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJHelpTextPane() {
		if (jHelpTextPane == null) {
			jHelpText = new JLabel();
			
			StringBuffer text;
			try {
				text = loadHelpText();
			} catch (IOException e) {
				text = new StringBuffer(e.getMessage());
			}
			jHelpText.setText(text.toString());
			jHelpText.setFont(new Font("Dialog", Font.PLAIN, 10));
			jHelpTextPane = new JScrollPane();
			jHelpTextPane.setBounds(new Rectangle(470, 10, 421, 341));
			jHelpTextPane.setViewportView(jHelpText);
		}
		return jHelpTextPane;
	}

	private StringBuffer loadHelpText() throws IOException {
		StringBuffer text = new StringBuffer();
		
		InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("RegularExpressionHelp.html");
		if(resourceAsStream == null) {
			text.append("Error loading help text!");
			return text;
		}
		
		InputStreamReader reader = new InputStreamReader(resourceAsStream);
		
		char buffer[] = new char[1024]; 
		while(reader.ready()) {
			int read = reader.read(buffer);
			text.append(buffer, 0, read);
		}
		reader.close();
		return text;
	}
	
	public void setRegularExpression(String pExpression) {
		this.jRegExpField.setText(pExpression);
	}
	
	public String getRegularExpression() {
		return this.jRegExpField.getText();
	}

}  //  @jve:decl-index=0:visual-constraint="26,15"
