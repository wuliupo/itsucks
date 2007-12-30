/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 16.12.2007
 */

package de.phleisch.app.itsucks.gui.main.panel;

import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.gui.job.EditDownloadJobHelper;
import de.phleisch.app.itsucks.gui.job.ifc.AddDownloadJobCapable;
import de.phleisch.app.itsucks.gui.util.ExtendedListModel;
import de.phleisch.app.itsucks.gui.util.FieldValidator;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

/**
 *
 * @author  __USER__
 */
public class BatchProcessingPanel extends javax.swing.JPanel implements
		AddDownloadJobCapable {

	private static final long serialVersionUID = 202226684812236519L;
	@SuppressWarnings("unused")
	private static Log mLog = LogFactory.getLog(BatchProcessingPanel.class);

	protected BatchListModel jobListModel;

	/** Creates new form BatchProcessingPanel */
	public BatchProcessingPanel() {
		jobListModel = new BatchListModel();

		initComponents();
	}

	public void addDownload(SerializableJobPackage pJob) {

		if (pJob != null) {
			
			final JobListElement jobListElement = new JobListElement(pJob);
			
			jobListModel.add(jobListModel.getSize(), jobListElement);

			jobListElement.addPropertyChangeListener("state", new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent pEvt) {
					int index = jobListModel.indexOf(jobListElement);
					if(index > -1) {
						jobListModel.fireContentsChanged(index, index);
					}
				}
			});
			
		}

	}

	public static class BatchListModel extends ExtendedListModel {
		
		private static final long serialVersionUID = 2321310044625450683L;

		public BatchListModel() {
		}
	}
	
	public static class JobListElement {

		public enum State {
			OPEN, RUNNING, FINISHED
		}

		private PropertyChangeSupport mChangeSupport;
		
		private SerializableJobPackage mJobList;
		private State mState = State.OPEN;
		private int mDispatcherListId = -1;

		public JobListElement(SerializableJobPackage pJobList) {
			mChangeSupport = new PropertyChangeSupport(this);
			mJobList = pJobList;
		}

		@Override
		public String toString() {
			return toHtmlString();
		}

		public SerializableJobPackage getJobList() {
			return mJobList;
		}

		public State getState() {
			return mState;
		}

		public void setState(State pState) {
			State oldState = mState;
			mState = pState;
			
			mChangeSupport.firePropertyChange("state", oldState, mState);
		}

		public int getDispatcherListId() {
			return mDispatcherListId;
		}

		public void setDispatcherListId(int pDispatcherListId) {
			int oldId = mDispatcherListId;
			mDispatcherListId = pDispatcherListId;
			
			mChangeSupport.firePropertyChange("dispatcherListId", oldId, mDispatcherListId);
		}

		/**
		 * Returns a string containing all information about the filter.
		 * HTML format.
		 * 
		 * @return
		 */
		public String toHtmlString() {
			return "<html>" + mJobList.getJobs().get(0).getName() + "<br>"
					+ "State: " + mState + "</html>";
		}

		public boolean isFinished() {
			return mState.equals(State.FINISHED);
		}

		public void addPropertyChangeListener(PropertyChangeListener pListener) {
			mChangeSupport.addPropertyChangeListener(pListener);
		}

		public void addPropertyChangeListener(String pPropertyName,
				PropertyChangeListener pListener) {
			mChangeSupport.addPropertyChangeListener(pPropertyName, pListener);
		}

		public void removePropertyChangeListener(
				PropertyChangeListener pListener) {
			mChangeSupport.removePropertyChangeListener(pListener);
		}

		public void removePropertyChangeListener(String pPropertyName,
				PropertyChangeListener pListener) {
			mChangeSupport.removePropertyChangeListener(pPropertyName,
					pListener);
		}
	}

	public void setElementsEnabled(boolean pValue) {

		addJobButton.setEnabled(pValue);
		loadJobButton.setEnabled(pValue);
		editJobButton.setEnabled(pValue);
		removeJobButton.setEnabled(pValue);
		moveUpButton.setEnabled(pValue);
		moveDownButton.setEnabled(pValue);
		concurrentJobsField.setEnabled(pValue);
		jobList.clearSelection();

	}

	public List<JobListElement> getJobListElements() {
		
		JobListElement[] list = new JobListElement[jobListModel.size()];
		jobListModel.copyInto(list);
		
		return Arrays.asList(list);
	}
	
	public List<String> validateFields() {
		
		FieldValidator validator = new FieldValidator();

		validator.assertInteger(this.concurrentJobsField.getText(),
				"Enter a valid number for the concurrent jobs.");
		
		return validator.getErrors();
	}
	
	public int getMaxConcurrentJobs() {
		return Integer.parseInt(this.concurrentJobsField.getText());
	}

	public boolean isCloseJobAfterFinish() {
		return this.closeJobAfterFinishCheckBox.isSelected();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();
		jobListScrollPane = new javax.swing.JScrollPane();
		jobList = new javax.swing.JList();
		addJobButton = new javax.swing.JButton();
		loadJobButton = new javax.swing.JButton();
		editJobButton = new javax.swing.JButton();
		removeJobButton = new javax.swing.JButton();
		moveUpButton = new javax.swing.JButton();
		moveDownButton = new javax.swing.JButton();
		concurrentJobsField = new javax.swing.JTextField();
		concurrentJobsLabel = new javax.swing.JLabel();
		closeJobAfterFinishCheckBox = new javax.swing.JCheckBox();

		jPanel1.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Batch Processing"));
		jobList.setFont(new java.awt.Font("Dialog", 0, 12));
		jobList.setModel(jobListModel);
		jobListScrollPane.setViewportView(jobList);

		addJobButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addJobButton.setText("Add Job");
		addJobButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addJobButtonActionPerformed(evt);
			}
		});

		loadJobButton.setFont(new java.awt.Font("Dialog", 0, 12));
		loadJobButton.setText("Load Job");
		loadJobButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadJobButtonActionPerformed(evt);
			}
		});

		editJobButton.setFont(new java.awt.Font("Dialog", 0, 12));
		editJobButton.setText("Edit Job");
		editJobButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				editJobButtonActionPerformed(evt);
			}
		});

		removeJobButton.setFont(new java.awt.Font("Dialog", 0, 12));
		removeJobButton.setText("Remove Job");
		removeJobButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				removeJobButtonActionPerformed(evt);
			}
		});

		moveUpButton.setFont(new java.awt.Font("Dialog", 0, 12));
		moveUpButton.setText("Move up");
		moveUpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				moveUpButtonActionPerformed(evt);
			}
		});

		moveDownButton.setFont(new java.awt.Font("Dialog", 0, 12));
		moveDownButton.setText("Move down");
		moveDownButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				moveDownButtonActionPerformed(evt);
			}
		});

		concurrentJobsField
				.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		concurrentJobsField.setText("1");

		concurrentJobsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		concurrentJobsLabel.setText("Jobs at the same time");

		closeJobAfterFinishCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
		closeJobAfterFinishCheckBox.setSelected(true);
		closeJobAfterFinishCheckBox
				.setText("Close Jobs when finished (Saves memory)");
		closeJobAfterFinishCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		closeJobAfterFinishCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												jPanel1Layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																jPanel1Layout
																		.createSequentialGroup()
																		.add(
																				concurrentJobsField,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				31,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				concurrentJobsLabel))
														.add(
																jPanel1Layout
																		.createSequentialGroup()
																		.add(
																				jobListScrollPane,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				351,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				jPanel1Layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								false)
																						.add(
																								addJobButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								loadJobButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								94,
																								Short.MAX_VALUE)
																						.add(
																								moveUpButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								moveDownButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								editJobButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								removeJobButton)))
														.add(
																closeJobAfterFinishCheckBox))
										.addContainerGap()));

		jPanel1Layout.linkSize(new java.awt.Component[] { addJobButton,
				editJobButton, loadJobButton, moveDownButton, moveUpButton,
				removeJobButton }, org.jdesktop.layout.GroupLayout.HORIZONTAL);

		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								jPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												jPanel1Layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																jobListScrollPane,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																318,
																Short.MAX_VALUE)
														.add(
																jPanel1Layout
																		.createSequentialGroup()
																		.add(
																				addJobButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				loadJobButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				editJobButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				removeJobButton)
																		.add(
																				26,
																				26,
																				26)
																		.add(
																				moveUpButton)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				moveDownButton)))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												jPanel1Layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																concurrentJobsField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(
																concurrentJobsLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(closeJobAfterFinishCheckBox)
										.addContainerGap()));

		jPanel1Layout.linkSize(new java.awt.Component[] { addJobButton,
				editJobButton, loadJobButton, moveDownButton, moveUpButton,
				removeJobButton }, org.jdesktop.layout.GroupLayout.VERTICAL);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING, jPanel1,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING, jPanel1,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
	}// </editor-fold>//GEN-END:initComponents

	//GEN-FIRST:event_moveDownButtonActionPerformed
	private void moveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {

		int[] selectedIndices = jobList.getSelectedIndices();

		//check if move is possible
		for (int i = 0; i < selectedIndices.length; i++) {
			if (selectedIndices[i] == (jobListModel.getSize() - 1))
				return;
		}

		//move the entries
		for (int i = (selectedIndices.length - 1); i > -1; i--) {
			selectedIndices[i] = jobListModel.moveEntry(selectedIndices[i], 1);
		}
		jobList.setSelectedIndices(selectedIndices);

	}//GEN-LAST:event_moveDownButtonActionPerformed

	//GEN-FIRST:event_moveUpButtonActionPerformed
	private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {

		int[] selectedIndices = jobList.getSelectedIndices();

		//check if move is possible
		for (int i = 0; i < selectedIndices.length; i++) {
			if (selectedIndices[i] == 0)
				return;
		}

		//move the entries
		for (int i = 0; i < selectedIndices.length; i++) {
			selectedIndices[i] = jobListModel.moveEntry(selectedIndices[i], -1);
		}
		jobList.setSelectedIndices(selectedIndices);

	}//GEN-LAST:event_moveUpButtonActionPerformed

	//GEN-FIRST:event_removeJobButtonActionPerformed
	private void removeJobButtonActionPerformed(java.awt.event.ActionEvent evt) {

		int[] selectedIndices = jobList.getSelectedIndices();
		for (int i = (selectedIndices.length - 1); i > -1; i--) {
			jobListModel.remove(selectedIndices[i]);
		}

	}//GEN-LAST:event_removeJobButtonActionPerformed

	//GEN-FIRST:event_editJobButtonActionPerformed
	private void editJobButtonActionPerformed(java.awt.event.ActionEvent evt) {

		EditDownloadJobHelper helper = new EditDownloadJobHelper(
				(Dialog) getRootPane().getParent());

		int[] selectedIndices = jobList.getSelectedIndices();
		for (int i = (selectedIndices.length - 1); i > -1; i--) {
			final JobListElement element = (JobListElement) jobListModel
					.get(selectedIndices[i]);

			helper.editDownload(new AddDownloadJobCapable() {
				public void addDownload(SerializableJobPackage pJob) {
					element.mJobList = pJob;

					int index = jobListModel.indexOf(element);
					jobListModel.fireContentsChanged(index, index);
				}
			}, element.mJobList);
		}

	}//GEN-LAST:event_editJobButtonActionPerformed

	//GEN-FIRST:event_loadJobButtonActionPerformed
	private void loadJobButtonActionPerformed(java.awt.event.ActionEvent evt) {

		EditDownloadJobHelper helper = new EditDownloadJobHelper(
				(Dialog) getRootPane().getParent());

		List<SerializableJobPackage> loadDownload = helper.loadDownload();
		for (SerializableJobPackage serializableJobPackage : loadDownload) {
			this.addDownload(serializableJobPackage);
		}

	}//GEN-LAST:event_loadJobButtonActionPerformed

	//GEN-FIRST:event_addJobButtonActionPerformed
	private void addJobButtonActionPerformed(java.awt.event.ActionEvent evt) {

		EditDownloadJobHelper helper = new EditDownloadJobHelper(
				(Dialog) getRootPane().getParent());
		helper.openAddDownloadDialog(this);

	}//GEN-LAST:event_addJobButtonActionPerformed

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JButton addJobButton;
	private javax.swing.JCheckBox closeJobAfterFinishCheckBox;
	private javax.swing.JTextField concurrentJobsField;
	private javax.swing.JLabel concurrentJobsLabel;
	private javax.swing.JButton editJobButton;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JList jobList;
	private javax.swing.JScrollPane jobListScrollPane;
	private javax.swing.JButton loadJobButton;
	private javax.swing.JButton moveDownButton;
	private javax.swing.JButton moveUpButton;
	private javax.swing.JButton removeJobButton;
	// End of variables declaration//GEN-END:variables

}