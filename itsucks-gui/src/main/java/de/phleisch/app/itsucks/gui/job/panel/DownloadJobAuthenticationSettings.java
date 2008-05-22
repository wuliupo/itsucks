/*
 * DownloadJobAuthenticationSettings.java
 *
 * Created on 1. Mai 2008, 17:13
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.util.List;

import de.phleisch.app.itsucks.filter.download.http.impl.HttpAuthenticationFilter;
import de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel;
import de.phleisch.app.itsucks.gui.common.panel.EditListPanel;
import de.phleisch.app.itsucks.gui.common.panel.EditListPanel.ListElement;
import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.util.ExtendedListModel;
import de.phleisch.app.itsucks.gui.util.FieldValidator;
import de.phleisch.app.itsucks.gui.util.SwingUtils;
import de.phleisch.app.itsucks.io.http.impl.HttpAuthenticationCredentials;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  olli
 */
public class DownloadJobAuthenticationSettings extends javax.swing.JPanel implements EditJobCapable {

	private static final long serialVersionUID = -3488931651547521039L;
	
	/** Creates new form DownloadJobAuthenticationSettings */
    public DownloadJobAuthenticationSettings() {
        initComponents();
        initListPanel();
    }

	private void initListPanel() {
		
        EditListCallback editListCallback = new EditListCallback();
        editListCallback.enableEditArea(false);
		this.authenticationListPane
			.setLogic(editListCallback);

		this.authenticationListPane
				.registerDataField(hostTextField);
		this.authenticationListPane
				.registerDataField(userTextField);
		this.authenticationListPane
				.registerDataField(passwordTextField);
	}    

	public void loadJobPackage(SerializableJobPackage pJobPackage) {
		
		HttpAuthenticationFilter authenticationFilter = 
			(HttpAuthenticationFilter) pJobPackage.getFilterByType(HttpAuthenticationFilter.class);
		
		if(authenticationFilter != null) {
			
			ExtendedListModel model = this.authenticationListPane.getListModel();
			for (HttpAuthenticationCredentials credentials : 
					authenticationFilter.getCredentials()) {
				
				model.addElement(this.new AuthenticationListElement(credentials));
			}
		}
	}

	public void saveJobPackage(SerializableJobPackage pJobPackage) {
		
		HttpAuthenticationFilter authenticationFilter = 
			(HttpAuthenticationFilter) pJobPackage.getFilterByType(HttpAuthenticationFilter.class);
		
		ExtendedListModel listModel = this.authenticationListPane.getListModel();
		if(listModel.size() > 0) {
		
			if(authenticationFilter == null) {
				authenticationFilter = new HttpAuthenticationFilter();
				pJobPackage.addFilter(authenticationFilter);
			}
		
			Object[] elements = listModel.toArray();
			for (int i = 0; i < elements.length; i++) {
				AuthenticationListElement element = (AuthenticationListElement) elements[i];
				authenticationFilter.addCredentials(element);
			}
		}
	}

	public List<String> validateFields() {

		FieldValidator validator = new FieldValidator();

		ExtendedListModel model = this.authenticationListPane.getListModel();
		Object[] elements = model.toArray();
		for (int i = 0; i < elements.length; i++) {
			AuthenticationListElement element = (AuthenticationListElement) elements[i];
			
			validator.assertNotEmpty(element.getHost(),
				"Enter a valid host for the authentication. (Entry: " + (i+1) + ")");

			validator.assertNotEmpty(element.getUser(),
					"Enter a valid user for the authentication. (Entry: " + (i+1) + ")");			
			
			validator.assertNotEmpty(element.getPassword(),
					"Enter a valid password for the authentication. (Entry: " + (i+1) + ")");			
			
		}
		
		return validator.getErrors();
	}
	
	public class AuthenticationListElement
		extends HttpAuthenticationCredentials
		implements EditListPanel.ListElement {
		
		public AuthenticationListElement() {
			super();
		}
		
		public AuthenticationListElement(
				final HttpAuthenticationCredentials pCredentials) {
			super(pCredentials);
		}
		
		@Override
		public String toString() {
			String result = "<html>Host: "
					+ this.getHost()
					+ "<br>"
					+ "User: "
					+ this.getUser() 
					+ "</html>";

			return result;
		}
	}
	
	protected class EditListCallback implements
		EditListCallbackPanel.EditListCallbackInterface {

		public ListElement createNewElement() {
			
			AuthenticationListElement element = 
				new AuthenticationListElement();
			
			element.setHost("");
			element.setUser("");
			
			return element;
		}

		public void emptyEditArea() {
			loadEditArea(new AuthenticationListElement());
		}

		public void enableEditArea(boolean pEnable) {
			SwingUtils.setContainerAndChildrenEnabled(
					editAuthenticationPane, pEnable);
		}

		public void loadEditArea(ListElement pElement) {
			
			AuthenticationListElement element = (AuthenticationListElement) pElement;
			
			hostTextField.setText(element.getHost());
			userTextField.setText(element.getUser());
			passwordTextField.setText(element.getPassword());
		}

		public void updateListElement(ListElement pElement) {
			AuthenticationListElement element = (AuthenticationListElement) pElement;
			
			element.setHost(hostTextField.getText());
			element.setUser(userTextField.getText());
			element.setPassword(passwordTextField.getText());
		}
		
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        authenticationSettingsPane = new javax.swing.JPanel();
        authenticationSettingsLabel = new javax.swing.JLabel();
        authenticationListPane = new de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel();
        editAuthenticationPane = new javax.swing.JPanel();
        hostLabel = new javax.swing.JLabel();
        hostTextField = new javax.swing.JTextField();
        userLabel = new javax.swing.JLabel();
        userTextField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordTextField = new javax.swing.JTextField();

        authenticationSettingsPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "HTTP Authentication Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));

        authenticationSettingsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        authenticationSettingsLabel.setText("<html>Server authentications.</html>");

        editAuthenticationPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "User/Password", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12)));

        hostLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        hostLabel.setText("Host:");

        userLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        userLabel.setText("User:");

        passwordLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        passwordLabel.setText("Password:");

        javax.swing.GroupLayout editAuthenticationPaneLayout = new javax.swing.GroupLayout(editAuthenticationPane);
        editAuthenticationPane.setLayout(editAuthenticationPaneLayout);
        editAuthenticationPaneLayout.setHorizontalGroup(
            editAuthenticationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editAuthenticationPaneLayout.createSequentialGroup()
                .addGroup(editAuthenticationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editAuthenticationPaneLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(editAuthenticationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(userLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(7, 7, 7)
                        .addGroup(editAuthenticationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(userTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                            .addComponent(passwordTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editAuthenticationPaneLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(hostLabel)
                        .addGap(7, 7, 7)
                        .addComponent(hostTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)))
                .addContainerGap())
        );
        editAuthenticationPaneLayout.setVerticalGroup(
            editAuthenticationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editAuthenticationPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editAuthenticationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostLabel)
                    .addComponent(hostTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editAuthenticationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userLabel)
                    .addComponent(userTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(editAuthenticationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout authenticationSettingsPaneLayout = new javax.swing.GroupLayout(authenticationSettingsPane);
        authenticationSettingsPane.setLayout(authenticationSettingsPaneLayout);
        authenticationSettingsPaneLayout.setHorizontalGroup(
            authenticationSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(authenticationSettingsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(authenticationSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editAuthenticationPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(authenticationListPane, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .addComponent(authenticationSettingsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
                .addContainerGap())
        );
        authenticationSettingsPaneLayout.setVerticalGroup(
            authenticationSettingsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(authenticationSettingsPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(authenticationSettingsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(authenticationListPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editAuthenticationPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(authenticationSettingsPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(authenticationSettingsPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel authenticationListPane;
    private javax.swing.JLabel authenticationSettingsLabel;
    private javax.swing.JPanel authenticationSettingsPane;
    private javax.swing.JPanel editAuthenticationPane;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JTextField hostTextField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField passwordTextField;
    private javax.swing.JLabel userLabel;
    private javax.swing.JTextField userTextField;
    // End of variables declaration//GEN-END:variables
    
}
