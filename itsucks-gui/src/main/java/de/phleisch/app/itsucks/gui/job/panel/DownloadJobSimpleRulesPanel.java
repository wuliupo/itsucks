/*
 * DownloadJobSimpleRulesPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.filter.download.impl.MaxLinksToFollowFilter;
import de.phleisch.app.itsucks.filter.download.impl.TimeLimitFilter;
import de.phleisch.app.itsucks.gui.job.ifc.EditJobCapable;
import de.phleisch.app.itsucks.gui.util.FieldValidator;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  __USER__
 */
public class DownloadJobSimpleRulesPanel extends JPanel implements EditJobCapable {

	private static final long serialVersionUID = -4537668236021804263L;

	protected DefaultTableModel hostnameFilterTableModel;
	protected DefaultTableModel saveToDiskFilterTabelModel;

	/** Creates new form DownloadJobSimpleRulesPanel */
	public DownloadJobSimpleRulesPanel() {

		hostnameFilterTableModel = new DefaultTableModel();
		hostnameFilterTableModel.addColumn("Hostname Filter");
		saveToDiskFilterTabelModel = new DefaultTableModel();
		saveToDiskFilterTabelModel.addColumn("'Save to disk' Filter");
		
		init();
	}
	
	protected void init() {
		initComponents();
	}

	public void loadJobPackage(SerializableJobPackage pJobPackage) {
		
		MaxLinksToFollowFilter maxLinksToFollowFilter = 
			(MaxLinksToFollowFilter) pJobPackage.getFilterByType(MaxLinksToFollowFilter.class);
		
		TimeLimitFilter timeLimitFilter = 
			(TimeLimitFilter) pJobPackage.getFilterByType(TimeLimitFilter.class);
		
		DownloadJobFilter downloadJobFilter = 
			(DownloadJobFilter) pJobPackage.getFilterByType(DownloadJobFilter.class);
		
		if (maxLinksToFollowFilter != null) {
			this.linksToFollowTextField
					.setText(String.valueOf(maxLinksToFollowFilter
							.getMaxLinksToFollow()));
		} else {
			this.linksToFollowTextField
					.setText("-1");
		}

		if (timeLimitFilter != null) {
			this.timeLimitTextField
					.setText(timeLimitFilter.getTimeLimitAsText());
		} else {
			this.timeLimitTextField.setText("-1");
		}

		if (downloadJobFilter != null) {

			this.recursionDepthTextField
					.setText(String.valueOf(downloadJobFilter
							.getMaxRecursionDepth()));

			if (downloadJobFilter.getURLPrefix() != null) {
				this.urlPrefixCheckBox
						.setSelected(true);
				this.urlPrefixTextField
						.setText(downloadJobFilter.getURLPrefix()
								.toExternalForm());
			} else {
				this.urlPrefixCheckBox
						.setSelected(false);
			}

			this.hostnameFilterTableModel
					.setRowCount(0);
			String[] allowedHostNames = downloadJobFilter.getAllowedHostNames();
			for (String string : allowedHostNames) {
				this.hostnameFilterTableModel
						.addRow(new Object[] { string });
			}

			this.saveToDiskFilterTabelModel
					.setRowCount(0);
			String[] saveToDiskFilter = downloadJobFilter.getSaveToDisk();
			for (String string : saveToDiskFilter) {
				this.saveToDiskFilterTabelModel
						.addRow(new Object[] { string });
			}

		}
		
	}

	public void saveJobPackage(SerializableJobPackage pJobPackage) {
		
		MaxLinksToFollowFilter maxLinksToFollowFilter = 
			(MaxLinksToFollowFilter) pJobPackage.getFilterByType(MaxLinksToFollowFilter.class);
		
		TimeLimitFilter timeLimitFilter = 
			(TimeLimitFilter) pJobPackage.getFilterByType(TimeLimitFilter.class);
		
		DownloadJobFilter downloadJobFilter = 
			(DownloadJobFilter) pJobPackage.getFilterByType(DownloadJobFilter.class);
		
		if(downloadJobFilter == null) {
			downloadJobFilter = new DownloadJobFilter();
			pJobPackage.addFilter(downloadJobFilter);
		}

		String maxRecursionDepth = this.recursionDepthTextField
				.getText();
		if (maxRecursionDepth != null && maxRecursionDepth.length() > 0) {
			downloadJobFilter.setMaxRecursionDepth(Integer
					.parseInt(maxRecursionDepth));
		}

		String maxLinksToFollow = this.linksToFollowTextField
				.getText();
		if (maxLinksToFollow != null && maxLinksToFollow.length() > 0) {
			
			if(maxLinksToFollowFilter == null) {
				maxLinksToFollowFilter = new MaxLinksToFollowFilter();
				pJobPackage.addFilter(maxLinksToFollowFilter);
			}
			
			maxLinksToFollowFilter.setMaxLinksToFollow(Integer
					.parseInt(maxLinksToFollow));

			if (maxLinksToFollowFilter.getMaxLinksToFollow() < 0) {
				pJobPackage.removeFilter(maxLinksToFollowFilter);
			}
		}

		String timeLimit = this.timeLimitTextField
				.getText();
		if (timeLimit != null && timeLimit.length() > 0) {
			
			if(timeLimitFilter == null) {
				timeLimitFilter = new TimeLimitFilter();
				pJobPackage.addFilter(timeLimitFilter);
			}
			
			timeLimitFilter.setTimeLimitAsText(timeLimit);
			if (timeLimitFilter.getTimeLimit() < 0) {
				pJobPackage.removeFilter(timeLimitFilter);
			}
		}

		if (this.urlPrefixCheckBox.isSelected()) {
			try {
				downloadJobFilter.setURLPrefix(new URL(
						this.urlPrefixTextField
								.getText()));
			} catch (MalformedURLException e) {
				throw new RuntimeException("Bad URL: "
						+ this.urlPrefixTextField
								.getText(), e);
			}
		} else {
			downloadJobFilter.setURLPrefix(null);
		}

		int hostnameFilterRowCount = this.hostnameFilterTableModel
				.getRowCount();
		List<String> allowedHostnames = new ArrayList<String>();
		for (int i = 0; i < hostnameFilterRowCount; i++) {
			allowedHostnames
					.add((String) this.hostnameFilterTableModel
							.getValueAt(i, 0));
		}
		downloadJobFilter.setAllowedHostNames(allowedHostnames
				.toArray(new String[allowedHostnames.size()]));

		int saveToDiskFilterRowCount = this.saveToDiskFilterTabelModel
				.getRowCount();
		List<String> saveToDiskFilters = new ArrayList<String>();
		for (int i = 0; i < saveToDiskFilterRowCount; i++) {
			saveToDiskFilters
					.add((String) this.saveToDiskFilterTabelModel
							.getValueAt(i, 0));
		}
		downloadJobFilter.setSaveToDisk(saveToDiskFilters
				.toArray(new String[saveToDiskFilters.size()]));
		
	}

	public List<String> validateFields() {

		FieldValidator validator = new FieldValidator();

		validator.assertInteger(this.recursionDepthTextField.getText(),
				"Enter a valid number for the recursion depth.");

		validator.assertInteger(this.linksToFollowTextField.getText(),
				"Enter a valid number of links to follow.");

		String timeLimitRegExp = "^([-]?[0-9]{1,})[ ]*(s|m|h|d|S|M|H|D|$)$";
		validator.assertRegExpResult(timeLimitRegExp, this.timeLimitTextField
				.getText(), "Enter a valid time limit.");

		if (this.urlPrefixCheckBox.isSelected()) {
			validator.assertURL(this.urlPrefixTextField.getText(),
					"Enter a valid URL prefix.");
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

        limitsPanel = new javax.swing.JPanel();
        limitsLabel = new javax.swing.JLabel();
        recursionDepthLabel = new javax.swing.JLabel();
        recursionDepthTextField = new javax.swing.JTextField();
        linksToFollowLabel = new javax.swing.JLabel();
        linksToFollowTextField = new javax.swing.JTextField();
        timeLimitLabel = new javax.swing.JLabel();
        timeLimitTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        urlPrefixFilterPanel = new javax.swing.JPanel();
        urlPrefixFilterLabel = new javax.swing.JLabel();
        urlPrefixCheckBox = new javax.swing.JCheckBox();
        urlPrefixFieldLabel = new javax.swing.JLabel();
        urlPrefixTextField = new javax.swing.JTextField();
        hostnameFilterPanel = new javax.swing.JPanel();
        hostnameFilterLabel = new javax.swing.JLabel();
        hostnameFilterScrollPane = new javax.swing.JScrollPane();
        hostnameFilterTable = new javax.swing.JTable();
        hostnameFilterAddButton = new javax.swing.JButton();
        hostnameFilterRemoveButton = new javax.swing.JButton();
        saveToDiskFilterPanel = new javax.swing.JPanel();
        saveToDiskFilterLabel = new javax.swing.JLabel();
        saveToDiskPane = new javax.swing.JScrollPane();
        saveToDiskTable = new javax.swing.JTable();
        saveToDiskAddButton = new javax.swing.JButton();
        saveToDiskRemoveButton = new javax.swing.JButton();

        limitsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Limits"));

        limitsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        limitsLabel.setText("<html>Set a limit to stop crawling when the limit is reached. To disable a limit, enter -1 as value.</html>");

        recursionDepthLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        recursionDepthLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        recursionDepthLabel.setText("Max. recursion depth:");
        recursionDepthLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        recursionDepthTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        linksToFollowLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        linksToFollowLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        linksToFollowLabel.setText("Max. links to follow:");
        linksToFollowLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        linksToFollowTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        timeLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        timeLimitLabel.setText("Time limit:");

        timeLimitTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setText("<html>Possible units are: s(econds), m(inutes), h(ours) and d(ays).</html>");

        org.jdesktop.layout.GroupLayout limitsPanelLayout = new org.jdesktop.layout.GroupLayout(limitsPanel);
        limitsPanel.setLayout(limitsPanelLayout);
        limitsPanelLayout.setHorizontalGroup(
            limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(limitsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(limitsLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                    .add(limitsPanelLayout.createSequentialGroup()
                        .add(limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(recursionDepthLabel)
                            .add(linksToFollowLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(recursionDepthTextField)
                            .add(linksToFollowTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(26, 26, 26)
                        .add(timeLimitLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(limitsPanelLayout.createSequentialGroup()
                                .add(timeLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 103, Short.MAX_VALUE))
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))))
                .addContainerGap())
        );

        limitsPanelLayout.linkSize(new java.awt.Component[] {linksToFollowLabel, recursionDepthLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        limitsPanelLayout.setVerticalGroup(
            limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(limitsPanelLayout.createSequentialGroup()
                .add(limitsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(recursionDepthLabel)
                    .add(recursionDepthTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(timeLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(timeLimitLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(limitsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(linksToFollowLabel)
                        .add(linksToFollowTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        urlPrefixFilterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("URL prefix Filter"));

        urlPrefixFilterLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        urlPrefixFilterLabel.setText("<html>Set a URL prefix filter to follow only links starting with the defined prefix.<br>Example: Enter \"http://www.example.com/section1/\" to allow only links from this folder.</html>");

        urlPrefixCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        urlPrefixCheckBox.setText("Enable 'URL prefix Filter'");
        urlPrefixCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        urlPrefixCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                urlPrefixCheckBoxStateChanged(evt);
            }
        });

        urlPrefixFieldLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        urlPrefixFieldLabel.setText("URL prefix:");
        urlPrefixFieldLabel.setEnabled(false);

        urlPrefixTextField.setText("http://");
        urlPrefixTextField.setEnabled(false);

        org.jdesktop.layout.GroupLayout urlPrefixFilterPanelLayout = new org.jdesktop.layout.GroupLayout(urlPrefixFilterPanel);
        urlPrefixFilterPanel.setLayout(urlPrefixFilterPanelLayout);
        urlPrefixFilterPanelLayout.setHorizontalGroup(
            urlPrefixFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(urlPrefixFilterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(urlPrefixFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(urlPrefixFilterLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                    .add(urlPrefixFilterPanelLayout.createSequentialGroup()
                        .add(urlPrefixFieldLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(urlPrefixTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 279, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(urlPrefixCheckBox))
                .addContainerGap())
        );
        urlPrefixFilterPanelLayout.setVerticalGroup(
            urlPrefixFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(urlPrefixFilterPanelLayout.createSequentialGroup()
                .add(urlPrefixFilterLabel)
                .add(14, 14, 14)
                .add(urlPrefixCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(urlPrefixFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(urlPrefixTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(urlPrefixFieldLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        hostnameFilterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hostname Filter"));

        hostnameFilterLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        hostnameFilterLabel.setText("<html>Enter an regular expression which fully matches the hostname. Default is to allow all hostnames. Double click an item to edit.<br>Example: Enter \".*example\\.com\" to match all subdomains from examples.com.</html>");

        hostnameFilterTable.setModel(hostnameFilterTableModel);
        hostnameFilterScrollPane.setViewportView(hostnameFilterTable);

        hostnameFilterAddButton.setText("+");
        hostnameFilterAddButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        hostnameFilterAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hostnameFilterAddButtonActionPerformed(evt);
            }
        });

        hostnameFilterRemoveButton.setText("-");
        hostnameFilterRemoveButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        hostnameFilterRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hostnameFilterRemoveButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout hostnameFilterPanelLayout = new org.jdesktop.layout.GroupLayout(hostnameFilterPanel);
        hostnameFilterPanel.setLayout(hostnameFilterPanelLayout);
        hostnameFilterPanelLayout.setHorizontalGroup(
            hostnameFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(hostnameFilterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(hostnameFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, hostnameFilterPanelLayout.createSequentialGroup()
                        .add(hostnameFilterScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(hostnameFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(hostnameFilterRemoveButton)
                            .add(hostnameFilterAddButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(hostnameFilterLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
                .addContainerGap())
        );

        hostnameFilterPanelLayout.linkSize(new java.awt.Component[] {hostnameFilterAddButton, hostnameFilterRemoveButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        hostnameFilterPanelLayout.setVerticalGroup(
            hostnameFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(hostnameFilterPanelLayout.createSequentialGroup()
                .add(hostnameFilterLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hostnameFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(hostnameFilterPanelLayout.createSequentialGroup()
                        .add(hostnameFilterAddButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(hostnameFilterRemoveButton))
                    .add(hostnameFilterScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addContainerGap())
        );

        hostnameFilterPanelLayout.linkSize(new java.awt.Component[] {hostnameFilterAddButton, hostnameFilterRemoveButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        saveToDiskFilterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("'Save to Disk' Filter"));

        saveToDiskFilterLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        saveToDiskFilterLabel.setText("<html>Enter an regular expression which fully matches the files you want to save on your disk. Default is to save all files. Double click an item to edit.<br>Example: Enter \".*jpg\" to save all jpeg's.</html>");

        saveToDiskTable.setModel(saveToDiskFilterTabelModel);
        saveToDiskPane.setViewportView(saveToDiskTable);

        saveToDiskAddButton.setText("+");
        saveToDiskAddButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        saveToDiskAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToDiskAddButtonActionPerformed(evt);
            }
        });

        saveToDiskRemoveButton.setText("-");
        saveToDiskRemoveButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        saveToDiskRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToDiskRemoveButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout saveToDiskFilterPanelLayout = new org.jdesktop.layout.GroupLayout(saveToDiskFilterPanel);
        saveToDiskFilterPanel.setLayout(saveToDiskFilterPanelLayout);
        saveToDiskFilterPanelLayout.setHorizontalGroup(
            saveToDiskFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(saveToDiskFilterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(saveToDiskFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, saveToDiskFilterPanelLayout.createSequentialGroup()
                        .add(saveToDiskPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(saveToDiskFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(saveToDiskRemoveButton)
                            .add(saveToDiskAddButton)))
                    .add(saveToDiskFilterLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
                .addContainerGap())
        );

        saveToDiskFilterPanelLayout.linkSize(new java.awt.Component[] {saveToDiskAddButton, saveToDiskRemoveButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        saveToDiskFilterPanelLayout.setVerticalGroup(
            saveToDiskFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(saveToDiskFilterPanelLayout.createSequentialGroup()
                .add(saveToDiskFilterLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveToDiskFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(saveToDiskFilterPanelLayout.createSequentialGroup()
                        .add(saveToDiskAddButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(saveToDiskRemoveButton))
                    .add(saveToDiskPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addContainerGap())
        );

        saveToDiskFilterPanelLayout.linkSize(new java.awt.Component[] {saveToDiskAddButton, saveToDiskRemoveButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, hostnameFilterPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, saveToDiskFilterPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, limitsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, urlPrefixFilterPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(limitsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 126, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(urlPrefixFilterPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(hostnameFilterPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveToDiskFilterPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(12, 12, 12))
        );
    }// </editor-fold>//GEN-END:initComponents

	//GEN-FIRST:event_hostnameFilterAddButtonActionPerformed
	private void hostnameFilterAddButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		hostnameFilterTableModel.addRow(new String[] { "" });
		hostnameFilterTable.getSelectionModel().clearSelection();

		//select new row
		int row = hostnameFilterTableModel.getRowCount() - 1;
		hostnameFilterTable.changeSelection(row, 0, true, false);

		//scroll to new row
		Rectangle rect = hostnameFilterTable.getCellRect(row, 0, true);
		hostnameFilterTable.scrollRectToVisible(rect);

		//switch focus
		hostnameFilterTable.requestFocusInWindow();

		//enable editing
		hostnameFilterTable.editCellAt(row, 0);

	}//GEN-LAST:event_hostnameFilterAddButtonActionPerformed

	//GEN-FIRST:event_hostnameFilterRemoveButtonActionPerformed
	private void hostnameFilterRemoveButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		int[] selections = hostnameFilterTable.getSelectedRows();
		if (selections.length > 0) {
			for (int i = selections.length - 1; i >= 0; i--) {
				hostnameFilterTableModel.removeRow(selections[i]);
			}
		}

	}//GEN-LAST:event_hostnameFilterRemoveButtonActionPerformed

	//GEN-FIRST:event_saveToDiskAddButtonActionPerformed
	private void saveToDiskAddButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		saveToDiskFilterTabelModel.addRow(new String[] { "" });
		saveToDiskTable.getSelectionModel().clearSelection();

		//select new row
		int row = saveToDiskFilterTabelModel.getRowCount() - 1;
		saveToDiskTable.changeSelection(row, 0, true, false);

		//scroll to new row
		Rectangle rect = saveToDiskTable.getCellRect(row, 0, true);
		saveToDiskTable.scrollRectToVisible(rect);

		//switch focus
		saveToDiskTable.requestFocusInWindow();

		//enable editing
		saveToDiskTable.editCellAt(row, 0);

	}//GEN-LAST:event_saveToDiskAddButtonActionPerformed

	//GEN-FIRST:event_saveToDiskRemoveButtonActionPerformed
	private void saveToDiskRemoveButtonActionPerformed(
			java.awt.event.ActionEvent evt) {

		int[] selections = saveToDiskTable.getSelectedRows();
		if (selections.length > 0) {
			for (int i = selections.length - 1; i >= 0; i--) {
				saveToDiskFilterTabelModel.removeRow(selections[i]);
			}
		}

	}//GEN-LAST:event_saveToDiskRemoveButtonActionPerformed

	//GEN-FIRST:event_urlPrefixCheckBoxStateChanged
	private void urlPrefixCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {
		boolean enabled = urlPrefixCheckBox.isSelected();

		urlPrefixFieldLabel.setEnabled(enabled);
		urlPrefixTextField.setEnabled(enabled);

	}//GEN-LAST:event_urlPrefixCheckBoxStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton hostnameFilterAddButton;
    protected javax.swing.JLabel hostnameFilterLabel;
    protected javax.swing.JPanel hostnameFilterPanel;
    protected javax.swing.JButton hostnameFilterRemoveButton;
    protected javax.swing.JScrollPane hostnameFilterScrollPane;
    protected javax.swing.JTable hostnameFilterTable;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JLabel limitsLabel;
    protected javax.swing.JPanel limitsPanel;
    protected javax.swing.JLabel linksToFollowLabel;
    protected javax.swing.JTextField linksToFollowTextField;
    protected javax.swing.JLabel recursionDepthLabel;
    protected javax.swing.JTextField recursionDepthTextField;
    protected javax.swing.JButton saveToDiskAddButton;
    protected javax.swing.JLabel saveToDiskFilterLabel;
    protected javax.swing.JPanel saveToDiskFilterPanel;
    protected javax.swing.JScrollPane saveToDiskPane;
    protected javax.swing.JButton saveToDiskRemoveButton;
    protected javax.swing.JTable saveToDiskTable;
    protected javax.swing.JLabel timeLimitLabel;
    protected javax.swing.JTextField timeLimitTextField;
    protected javax.swing.JCheckBox urlPrefixCheckBox;
    protected javax.swing.JLabel urlPrefixFieldLabel;
    protected javax.swing.JLabel urlPrefixFilterLabel;
    protected javax.swing.JPanel urlPrefixFilterPanel;
    protected javax.swing.JTextField urlPrefixTextField;
    // End of variables declaration//GEN-END:variables

}