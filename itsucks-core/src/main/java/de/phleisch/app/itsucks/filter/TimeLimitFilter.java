/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 20.06.2007
 */

package de.phleisch.app.itsucks.filter;

import java.io.Serializable;

import de.phleisch.app.itsucks.Context;
import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.event.CoreEvents;
import de.phleisch.app.itsucks.event.DefaultEventFilter;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.io.DownloadJob;

public class TimeLimitFilter
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	private static final long serialVersionUID = -8017814372426119881L;
	
	private long mTimeLimit = 0;
	
	private long mStartTime = 0;
	private long mPauseTime = 0;
	private long mEndTime = 0;
	
	private long mStartPauseAt;
	private boolean mIsInPause = false;
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		
		if(!mIsInPause && System.currentTimeMillis() < mEndTime) { 
			return pJob;
		} else if(mIsInPause && mStartPauseAt < mEndTime) {
			return pJob;
		}
		
		//ignore job, time limit reached
		DownloadJob downloadJob = (DownloadJob) pJob;
		
		if(downloadJob.getState() == Job.STATE_OPEN) {
			
			downloadJob.setState(Job.STATE_IGNORED);
		}
		
		return pJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#supports(de.phleisch.app.itsucks.Job)
	 */
	public boolean supports(Job pJob) {
		return pJob instanceof DownloadJob;
	}

	@Override
	public void setContext(Context pContext) {
		super.setContext(pContext);
		
		//register observer
		
		DefaultEventFilter eventFilter = new DefaultEventFilter();
		eventFilter.addAllowedCategory(CoreEvents.EVENT_CATEGORY_DISPATCHER);
		eventFilter.addAllowedType(CoreEvents.EVENT_DISPATCHER_START.getType());
		eventFilter.addAllowedType(CoreEvents.EVENT_DISPATCHER_PAUSE.getType());
		eventFilter.addAllowedType(CoreEvents.EVENT_DISPATCHER_UNPAUSE.getType());
		
		pContext.getEventDispatcher().registerObserver(
			new EventObserver() {

				public void processEvent(Event pEvent) {
					
					if(pEvent.getType() == CoreEvents.EVENT_DISPATCHER_START.getType()) {
						mStartTime = System.currentTimeMillis();
						mPauseTime = 0;
						updateEndTime();
						
					} else if(pEvent.getType() == CoreEvents.EVENT_DISPATCHER_PAUSE.getType()) {
						mStartPauseAt = System.currentTimeMillis();
						mIsInPause = true;
						
					} else if(pEvent.getType() == CoreEvents.EVENT_DISPATCHER_UNPAUSE.getType()) {
						if(!mIsInPause) {
							throw new IllegalStateException("Got unpause event but no pause event!");
						}
						
						mPauseTime += System.currentTimeMillis() - mStartPauseAt;
						updateEndTime();
						mIsInPause = false;
					}
					
				}

				private void updateEndTime() {
					mEndTime = mStartTime + mPauseTime + mTimeLimit;
				}
				
			}, eventFilter);
	}

	public long getTimeLimit() {
		return mTimeLimit;
	}

	public void setTimeLimit(long pTimeLimit) {
		mTimeLimit = pTimeLimit;
	}

	
}
