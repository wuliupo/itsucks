/*
 * DownloadJobSpecialRulesPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import de.phleisch.app.itsucks.filter.download.http.impl.ChangeHttpResponseCodeBehaviourFilter;
import de.phleisch.app.itsucks.filter.download.http.impl.ChangeHttpResponseCodeBehaviourFilter.HttpResponseCodeBehaviourHostConfig;
import de.phleisch.app.itsucks.filter.download.impl.FileSizeFilter;
import de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel;
import de.phleisch.app.itsucks.gui.common.panel.EditListPanel;
import de.phleisch.app.itsucks.gui.common.panel.EditListPanel.ListElement;
import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.util.ExtendedListModel;
import de.phleisch.app.itsucks.gui.util.FieldValidator;
import de.phleisch.app.itsucks.gui.util.ListItem;
import de.phleisch.app.itsucks.gui.util.SwingUtils;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour.Action;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour.ResponseCodeRange;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  __USER__
 */
public class DownloadJobSpecialRulesPanel extends javax.swing.JPanel 
		implements EditJobCapable {

	private static final long serialVersionUID = -2550810599331718712L;

	public List<ListItem<Action>> mHttpResponseCodeFilterActions = createFilterActions();

	protected List<ListItem<Action>> createFilterActions() {

		List<ListItem<Action>> list = new ArrayList<ListItem<Action>>();
		list.add(new ListItem<Action>("Retry", Action.FAILED_BUT_RETRYABLE));
		list.add(new ListItem<Action>("Ok", Action.OK));
		list.add(new ListItem<Action>("Error", Action.FAILED));

		return list;
	}

	/** Creates new form DownloadJobSpecialRulesPanel */
	public DownloadJobSpecialRulesPanel() {
		initComponents();
		this.httpStatusCodeBehaviourEditListPanel
				.setLogic(new EditListCallback());

		this.httpStatusCodeBehaviourEditListPanel
				.registerDataField(httpStatusCodeBehaviourHostnameTextField);
		this.httpStatusCodeBehaviourEditListPanel
				.registerDataField(httpStatusCodeBehaviourStatusCodeFromTextField);
		this.httpStatusCodeBehaviourEditListPanel
				.registerDataField(httpStatusCodeBehaviourStatusCodeToTextField);
		this.httpStatusCodeBehaviourEditListPanel
				.registerDataField(httpStatusCodeBehaviourActionComboBox);
		this.httpStatusCodeBehaviourEditListPanel
				.registerDataField(httpStatusCodeBehaviourWaitTextField);

	}

	public void loadJobPackage(SerializableJobPackage pJobPackage) {
		
		FileSizeFilter fileSizeFilter = 
			(FileSizeFilter) pJobPackage.getFilterByType(FileSizeFilter.class);
		
		ChangeHttpResponseCodeBehaviourFilter httpResponseCodeFilter = 
			(ChangeHttpResponseCodeBehaviourFilter) 
			pJobPackage.getFilterByType(ChangeHttpResponseCodeBehaviourFilter.class);
		
		
		if (fileSizeFilter != null) {

			this.fileSizeEnableCheckBox
					.setSelected(true);

			this.fileSizeMinField
					.setText(fileSizeFilter.getMinSizeAsText());
			this.fileSizeMaxField
					.setText(fileSizeFilter.getMaxSizeAsText());
			this.fileSizeNotKnownComboBox
					.setSelectedIndex(fileSizeFilter.isAcceptWhenLengthNotSet() ? 0
							: 1);
		}
		

		if(httpResponseCodeFilter != null) {
			
			this.httpStatusCodeBehaviourCheckBox
				.setSelected(true);
			
			ExtendedListModel model = 
				this.httpStatusCodeBehaviourEditListPanel.getListModel();
			
			for (HttpResponseCodeBehaviourHostConfig hostConfig : 
				httpResponseCodeFilter.getConfigList()) {
				
				HttpRetrieverResponseCodeBehaviour responseCodeBehaviour = 
					hostConfig.getResponseCodeBehaviour();
				
				for (ResponseCodeRange responseCodeRange : 
					responseCodeBehaviour.getConfigurationList()) {
					
					HttpStatusCodeBehaviourListElement element = 
						this.new HttpStatusCodeBehaviourListElement();
					
					element.setHostnameRegexp(hostConfig.getHostnameRegexp());
					element.setResponseCodeFrom(Integer.toString(responseCodeRange.getResponseCodeFrom()));
					element.setResponseCodeTo(Integer.toString(responseCodeRange.getResponseCodeTo()));
					element.setAction(
							this.findIndexForHttpRetrieverResponseCodeBehaviour(
									responseCodeRange.getAction()));
					if(responseCodeRange.getTimeToWaitBetweenRetry() != null) {
						element.setTimeToWaitBetweenRetry(Long.toString(responseCodeRange.getTimeToWaitBetweenRetry()));
					}

					model.addElement(element);
				}
			}
		}
		
	}

	public void saveJobPackage(SerializableJobPackage pJobPackage) {

		FileSizeFilter fileSizeFilter = 
			(FileSizeFilter) pJobPackage.getFilterByType(FileSizeFilter.class);
		
		//file size filter
		if (this.fileSizeEnableCheckBox
				.isSelected()) {

			if(fileSizeFilter == null) {
				fileSizeFilter = new FileSizeFilter();
				pJobPackage.addFilter(fileSizeFilter);
			}

			fileSizeFilter
					.setMinSizeAsText(this.fileSizeMinField
							.getText().trim());
			fileSizeFilter
					.setMaxSizeAsText(this.fileSizeMaxField
							.getText().trim());

			fileSizeFilter
					.setAcceptWhenLengthNotSet(this.fileSizeNotKnownComboBox
							.getSelectedIndex() > 0 ? false : true);
		}
		
		ChangeHttpResponseCodeBehaviourFilter httpResponseCodeFilter = 
			(ChangeHttpResponseCodeBehaviourFilter) 
			pJobPackage.getFilterByType(ChangeHttpResponseCodeBehaviourFilter.class);

		//http status code filter
		if (this.httpStatusCodeBehaviourCheckBox
				.isSelected()) {
			
			if(httpResponseCodeFilter == null) {
				httpResponseCodeFilter = 
					new ChangeHttpResponseCodeBehaviourFilter();
				pJobPackage.addFilter(httpResponseCodeFilter);
			}
			
			ExtendedListModel listModel = 
				this.httpStatusCodeBehaviourEditListPanel.getListModel();
			
			Object[] elements = listModel.toArray();
			for (int i = 0; i < elements.length; i++) {
				HttpStatusCodeBehaviourListElement element = 
					(HttpStatusCodeBehaviourListElement) elements[i];
				
				HttpRetrieverResponseCodeBehaviour.Action action = 
					this.mHttpResponseCodeFilterActions.get(element.getAction()).getValue();
				
				ResponseCodeRange responseCodeRange = 
					new ResponseCodeRange(
							Integer.parseInt(element.getResponseCodeFrom()),
							Integer.parseInt(element.getResponseCodeTo()),
							action
					);
				
				if(action.equals(HttpRetrieverResponseCodeBehaviour.Action.FAILED_BUT_RETRYABLE)) {
					responseCodeRange.setTimeToWaitBetweenRetry(
							Long.parseLong(element.getTimeToWaitBetweenRetry()));
				}
				
				HttpRetrieverResponseCodeBehaviour responseCodeBehaviour =
					new HttpRetrieverResponseCodeBehaviour();
				responseCodeBehaviour.add(responseCodeRange);
				
				HttpResponseCodeBehaviourHostConfig hostConfig = 
					new HttpResponseCodeBehaviourHostConfig(element.getHostnameRegexp(), 
							responseCodeBehaviour);
				
				httpResponseCodeFilter.addConfig(hostConfig);
			}
		}
		
	}
	
	
	public List<String> validateFields() {

		FieldValidator validator = new FieldValidator();

		if (fileSizeEnableCheckBox.isSelected()) {

			String regExp = "^([-]?[0-9]{1,})[ ]*(KB|kb|MB|mb|GB|gb|$)$";

			validator.assertRegExpResult(regExp, this.fileSizeMinField
					.getText().trim(),
					"Enter a valid value for the minimum file size.");

			validator.assertRegExpResult(regExp, this.fileSizeMaxField
					.getText().trim(),
					"Enter a valid value for the maximum file size.");
		}

		if (httpStatusCodeBehaviourCheckBox.isSelected()) {

			ExtendedListModel listModel = httpStatusCodeBehaviourEditListPanel
					.getListModel();

			Object[] elements = listModel.toArray();
			for (int i = 0; i < elements.length; i++) {
				HttpStatusCodeBehaviourListElement element = (HttpStatusCodeBehaviourListElement) elements[i];

				validator.assertValidRegExp(element.getHostnameRegexp(),
						"Enter a valid regular expression for the hostname in HTTP Status Filter "
								+ (i + 1));

				validator.assertInteger(element.getResponseCodeFrom(),
						"Enter a valid value for the 'from' status code in HTTP Status Filter "
								+ (i + 1));
				validator.assertInteger(element.getResponseCodeTo(),
						"Enter a valid value for the 'to' status code in HTTP Status Filter "
								+ (i + 1));

				if (element.getAction() == findIndexForHttpRetrieverResponseCodeBehaviour(Action.FAILED_BUT_RETRYABLE)) {

					validator.assertInteger(
							element.getTimeToWaitBetweenRetry(),
							"Enter a valid value for 'wait between retry' in HTTP Status Filter "
									+ (i + 1));

				}
			}
		}

		return validator.getErrors();
	}

	public class HttpStatusCodeBehaviourListElement implements
			EditListPanel.ListElement {

		private String mHostnameRegexp = "";
		private String mResponseCodeFrom = "";
		private String mResponseCodeTo = "";
		private int mAction;
		private String mTimeToWaitBetweenRetry = "";
		private int mQueueBehaviour;

		public String getHostnameRegexp() {
			return mHostnameRegexp;
		}

		public void setHostnameRegexp(String pHostnameRegexp) {
			mHostnameRegexp = pHostnameRegexp;
		}

		public String getResponseCodeFrom() {
			return mResponseCodeFrom;
		}

		public void setResponseCodeFrom(String pResponseCodeFrom) {
			mResponseCodeFrom = pResponseCodeFrom;
		}

		public String getResponseCodeTo() {
			return mResponseCodeTo;
		}

		public void setResponseCodeTo(String pResponseCodeTo) {
			mResponseCodeTo = pResponseCodeTo;
		}

		public int getAction() {
			return mAction;
		}

		public void setAction(int pAction) {
			mAction = pAction;
		}

		public String getTimeToWaitBetweenRetry() {
			return mTimeToWaitBetweenRetry;
		}

		public void setTimeToWaitBetweenRetry(String pTimeToWaitBetweenRetry) {
			mTimeToWaitBetweenRetry = pTimeToWaitBetweenRetry;
		}

		public int getQueueBehaviour() {
			return mQueueBehaviour;
		}

		public void setQueueBehaviour(int pQueueBehaviour) {
			mQueueBehaviour = pQueueBehaviour;
		}

		@Override
		public String toString() {
			String result = "<html>Hostname RegExp: '"
					+ getHostnameRegexp()
					+ "' / Status Code: "
					+ this.getResponseCodeFrom()
					+ " - "
					+ this.getResponseCodeTo()
					+ "<br>"
					+ "Action: "
					+ httpStatusCodeBehaviourActionComboBox.getModel()
							.getElementAt(this.getAction());
			if (this.getAction() == 0) {
				result += "<br>" + "Waiting time: "
						+ this.getTimeToWaitBetweenRetry() + "ms ";
			}
			result += "</html>";

			return result;
		}

	}

	protected class EditListCallback implements
			EditListCallbackPanel.EditListCallbackInterface {

		public ListElement createNewElement() {
			HttpStatusCodeBehaviourListElement element = new HttpStatusCodeBehaviourListElement();
			return element;
		}

		public void emptyEditArea() {
			loadEditArea(new HttpStatusCodeBehaviourListElement());
		}

		public void enableEditArea(boolean pEnable) {
			SwingUtils.setContainerAndChildrenEnabled(
					httpStatusCodeBehaviourSubPanel, pEnable);
		}

		public void loadEditArea(ListElement pElement) {

			HttpStatusCodeBehaviourListElement element = (HttpStatusCodeBehaviourListElement) pElement;

			httpStatusCodeBehaviourHostnameTextField.setText(element
					.getHostnameRegexp());
			httpStatusCodeBehaviourStatusCodeFromTextField.setText(element
					.getResponseCodeFrom());
			httpStatusCodeBehaviourStatusCodeToTextField.setText(element
					.getResponseCodeTo());
			httpStatusCodeBehaviourActionComboBox.setSelectedIndex(element
					.getAction());
			httpStatusCodeBehaviourWaitTextField.setText(element
					.getTimeToWaitBetweenRetry());

		}

		public void updateListElement(ListElement pElement) {

			HttpStatusCodeBehaviourListElement element = (HttpStatusCodeBehaviourListElement) pElement;

			element.setHostnameRegexp(httpStatusCodeBehaviourHostnameTextField
					.getText());
			element
					.setResponseCodeFrom(httpStatusCodeBehaviourStatusCodeFromTextField
							.getText());
			element
					.setResponseCodeTo(httpStatusCodeBehaviourStatusCodeToTextField
							.getText());
			element.setAction(httpStatusCodeBehaviourActionComboBox
					.getSelectedIndex());
			element
					.setTimeToWaitBetweenRetry(httpStatusCodeBehaviourWaitTextField
							.getText());

		}
	}

	public int findIndexForHttpRetrieverResponseCodeBehaviour(
			HttpRetrieverResponseCodeBehaviour.Action pAction) {
		for (ListItem<Action> item : mHttpResponseCodeFilterActions) {
			if (item.getValue().equals(pAction)) {
				return mHttpResponseCodeFilterActions.indexOf(item);
			}
		}

		return -1;
	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileSizePanel = new javax.swing.JPanel();
        fileSizeLabel = new javax.swing.JLabel();
        fileSizeEnableCheckBox = new javax.swing.JCheckBox();
        fileSizeMinLabel = new javax.swing.JLabel();
        fileSizeMinField = new javax.swing.JTextField();
        fileSizeMaxLabel = new javax.swing.JLabel();
        fileSizeMaxField = new javax.swing.JTextField();
        fileSizeUnitExplanationLabel = new javax.swing.JLabel();
        fileSizeNotKnownLabel = new javax.swing.JLabel();
        fileSizeNotKnownComboBox = new javax.swing.JComboBox();
        httpStatusCodeBehaviourPanel = new javax.swing.JPanel();
        httpStatusCodeBehaviourLabel = new javax.swing.JLabel();
        httpStatusCodeBehaviourCheckBox = new javax.swing.JCheckBox();
        httpStatusCodeBehaviourEditListPanel = new de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel();
        httpStatusCodeBehaviourSubPanel = new javax.swing.JPanel();
        httpStatusCodeBehaviourHostnameLabel = new javax.swing.JLabel();
        httpStatusCodeBehaviourHostnameTextField = new javax.swing.JTextField();
        httpStatusCodeBehaviourHostnameDescLabel = new javax.swing.JLabel();
        httpStatusCodeBehaviourStatusCodeLabel = new javax.swing.JLabel();
        httpStatusCodeBehaviourStatusCodeFromTextField = new javax.swing.JTextField();
        httpStatusCodeBehaviourStatusCodeToPanel = new javax.swing.JLabel();
        httpStatusCodeBehaviourStatusCodeToTextField = new javax.swing.JTextField();
        httpStatusCodeBehaviourActionLabel = new javax.swing.JLabel();
        httpStatusCodeBehaviourActionComboBox = new javax.swing.JComboBox();
        httpStatusCodeBehaviourWaitLabel = new javax.swing.JLabel();
        httpStatusCodeBehaviourWaitTextField = new javax.swing.JTextField();
        httpStatusCodeBehaviourWaitMsLabel = new javax.swing.JLabel();

        fileSizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File Size Filter"));

        fileSizeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fileSizeLabel.setText("<html>Set file size limits to prevent ItSucks from saving certain files on the disk. To get the file size, a connection has to be opened to the server. This can be slow when the server response time is bad. It is not guranteed that the server can send the file size (eg. dynamic sites).</html>");

        fileSizeEnableCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        fileSizeEnableCheckBox.setText("Enable File Size Filter");
        fileSizeEnableCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fileSizeEnableCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fileSizeEnableCheckBoxStateChanged(evt);
            }
        });

        fileSizeMinLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fileSizeMinLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fileSizeMinLabel.setText("Min. file size:");
        fileSizeMinLabel.setEnabled(false);
        fileSizeMinLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        fileSizeMinField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fileSizeMinField.setText("-1");
        fileSizeMinField.setEnabled(false);

        fileSizeMaxLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fileSizeMaxLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fileSizeMaxLabel.setText("Max. file size:");
        fileSizeMaxLabel.setEnabled(false);
        fileSizeMaxLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        fileSizeMaxField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fileSizeMaxField.setText("-1");
        fileSizeMaxField.setEnabled(false);

        fileSizeUnitExplanationLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fileSizeUnitExplanationLabel.setText("<html>To disable a limit, enter -1 as value. <br>Possible units are: KB (1024 byte), MB (1024 KB) and GB (1024 MB). If no unit is given, the value is interpreted as bytes. </html>");
        fileSizeUnitExplanationLabel.setMinimumSize(new java.awt.Dimension(45, 30));

        fileSizeNotKnownLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fileSizeNotKnownLabel.setText("In case the file size is not sent by server:");
        fileSizeNotKnownLabel.setEnabled(false);

        fileSizeNotKnownComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        fileSizeNotKnownComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Save file", "Reject File" }));
        fileSizeNotKnownComboBox.setEnabled(false);

        org.jdesktop.layout.GroupLayout fileSizePanelLayout = new org.jdesktop.layout.GroupLayout(fileSizePanel);
        fileSizePanel.setLayout(fileSizePanelLayout);
        fileSizePanelLayout.setHorizontalGroup(
            fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fileSizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fileSizeLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .add(fileSizePanelLayout.createSequentialGroup()
                        .add(fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(fileSizeMinLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(fileSizeMaxLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(fileSizeMinField)
                            .add(fileSizeMaxField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fileSizeUnitExplanationLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                    .add(fileSizePanelLayout.createSequentialGroup()
                        .add(fileSizeEnableCheckBox)
                        .add(62, 62, 62))
                    .add(fileSizePanelLayout.createSequentialGroup()
                        .add(fileSizeNotKnownLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fileSizeNotKnownComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        fileSizePanelLayout.setVerticalGroup(
            fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fileSizePanelLayout.createSequentialGroup()
                .add(fileSizeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(fileSizePanelLayout.createSequentialGroup()
                        .add(fileSizeEnableCheckBox)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(fileSizeMinLabel)
                            .add(fileSizeMinField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(fileSizeMaxLabel)
                            .add(fileSizeMaxField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(fileSizeUnitExplanationLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileSizePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fileSizeNotKnownLabel)
                    .add(fileSizeNotKnownComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        httpStatusCodeBehaviourPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("HTTP Status Code Behaviour"));

        httpStatusCodeBehaviourLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourLabel.setText("<html>Define the behaviour of ItSucks for certain HTTP Status Codes. For instance if an server sends 403 (Forbidden) after to many downloads, a retry + waiting time can be defined here.</html>");
        httpStatusCodeBehaviourLabel.setPreferredSize(new java.awt.Dimension(700, 15));

        httpStatusCodeBehaviourCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourCheckBox.setText("Enable HTTP Status Code Filter");
        httpStatusCodeBehaviourCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        httpStatusCodeBehaviourCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                httpStatusCodeBehaviourCheckBoxStateChanged(evt);
            }
        });

        httpStatusCodeBehaviourEditListPanel.setEnabled(false);

        httpStatusCodeBehaviourSubPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("HTTP Status Filter"));

        httpStatusCodeBehaviourHostnameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourHostnameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        httpStatusCodeBehaviourHostnameLabel.setText("Hostname:");
        httpStatusCodeBehaviourHostnameLabel.setEnabled(false);

        httpStatusCodeBehaviourHostnameTextField.setEnabled(false);

        httpStatusCodeBehaviourHostnameDescLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourHostnameDescLabel.setText("<html>(regular expression, partial match)</html>");
        httpStatusCodeBehaviourHostnameDescLabel.setEnabled(false);

        httpStatusCodeBehaviourStatusCodeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourStatusCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        httpStatusCodeBehaviourStatusCodeLabel.setText("Status Code:");
        httpStatusCodeBehaviourStatusCodeLabel.setEnabled(false);

        httpStatusCodeBehaviourStatusCodeFromTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        httpStatusCodeBehaviourStatusCodeFromTextField.setEnabled(false);
        httpStatusCodeBehaviourStatusCodeFromTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                httpStatusCodeBehaviourStatusCodeFromTextFieldFocusLost(evt);
            }
        });

        httpStatusCodeBehaviourStatusCodeToPanel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourStatusCodeToPanel.setText("to");
        httpStatusCodeBehaviourStatusCodeToPanel.setEnabled(false);

        httpStatusCodeBehaviourStatusCodeToTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        httpStatusCodeBehaviourStatusCodeToTextField.setEnabled(false);

        httpStatusCodeBehaviourActionLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourActionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        httpStatusCodeBehaviourActionLabel.setText("Action:");
        httpStatusCodeBehaviourActionLabel.setEnabled(false);

        httpStatusCodeBehaviourActionComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourActionComboBox.setModel(new DefaultComboBoxModel(mHttpResponseCodeFilterActions.toArray()));
        httpStatusCodeBehaviourActionComboBox.setEnabled(false);
        httpStatusCodeBehaviourActionComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                httpStatusCodeBehaviourActionComboBoxItemStateChanged(evt);
            }
        });

        httpStatusCodeBehaviourWaitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourWaitLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        httpStatusCodeBehaviourWaitLabel.setText("Wait between retry:");
        httpStatusCodeBehaviourWaitLabel.setEnabled(false);

        httpStatusCodeBehaviourWaitTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        httpStatusCodeBehaviourWaitTextField.setEnabled(false);

        httpStatusCodeBehaviourWaitMsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourWaitMsLabel.setText("ms");

        org.jdesktop.layout.GroupLayout httpStatusCodeBehaviourSubPanelLayout = new org.jdesktop.layout.GroupLayout(httpStatusCodeBehaviourSubPanel);
        httpStatusCodeBehaviourSubPanel.setLayout(httpStatusCodeBehaviourSubPanelLayout);
        httpStatusCodeBehaviourSubPanelLayout.setHorizontalGroup(
            httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(httpStatusCodeBehaviourSubPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, httpStatusCodeBehaviourActionLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, httpStatusCodeBehaviourStatusCodeLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, httpStatusCodeBehaviourWaitLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpStatusCodeBehaviourHostnameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(httpStatusCodeBehaviourHostnameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpStatusCodeBehaviourSubPanelLayout.createSequentialGroup()
                        .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(httpStatusCodeBehaviourSubPanelLayout.createSequentialGroup()
                                    .add(httpStatusCodeBehaviourStatusCodeFromTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(httpStatusCodeBehaviourStatusCodeToPanel))
                                .add(httpStatusCodeBehaviourWaitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 68, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(httpStatusCodeBehaviourActionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(httpStatusCodeBehaviourHostnameDescLabel)
                            .add(httpStatusCodeBehaviourStatusCodeToTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(httpStatusCodeBehaviourWaitMsLabel))))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        httpStatusCodeBehaviourSubPanelLayout.setVerticalGroup(
            httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(httpStatusCodeBehaviourSubPanelLayout.createSequentialGroup()
                .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(httpStatusCodeBehaviourHostnameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpStatusCodeBehaviourHostnameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(httpStatusCodeBehaviourStatusCodeLabel)
                    .add(httpStatusCodeBehaviourStatusCodeFromTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpStatusCodeBehaviourStatusCodeToPanel)
                    .add(httpStatusCodeBehaviourStatusCodeToTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(httpStatusCodeBehaviourActionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpStatusCodeBehaviourActionLabel)
                    .add(httpStatusCodeBehaviourHostnameDescLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(httpStatusCodeBehaviourWaitMsLabel)
                    .add(httpStatusCodeBehaviourWaitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpStatusCodeBehaviourWaitLabel))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout httpStatusCodeBehaviourPanelLayout = new org.jdesktop.layout.GroupLayout(httpStatusCodeBehaviourPanel);
        httpStatusCodeBehaviourPanel.setLayout(httpStatusCodeBehaviourPanelLayout);
        httpStatusCodeBehaviourPanelLayout.setHorizontalGroup(
            httpStatusCodeBehaviourPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(httpStatusCodeBehaviourPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(httpStatusCodeBehaviourPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(httpStatusCodeBehaviourCheckBox)
                    .add(httpStatusCodeBehaviourEditListPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .add(httpStatusCodeBehaviourSubPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(httpStatusCodeBehaviourLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE))
                .addContainerGap())
        );
        httpStatusCodeBehaviourPanelLayout.setVerticalGroup(
            httpStatusCodeBehaviourPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(httpStatusCodeBehaviourPanelLayout.createSequentialGroup()
                .add(httpStatusCodeBehaviourLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(httpStatusCodeBehaviourCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(httpStatusCodeBehaviourEditListPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(httpStatusCodeBehaviourSubPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, fileSizePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, httpStatusCodeBehaviourPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(fileSizePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(httpStatusCodeBehaviourPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

	private void httpStatusCodeBehaviourStatusCodeFromTextFieldFocusLost(
			java.awt.event.FocusEvent evt) {

		//copy the 'from' value into the 'to' field when to is empty 
		String from = httpStatusCodeBehaviourStatusCodeFromTextField.getText();
		String to = httpStatusCodeBehaviourStatusCodeToTextField.getText();
		if (from.trim().length() > 0 && to.trim().length() == 0) {
			httpStatusCodeBehaviourStatusCodeToTextField.setText(from);
		}

	}

	private void httpStatusCodeBehaviourActionComboBoxItemStateChanged(
			java.awt.event.ItemEvent evt) {

		boolean enabled = false;

		//if action is retry (0) and http status filter is active
		if (this.httpStatusCodeBehaviourActionComboBox.getSelectedIndex() == 0
				&& this.httpStatusCodeBehaviourActionComboBox.isEnabled()) {

			enabled = true;
		}

		this.httpStatusCodeBehaviourWaitLabel.setEnabled(enabled);
		this.httpStatusCodeBehaviourWaitTextField.setEnabled(enabled);
		this.httpStatusCodeBehaviourWaitMsLabel.setEnabled(enabled);
	}

	private void httpStatusCodeBehaviourCheckBoxStateChanged(
			javax.swing.event.ChangeEvent evt) {

		boolean enabled = this.httpStatusCodeBehaviourCheckBox.isSelected();

		this.httpStatusCodeBehaviourEditListPanel.setEnabled(enabled);
	}

	//GEN-FIRST:event_fileSizeEnableCheckBoxStateChanged
	private void fileSizeEnableCheckBoxStateChanged(
			javax.swing.event.ChangeEvent evt) {

		boolean enabled = fileSizeEnableCheckBox.isSelected();

		fileSizeMinLabel.setEnabled(enabled);
		fileSizeMinField.setEnabled(enabled);
		fileSizeMaxLabel.setEnabled(enabled);
		fileSizeMaxField.setEnabled(enabled);
		fileSizeNotKnownLabel.setEnabled(enabled);
		fileSizeNotKnownComboBox.setEnabled(enabled);

	}//GEN-LAST:event_fileSizeEnableCheckBoxStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JCheckBox fileSizeEnableCheckBox;
    protected javax.swing.JLabel fileSizeLabel;
    protected javax.swing.JTextField fileSizeMaxField;
    protected javax.swing.JLabel fileSizeMaxLabel;
    protected javax.swing.JTextField fileSizeMinField;
    protected javax.swing.JLabel fileSizeMinLabel;
    protected javax.swing.JComboBox fileSizeNotKnownComboBox;
    protected javax.swing.JLabel fileSizeNotKnownLabel;
    protected javax.swing.JPanel fileSizePanel;
    protected javax.swing.JLabel fileSizeUnitExplanationLabel;
    protected javax.swing.JComboBox httpStatusCodeBehaviourActionComboBox;
    protected javax.swing.JLabel httpStatusCodeBehaviourActionLabel;
    protected javax.swing.JCheckBox httpStatusCodeBehaviourCheckBox;
    protected de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel httpStatusCodeBehaviourEditListPanel;
    protected javax.swing.JLabel httpStatusCodeBehaviourHostnameDescLabel;
    protected javax.swing.JLabel httpStatusCodeBehaviourHostnameLabel;
    protected javax.swing.JTextField httpStatusCodeBehaviourHostnameTextField;
    protected javax.swing.JLabel httpStatusCodeBehaviourLabel;
    protected javax.swing.JPanel httpStatusCodeBehaviourPanel;
    protected javax.swing.JTextField httpStatusCodeBehaviourStatusCodeFromTextField;
    protected javax.swing.JLabel httpStatusCodeBehaviourStatusCodeLabel;
    protected javax.swing.JLabel httpStatusCodeBehaviourStatusCodeToPanel;
    protected javax.swing.JTextField httpStatusCodeBehaviourStatusCodeToTextField;
    protected javax.swing.JPanel httpStatusCodeBehaviourSubPanel;
    protected javax.swing.JLabel httpStatusCodeBehaviourWaitLabel;
    protected javax.swing.JLabel httpStatusCodeBehaviourWaitMsLabel;
    protected javax.swing.JTextField httpStatusCodeBehaviourWaitTextField;
    // End of variables declaration//GEN-END:variables

}