/*
 * QueueDownloadJobOverview.java
 *
 * Created on __DATE__, __TIME__
 */

package de.phleisch.app.itsucks.gui.job.panel;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.event.impl.CoreEvents;
import de.phleisch.app.itsucks.event.impl.DefaultEventFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.job.event.JobAddedEvent;
import de.phleisch.app.itsucks.job.event.JobChangedEvent;
import de.phleisch.app.itsucks.job.event.JobEvent;

/**
 *
 * @author  __USER__
 */
public class DownloadJobQueueOverviewPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 2406761069696757338L;
	private transient Dispatcher mJobDispatcher;
	private transient int mDispatcherId;
	
	private EventObserver mEventObserver = new JobEventObserver();

	/** Creates new form QueueDownloadJobOverview */
	public DownloadJobQueueOverviewPanel() {
		initComponents();
	}

	public void setDispatcher(Dispatcher pDispatcher) {
		mJobDispatcher = pDispatcher;

		DefaultEventFilter eventFilter = new DefaultEventFilter();
		eventFilter.addAllowedCategory(CoreEvents.EVENT_CATEGORY_JOBMANAGER);

		mJobDispatcher.getEventManager().registerObserver(mEventObserver,
				eventFilter);
	}

	public Dispatcher getDispatcher() {
		return mJobDispatcher;
	}

	public void removeDispatcher() {
		mJobDispatcher.getEventManager().unregisterObserver(mEventObserver);

		downloadJobStatusTableAllPanel.shutdown();
		downloadJobStatusTableRunningPanel.shutdown();
		downloadJobStatusTableOpenPanel.shutdown();
		downloadJobStatusTableFinishedPanel.shutdown();

		mJobDispatcher = null;
	}

	private void addDownloadJob(UrlDownloadJob pJob, int pInitialState) {

		List<DownloadJobStatusTablePanel> panels = getPanelsForState(pInitialState);
		for (DownloadJobStatusTablePanel panel : panels) {
			panel.addDownloadJob(pJob);
		}
	}

	private void removeDownloadJob(UrlDownloadJob pJob) {

		List<DownloadJobStatusTablePanel> panels = getPanelsForState(pJob
				.getState());
		for (DownloadJobStatusTablePanel panel : panels) {
			panel.removeDownloadJob(pJob);
		}
	}

	private void moveDownloadJob(UrlDownloadJob pJob, int pOldState, int pNewState) {

		List<DownloadJobStatusTablePanel> oldPanels = getPanelsForState(pOldState);
		List<DownloadJobStatusTablePanel> newPanels = getPanelsForState(pNewState);

		for (DownloadJobStatusTablePanel oldPanel : oldPanels) {
			if (!newPanels.contains(oldPanel)) {
				oldPanel.removeDownloadJob(pJob);
			}
		}

		for (DownloadJobStatusTablePanel newPanel : newPanels) {
			if (!oldPanels.contains(newPanel)) {
				newPanel.addDownloadJob(pJob);
			}
		}
	}

	private List<DownloadJobStatusTablePanel> getPanelsForState(int pState) {

		List<DownloadJobStatusTablePanel> panels = new ArrayList<DownloadJobStatusTablePanel>();

		panels.add(downloadJobStatusTableAllPanel);

		switch (pState) {

		case Job.STATE_OPEN:
		case Job.STATE_REOPEN:
			panels.add(downloadJobStatusTableOpenPanel);
			break;

		case Job.STATE_ASSIGNED:
		case Job.STATE_IN_PROGRESS:
		case Job.STATE_IN_PROGRESS_RETRY:
			panels.add(downloadJobStatusTableRunningPanel);
			break;

		case Job.STATE_CLOSED:
		case Job.STATE_ERROR:
		case Job.STATE_FAILED:
		case Job.STATE_FINISHED:
		case Job.STATE_IGNORED:
		case Job.STATE_ALREADY_PROCESSED:
			panels.add(downloadJobStatusTableFinishedPanel);
			break;

		default:
			throw new IllegalStateException("Unknown state: " + pState);

		}

		return panels;
	}
	
	public int getDispatcherId() {
		return mDispatcherId;
	}

	public void setDispatcherId(int pDispatcherId) {
		mDispatcherId = pDispatcherId;
	}

	private class JobEventObserver implements EventObserver, Serializable {

		private static final long serialVersionUID = -2976276628351550703L;

		public void processEvent(Event pEvent) {

			JobEvent jobEvent = (JobEvent) pEvent;
			UrlDownloadJob job = (UrlDownloadJob) jobEvent.getJob();

			if (jobEvent.getType() == CoreEvents.EVENT_JOBMANAGER_JOB_CHANGED.getType()) {

				PropertyChangeEvent propertyChangeEvent = ((JobChangedEvent) jobEvent)
						.getPropertyChangeEvent();

				if (Job.JOB_STATE_PROPERTY.equals(propertyChangeEvent
						.getPropertyName())) {
					moveDownloadJob(job, (Integer) propertyChangeEvent
							.getOldValue(), (Integer) propertyChangeEvent
							.getNewValue());
				}

			} else if (jobEvent.getType() == CoreEvents.EVENT_JOBMANAGER_JOB_ADDED
					.getType()) {

				int initialState = ((JobAddedEvent) jobEvent).getInitialState();

				addDownloadJob(job, initialState);

			} else if (jobEvent.getType() == CoreEvents.EVENT_JOBMANAGER_JOB_REMOVED
					.getType()) {

				removeDownloadJob(job);

			}
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jTabbedPane1 = new javax.swing.JTabbedPane();
		downloadJobStatusTableRunningPanel = new de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel();
		downloadJobStatusTableOpenPanel = new de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel();
		downloadJobStatusTableFinishedPanel = new de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel();
		downloadJobStatusTableAllPanel = new de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel();
		infoPanel = new javax.swing.JPanel();

		jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
		jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 12));
		jTabbedPane1.addTab("Running", downloadJobStatusTableRunningPanel);

		jTabbedPane1.addTab("Open", downloadJobStatusTableOpenPanel);

		jTabbedPane1.addTab("Finished", downloadJobStatusTableFinishedPanel);

		jTabbedPane1.addTab("All", downloadJobStatusTableAllPanel);

		org.jdesktop.layout.GroupLayout infoPanelLayout = new org.jdesktop.layout.GroupLayout(
				infoPanel);
		infoPanel.setLayout(infoPanelLayout);
		infoPanelLayout.setHorizontalGroup(infoPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(0, 584,
				Short.MAX_VALUE));
		infoPanelLayout.setVerticalGroup(infoPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(0, 445,
				Short.MAX_VALUE));
		jTabbedPane1.addTab("Info", infoPanel);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(jTabbedPane1,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(jTabbedPane1,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 472,
				Short.MAX_VALUE));
	}// </editor-fold>//GEN-END:initComponents

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel downloadJobStatusTableAllPanel;
	private de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel downloadJobStatusTableFinishedPanel;
	private de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel downloadJobStatusTableOpenPanel;
	private de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel downloadJobStatusTableRunningPanel;
	private javax.swing.JPanel infoPanel;
	private javax.swing.JTabbedPane jTabbedPane1;
	// End of variables declaration//GEN-END:variables

}