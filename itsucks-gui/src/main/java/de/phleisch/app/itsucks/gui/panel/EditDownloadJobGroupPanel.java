/*
 * EditDownloadJobGroupPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package de.phleisch.app.itsucks.gui.panel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import de.phleisch.app.itsucks.JobFactory;
import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.filter.DownloadJobFilter;
import de.phleisch.app.itsucks.filter.FileSizeFilter;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.MaxLinksToFollowFilter;
import de.phleisch.app.itsucks.filter.RegExpJobFilter;
import de.phleisch.app.itsucks.filter.TimeLimitFilter;
import de.phleisch.app.itsucks.filter.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.gui.util.ExtendedListModel;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.io.http.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.persistence.SerializableDispatcherConfiguration;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

/**
 *
 * @author  __USER__
 */
public class EditDownloadJobGroupPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 8250877774474103209L;

	/** Creates new form EditDownloadJobGroupPanel */
	public EditDownloadJobGroupPanel() {
		initComponents();
	}

	public void loadJob(SerializableJobList pJobList) {

		DownloadJob pJob = (DownloadJob) pJobList.getJobs().get(0);
		List<JobFilter> pFilters = pJobList.getFilters();

		SerializableDispatcherConfiguration dispatcherConfiguration = pJobList
				.getDispatcherConfiguration();

		HttpRetrieverConfiguration httpRetrieverConfiguration = (HttpRetrieverConfiguration) pJobList
				.getContextParameter(HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION);

		DownloadJobFilter downloadJobFilter = null;
		MaxLinksToFollowFilter maxLinksToFollowFilter = null;
		RegExpJobFilter regExpJobFilter = null;
		FileSizeFilter fileSizeFilter = null;
		TimeLimitFilter timeLimitFilter = null;

		for (JobFilter jobFilter : pFilters) {
			if (jobFilter instanceof DownloadJobFilter) {
				downloadJobFilter = (DownloadJobFilter) jobFilter;
				continue;
			}
			if (jobFilter instanceof MaxLinksToFollowFilter) {
				maxLinksToFollowFilter = (MaxLinksToFollowFilter) jobFilter;
				continue;
			}
			if (jobFilter instanceof RegExpJobFilter) {
				regExpJobFilter = (RegExpJobFilter) jobFilter;
				continue;
			}
			if (jobFilter instanceof FileSizeFilter) {
				fileSizeFilter = (FileSizeFilter) jobFilter;
				continue;
			}
			if (jobFilter instanceof TimeLimitFilter) {
				timeLimitFilter = (TimeLimitFilter) jobFilter;
				continue;
			}
		}

		//load basic panel
		this.downloadJobBasicPanel.nameTextField.setText(pJob.getName());
		this.downloadJobBasicPanel.urlTextField.setText(pJob.getUrl()
				.toExternalForm());
		this.downloadJobBasicPanel.savePathTextField.setText(pJob.getSavePath()
				.getAbsolutePath());
		this.downloadJobBasicPanel.maxRetriesTextField.setText(String
				.valueOf(pJob.getMaxRetryCount()));

		if (dispatcherConfiguration != null) {
			if (dispatcherConfiguration.getWorkerThreads() != null) {
				this.downloadJobBasicPanel.workingThreadsTextField
						.setText(String.valueOf(dispatcherConfiguration
								.getWorkerThreads()));
			}
		}

		if (httpRetrieverConfiguration != null) {

			if (httpRetrieverConfiguration.getMaxConnectionsPerServer() != null) {
				this.downloadJobBasicPanel.maxConnectionsTextField
						.setText(String.valueOf(httpRetrieverConfiguration
								.getMaxConnectionsPerServer()));
			}

			if (httpRetrieverConfiguration.isProxyEnabled()) {
				this.downloadJobBasicPanel.enableProxyCheckBox
						.setSelected(true);
				this.downloadJobBasicPanel.proxyServerTextField
						.setText(httpRetrieverConfiguration.getProxyServer());
				this.downloadJobBasicPanel.proxyPortTextField.setText(String
						.valueOf(httpRetrieverConfiguration.getProxyPort()));

				if (httpRetrieverConfiguration.isProxyAuthenticationEnabled()) {

					this.downloadJobBasicPanel.enableAuthenticationCheckBox
							.setSelected(true);
					this.downloadJobBasicPanel.authenticationUserTextField
							.setText(httpRetrieverConfiguration.getProxyUser());
					this.downloadJobBasicPanel.authenticationPasswordTextField
							.setText(httpRetrieverConfiguration
									.getProxyPassword());
				}
			}
		}

		//load simple rules
		if (maxLinksToFollowFilter != null) {
			this.downloadJobSimpleRulesPanel.linksToFollowTextField
					.setText(String.valueOf(maxLinksToFollowFilter
							.getMaxLinksToFollow()));
		} else {
			this.downloadJobSimpleRulesPanel.linksToFollowTextField.setText("-1");
		}

		if(timeLimitFilter != null) {
			this.downloadJobSimpleRulesPanel.timeLimitTextField
				.setText(timeLimitFilter.getTimeLimitAsText());
		} else {
			this.downloadJobSimpleRulesPanel.timeLimitTextField.setText("-1");
		}
		
		if (downloadJobFilter != null) {
			
			this.downloadJobSimpleRulesPanel.recursionDepthTextField.setText(String
					.valueOf(downloadJobFilter.getMaxRecursionDepth()));

			if (downloadJobFilter.getURLPrefix() != null) {
				this.downloadJobSimpleRulesPanel.urlPrefixCheckBox
						.setSelected(true);
				this.downloadJobSimpleRulesPanel.urlPrefixTextField
						.setText(downloadJobFilter.getURLPrefix()
								.toExternalForm());
			} else {
				this.downloadJobSimpleRulesPanel.urlPrefixCheckBox
						.setSelected(false);
			}

			this.downloadJobSimpleRulesPanel.hostnameFilterTableModel
					.setRowCount(0);
			String[] allowedHostNames = downloadJobFilter.getAllowedHostNames();
			for (String string : allowedHostNames) {
				this.downloadJobSimpleRulesPanel.hostnameFilterTableModel
						.addRow(new Object[] { string });
			}

			this.downloadJobSimpleRulesPanel.saveToDiskFilterTabelModel
					.setRowCount(0);
			String[] saveToDiskFilter = downloadJobFilter.getSaveToDisk();
			for (String string : saveToDiskFilter) {
				this.downloadJobSimpleRulesPanel.saveToDiskFilterTabelModel
						.addRow(new Object[] { string });
			}

		}

		//load special rules
		if (fileSizeFilter != null) {
			
			this.downloadJobSpecialRulesPanel.fileSizeEnableCheckBox.setSelected(true);
			
			this.downloadJobSpecialRulesPanel.fileSizeMinField.setText(
					fileSizeFilter.getMinSizeAsText());
			this.downloadJobSpecialRulesPanel.fileSizeMaxField.setText(
					fileSizeFilter.getMaxSizeAsText());
			this.downloadJobSpecialRulesPanel.fileSizeNotKnownComboBox.setSelectedIndex(
					fileSizeFilter.isAcceptWhenLengthNotSet() ? 0 : 1);
		}
		
		//load advanced rules
		ExtendedListModel model = this.downloadJobRegExpRulesPanel.regExpFilterListModel;
		this.downloadJobRegExpRulesPanel.regExpFilterList.setModel(model);
		for (RegExpFilterRule jobFilterRule : regExpJobFilter.getFilterRules()) {
			model.addElement(this.downloadJobRegExpRulesPanel.new RegExpFilterRuleListElement(
							jobFilterRule));
		}

	}

	public SerializableJobList buildJob() {

		if (!validatePanels())
			return null;

		JobFactory jobFactory = (JobFactory) SpringContextSingelton
				.getApplicationContext().getBean("JobFactory");
		DownloadJob job = jobFactory.createDownloadJob();
		List<JobFilter> jobFilterList = new ArrayList<JobFilter>();
		SerializableDispatcherConfiguration dispatcherConfiguration = new SerializableDispatcherConfiguration();
		HttpRetrieverConfiguration retrieverConfiguration = new HttpRetrieverConfiguration();

		//build download job
		job.setIgnoreFilter(true);
		job.setState(DownloadJob.STATE_OPEN);

		//basic panel
		job.setName(this.downloadJobBasicPanel.nameTextField.getText());

		try {
			job.setUrl(new URL(this.downloadJobBasicPanel.urlTextField
					.getText()));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Bad URL: "
					+ this.downloadJobBasicPanel.urlTextField.getText(), e);
		}

		job.setSavePath(new File(this.downloadJobBasicPanel.savePathTextField
				.getText()));
		job.setMaxRetryCount(Integer
				.parseInt(this.downloadJobBasicPanel.maxRetriesTextField
						.getText()));

		dispatcherConfiguration.setWorkerThreads(Integer
				.parseInt(this.downloadJobBasicPanel.workingThreadsTextField
						.getText()));

		retrieverConfiguration.setMaxConnectionsPerServer(Integer
				.parseInt(this.downloadJobBasicPanel.maxConnectionsTextField
						.getText()));

		//proxy configuration
		if (this.downloadJobBasicPanel.enableProxyCheckBox.isSelected()) {
			retrieverConfiguration.setProxyEnabled(true);

			retrieverConfiguration
					.setProxyServer(this.downloadJobBasicPanel.proxyServerTextField
							.getText());

			retrieverConfiguration.setProxyPort(Integer
					.parseInt(this.downloadJobBasicPanel.proxyPortTextField
							.getText()));
		} else {
			retrieverConfiguration.setProxyEnabled(false);
		}

		if (this.downloadJobBasicPanel.enableAuthenticationCheckBox
				.isSelected()) {
			retrieverConfiguration.setProxyAuthenticationEnabled(true);

			retrieverConfiguration
					.setProxyUser(this.downloadJobBasicPanel.authenticationUserTextField
							.getText());

			retrieverConfiguration
					.setProxyPassword(this.downloadJobBasicPanel.authenticationPasswordTextField
							.getText());
		} else {
			retrieverConfiguration.setProxyAuthenticationEnabled(false);
		}

		//simple rules panel
		DownloadJobFilter downloadJobFilter = new DownloadJobFilter();
		jobFilterList.add(downloadJobFilter);

		String maxRecursionDepth = this.downloadJobSimpleRulesPanel.recursionDepthTextField
				.getText();
		if (maxRecursionDepth != null && maxRecursionDepth.length() > 0) {
			downloadJobFilter.setMaxRecursionDepth(Integer
					.parseInt(maxRecursionDepth));
		}

		String maxLinksToFollow = this.downloadJobSimpleRulesPanel.linksToFollowTextField
				.getText();
		if (maxLinksToFollow != null && maxLinksToFollow.length() > 0) {
			MaxLinksToFollowFilter maxLinksToFollowFilter = new MaxLinksToFollowFilter();
			maxLinksToFollowFilter.setMaxLinksToFollow(Integer
					.parseInt(maxLinksToFollow));
			
			if(maxLinksToFollowFilter.getMaxLinksToFollow() > -1) {
				jobFilterList.add(maxLinksToFollowFilter);
			}
		}

		String timeLimit = this.downloadJobSimpleRulesPanel.timeLimitTextField.getText();
		if (timeLimit != null && timeLimit.length() > 0) {
			TimeLimitFilter timeLimitFilter = new TimeLimitFilter();
			timeLimitFilter.setTimeLimitAsText(timeLimit);
			if(timeLimitFilter.getTimeLimit() > -1) { 
				jobFilterList.add(timeLimitFilter);
			}
		}
		
		if (this.downloadJobSimpleRulesPanel.urlPrefixCheckBox.isSelected()) {
			try {
				downloadJobFilter.setURLPrefix(new URL(
						this.downloadJobSimpleRulesPanel.urlPrefixTextField
								.getText()));
			} catch (MalformedURLException e) {
				throw new RuntimeException("Bad URL: "
						+ this.downloadJobSimpleRulesPanel.urlPrefixTextField
								.getText(), e);
			}
		} else {
			downloadJobFilter.setURLPrefix(null);
		}

		int hostnameFilterRowCount = this.downloadJobSimpleRulesPanel.hostnameFilterTableModel
				.getRowCount();
		List<String> allowedHostnames = new ArrayList<String>();
		for (int i = 0; i < hostnameFilterRowCount; i++) {
			allowedHostnames
					.add((String) this.downloadJobSimpleRulesPanel.hostnameFilterTableModel
							.getValueAt(i, 0));
		}
		downloadJobFilter.setAllowedHostNames(allowedHostnames
				.toArray(new String[allowedHostnames.size()]));

		int saveToDiskFilterRowCount = this.downloadJobSimpleRulesPanel.saveToDiskFilterTabelModel
				.getRowCount();
		List<String> saveToDiskFilters = new ArrayList<String>();
		for (int i = 0; i < saveToDiskFilterRowCount; i++) {
			saveToDiskFilters
					.add((String) this.downloadJobSimpleRulesPanel.saveToDiskFilterTabelModel
							.getValueAt(i, 0));
		}
		downloadJobFilter.setSaveToDisk(saveToDiskFilters
				.toArray(new String[saveToDiskFilters.size()]));

		//advanced rules
		int advancedFilterCount = this.downloadJobRegExpRulesPanel.regExpFilterListModel
				.getSize();
		if (advancedFilterCount > 0) {
			RegExpJobFilter regExpFilter = new RegExpJobFilter();

			for (int i = 0; i < advancedFilterCount; i++) {
				RegExpFilterRule rule = ((DownloadJobRegExpRulesPanel.RegExpFilterRuleListElement) this.downloadJobRegExpRulesPanel.regExpFilterListModel
						.get(i)).getRule();
				regExpFilter.addFilterRule(rule);
			}

			jobFilterList.add(regExpFilter);
		}

		//file size filter
		if (this.downloadJobSpecialRulesPanel.fileSizeEnableCheckBox.isSelected()) {

			FileSizeFilter fileSizeFilter = new FileSizeFilter();
			
			fileSizeFilter.setMinSizeAsText(
					this.downloadJobSpecialRulesPanel.fileSizeMinField.getText().trim());
			fileSizeFilter.setMaxSizeAsText(
					this.downloadJobSpecialRulesPanel.fileSizeMaxField.getText().trim());

			fileSizeFilter.setAcceptWhenLengthNotSet(
					this.downloadJobSpecialRulesPanel.fileSizeNotKnownComboBox
						.getSelectedIndex() > 0 ? false : true);
			
			jobFilterList.add(fileSizeFilter);
		}

		//build result
		SerializableJobList result = new SerializableJobList();
		result.addJob(job);
		result.setFilters(jobFilterList);
		result.setDispatcherConfiguration(dispatcherConfiguration);
		result
				.putContextParameter(
						HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION,
						retrieverConfiguration);

		return result;
	}

	private boolean validatePanels() {

		boolean result = true;

		List<String> errorsBasicPanel = downloadJobBasicPanel.validateFields();
		List<String> errorsSimplePanel = downloadJobSimpleRulesPanel
				.validateFields();
		List<String> errorsSpecialPanel = downloadJobSpecialRulesPanel
				.validateFields();

		if (errorsBasicPanel.size() > 0) {
			result = false;
			tabbedPane.setSelectedComponent(downloadJobBasicPanel);
			displayErrors(errorsBasicPanel);
		} else if (errorsSimplePanel.size() > 0) {
			result = false;
			tabbedPane.setSelectedComponent(downloadJobSimpleRulesPanel);
			displayErrors(errorsSimplePanel);
		} else if (errorsSpecialPanel.size() > 0) {
			result = false;
			tabbedPane.setSelectedComponent(downloadJobSpecialRulesPanel);
			displayErrors(errorsSpecialPanel);
		}

		return result;
	}

	private void displayErrors(List<String> errorsBasicPanel) {
		StringBuffer buffer = new StringBuffer();
		for (String string : errorsBasicPanel) {
			buffer.append(string + '\n');
		}

		JOptionPane.showMessageDialog(this, buffer.toString(),
				"Validation errors", JOptionPane.ERROR_MESSAGE);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		tabbedPane = new javax.swing.JTabbedPane();
		downloadJobBasicPanel = new de.phleisch.app.itsucks.gui.panel.DownloadJobBasicPanel();
		downloadJobSimpleRulesPanel = new de.phleisch.app.itsucks.gui.panel.DownloadJobSimpleRulesPanel();
		downloadJobSpecialRulesPanel = new de.phleisch.app.itsucks.gui.panel.DownloadJobSpecialRulesPanel();
		downloadJobRegExpRulesPanel = new de.phleisch.app.itsucks.gui.panel.DownloadJobRegExpRulesPanel();

		tabbedPane.addTab("Basic Parameters", downloadJobBasicPanel);

		tabbedPane.addTab("Simple Rules", downloadJobSimpleRulesPanel);

		tabbedPane.addTab("Special Rules", downloadJobSpecialRulesPanel);

		tabbedPane.addTab("Advanced RegExp Rules", downloadJobRegExpRulesPanel);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(tabbedPane,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(tabbedPane,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 587,
				Short.MAX_VALUE));
	}// </editor-fold>//GEN-END:initComponents

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private de.phleisch.app.itsucks.gui.panel.DownloadJobBasicPanel downloadJobBasicPanel;
	private de.phleisch.app.itsucks.gui.panel.DownloadJobRegExpRulesPanel downloadJobRegExpRulesPanel;
	private de.phleisch.app.itsucks.gui.panel.DownloadJobSimpleRulesPanel downloadJobSimpleRulesPanel;
	private de.phleisch.app.itsucks.gui.panel.DownloadJobSpecialRulesPanel downloadJobSpecialRulesPanel;
	private javax.swing.JTabbedPane tabbedPane;
	// End of variables declaration//GEN-END:variables

}