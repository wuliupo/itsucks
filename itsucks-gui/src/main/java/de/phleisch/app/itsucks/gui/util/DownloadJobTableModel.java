/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.io.http.HttpMetadata;

public class DownloadJobTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 7125707024529473869L;

	private static final int COLUMN_ID 			= 0;
	private static final int COLUMN_PRIORITY 	= 1;
	private static final int COLUMN_STATE 		= 2;
	private static final int COLUMN_PROGRESS 	= 3;
	private static final int COLUMN_KILOBYTES 	= 4;
	private static final int COLUMN_RESULT 		= 5;
	private static final int COLUMN_URL 		= 6;
	
	private static final int COLUMN_COUNT 		= 7;
	
	private static final int JOB_PROGRESS_UPDATE_FREQUENCY = 250; //ms

	private static Log mLog = LogFactory.getLog(DownloadJobTableModel.class);
	
	private List<DownloadJob> mRows;
	
	private JobObserver mJobObserver;
	
	public DownloadJobTableModel() {
		mRows = Collections.synchronizedList(new IndexedList<DownloadJob>());
		mJobObserver = new JobObserver();
		
		mJobObserver.start();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int pColumn) {
		
		String name;
		
		switch (pColumn) {
		case COLUMN_ID:
			name = "ID";
			break;
		
		case COLUMN_PRIORITY:
			name = "Priority";
			break;

		case COLUMN_STATE:
			name = "State";
			break;
			
		case COLUMN_URL:
			name = "URL";
			break;
			
		case COLUMN_PROGRESS:
			name = "Progress";
			break;	
			
		case COLUMN_KILOBYTES:
			name = "Downloaded";
			break;			
			
		case COLUMN_RESULT:
			name = "Result";
			break;					
			
		default:
			name = "";
			break;
		}
		
		return name;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return mRows.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int pRowIndex, int pColumnIndex) {
		
		DownloadJob job = mRows.get(pRowIndex);
		
		return getColumnValue(job, pColumnIndex);
	}
	
	public void addDownloadJob(DownloadJob pJob) {
		
		mJobObserver.queueJobAdd(pJob);
	}
	
	public void removeDownloadJob(DownloadJob pJob) {
		
		mJobObserver.queueJobRemove(pJob);
	}

	public void removeAllDownloadJobs() {
		
		mJobObserver.queueClear();
		
	}
	
	public void stop() {
		mJobObserver.stop();
	}
	
	private Object getColumnValue(DownloadJob pJob, int pColumnIndex) {
		
		Object val = null;
		
		switch (pColumnIndex) {
			case COLUMN_ID:
				val = pJob.getId();
				break;
				
			case COLUMN_PRIORITY:
				val = pJob.getPriority();
				break;
	
			case COLUMN_STATE:
				val = translateState(pJob.getState());
				break;			
			
			case COLUMN_URL:
				val = pJob.getUrl();
				break;		
	
			case COLUMN_PROGRESS:
				float progress = pJob.getProgress();
				if(progress > -1) {
					val = ((int)(progress * 100)) + "%";
				} else {
					val = "-";
				}
				break;
				
			case COLUMN_KILOBYTES:
				long byteCount = pJob.getBytesDownloaded();
				if(byteCount > -1) {
					val = formatByteCount(pJob.getBytesDownloaded());
				} else {
					val = "-";
				}
				break;
			
			case COLUMN_RESULT:
				HttpMetadata metadata = (HttpMetadata) pJob.getMetadata();
				if(metadata != null) {
					val = String.valueOf(metadata.getStatusCode());
					if(metadata.getStatusText() != null) {
						val = val + " - " + metadata.getStatusText();
					}
				}
				if(val == null) {
					val = "-";
				}
				
				break;
				
			default:
				break;
		}
		return val;
	}

	private static long KB = 1024;
	private static long MB = KB * 1024;
	private static long GB = MB * 1024;
	
	private static BigDecimal KB_D = new BigDecimal(1024);
	private static BigDecimal MB_D = new BigDecimal(KB * 1024);
	private static BigDecimal GB_D = new BigDecimal(MB * 1024);
	
    private String formatByteCount(long pBytesDownloaded) {
		String result = null;
    	
    	BigDecimal bytesDownloaded = new BigDecimal(pBytesDownloaded);
		
    	if(pBytesDownloaded <= KB) {
    		result = bytesDownloaded + " B";
    	} else if(pBytesDownloaded > KB && pBytesDownloaded <= MB) {
    		result = bytesDownloaded.divide(KB_D, 2, BigDecimal.ROUND_HALF_UP) + " KB";
    	} else if(pBytesDownloaded > MB && pBytesDownloaded <= GB) {
    		result = bytesDownloaded.divide(MB_D, 2, BigDecimal.ROUND_HALF_UP) + " MB";
    	} else if(pBytesDownloaded > GB) {
    		result = bytesDownloaded.divide(GB_D, 2, BigDecimal.ROUND_HALF_UP) + " GB";
    	}
    	
    	if(result == null) {
    		throw new IllegalStateException("Uhoh, this is a bug, please report!: " + pBytesDownloaded);
    	}
    		
		return result;
	}

	private Object translateState(int pState) {
    	
    	String result;
    	
    	switch (pState) {
    	
    	case Job.STATE_OPEN:
    		result = "Open";
    		break;
    	
    		
    	case Job.STATE_ASSIGNED:
    		result = "Assigned";
    		break;
    		
    	case Job.STATE_IN_PROGRESS:
    		result = "In progress";
    		break;
    		

    	// every state over 50 is closed
    	case Job.STATE_CLOSED:
    		result = "Closed";
    		break;
    		
    	case Job.STATE_FINISHED:
    		result = "Finished";
    		break;
    		
    	case Job.STATE_IGNORED:
    		result = "Ignored";
    		break;
    		
    	case Job.STATE_ALREADY_PROCESSED:
    		result = "Already processed";
    		break;
    		
    	case Job.STATE_ERROR:
    		result = "Error";
    		break;
    	
    	default:
    		result = "Unknown";
    		break;
		}
    	
		return result;
	}

	/**
     * Notifies all listeners that all cell values in the table's
     * rows may have changed. The number of rows may also have changed
     * and the <code>JTable</code> should redraw the
     * table from scratch. The structure of the table (as in the order of the
     * columns) is assumed to be the same.
     *
     * @see TableModelEvent
     * @see EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Notifies all listeners that the table's structure has changed.
     * The number of columns in the table, and the names and types of
     * the new columns may be different from the previous state.
     * If the <code>JTable</code> receives this event and its
     * <code>autoCreateColumnsFromModel</code>
     * flag is set it discards any table columns that it had and reallocates
     * default columns in the order they appear in the model. This is the
     * same as calling <code>setModel(TableModel)</code> on the
     * <code>JTable</code>.
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
     *
     * @param  firstRow  the first row
     * @param  lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     *
     */
    public void fireTableRowsInserted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been updated.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }
	
    static private class TableModelAction {
    	
    	private Action mAction;
    	private DownloadJob mJob;
    	
		public TableModelAction(Action pAction, DownloadJob pJob) {
			mAction = pAction;
			mJob = pJob;
		}

		public enum Action
		{
			ADD, REMOVE, CHANGE, CLEAR;
		}
		
		public Action getAction() {
			return mAction;
		}

		public DownloadJob getJob() {
			return mJob;
		};
	}
    
	private class JobObserver implements PropertyChangeListener, Runnable {
		
		private Thread mEventCollector;
		private BlockingDeque<TableModelAction> mActionDequeue = 
			new LinkedBlockingDeque<TableModelAction>();
		
		private boolean mStop;
		
		public JobObserver() {
			
			mEventCollector = new Thread(this);
			mEventCollector.setName("GUI Event Collector");
			mEventCollector.setDaemon(true);
		}
		
		public void start() {
			mStop = false;
			mEventCollector.start();
		}
		
		public void stop() {
			mStop = true;
		}

		public void propertyChange(PropertyChangeEvent pEvt) {
			
			DownloadJob job = (DownloadJob) pEvt.getSource();
			
			mActionDequeue.add(new TableModelAction(TableModelAction.Action.CHANGE, job));
		}
		
		public void queueJobAdd(DownloadJob pJob) {
			
			mActionDequeue.add(new TableModelAction(TableModelAction.Action.ADD, pJob));
		}
		
		public void queueJobRemove(DownloadJob pJob) {
			
			mActionDequeue.add(new TableModelAction(TableModelAction.Action.REMOVE, pJob));
		}
		
		public void queueClear() {
			
			mActionDequeue.add(new TableModelAction(TableModelAction.Action.CLEAR, null));
			
		}
		
		
		public void run() {
			
			Runnable eventRunnable = new Runnable(){
				public void run() {
					processEvents();
				}
			};
			
			
			while(!mStop) {
				
				try {
					Thread.sleep(JOB_PROGRESS_UPDATE_FREQUENCY);
				} catch (InterruptedException e) {
				}
				
				//process the events
				try {
					SwingUtilities.invokeAndWait(eventRunnable);
				} catch (InterruptedException e) {
					mLog.warn("Interrupted while processing events", e);
				} catch (InvocationTargetException e) {
					mLog.error("Error processing events", e);
				}
			}
			
		}

		private void processEvents() {
			Set<DownloadJob> alreadyUpdatedJobs = 
				new HashSet<DownloadJob>();
			
			TableModelAction action;
			while ((action = mActionDequeue.pollFirst()) != null) {
				
				DownloadJob job = action.getJob();
				
				switch(action.getAction()) {
				
				case CHANGE:
					
					if(!alreadyUpdatedJobs.contains(job)) {
						alreadyUpdatedJobs.add(job); //only update a job once
						
						int indexChange = mRows.indexOf(job);
						if(indexChange > -1) {
							fireTableRowsUpdated(indexChange, indexChange);
						}
					}
					
					break;
				
				case ADD:
					mRows.add(job);
					int indexAdd = mRows.size() - 1;
					fireTableRowsInserted(indexAdd, indexAdd);
					job.addPropertyChangeListener(mJobObserver);
					
					break;
					
				case REMOVE:
					
					int indexRemove = mRows.indexOf(job);
					if(indexRemove < 0) {
						throw new IllegalStateException("Tried to remove nonexisting entry!");
					}
						
					job.removePropertyChangeListener(mJobObserver);
					mRows.remove((int)indexRemove);
					fireTableRowsDeleted(indexRemove, indexRemove);
					
					break;
					
				case CLEAR:
					
					for (DownloadJob registeredJob : mRows) {
						registeredJob.removePropertyChangeListener(mJobObserver);
					}
					
					int size = mRows.size();
					mRows.clear();
					mActionDequeue.clear(); //remove all following events
					
					fireTableRowsDeleted(1, size);
					
					break;
				}
				
			}
		}

	}

}
