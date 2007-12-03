/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 20.06.2007
 */

package de.phleisch.app.itsucks.filter.download.impl;

import java.io.Serializable;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.impl.AbstractJobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class MaxLinksToFollowFilter
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	private static final long serialVersionUID = -8017814372426119881L;
	
	private int mMaxLinksToFollow = -1; // -1 = unlimited
	private int mLinksFollowed = 0;
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		if(mMaxLinksToFollow == -1) return pJob;
		
		UrlDownloadJob downloadJob = (UrlDownloadJob) pJob;
		
		if(downloadJob.getState() == Job.STATE_OPEN) {
			
			if(mLinksFollowed <= mMaxLinksToFollow) {
				mLinksFollowed++;
			} else {
				downloadJob.setState(Job.STATE_IGNORED);
			}
		}
		
		return pJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#supports(de.phleisch.app.itsucks.Job)
	 */
	public boolean supports(Job pJob) {
		return pJob instanceof UrlDownloadJob;
	}

	public int getMaxLinksToFollow() {
		return mMaxLinksToFollow;
	}

	public void setMaxLinksToFollow(int pMaxDownloadFiles) {
		mMaxLinksToFollow = pMaxDownloadFiles;
	}
	
}
