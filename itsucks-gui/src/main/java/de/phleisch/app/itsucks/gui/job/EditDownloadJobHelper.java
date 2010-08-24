/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.12.2007
 */

package de.phleisch.app.itsucks.gui.job;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.GuiceContextSingelton;
import de.phleisch.app.itsucks.configuration.ApplicationConfiguration;
import de.phleisch.app.itsucks.gui.job.ifc.AddDownloadJobCapable;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

public class EditDownloadJobHelper {

	private static Log mLog = LogFactory.getLog(EditDownloadJobHelper.class);
	private Frame mParentFrame;
	private Dialog mParentDialog;
	private Component mParentComponent;
	
	public EditDownloadJobHelper(Frame pParentFrame) {
		mParentFrame = pParentFrame;
		mParentComponent = pParentFrame;
	}
	
	public EditDownloadJobHelper(Dialog pParentDialog) {
		mParentDialog = pParentDialog;
		mParentComponent = pParentDialog;
	}
	
	public JDialog openAddDownloadDialog(AddDownloadJobCapable pAddDownloadJobCapable) {

		JobSerialization serializationManager = GuiceContextSingelton.getInjector().getInstance(JobSerialization.class);
		
		SerializableJobPackage jobList = null;
		try {
			jobList = serializationManager.deserialize(getClass()
					.getResourceAsStream("/ItSucks_Default_Template.suck"));
		} catch (Exception e1) {

			mLog.error("Error occured while loading download template", e1);

			JOptionPane.showMessageDialog(mParentComponent,
					"Error occured while loading download template.\n"
							+ e1.getMessage(), "Error occured",
					JOptionPane.ERROR_MESSAGE);
		}

		JDialog dialog = null;
		
		if (jobList != null) {

			//set defaults
			UrlDownloadJob job = (UrlDownloadJob) jobList.getJobs().get(0);

			job.setSavePath(new File(System.getProperty("user.home")
					+ File.separatorChar + "itsucks" + File.separatorChar));

			dialog = editDownload(pAddDownloadJobCapable, jobList);
		}
		
		return dialog;
	}

	public void loadAndEditDownload(AddDownloadJobCapable pAddDownloadJobCapable) {
		
		List<SerializableJobPackage> jobList = loadDownload();
		
		for (SerializableJobPackage serializableJobPackage : jobList) {
			editDownload(pAddDownloadJobCapable, serializableJobPackage);
		}
	}
	
	public List<SerializableJobPackage> loadDownload() {
		
		List<SerializableJobPackage> jobPackageList = 
			new ArrayList<SerializableJobPackage>();
		
		//open dialog
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter(
			"ItSucks Download Templates (*.suck)",
			new String[] { "suck" }));

		fc.setMultiSelectionEnabled(true);
		
		String lastDirectory = getLastDirectory();
		if(lastDirectory != null) {
			fc.setSelectedFile(new File(lastDirectory  + "."));
		}
		
		//Show load dialog; this method does not return until the dialog is closed
		int result = fc.showOpenDialog(mParentComponent);

		if (result == JFileChooser.APPROVE_OPTION) {
			JobSerialization serializationManager = GuiceContextSingelton.getInjector().getInstance(JobSerialization.class);

			for (File selectedFile : fc.getSelectedFiles()) {
			
				try {
					jobPackageList.add(serializationManager
							.deserialize(selectedFile));
				} catch (Exception e1) {
	
					mLog.error("Error occured while loading download template", e1);
	
					JOptionPane.showMessageDialog(mParentComponent,
							"Error occured while loading download template.\n"
									+ e1.getMessage(), "Error occured",
							JOptionPane.ERROR_MESSAGE);
				}

				setLastDirectory(selectedFile.getParent());
			}
			
		}
		
		return jobPackageList;
	}

	public JDialog editDownload(AddDownloadJobCapable pAddDownloadJobCapable,
		SerializableJobPackage jobList) {
		
		EditDownloadJobTreeDialog dialog = null;
		
		if (jobList != null) {
			//addDownload((DownloadJob)jobList.getJobs().get(0), jobList.getFilters());

//			EditDownloadJobDialog dialog;
//			if(mParentFrame != null) {
//				dialog = new EditDownloadJobDialog(mParentFrame, pAddDownloadJobCapable);
//			} else if(mParentDialog != null) {
//				dialog = new EditDownloadJobDialog(mParentDialog, pAddDownloadJobCapable);
//			} else {
//				throw new RuntimeException("No parent defined.");
//			}
			
			if(mParentFrame != null) {
				dialog = new EditDownloadJobTreeDialog(mParentFrame, pAddDownloadJobCapable);
			} else if(mParentDialog != null) {
				dialog = new EditDownloadJobTreeDialog(mParentDialog, pAddDownloadJobCapable);
			} else {
				throw new RuntimeException("No parent defined.");
			}
			
			dialog.loadJob(jobList);
			dialog.setVisible(true);
		}
		
		return dialog;
	}
	
	public void saveDownloadTemplate(SerializableJobPackage pDownloadJobList) {

		//open dialog
		JFileChooser fc = new JFileChooser();
		fc
				.setFileFilter(new FileNameExtensionFilter(
						"ItSucks Download Templates (*.suck)",
						new String[] { "suck" }));

		String fileName = "ItSucks_"
			+ pDownloadJobList.getJobs().get(0).getName().replace(' ', '_')
			+ "_Template.suck";
		
		String lastDirectory = getLastDirectory();
		if(lastDirectory != null) {
			fileName = lastDirectory + fileName; 
		}
		fc.setSelectedFile(new File(fileName));

		// Show save dialog; this method does not return until the dialog is closed
		int result = fc.showSaveDialog(mParentComponent);
		if (result == JFileChooser.APPROVE_OPTION) {

			JobSerialization serializationManager = GuiceContextSingelton.getInjector().getInstance(JobSerialization.class);
			
			try {
				serializationManager.serialize(pDownloadJobList, fc
						.getSelectedFile());
			} catch (Exception e1) {

				mLog.error("Error occured while saving download template", e1);

				String message = e1.getMessage();
				if (message == null) {
					message = e1.toString();
				}

				JOptionPane.showMessageDialog(mParentComponent,
						"Error occured while saving download template.\n"
								+ message, "Error occured",
						JOptionPane.ERROR_MESSAGE);
			}
			
			setLastDirectory(fc.getSelectedFile().getParent());
		}
		
	}
	
	public String getLastDirectory() {
		
		ApplicationConfiguration configuration = GuiceContextSingelton.getInjector().getInstance(ApplicationConfiguration.class);
		
		return configuration.getValue("last_directory");
	}

	public void setLastDirectory(String pPath) {
		
		ApplicationConfiguration configuration = GuiceContextSingelton.getInjector().getInstance(ApplicationConfiguration.class);
		
		configuration.setValue("last_directory", pPath + File.separator);
	}
	
}
