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

import de.phleisch.app.itsucks.JobFactory;
import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.filter.DownloadJobFilter;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.MaxLinksToFollowFilter;
import de.phleisch.app.itsucks.filter.RegExpJobFilter;
import de.phleisch.app.itsucks.filter.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.gui.util.ExtendedListModel;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.io.HttpRetrieverConfiguration;
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
	
	public void loadJob(DownloadJob pJob, List<JobFilter> pFilters) {
		
		DownloadJobFilter downloadJobFilter = null;
		MaxLinksToFollowFilter maxLinksToFollowFilter = null;
		RegExpJobFilter regExpJobFilter = null;
		
		for (JobFilter jobFilter : pFilters) {
			if(jobFilter instanceof DownloadJobFilter) {
				downloadJobFilter = (DownloadJobFilter) jobFilter;
				continue;
			}
			if(jobFilter instanceof MaxLinksToFollowFilter) {
				maxLinksToFollowFilter = (MaxLinksToFollowFilter) jobFilter;
				continue;
			}
			if(jobFilter instanceof RegExpJobFilter) {
				regExpJobFilter = (RegExpJobFilter) jobFilter;
				continue;
			}
		}
		
		//load basic panel
		this.downloadJobBasicPanel.nameTextField.setText(pJob.getName());
		this.downloadJobBasicPanel.urlTextField.setText(pJob.getUrl().toExternalForm());
		this.downloadJobBasicPanel.savePathTextField.setText(
				pJob.getSavePath().getAbsolutePath());
		this.downloadJobBasicPanel.maxRetriesTextField.setText(
				String.valueOf(pJob.getMaxRetryCount()));
		
		//load simple rules
		if(maxLinksToFollowFilter != null) {
			this.downloadJobSimpleRulesPanel.linksToFollowTextField.setText(
					String.valueOf(maxLinksToFollowFilter.getMaxLinksToFollow()));
		}
		this.downloadJobSimpleRulesPanel.recursionDepthTextField.setText(
				String.valueOf(downloadJobFilter.getMaxRecursionDepth()));
		if(downloadJobFilter != null) {
			
			if(downloadJobFilter.getURLPrefix() != null) {
				this.downloadJobSimpleRulesPanel.urlPrefixCheckBox.setSelected(true);
				this.downloadJobSimpleRulesPanel.urlPrefixTextField.setText(
						downloadJobFilter.getURLPrefix().toExternalForm());
			} else {
				this.downloadJobSimpleRulesPanel.urlPrefixCheckBox.setSelected(false);
			}
			
			this.downloadJobSimpleRulesPanel.hostnameFilterTableModel.setRowCount(0);
			String[] allowedHostNames = downloadJobFilter.getAllowedHostNames();
			for (String string : allowedHostNames) {
				this.downloadJobSimpleRulesPanel.hostnameFilterTableModel.addRow(
						new Object[] {string});
			}

			this.downloadJobSimpleRulesPanel.saveToDiskFilterTabelModel.setRowCount(0);
			String[] saveToDiskFilter = downloadJobFilter.getSaveToDisk();
			for (String string : saveToDiskFilter) {
				this.downloadJobSimpleRulesPanel.saveToDiskFilterTabelModel.addRow(
						new Object[] {string});
			}
			
		}
		
		//load advanced rules
		ExtendedListModel model = this.downloadJobAdvancedRulesPanel.advancedFilterFilterListModel;
		this.downloadJobAdvancedRulesPanel.advancedFilterList.setModel(model);
		for (RegExpFilterRule jobFilterRule : regExpJobFilter.getFilterRules()) {
			model.addElement(this.downloadJobAdvancedRulesPanel.new RegExpFilterRuleListElement(
					jobFilterRule));
		}
		
	}
	
	public SerializableJobList buildJob() {
		
		JobFactory jobFactory =  (JobFactory) 
		SpringContextSingelton.getApplicationContext().getBean("JobFactory");
		DownloadJob job = jobFactory.createDownloadJob();
		List<JobFilter> jobFilterList = new ArrayList<JobFilter>();
		SerializableDispatcherConfiguration dispatcherConfiguration = 
			new SerializableDispatcherConfiguration();
		HttpRetrieverConfiguration retrieverConfiguration = new HttpRetrieverConfiguration();
		
		//build download job
		job.setIgnoreFilter(true);
		job.setState(DownloadJob.STATE_OPEN);
		
		//basic panel
		job.setName(this.downloadJobBasicPanel.nameTextField.getText());
		
		try {
			job.setUrl(new URL(this.downloadJobBasicPanel.urlTextField.getText()));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Bad URL: " + this.downloadJobBasicPanel.urlTextField.getText(), e);
		}

		job.setSavePath(new File(this.downloadJobBasicPanel.savePathTextField.getText()));
		job.setMaxRetryCount(Integer.parseInt(this.downloadJobBasicPanel.maxRetriesTextField.getText()));
		
		dispatcherConfiguration.setWorkerThreads(
				Integer.parseInt(this.downloadJobBasicPanel.workingThreadsTextField.getText()));

		//proxy configuration
		if(this.downloadJobBasicPanel.enableProxyCheckBox.isSelected()) {
			retrieverConfiguration.setProxyEnabled(true);
			
			retrieverConfiguration.setProxyServer(
					this.downloadJobBasicPanel.proxyServerLabel.getText());
			
			retrieverConfiguration.setProxyPort(Integer.parseInt(
					this.downloadJobBasicPanel.proxyPortTextField.getText()));
		} else {
			retrieverConfiguration.setProxyEnabled(false);
		}
		
		if(this.downloadJobBasicPanel.enableAuthenticationCheckBox.isSelected()) {
			retrieverConfiguration.setProxyAuthenticatenEnabled(true);
			
			retrieverConfiguration.setProxyUser(
					this.downloadJobBasicPanel.authenticationUserTextField.getText());
			
			retrieverConfiguration.setProxyPassword(
					this.downloadJobBasicPanel.authenticationPasswordTextField.getText());
		} else {
			retrieverConfiguration.setProxyAuthenticatenEnabled(false);
		}
		
		
		//simple rules panel
		DownloadJobFilter downloadJobFilter = new DownloadJobFilter();
		jobFilterList.add(downloadJobFilter);
		
		String maxRecursionDepth = 
			this.downloadJobSimpleRulesPanel.recursionDepthTextField.getText();
		if(maxRecursionDepth != null && maxRecursionDepth.length() > 0) {
			downloadJobFilter.setMaxRecursionDepth(Integer.parseInt(maxRecursionDepth));
		}
		
		String maxLinksToFollow = 
			this.downloadJobSimpleRulesPanel.linksToFollowTextField.getText();
		if(maxLinksToFollow != null && maxLinksToFollow.length() > 0) {
			MaxLinksToFollowFilter maxLinksToFollowFilter = new MaxLinksToFollowFilter();
			maxLinksToFollowFilter.setMaxLinksToFollow(Integer.parseInt(maxLinksToFollow));
			jobFilterList.add(maxLinksToFollowFilter);
		}
		
		if(this.downloadJobSimpleRulesPanel.urlPrefixCheckBox.isSelected()) {
			try {
				downloadJobFilter.setURLPrefix(new URL(this.downloadJobSimpleRulesPanel.urlPrefixTextField.getText()));
			} catch (MalformedURLException e) {
				throw new RuntimeException("Bad URL: " + this.downloadJobSimpleRulesPanel.urlPrefixTextField.getText(), e);
			}
		} else {
			downloadJobFilter.setURLPrefix(null);
		}
		
		int hostnameFilterRowCount = 
			this.downloadJobSimpleRulesPanel.hostnameFilterTableModel.getRowCount();
		List<String> allowedHostnames = new ArrayList<String>();
		for (int i = 0; i < hostnameFilterRowCount; i++) {
			allowedHostnames.add((String) this.downloadJobSimpleRulesPanel.hostnameFilterTableModel.getValueAt(i, 0));
		}
		downloadJobFilter.setAllowedHostNames(allowedHostnames.toArray(new String[allowedHostnames.size()]));

		int saveToDiskFilterRowCount = 
			this.downloadJobSimpleRulesPanel.saveToDiskFilterTabelModel.getRowCount();
		List<String> saveToDiskFilters = new ArrayList<String>();
		for (int i = 0; i < saveToDiskFilterRowCount; i++) {
			saveToDiskFilters.add((String) this.downloadJobSimpleRulesPanel.saveToDiskFilterTabelModel.getValueAt(i, 0));
		}
		downloadJobFilter.setSaveToDisk(saveToDiskFilters.toArray(new String[saveToDiskFilters.size()]));

		//advanced rules
		int advancedFilterCount = this.downloadJobAdvancedRulesPanel.advancedFilterFilterListModel.getSize();
		if(advancedFilterCount > 0) {
			RegExpJobFilter regExpFilter = new RegExpJobFilter();
			
			for (int i = 0; i < advancedFilterCount; i++) {
				RegExpFilterRule rule = ((DownloadJobAdvancedRulesPanel.RegExpFilterRuleListElement)
						this.downloadJobAdvancedRulesPanel.advancedFilterFilterListModel.get(i))
						.getRule();
				regExpFilter.addFilterRule(rule);
			}
			
			jobFilterList.add(regExpFilter);
		}
		
		//build result
		SerializableJobList result = new SerializableJobList();
		result.addJob(job);
		result.setFilters(jobFilterList);
		result.setDispatcherConfiguration(dispatcherConfiguration);
		result.putContextParameter(
				HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION, 
				retrieverConfiguration);
		
		return result;
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
		downloadJobAdvancedRulesPanel = new de.phleisch.app.itsucks.gui.panel.DownloadJobAdvancedRulesPanel();

		tabbedPane.addTab("Basic Parameters", downloadJobBasicPanel);

		tabbedPane.addTab("Simple Rules", downloadJobSimpleRulesPanel);

		tabbedPane.addTab("Advanced Rules", downloadJobAdvancedRulesPanel);

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
	private de.phleisch.app.itsucks.gui.panel.DownloadJobAdvancedRulesPanel downloadJobAdvancedRulesPanel;
	private de.phleisch.app.itsucks.gui.panel.DownloadJobBasicPanel downloadJobBasicPanel;
	private de.phleisch.app.itsucks.gui.panel.DownloadJobSimpleRulesPanel downloadJobSimpleRulesPanel;
	private javax.swing.JTabbedPane tabbedPane;
	// End of variables declaration//GEN-END:variables

}