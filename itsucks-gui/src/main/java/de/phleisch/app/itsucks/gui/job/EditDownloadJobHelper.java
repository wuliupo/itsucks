/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.12.2007
 */

package de.phleisch.app.itsucks.gui.job;

import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.SpringContextSingelton;
import de.phleisch.app.itsucks.gui.job.ifc.AddDownloadJobCapable;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.persistence.JobSerialization;
import de.phleisch.app.itsucks.persistence.SerializableJobList;

public class EditDownloadJobHelper {

	private static Log mLog = LogFactory.getLog(EditDownloadJobHelper.class);
	private Frame mParentFrame;
	private Dialog mParentDialog;
	
	public EditDownloadJobHelper(Frame pParentFrame) {
		mParentFrame = pParentFrame;
	}
	
	public EditDownloadJobHelper(Dialog pParentDialog) {
		mParentDialog = pParentDialog;
	}
	
	public void openAddDownloadDialog(AddDownloadJobCapable pAddDownloadJobCapable) {

		JobSerialization serializationManager = (JobSerialization) SpringContextSingelton
				.getApplicationContext().getBean("JobSerialization");

		SerializableJobList jobList = null;
		try {
			jobList = serializationManager.deserialize(getClass()
					.getResourceAsStream("/ItSucks_Default_Template.suck"));
		} catch (Exception e1) {

			mLog.error("Error occured while loading download template", e1);

			JOptionPane.showMessageDialog(mParentFrame,
					"Error occured while loading download template.\n"
							+ e1.getMessage(), "Error occured",
					JOptionPane.ERROR_MESSAGE);
		}

		if (jobList != null) {

			//set defaults
			UrlDownloadJob job = (UrlDownloadJob) jobList.getJobs().get(0);

			job.setSavePath(new File(System.getProperty("user.home")
					+ File.separatorChar + "itsucks" + File.separatorChar));

			EditDownloadJobDialog dialog;
			if(mParentFrame != null) {
				dialog = new EditDownloadJobDialog(mParentFrame, pAddDownloadJobCapable);
			} else if(mParentDialog != null) {
				dialog = new EditDownloadJobDialog(mParentDialog, pAddDownloadJobCapable);
			} else {
				throw new RuntimeException("No parent defined.");
			}
			
			dialog.loadJob(jobList);
			dialog.setVisible(true);
		}
	}

	public void loadDownload(AddDownloadJobCapable pAddDownloadJobCapable) {
		//open dialog
		JFileChooser fc = new JFileChooser();
		fc
				.setFileFilter(new FileNameExtensionFilter(
						"ItSucks Download Templates (*.suck)",
						new String[] { "suck" }));

		//Show load dialog; this method does not return until the dialog is closed
		int result = fc.showOpenDialog(mParentFrame);

		if (result == JFileChooser.APPROVE_OPTION) {
			JobSerialization serializationManager = (JobSerialization) SpringContextSingelton
					.getApplicationContext().getBean("JobSerialization");

			SerializableJobList jobList = null;
			try {
				jobList = serializationManager
						.deserialize(fc.getSelectedFile());
			} catch (Exception e1) {

				mLog.error("Error occured while loading download template", e1);

				JOptionPane.showMessageDialog(mParentFrame,
						"Error occured while loading download template.\n"
								+ e1.getMessage(), "Error occured",
						JOptionPane.ERROR_MESSAGE);
			}

			if (jobList != null) {
				//addDownload((DownloadJob)jobList.getJobs().get(0), jobList.getFilters());

				EditDownloadJobDialog dialog;
				if(mParentFrame != null) {
					dialog = new EditDownloadJobDialog(mParentFrame, pAddDownloadJobCapable);
				} else if(mParentDialog != null) {
					dialog = new EditDownloadJobDialog(mParentDialog, pAddDownloadJobCapable);
				} else {
					throw new RuntimeException("No parent defined.");
				}
				
				dialog.loadJob(jobList);
				dialog.setVisible(true);
			}

		}

	}
	
	
}
