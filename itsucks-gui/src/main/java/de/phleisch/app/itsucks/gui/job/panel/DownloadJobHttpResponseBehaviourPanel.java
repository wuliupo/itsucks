/*
 * DownloadJobHttpResponseBehaviourPanel.java
 *
 * Created on 4. Mai 2008, 20:15
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import de.phleisch.app.itsucks.filter.download.http.impl.ChangeHttpResponseCodeBehaviourFilter;
import de.phleisch.app.itsucks.filter.download.http.impl.ChangeHttpResponseCodeBehaviourFilter.HttpResponseCodeBehaviourHostConfig;
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
 * @author  olli
 */
public class DownloadJobHttpResponseBehaviourPanel extends javax.swing.JPanel
		implements EditJobCapable {
    
	private static final long serialVersionUID = -6458439163993474019L;
	
	protected List<ListItem<Action>> mHttpResponseCodeFilterActions = createFilterActions();

	protected List<ListItem<Action>> createFilterActions() {

		List<ListItem<Action>> list = new ArrayList<ListItem<Action>>();
		list.add(new ListItem<Action>("Retry", Action.FAILED_BUT_RETRYABLE));
		list.add(new ListItem<Action>("Ok", Action.OK));
		list.add(new ListItem<Action>("Error", Action.FAILED));

		return list;
	}
	
    /** Creates new form DownloadJobHttpResponseBehaviourPanel */
    public DownloadJobHttpResponseBehaviourPanel() {
        initComponents();
        initListPanel();
    }
    
	private void initListPanel() {
		EditListCallback editListCallback = new EditListCallback();
		editListCallback.enableEditArea(false);
		this.httpStatusCodeBehaviourEditListPanel
				.setLogic(editListCallback);

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
		
		ChangeHttpResponseCodeBehaviourFilter httpResponseCodeFilter = 
			(ChangeHttpResponseCodeBehaviourFilter) 
			pJobPackage.getFilterByType(ChangeHttpResponseCodeBehaviourFilter.class);

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
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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

        httpStatusCodeBehaviourHostnameDescLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourHostnameDescLabel.setText("<html>(regular expression, partial match)</html>");

        httpStatusCodeBehaviourStatusCodeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourStatusCodeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        httpStatusCodeBehaviourStatusCodeLabel.setText("Status Code:");

        httpStatusCodeBehaviourStatusCodeFromTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        httpStatusCodeBehaviourStatusCodeFromTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                httpStatusCodeBehaviourStatusCodeFromTextFieldFocusLost(evt);
            }
        });

        httpStatusCodeBehaviourStatusCodeToPanel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourStatusCodeToPanel.setText("to");

        httpStatusCodeBehaviourStatusCodeToTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        httpStatusCodeBehaviourActionLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourActionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        httpStatusCodeBehaviourActionLabel.setText("Action:");

        httpStatusCodeBehaviourActionComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourActionComboBox.setModel(new DefaultComboBoxModel(mHttpResponseCodeFilterActions.toArray()));
        httpStatusCodeBehaviourActionComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                httpStatusCodeBehaviourActionComboBoxItemStateChanged(evt);
            }
        });

        httpStatusCodeBehaviourWaitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourWaitLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        httpStatusCodeBehaviourWaitLabel.setText("Wait between retry:");

        httpStatusCodeBehaviourWaitTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        httpStatusCodeBehaviourWaitMsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        httpStatusCodeBehaviourWaitMsLabel.setText("ms");

        javax.swing.GroupLayout httpStatusCodeBehaviourSubPanelLayout = new javax.swing.GroupLayout(httpStatusCodeBehaviourSubPanel);
        httpStatusCodeBehaviourSubPanel.setLayout(httpStatusCodeBehaviourSubPanelLayout);
        httpStatusCodeBehaviourSubPanelLayout.setHorizontalGroup(
            httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(httpStatusCodeBehaviourSubPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(httpStatusCodeBehaviourActionLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(httpStatusCodeBehaviourStatusCodeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                    .addComponent(httpStatusCodeBehaviourWaitLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(httpStatusCodeBehaviourHostnameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(httpStatusCodeBehaviourHostnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(httpStatusCodeBehaviourSubPanelLayout.createSequentialGroup()
                        .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(httpStatusCodeBehaviourSubPanelLayout.createSequentialGroup()
                                    .addComponent(httpStatusCodeBehaviourStatusCodeFromTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(httpStatusCodeBehaviourStatusCodeToPanel))
                                .addComponent(httpStatusCodeBehaviourWaitTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(httpStatusCodeBehaviourActionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(httpStatusCodeBehaviourHostnameDescLabel)
                            .addComponent(httpStatusCodeBehaviourStatusCodeToTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(httpStatusCodeBehaviourWaitMsLabel))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        httpStatusCodeBehaviourSubPanelLayout.setVerticalGroup(
            httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(httpStatusCodeBehaviourSubPanelLayout.createSequentialGroup()
                .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(httpStatusCodeBehaviourHostnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(httpStatusCodeBehaviourHostnameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(httpStatusCodeBehaviourStatusCodeLabel)
                    .addComponent(httpStatusCodeBehaviourStatusCodeFromTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(httpStatusCodeBehaviourStatusCodeToPanel)
                    .addComponent(httpStatusCodeBehaviourStatusCodeToTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(httpStatusCodeBehaviourActionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(httpStatusCodeBehaviourActionLabel)
                    .addComponent(httpStatusCodeBehaviourHostnameDescLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(httpStatusCodeBehaviourSubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(httpStatusCodeBehaviourWaitMsLabel)
                    .addComponent(httpStatusCodeBehaviourWaitTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(httpStatusCodeBehaviourWaitLabel))
                .addContainerGap())
        );

        javax.swing.GroupLayout httpStatusCodeBehaviourPanelLayout = new javax.swing.GroupLayout(httpStatusCodeBehaviourPanel);
        httpStatusCodeBehaviourPanel.setLayout(httpStatusCodeBehaviourPanelLayout);
        httpStatusCodeBehaviourPanelLayout.setHorizontalGroup(
            httpStatusCodeBehaviourPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(httpStatusCodeBehaviourPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(httpStatusCodeBehaviourPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(httpStatusCodeBehaviourCheckBox)
                    .addComponent(httpStatusCodeBehaviourEditListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                    .addComponent(httpStatusCodeBehaviourSubPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(httpStatusCodeBehaviourLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
                .addContainerGap())
        );
        httpStatusCodeBehaviourPanelLayout.setVerticalGroup(
            httpStatusCodeBehaviourPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(httpStatusCodeBehaviourPanelLayout.createSequentialGroup()
                .addComponent(httpStatusCodeBehaviourLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(httpStatusCodeBehaviourCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(httpStatusCodeBehaviourEditListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(httpStatusCodeBehaviourSubPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 504, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(httpStatusCodeBehaviourPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 458, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(httpStatusCodeBehaviourPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox httpStatusCodeBehaviourActionComboBox;
    private javax.swing.JLabel httpStatusCodeBehaviourActionLabel;
    private javax.swing.JCheckBox httpStatusCodeBehaviourCheckBox;
    private de.phleisch.app.itsucks.gui.common.panel.EditListCallbackPanel httpStatusCodeBehaviourEditListPanel;
    private javax.swing.JLabel httpStatusCodeBehaviourHostnameDescLabel;
    private javax.swing.JLabel httpStatusCodeBehaviourHostnameLabel;
    private javax.swing.JTextField httpStatusCodeBehaviourHostnameTextField;
    private javax.swing.JLabel httpStatusCodeBehaviourLabel;
    private javax.swing.JPanel httpStatusCodeBehaviourPanel;
    private javax.swing.JTextField httpStatusCodeBehaviourStatusCodeFromTextField;
    private javax.swing.JLabel httpStatusCodeBehaviourStatusCodeLabel;
    private javax.swing.JLabel httpStatusCodeBehaviourStatusCodeToPanel;
    private javax.swing.JTextField httpStatusCodeBehaviourStatusCodeToTextField;
    private javax.swing.JPanel httpStatusCodeBehaviourSubPanel;
    private javax.swing.JLabel httpStatusCodeBehaviourWaitLabel;
    private javax.swing.JLabel httpStatusCodeBehaviourWaitMsLabel;
    private javax.swing.JTextField httpStatusCodeBehaviourWaitTextField;
    // End of variables declaration//GEN-END:variables
    
}
