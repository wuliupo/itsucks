/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 20.06.2007
 */

package de.phleisch.app.itsucks.filter;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DownloadJob;

public class MaxFileDownloadFilter implements JobFilter {

	private int mMaxDownloadFiles = -1; // -1 = unlimited
	private int mDownloadedFiles = 0;
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		if(mMaxDownloadFiles == -1) return pJob;
		
		DownloadJob downloadJob = (DownloadJob) pJob;
		
		if(downloadJob.getState() == Job.STATE_OPEN && downloadJob.isSaveToFile()) {
			
			if(mDownloadedFiles <= mMaxDownloadFiles) {
				mDownloadedFiles++;
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
		return pJob instanceof DownloadJob;
	}

	public int getMaxDownloadFiles() {
		return mMaxDownloadFiles;
	}

	public void setMaxDownloadFiles(int pMaxDownloadFiles) {
		mMaxDownloadFiles = pMaxDownloadFiles;
	}
	
}
