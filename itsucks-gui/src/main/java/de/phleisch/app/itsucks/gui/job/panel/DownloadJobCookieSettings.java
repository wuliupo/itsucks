/*
 * DownloadJobCookieSettings.java
 *
 * Created on 1. Mai 2008, 17:13
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.util.List;

import javax.swing.JOptionPane;

import de.phleisch.app.itsucks.filter.download.http.impl.CookieFilter;
import de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel;
import de.phleisch.app.itsucks.gui.common.panel.EditListPanel;
import de.phleisch.app.itsucks.gui.common.panel.EditListPanel.ListElement;
import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.util.CookieParser;
import de.phleisch.app.itsucks.gui.util.ExtendedListModel;
import de.phleisch.app.itsucks.gui.util.FieldValidator;
import de.phleisch.app.itsucks.gui.util.Firefox3CookieParser;
import de.phleisch.app.itsucks.gui.util.IECookieParser;
import de.phleisch.app.itsucks.gui.util.MozillaCookieParser;
import de.phleisch.app.itsucks.gui.util.SwingUtils;
import de.phleisch.app.itsucks.io.http.impl.Cookie;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  olli
 */
public class DownloadJobCookieSettings extends javax.swing.JPanel implements EditJobCapable {
    
	private static final long serialVersionUID = 7000553342079429183L;

	/** Creates new form DownloadJobCookieSettings */
    public DownloadJobCookieSettings() {
        initComponents();
        initListPanel();
    }

	private void initListPanel() {
		
        EditListCallback editListCallback = new EditListCallback();
        editListCallback.enableEditArea(false);
		this.cookieListPane
			.setLogic(editListCallback);

		this.cookieListPane
				.registerDataField(nameTextField);
		this.cookieListPane
				.registerDataField(valueTextField);
		this.cookieListPane
				.registerDataField(domainTextField);
		this.cookieListPane
				.registerDataField(pathTextField);
	}    

	public void loadJobPackage(SerializableJobPackage pJobPackage) {
		
		CookieFilter cookieFilter = 
			(CookieFilter) pJobPackage.getFilterByType(CookieFilter.class);
		
		if(cookieFilter != null) {
			
			ExtendedListModel model = this.cookieListPane.getListModel();
			for (Cookie cookie : cookieFilter.getCookies()) {
				model.addElement(this.new CookieListElement(cookie));
			}
		}
	}

	public void saveJobPackage(SerializableJobPackage pJobPackage) {
		
		CookieFilter cookieFilter = 
			(CookieFilter) pJobPackage.getFilterByType(CookieFilter.class);
		
		ExtendedListModel listModel = this.cookieListPane.getListModel();
		if(listModel.size() > 0) {
		
			if(cookieFilter == null) {
				cookieFilter = new CookieFilter();
				pJobPackage.addFilter(cookieFilter);
			}
		
			Object[] elements = listModel.toArray();
			for (int i = 0; i < elements.length; i++) {
				CookieListElement element = (CookieListElement) elements[i];
				cookieFilter.addCookie(element);
			}
		}
	}

	public List<String> validateFields() {

		FieldValidator validator = new FieldValidator();

		ExtendedListModel model = this.cookieListPane.getListModel();
		Object[] elements = model.toArray();
		for (int i = 0; i < elements.length; i++) {
			CookieListElement element = (CookieListElement) elements[i];
			
			validator.assertNotEmpty(element.getName(),
				"Enter a valid name for the cookie. (Entry: " + (i+1) + ")");
			
			validator.assertNotEmpty(element.getDomain(),
					"Enter a valid domain for the cookie. (Entry: " + (i+1) + ")");
		}
		
		return validator.getErrors();
	}
	
	public class CookieListElement
		extends Cookie
		implements EditListPanel.ListElement {
		
		public CookieListElement() {
			super();
		}
		
		public CookieListElement(final Cookie pCookie) {
			super(pCookie);
		}
		
		@Override
		public String toString() {
			String result = "<html>Server: "
					+ getDomain() + getPath()
					+ "<br>"
					+ "Name: "
					+ getName() 
					+ "</html>";

			return result;
		}
	}
	
	protected class EditListCallback implements
		EditListCallbackPanel.EditListCallbackInterface {

		public ListElement createNewElement() {
			
			CookieListElement element = new CookieListElement();
			element.setDomain("");
			element.setName("");
			element.setPath("");
			
			return element;
		}

		public void emptyEditArea() {
			loadEditArea(new CookieListElement());
		}

		public void enableEditArea(boolean pEnable) {
			SwingUtils.setContainerAndChildrenEnabled(
					editCookiePane, pEnable);
		}

		public void loadEditArea(ListElement pElement) {
			
			CookieListElement element = (CookieListElement) pElement;
			
			nameTextField.setText(element.getName());
			valueTextField.setText(element.getValue());
			domainTextField.setText(element.getDomain());
			pathTextField.setText(element.getPath());
		}

		public void updateListElement(ListElement pElement) {
			CookieListElement element = (CookieListElement) pElement;
			
			element.setName(nameTextField.getText());
			element.setValue(valueTextField.getText());
			element.setDomain(domainTextField.getText());
			element.setPath(pathTextField.getText());
		}
		
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cookieSettingsPane = new javax.swing.JPanel();
        cookieSettingsLabel = new javax.swing.JLabel();
        cookieListPane = new de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel();
        editCookiePane = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        valueLabel = new javax.swing.JLabel();
        valueTextField = new javax.swing.JTextField();
        domainLabel = new javax.swing.JLabel();
        domainTextField = new javax.swing.JTextField();
        pathLabel = new javax.swing.JLabel();
        pathTextField = new javax.swing.JTextField();
        cookieParserPane = new javax.swing.JPanel();
        cookieParserLabel = new javax.swing.JLabel();
        formatLabel = new javax.swing.JLabel();
        formatComboBox = new javax.swing.JComboBox();
        dataLabel = new javax.swing.JLabel();
        dataScrollPane = new javax.swing.JScrollPane();
        dataTextArea = new javax.swing.JTextArea();
        parseButton = new javax.swing.JButton();

        cookieSettingsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cookie Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));

        cookieSettingsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        cookieSettingsLabel.setText("<html>Configuration for cookies which will be used by ItSucks.</html>");

        editCookiePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cookie", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));

        nameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        nameLabel.setText("Name:");

        valueLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        valueLabel.setText("Value:");

        domainLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        domainLabel.setText("Domain:");

        pathLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        pathLabel.setText("Path:");

        javax.swing.GroupLayout editCookiePaneLayout = new javax.swing.GroupLayout(editCookiePane);
        editCookiePane.setLayout(editCookiePaneLayout);
        editCookiePaneLayout.setHorizontalGroup(
            editCookiePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editCookiePaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editCookiePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pathLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(domainLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(valueLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(7, 7, 7)
                .addGroup(editCookiePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addComponent(valueTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addComponent(domainTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                    .addComponent(pathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                .addContainerGap())
        );
        editCookiePaneLayout.setVerticalGroup(
            editCookiePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editCookiePaneLayout.createSequentialGroup()
                .addGroup(editCookiePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editCookiePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valueLabel)
                    .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editCookiePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(domainLabel)
                    .addComponent(domainTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editCookiePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathLabel)
                    .addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cookieParserPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cookie Parser", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));

        cookieParserLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        cookieParserLabel.setText("<html>To add cookies from an browser, please paste the data into the data field.<br>More details can be found in the help.</html>");

        formatLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        formatLabel.setText("Format:");

        formatComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        formatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Firefox2 (Cookies.txt)", "Firefox3 (SQLite Export)", "Internet Explorer (user@host.domain[n].txt)" }));

        dataLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        dataLabel.setText("Data:");

        dataTextArea.setColumns(20);
        dataTextArea.setFont(new java.awt.Font("Dialog", 0, 12));
        dataTextArea.setRows(5);
        dataScrollPane.setViewportView(dataTextArea);

        parseButton.setFont(new java.awt.Font("Dialog", 0, 12));
        parseButton.setText("Parse Cookie String");
        parseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parseButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cookieParserPaneLayout = new javax.swing.GroupLayout(cookieParserPane);
        cookieParserPane.setLayout(cookieParserPaneLayout);
        cookieParserPaneLayout.setHorizontalGroup(
            cookieParserPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cookieParserPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cookieParserPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cookieParserLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                    .addGroup(cookieParserPaneLayout.createSequentialGroup()
                        .addComponent(formatLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formatComboBox, 0, 235, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cookieParserPaneLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(dataLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(cookieParserPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(parseButton)
                            .addComponent(dataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))))
                .addContainerGap())
        );
        cookieParserPaneLayout.setVerticalGroup(
            cookieParserPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cookieParserPaneLayout.createSequentialGroup()
                .addComponent(cookieParserLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cookieParserPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(formatLabel)
                    .addComponent(formatComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cookieParserPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parseButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout cookieSettingsPaneLayout = new javax.swing.GroupLayout(cookieSettingsPane);
        cookieSettingsPane.setLayout(cookieSettingsPaneLayout);
        cookieSettingsPaneLayout.setHorizontalGroup(
            cookieSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cookieSettingsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cookieSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cookieParserPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editCookiePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cookieListPane, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                    .addComponent(cookieSettingsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                .addContainerGap())
        );
        cookieSettingsPaneLayout.setVerticalGroup(
            cookieSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cookieSettingsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cookieSettingsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cookieListPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editCookiePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cookieParserPane, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cookieSettingsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cookieSettingsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void parseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parseButtonActionPerformed
    	
    	CookieParser parser = null;
    	if(formatComboBox.getSelectedIndex() == 0) {
    		parser = new MozillaCookieParser();
    	} else if(formatComboBox.getSelectedIndex() == 1) {
    		parser = new Firefox3CookieParser();
    	} else if(formatComboBox.getSelectedIndex() == 2) {
    		parser = new IECookieParser();    		
    	}
    	
    	List<Cookie> parsedCookies = null;
    	try {
    		parsedCookies = parser.parseCookies(dataTextArea.getText());
    	} catch(Exception ex) {
    		JOptionPane.showMessageDialog(this, ex.getMessage(),
    				"Validation errors", JOptionPane.ERROR_MESSAGE);
//    		ex.printStackTrace();
    	}
    	
    	if(parsedCookies != null) {
    		ExtendedListModel model = this.cookieListPane.getListModel();
    		
    		CookieListElement cookieListElement = null;
    		for (Cookie cookie : parsedCookies) {
    			cookieListElement = this.new CookieListElement(cookie);
   				model.addElement(cookieListElement);
			}
    		
    		//select last added element
    		if(cookieListElement != null) {
    			this.cookieListPane.getList().setSelectedValue(cookieListElement, true);
    		}
    	}
    	
}//GEN-LAST:event_parseButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel cookieListPane;
    private javax.swing.JLabel cookieParserLabel;
    private javax.swing.JPanel cookieParserPane;
    private javax.swing.JLabel cookieSettingsLabel;
    private javax.swing.JPanel cookieSettingsPane;
    private javax.swing.JLabel dataLabel;
    private javax.swing.JScrollPane dataScrollPane;
    private javax.swing.JTextArea dataTextArea;
    private javax.swing.JLabel domainLabel;
    private javax.swing.JTextField domainTextField;
    private javax.swing.JPanel editCookiePane;
    private javax.swing.JComboBox formatComboBox;
    private javax.swing.JLabel formatLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton parseButton;
    private javax.swing.JLabel pathLabel;
    private javax.swing.JTextField pathTextField;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables
    
}
