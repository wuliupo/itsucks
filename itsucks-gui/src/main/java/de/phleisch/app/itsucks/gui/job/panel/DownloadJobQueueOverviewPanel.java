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

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class DownloadJobQueueOverviewPanel extends JPanel {

	private static final long serialVersionUID = 2406761069696757338L;
	private transient Dispatcher mJobDispatcher;
	private transient int mDispatcherId;
	
	private static Log mLog = LogFactory.getLog(DownloadJobQueueOverviewPanel.class);
	
	private EventObserver mEventObserver = new JobEventObserver();

	/** Creates new form QueueDownloadJobOverview */
	public DownloadJobQueueOverviewPanel() {
		init();
	}
	
	protected void init() {
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
			mLog.debug("Add job " + pJob.getId() + " to " + panel.getName());
			panel.addDownloadJob(pJob);
		}
	}

	private void removeDownloadJob(UrlDownloadJob pJob) {

		List<DownloadJobStatusTablePanel> panels = getPanelsForState(pJob.getState());
		for (DownloadJobStatusTablePanel panel : panels) {
			mLog.debug("Remove job " + pJob.getId() + " from " + panel.getName());
			panel.removeDownloadJob(pJob);
		}
	}

	private void moveDownloadJob(UrlDownloadJob pJob, int pOldState, int pNewState) {

		List<DownloadJobStatusTablePanel> oldPanels = getPanelsForState(pOldState);
		List<DownloadJobStatusTablePanel> newPanels = getPanelsForState(pNewState);

		for (DownloadJobStatusTablePanel oldPanel : oldPanels) {
			if (!newPanels.contains(oldPanel)) {
				mLog.debug("(Move) Remove job " + pJob.getId() + " from " + oldPanel.getName());
				oldPanel.removeDownloadJob(pJob);
			}
		}

		for (DownloadJobStatusTablePanel newPanel : newPanels) {
			if (!oldPanels.contains(newPanel)) {
				mLog.debug("(Move) Add job " + pJob.getId() + " to " + newPanel.getName());
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        downloadJobStatusTableRunningPanel = new de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel();
        downloadJobStatusTableOpenPanel = new de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel();
        downloadJobStatusTableFinishedPanel = new de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel();
        downloadJobStatusTableAllPanel = new de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel();
        infoPanel = new javax.swing.JPanel();

        tabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));

        downloadJobStatusTableRunningPanel.setName("runningJobs"); // NOI18N
        tabbedPane.addTab("Running", downloadJobStatusTableRunningPanel);

        downloadJobStatusTableOpenPanel.setName("openJobs"); // NOI18N
        tabbedPane.addTab("Open", downloadJobStatusTableOpenPanel);

        downloadJobStatusTableFinishedPanel.setName("finishedJobs"); // NOI18N
        tabbedPane.addTab("Finished", downloadJobStatusTableFinishedPanel);

        downloadJobStatusTableAllPanel.setName("allJobs"); // NOI18N
        tabbedPane.addTab("All", downloadJobStatusTableAllPanel);

        org.jdesktop.layout.GroupLayout infoPanelLayout = new org.jdesktop.layout.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 585, Short.MAX_VALUE)
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 443, Short.MAX_VALUE)
        );

        tabbedPane.addTab("Info", infoPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel downloadJobStatusTableAllPanel;
    private de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel downloadJobStatusTableFinishedPanel;
    private de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel downloadJobStatusTableOpenPanel;
    private de.phleisch.app.itsucks.gui.job.panel.DownloadJobStatusTablePanel downloadJobStatusTableRunningPanel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

}