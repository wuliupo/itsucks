/*
 * DownloadJobConnectionSettingsPanel.java
 *
 * Created on 1. Mai 2008, 12:18
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.util.List;

import javax.swing.JPanel;

import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.util.FieldValidator;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  olli
 */
public class DownloadJobConnectionSettingsPanel extends JPanel implements EditJobCapable {
    
	private static final long serialVersionUID = -3590680545290495451L;
	
	/** Creates new form DownloadJobConnectionSettingsPanel */
    public DownloadJobConnectionSettingsPanel() {
    	init();
    }
    
    protected void init() {
    	initComponents();
    }
    
	public void loadJobPackage(SerializableJobPackage pJobPackage) {
		
		UrlDownloadJob job = (UrlDownloadJob) pJobPackage.getFirstJob();
		
		HttpRetrieverConfiguration httpRetrieverConfiguration = (HttpRetrieverConfiguration) pJobPackage
			.getContextParameter(HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION);

		
		//load basic connection settings
		this.maxRetriesTextField.setText(String
				.valueOf(job.getMaxRetryCount()));

		if (httpRetrieverConfiguration != null) {

			if (httpRetrieverConfiguration.getMaxConnectionsPerServer() != null) {
				this.maxConnectionsTextField
						.setText(String.valueOf(httpRetrieverConfiguration
								.getMaxConnectionsPerServer()));
			}
			
			this.sendReferralCheckBox.setSelected(
					httpRetrieverConfiguration.isSendReferer());

			if (httpRetrieverConfiguration.isProxyEnabled()) {
				this.enableProxyCheckBox
						.setSelected(true);
				this.proxyServerTextField
						.setText(httpRetrieverConfiguration.getProxyServer());
				this.proxyPortTextField.setText(String
						.valueOf(httpRetrieverConfiguration.getProxyPort()));

				if (httpRetrieverConfiguration.isProxyAuthenticationEnabled()) {

					this.enableAuthenticationCheckBox
							.setSelected(true);
					this.authenticationUserTextField
							.setText(httpRetrieverConfiguration.getProxyUser());
					this.authenticationPasswordTextField
							.setText(httpRetrieverConfiguration
									.getProxyPassword());
				}
			}
			
			if(httpRetrieverConfiguration.getUserAgent() != null) {
				this.userAgentCheckBox
					.setSelected(true);
				this.userAgentTextField
					.setText(httpRetrieverConfiguration.getUserAgent());
			}
			
			if (httpRetrieverConfiguration.getBandwidthLimit() != null) {
				
				final int kbytes = 1024;
				final int mbytes = 1024 * 1024;
				
				int limit = httpRetrieverConfiguration.getBandwidthLimit();
				int index = 0;
				
				if((limit % mbytes) == 0) {
					limit /= mbytes;
					index = 2;
				} else if((limit % kbytes) == 0) {
					limit /= kbytes;
					index = 1;
				} 
				
				this.enableBandwidthLimitCheckBox.setSelected(true);
				this.bandwidthLimitComboBox.setSelectedIndex(index);
				this.bandwidthLimitTextField.setText(String.valueOf(limit));
			}
		}
		
		
	}

	public void saveJobPackage(SerializableJobPackage pJobPackage) {
		
		HttpRetrieverConfiguration retrieverConfiguration = (HttpRetrieverConfiguration) pJobPackage
			.getContextParameter(HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION);
		if(retrieverConfiguration == null) {
			retrieverConfiguration = new HttpRetrieverConfiguration();
		}
		
		int maxRetryCount = (Integer
				.parseInt(this.maxRetriesTextField
						.getText()));

		retrieverConfiguration.setMaxConnectionsPerServer(Integer
				.parseInt(this.maxConnectionsTextField
						.getText()));

		//proxy configuration
		if (this.enableProxyCheckBox.isSelected()) {
			retrieverConfiguration.setProxyEnabled(true);

			retrieverConfiguration
					.setProxyServer(this.proxyServerTextField
							.getText());

			retrieverConfiguration.setProxyPort(Integer
					.parseInt(this.proxyPortTextField
							.getText()));
		} else {
			retrieverConfiguration.setProxyEnabled(false);
		}

		if (this.enableAuthenticationCheckBox
				.isSelected()) {
			retrieverConfiguration.setProxyAuthenticationEnabled(true);

			retrieverConfiguration
					.setProxyUser(this.authenticationUserTextField
							.getText());

			retrieverConfiguration
					.setProxyPassword(this.authenticationPasswordTextField
							.getText());
		} else {
			retrieverConfiguration.setProxyAuthenticationEnabled(false);
		}
		
		//user agent
		if (this.userAgentCheckBox.isSelected()) {
			retrieverConfiguration
					.setUserAgent(this.userAgentTextField
							.getText());
		}
		
		//bandwidth limit
		if (this.enableBandwidthLimitCheckBox.isSelected()) {
			int bandwidthLimit = Integer.parseInt(this.bandwidthLimitTextField
					.getText());
			int multiplier = (int) Math.pow(1024, this.bandwidthLimitComboBox.getSelectedIndex());
			bandwidthLimit *= multiplier;
			
			retrieverConfiguration.setBandwidthLimit(bandwidthLimit);
		}
		
		//build result
		for (Job job : pJobPackage.getJobs()) {
			UrlDownloadJob downloadJob = (UrlDownloadJob) job;
			downloadJob.setMaxRetryCount(maxRetryCount);
		}

		pJobPackage
				.putContextParameter(
						HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION,
						retrieverConfiguration);
		
	}	
	
	public List<String> validateFields() {

		FieldValidator validator = new FieldValidator();

		validator.assertInteger(this.maxConnectionsTextField.getText(),
				"Enter a valid number of max. connections per server.");

		validator.assertInteger(this.maxRetriesTextField.getText(),
				"Enter a valid number of max. retries.");

		if (this.enableProxyCheckBox.isSelected()) {

			validator.assertNotEmpty(this.proxyServerTextField.getText(),
					"Enter a valid proxy server.");

			validator.assertInteger(this.proxyPortTextField.getText(),
					"Enter a valid proxy port.");
		}

		if (this.enableAuthenticationCheckBox.isSelected()) {

			validator.assertNotEmpty(
					this.authenticationUserTextField.getText(),
					"Enter a valid proxy user.");

			validator.assertNotEmpty(this.authenticationPasswordTextField
					.getText(), "Enter a valid proxy password.");
		}
		
		if(this.enableBandwidthLimitCheckBox.isSelected()) {
			
			validator.assertInteger(this.bandwidthLimitTextField.getText(),
				"Enter a valid number for the bandwidth limit.");
		}

		return validator.getErrors();
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        connectionSettingsPanel = new javax.swing.JPanel();
        maxConnectionsLabel = new javax.swing.JLabel();
        maxConnectionsTextField = new javax.swing.JTextField();
        maxRetriesLabel = new javax.swing.JLabel();
        maxRetriesTextField = new javax.swing.JTextField();
        sendReferralCheckBox = new javax.swing.JCheckBox();
        proxySettingsPanel = new javax.swing.JPanel();
        enableProxyCheckBox = new javax.swing.JCheckBox();
        proxyServerLabel = new javax.swing.JLabel();
        proxyServerTextField = new javax.swing.JTextField();
        proxyPortLabel = new javax.swing.JLabel();
        proxyPortTextField = new javax.swing.JTextField();
        enableAuthenticationCheckBox = new javax.swing.JCheckBox();
        authenticationUserLabel = new javax.swing.JLabel();
        authenticationUserTextField = new javax.swing.JTextField();
        authenticationPasswordLabel = new javax.swing.JLabel();
        authenticationPasswordTextField = new javax.swing.JTextField();
        userAgentPanel = new javax.swing.JPanel();
        userAgentCheckBox = new javax.swing.JCheckBox();
        userAgentLabel = new javax.swing.JLabel();
        userAgentTextField = new javax.swing.JTextField();
        bandwidthLimitPanel = new javax.swing.JPanel();
        bandwidthLimitLabel = new javax.swing.JLabel();
        bandwidthLimitTextField = new javax.swing.JTextField();
        bandwidthLimitComboBox = new javax.swing.JComboBox();
        enableBandwidthLimitCheckBox = new javax.swing.JCheckBox();

        connectionSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Connection Settings"));

        maxConnectionsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        maxConnectionsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maxConnectionsLabel.setText("Max. connections per server:");

        maxConnectionsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        maxRetriesLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        maxRetriesLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maxRetriesLabel.setText("Max. retries before giving up:");

        maxRetriesTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        sendReferralCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        sendReferralCheckBox.setText("Send referral");

        javax.swing.GroupLayout connectionSettingsPanelLayout = new javax.swing.GroupLayout(connectionSettingsPanel);
        connectionSettingsPanel.setLayout(connectionSettingsPanelLayout);
        connectionSettingsPanelLayout.setHorizontalGroup(
            connectionSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectionSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectionSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(connectionSettingsPanelLayout.createSequentialGroup()
                        .addGroup(connectionSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxRetriesLabel)
                            .addComponent(maxConnectionsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(connectionSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(maxConnectionsTextField)
                            .addComponent(maxRetriesTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)))
                    .addComponent(sendReferralCheckBox))
                .addContainerGap(198, Short.MAX_VALUE))
        );
        connectionSettingsPanelLayout.setVerticalGroup(
            connectionSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(connectionSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connectionSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxConnectionsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxConnectionsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(connectionSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxRetriesLabel)
                    .addComponent(maxRetriesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendReferralCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        proxySettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Proxy Settings"));
        proxySettingsPanel.setEnabled(false);

        enableProxyCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        enableProxyCheckBox.setText("Enable Proxy");
        enableProxyCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableProxyCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                enableProxyCheckBoxStateChanged(evt);
            }
        });

        proxyServerLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        proxyServerLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        proxyServerLabel.setText("Server:");
        proxyServerLabel.setEnabled(false);

        proxyServerTextField.setEnabled(false);

        proxyPortLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        proxyPortLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        proxyPortLabel.setText("Port:");
        proxyPortLabel.setEnabled(false);

        proxyPortTextField.setEnabled(false);

        enableAuthenticationCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        enableAuthenticationCheckBox.setText("Enable proxy authentication");
        enableAuthenticationCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableAuthenticationCheckBox.setEnabled(false);
        enableAuthenticationCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                enableAuthenticationCheckBoxStateChanged(evt);
            }
        });

        authenticationUserLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        authenticationUserLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        authenticationUserLabel.setText("User:");
        authenticationUserLabel.setEnabled(false);

        authenticationUserTextField.setEnabled(false);

        authenticationPasswordLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        authenticationPasswordLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        authenticationPasswordLabel.setText("Password:");
        authenticationPasswordLabel.setEnabled(false);

        authenticationPasswordTextField.setEnabled(false);

        javax.swing.GroupLayout proxySettingsPanelLayout = new javax.swing.GroupLayout(proxySettingsPanel);
        proxySettingsPanel.setLayout(proxySettingsPanelLayout);
        proxySettingsPanelLayout.setHorizontalGroup(
            proxySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proxySettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(proxySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(proxySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(proxySettingsPanelLayout.createSequentialGroup()
                            .addComponent(authenticationUserLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(authenticationUserTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(authenticationPasswordLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(authenticationPasswordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(proxySettingsPanelLayout.createSequentialGroup()
                            .addComponent(enableAuthenticationCheckBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(proxySettingsPanelLayout.createSequentialGroup()
                        .addGroup(proxySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(enableProxyCheckBox, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, proxySettingsPanelLayout.createSequentialGroup()
                                .addComponent(proxyServerLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(proxyServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(proxyPortLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(proxyPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(72, 72, 72))
        );
        proxySettingsPanelLayout.setVerticalGroup(
            proxySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, proxySettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enableProxyCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(proxySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proxyServerLabel)
                    .addComponent(proxyServerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(proxyPortLabel)
                    .addComponent(proxyPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enableAuthenticationCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(proxySettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authenticationUserLabel)
                    .addComponent(authenticationPasswordLabel)
                    .addComponent(authenticationPasswordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(authenticationUserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        userAgentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("User Agent"));

        userAgentCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        userAgentCheckBox.setText("Override User Agent");
        userAgentCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                userAgentCheckBoxStateChanged(evt);
            }
        });

        userAgentLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        userAgentLabel.setText("User Agent:");
        userAgentLabel.setEnabled(false);

        userAgentTextField.setText("Mozilla/5.0");
        userAgentTextField.setEnabled(false);

        javax.swing.GroupLayout userAgentPanelLayout = new javax.swing.GroupLayout(userAgentPanel);
        userAgentPanel.setLayout(userAgentPanelLayout);
        userAgentPanelLayout.setHorizontalGroup(
            userAgentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userAgentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(userAgentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userAgentCheckBox)
                    .addGroup(userAgentPanelLayout.createSequentialGroup()
                        .addComponent(userAgentLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userAgentTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)))
                .addContainerGap())
        );
        userAgentPanelLayout.setVerticalGroup(
            userAgentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userAgentPanelLayout.createSequentialGroup()
                .addComponent(userAgentCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(userAgentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userAgentLabel)
                    .addComponent(userAgentTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bandwidthLimitPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Bandwidth Limit"));

        bandwidthLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bandwidthLimitLabel.setText("<html>Set a bandwidth limitiation per download thread.</html>");

        bandwidthLimitTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        bandwidthLimitTextField.setEnabled(false);

        bandwidthLimitComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        bandwidthLimitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "byte/s", "kbyte/s", "mbyte/s" }));
        bandwidthLimitComboBox.setSelectedIndex(1);
        bandwidthLimitComboBox.setEnabled(false);

        enableBandwidthLimitCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        enableBandwidthLimitCheckBox.setText("Enable Bandwidth Limitation");
        enableBandwidthLimitCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                enableBandwidthLimitCheckBoxStateChanged(evt);
            }
        });

        javax.swing.GroupLayout bandwidthLimitPanelLayout = new javax.swing.GroupLayout(bandwidthLimitPanel);
        bandwidthLimitPanel.setLayout(bandwidthLimitPanelLayout);
        bandwidthLimitPanelLayout.setHorizontalGroup(
            bandwidthLimitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bandwidthLimitPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bandwidthLimitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bandwidthLimitLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                    .addComponent(enableBandwidthLimitCheckBox)
                    .addGroup(bandwidthLimitPanelLayout.createSequentialGroup()
                        .addComponent(bandwidthLimitTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bandwidthLimitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        bandwidthLimitPanelLayout.setVerticalGroup(
            bandwidthLimitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bandwidthLimitPanelLayout.createSequentialGroup()
                .addComponent(bandwidthLimitLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enableBandwidthLimitCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(bandwidthLimitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bandwidthLimitTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bandwidthLimitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 475, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(bandwidthLimitPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(userAgentPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(connectionSettingsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(proxySettingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(connectionSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(proxySettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(userAgentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(bandwidthLimitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void enableProxyCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_enableProxyCheckBoxStateChanged
        boolean enabled = enableProxyCheckBox.isSelected();
        
        proxyServerLabel.setEnabled(enabled);
        proxyServerTextField.setEnabled(enabled);
        proxyPortLabel.setEnabled(enabled);
        proxyPortTextField.setEnabled(enabled);
        
        if (!enabled) {
            enableAuthenticationCheckBox.setSelected(false);
        }
        enableAuthenticationCheckBox.setEnabled(enabled);
    }//GEN-LAST:event_enableProxyCheckBoxStateChanged

    private void enableAuthenticationCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_enableAuthenticationCheckBoxStateChanged
        boolean enabled = enableAuthenticationCheckBox.isSelected();
        
        authenticationUserLabel.setEnabled(enabled);
        authenticationUserTextField.setEnabled(enabled);
        authenticationPasswordLabel.setEnabled(enabled);
        authenticationPasswordTextField.setEnabled(enabled);
    }//GEN-LAST:event_enableAuthenticationCheckBoxStateChanged

    private void userAgentCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_userAgentCheckBoxStateChanged
        boolean enabled = userAgentCheckBox.isSelected();

        userAgentLabel.setEnabled(enabled);
        userAgentTextField.setEnabled(enabled);
    }//GEN-LAST:event_userAgentCheckBoxStateChanged

    private void enableBandwidthLimitCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_enableBandwidthLimitCheckBoxStateChanged
        boolean enabled = enableBandwidthLimitCheckBox.isSelected();

        bandwidthLimitTextField.setEnabled(enabled);
        bandwidthLimitComboBox.setEnabled(enabled);
    }//GEN-LAST:event_enableBandwidthLimitCheckBoxStateChanged
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authenticationPasswordLabel;
    private javax.swing.JTextField authenticationPasswordTextField;
    private javax.swing.JLabel authenticationUserLabel;
    private javax.swing.JTextField authenticationUserTextField;
    private javax.swing.JComboBox bandwidthLimitComboBox;
    private javax.swing.JLabel bandwidthLimitLabel;
    private javax.swing.JPanel bandwidthLimitPanel;
    private javax.swing.JTextField bandwidthLimitTextField;
    private javax.swing.JPanel connectionSettingsPanel;
    private javax.swing.JCheckBox enableAuthenticationCheckBox;
    private javax.swing.JCheckBox enableBandwidthLimitCheckBox;
    private javax.swing.JCheckBox enableProxyCheckBox;
    private javax.swing.JLabel maxConnectionsLabel;
    private javax.swing.JTextField maxConnectionsTextField;
    private javax.swing.JLabel maxRetriesLabel;
    private javax.swing.JTextField maxRetriesTextField;
    private javax.swing.JLabel proxyPortLabel;
    private javax.swing.JTextField proxyPortTextField;
    private javax.swing.JLabel proxyServerLabel;
    private javax.swing.JTextField proxyServerTextField;
    private javax.swing.JPanel proxySettingsPanel;
    private javax.swing.JCheckBox sendReferralCheckBox;
    private javax.swing.JCheckBox userAgentCheckBox;
    private javax.swing.JLabel userAgentLabel;
    private javax.swing.JPanel userAgentPanel;
    private javax.swing.JTextField userAgentTextField;
    // End of variables declaration//GEN-END:variables
    
}
