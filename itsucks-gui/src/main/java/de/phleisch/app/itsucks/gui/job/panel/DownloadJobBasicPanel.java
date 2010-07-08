/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * olli
 * Created on 26.08.2007
 */
package de.phleisch.app.itsucks.gui.job.panel;

import java.awt.Dialog;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.gui.common.EditUrlListDialog;
import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.util.FieldValidator;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobManagerConfiguration;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.persistence.SerializableDispatcherConfiguration;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  olli
 */
public class DownloadJobBasicPanel extends JPanel implements EditJobCapable {

	private static final long serialVersionUID = 6676129664345121404L;

	private List<URL> mUrlList = null;

	/** Creates new form DownloadJobMainPanel */
	public DownloadJobBasicPanel() {
		init();
	}

	protected void init() {
		initComponents();
	}
	
	public void loadJobPackage(SerializableJobPackage pJobPackage) {
		
		UrlDownloadJob job = (UrlDownloadJob) pJobPackage.getFirstJob();
		
		SerializableDispatcherConfiguration dispatcherConfiguration = pJobPackage
			.getDispatcherConfiguration();
		
		//load basic panel
		this.nameTextField.setText(job.getName());
		
		List<URL> urlList = new ArrayList<URL>();
		for (Job job_it : pJobPackage.getJobs()) {
			urlList.add(((UrlDownloadJob) job_it).getUrl());
		}
		this.setUrlList(urlList);
		this.savePathTextField.setText(job.getSavePath()
				.getAbsolutePath());

		if (dispatcherConfiguration != null) {
			if (dispatcherConfiguration.getWorkerThreads() != null) {
				this.workingThreadsTextField
						.setText(String.valueOf(dispatcherConfiguration
								.getWorkerThreads()));
			}
		}
		
		//memory settings
		JobManagerConfiguration jobManagerConfiguration = (JobManagerConfiguration) pJobPackage
			.getContextParameter(JobManagerConfiguration.CONTEXT_PARAMETER_JOB_MANAGER_CONFIGURATION);
		if(jobManagerConfiguration != null) {
			this.dropFinishedLinksCheckbox.setSelected(jobManagerConfiguration.isDropFinishedJobs());
			this.dropIgnoredLinksCheckbox.setSelected(jobManagerConfiguration.isDropIgnoredJobs());
		}
	}

	public void saveJobPackage(SerializableJobPackage pJobPackage) {
		
		//build download job
		DownloadJobFactory jobFactory = (DownloadJobFactory) SpringContextSingelton
				.getApplicationContext().getBean("JobFactory");
		UrlDownloadJob basicJob = jobFactory.createDownloadJob();
		basicJob.setIgnoreFilter(true);
		basicJob.setState(UrlDownloadJob.STATE_OPEN);
		
		List<URL> urls = new ArrayList<URL>();
		
		SerializableDispatcherConfiguration dispatcherConfiguration = pJobPackage
			.getDispatcherConfiguration();
		if(dispatcherConfiguration == null) {
			dispatcherConfiguration = new SerializableDispatcherConfiguration();
		}

		//memory settings
		JobManagerConfiguration jobManagerConfiguration = (JobManagerConfiguration) pJobPackage
			.getContextParameter(JobManagerConfiguration.CONTEXT_PARAMETER_JOB_MANAGER_CONFIGURATION);
		if(jobManagerConfiguration == null) {
			jobManagerConfiguration = new JobManagerConfiguration();
			pJobPackage.putContextParameter(
					JobManagerConfiguration.CONTEXT_PARAMETER_JOB_MANAGER_CONFIGURATION, jobManagerConfiguration);
		}
		jobManagerConfiguration.setDropFinishedJobs(this.dropFinishedLinksCheckbox.isSelected());
		jobManagerConfiguration.setDropIgnoredJobs(this.dropIgnoredLinksCheckbox.isSelected());

		basicJob.setName(this.nameTextField.getText());

		try {
			urls.addAll(this.getUrlList());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		basicJob.setSavePath(new File(
				this.savePathTextField.getText()));
		
		dispatcherConfiguration.setWorkerThreads(Integer
				.parseInt(this.workingThreadsTextField
						.getText()));
		
		//build result
		for (URL url : urls) {
			UrlDownloadJob job = jobFactory.createDownloadJob();

			job.setUrl(url);
			job.setIgnoreFilter(basicJob.isIgnoreFilter());
			job.setState(basicJob.getState());
			job.setName(basicJob.getName());
			job.setSavePath(basicJob.getSavePath());
			job.setMaxRetryCount(basicJob.getMaxRetryCount());

			pJobPackage.addJob(job);
		}

		pJobPackage.setDispatcherConfiguration(dispatcherConfiguration);
		
	}	
	
	public List<String> validateFields() {

		FieldValidator validator = new FieldValidator();

		validator.assertNotEmpty(this.nameTextField.getText(),
				"Enter a valid name.");

		if (mUrlList != null && mUrlList.size() == 0) {
			validator.addError("Enter a valid URL.");
		}
		if (mUrlList == null) {
			validator.assertURL(this.urlTextField.getText(),
					"Enter a valid URL.");
		}

		validator.assertNotEmpty(this.savePathTextField.getText(),
				"Enter a valid path to save files.");

		validator.assertInteger(this.workingThreadsTextField.getText(),
				"Enter a valid number of working threads.");

		return validator.getErrors();
	}

	public List<URL> getUrlList() throws MalformedURLException {

		List<URL> urlList = null;

		if (mUrlList != null) {
			urlList = mUrlList;
		} else {
			urlList = new ArrayList<URL>();
			String url = urlTextField.getText();
			if (url.trim().length() > 0) {
				urlList.add(new URL(url));
			}
		}

		return urlList;
	}

	public void setUrlList(List<URL> pUrls) {

		List<URL> urlList = pUrls;
		if (urlList.size() == 0) {
			urlTextField.setEditable(true);
			urlTextField.setText(null);
			mUrlList = null;
		}
		if (urlList.size() == 1) {
			urlTextField.setEditable(true);
			urlTextField.setText(urlList.get(0).toExternalForm());
			mUrlList = null;
		}
		if (urlList.size() > 1) {
			urlTextField.setEditable(false);
			urlTextField.setText("< " + urlList.size() + " URL's >");
			mUrlList = urlList;
		}

	}

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        basicParametersPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        urlLabel = new javax.swing.JLabel();
        urlTextField = new javax.swing.JTextField();
        moreUrlsButton = new javax.swing.JButton();
        savePathLabel = new javax.swing.JLabel();
        savePathTextField = new javax.swing.JTextField();
        savePathButton = new javax.swing.JButton();
        connectionSettingsPanel = new javax.swing.JPanel();
        workingThreadsLabel = new javax.swing.JLabel();
        workingThreadsTextField = new javax.swing.JTextField();
        memorySettingsPanel = new javax.swing.JPanel();
        dropIgnoredLinksCheckbox = new javax.swing.JCheckBox();
        dropFinishedLinksCheckbox = new javax.swing.JCheckBox();

        basicParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Basic Parameters"));

        nameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nameLabel.setText("Name:");

        urlLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        urlLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        urlLabel.setText("Start URL:");

        moreUrlsButton.setText("More");
        moreUrlsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreUrlsButtonActionPerformed(evt);
            }
        });

        savePathLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        savePathLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        savePathLabel.setText("Save path:");

        savePathButton.setText("Browse");
        savePathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePathButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout basicParametersPanelLayout = new org.jdesktop.layout.GroupLayout(basicParametersPanel);
        basicParametersPanel.setLayout(basicParametersPanelLayout);
        basicParametersPanelLayout.setHorizontalGroup(
            basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(basicParametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, savePathLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, urlLabel)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, nameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(basicParametersPanelLayout.createSequentialGroup()
                        .add(nameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                        .add(204, 204, 204))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, basicParametersPanelLayout.createSequentialGroup()
                        .add(basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, urlTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                            .add(savePathTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(moreUrlsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(savePathButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        basicParametersPanelLayout.linkSize(new java.awt.Component[] {nameLabel, savePathLabel, urlLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        basicParametersPanelLayout.setVerticalGroup(
            basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(basicParametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameLabel)
                    .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(urlLabel)
                    .add(urlTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(moreUrlsButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(basicParametersPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(savePathLabel)
                    .add(savePathTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(savePathButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        connectionSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Concurrency Settings"));

        workingThreadsLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        workingThreadsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        workingThreadsLabel.setText("Working Threads:");

        workingThreadsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.jdesktop.layout.GroupLayout connectionSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(connectionSettingsPanel);
        connectionSettingsPanel.setLayout(connectionSettingsPanelLayout);
        connectionSettingsPanelLayout.setHorizontalGroup(
            connectionSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectionSettingsPanelLayout.createSequentialGroup()
                .add(16, 16, 16)
                .add(workingThreadsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(workingThreadsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(309, Short.MAX_VALUE))
        );
        connectionSettingsPanelLayout.setVerticalGroup(
            connectionSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectionSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(connectionSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(workingThreadsLabel)
                    .add(workingThreadsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        memorySettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Memory Settings"));

        dropIgnoredLinksCheckbox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        dropIgnoredLinksCheckbox.setText("Drop Ignored Links");
        dropIgnoredLinksCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropIgnoredLinksCheckboxActionPerformed(evt);
            }
        });

        dropFinishedLinksCheckbox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        dropFinishedLinksCheckbox.setText("Drop Finished Links");

        org.jdesktop.layout.GroupLayout memorySettingsPanelLayout = new org.jdesktop.layout.GroupLayout(memorySettingsPanel);
        memorySettingsPanel.setLayout(memorySettingsPanelLayout);
        memorySettingsPanelLayout.setHorizontalGroup(
            memorySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(memorySettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(memorySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dropIgnoredLinksCheckbox)
                    .add(dropFinishedLinksCheckbox))
                .addContainerGap(329, Short.MAX_VALUE))
        );
        memorySettingsPanelLayout.setVerticalGroup(
            memorySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(memorySettingsPanelLayout.createSequentialGroup()
                .add(dropIgnoredLinksCheckbox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dropFinishedLinksCheckbox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(connectionSettingsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(basicParametersPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, memorySettingsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(basicParametersPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(connectionSettingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(memorySettingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

	//GEN-FIRST:event_moreUrlsButtonActionPerformed
	private void moreUrlsButtonActionPerformed(java.awt.event.ActionEvent evt) {

		EditUrlListDialog urlEditDialog = new EditUrlListDialog(
				(Dialog) getRootPane().getParent(), true);

		try {
			urlEditDialog.setUrlList(getUrlList());
		} catch (MalformedURLException e) {

			JOptionPane.showMessageDialog(this, "Malformed URL: "
					+ urlTextField.getText(), "Validation error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		urlEditDialog.setVisible(true);

		if (urlEditDialog.isOk()) {
			setUrlList(urlEditDialog.getUrlList());
		}

	}//GEN-LAST:event_moreUrlsButtonActionPerformed

	//GEN-FIRST:event_savePathButtonActionPerformed
	private void savePathButtonActionPerformed(java.awt.event.ActionEvent evt) {

		//open dialog
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fc.setSelectedFile(new File(this.savePathTextField.getText()));

		//Show load dialog; this method does not return until the dialog is closed
		int result = fc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			this.savePathTextField.setText(fc.getSelectedFile()
					.getAbsolutePath());
		}

	}//GEN-LAST:event_savePathButtonActionPerformed

        private void dropIgnoredLinksCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropIgnoredLinksCheckboxActionPerformed
            // TODO add your handling code here:
        }//GEN-LAST:event_dropIgnoredLinksCheckboxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JPanel basicParametersPanel;
    protected javax.swing.JPanel connectionSettingsPanel;
    protected javax.swing.JCheckBox dropFinishedLinksCheckbox;
    protected javax.swing.JCheckBox dropIgnoredLinksCheckbox;
    protected javax.swing.JPanel memorySettingsPanel;
    protected javax.swing.JButton moreUrlsButton;
    protected javax.swing.JLabel nameLabel;
    protected javax.swing.JTextField nameTextField;
    protected javax.swing.JButton savePathButton;
    protected javax.swing.JLabel savePathLabel;
    protected javax.swing.JTextField savePathTextField;
    protected javax.swing.JLabel urlLabel;
    protected javax.swing.JTextField urlTextField;
    protected javax.swing.JLabel workingThreadsLabel;
    protected javax.swing.JTextField workingThreadsTextField;
    // End of variables declaration//GEN-END:variables

}