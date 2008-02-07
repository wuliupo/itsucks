/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 20.06.2007
 */

package de.phleisch.app.itsucks.filter.download.impl;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.phleisch.app.itsucks.context.EventContext;
import de.phleisch.app.itsucks.event.Event;
import de.phleisch.app.itsucks.event.EventObserver;
import de.phleisch.app.itsucks.event.impl.CoreEvents;
import de.phleisch.app.itsucks.event.impl.DefaultEventFilter;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.impl.AbstractJobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class TimeLimitFilter
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	private static final long serialVersionUID = -8017814372426119881L;
	
	private String mTimeLimitAsText;
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
		UrlDownloadJob downloadJob = (UrlDownloadJob) pJob;
		
		if(downloadJob.getState() == Job.STATE_OPEN) {
			
			downloadJob.setState(Job.STATE_IGNORED);
		}
		
		return pJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#supports(de.phleisch.app.itsucks.Job)
	 */
	public boolean supports(Job pJob) {
		return pJob instanceof UrlDownloadJob;
	}

	@Override
	public void setContext(EventContext pContext) {
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

	public String getTimeLimitAsText() {
		return mTimeLimitAsText;
	}

	public void setTimeLimitAsText(String pTimeLimitAsText) {
		setTimeLimit(parseTextValue(pTimeLimitAsText));
		mTimeLimitAsText = pTimeLimitAsText;
	}

	private long parseTextValue(String pTimeLimit) {
		String regExp = "^([-]?[0-9]{1,})[ ]*(s|m|h|d|S|M|H|D|$)$";
		
		Pattern pattern = null;
		try {
			pattern = Pattern.compile(regExp);
		} catch (PatternSyntaxException ex) {
			throw new RuntimeException("Bad regular expression given.", ex);
		}
		
		Matcher matcher = pattern.matcher(pTimeLimit);
		if(!matcher.matches()) {
			throw new IllegalArgumentException("Bad value given: " + pTimeLimit);
		}
		
		long value = Long.parseLong(matcher.group(1));
		String unit = matcher.group(2);
		if(unit != null && !"".equals(unit)) {
			
			if(unit.equalsIgnoreCase("s")) {
				value *= 1000;
			} else if(unit.equalsIgnoreCase("m")) {
				value *= 1000 * 60;
			} else if(unit.equalsIgnoreCase("h")) {
				value *= 1000 * 60 * 60;
			} else if(unit.equalsIgnoreCase("d")) {
				value *= 1000 * 60 * 60 * 24;
			}
		} else {
			value *= 1000; //default is seconds
		}
		
		return value;
	}
	
}
