/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 14.10.2007
 */

package de.phleisch.app.itsucks.filter;

import java.io.Serializable;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.JobParameter;
import de.phleisch.app.itsucks.io.DownloadJob;

/**
 * This filter is able to filter DownloadJobs by the content length of the file. 
 * 
 * @author olli
 *
 */
public class FileSizeFilter implements JobFilter, Serializable {

	public static final String FILE_SIZE_CONFIG_PARAMETER = "FileSizeConfig";

	private static final long serialVersionUID = 7530902338022529741L;
	
	private FileSizeConfig mConfig = new FileSizeConfig();
	
	public class FileSizeConfig {
		
		private long mMinSize = -1;
		private long mMaxSize = -1;
		private boolean mAcceptWhenLengthNotSet = true;
		
		public long getMinSize() {
			return mMinSize;
		}
		public void setMinSize(long pMinSize) {
			mMinSize = pMinSize;
		}
		public long getMaxSize() {
			return mMaxSize;
		}
		public void setMaxSize(long pMaxSize) {
			mMaxSize = pMaxSize;
		}
		public boolean isAcceptWhenLengthNotSet() {
			return mAcceptWhenLengthNotSet;
		}
		public void setAcceptWhenLengthNotSet(boolean pAcceptWhenLengthNotSet) {
			mAcceptWhenLengthNotSet = pAcceptWhenLengthNotSet;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		
		pJob.addParameter(new JobParameter(FILE_SIZE_CONFIG_PARAMETER, mConfig));
		
		return pJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#supports(de.phleisch.app.itsucks.Job)
	 */
	public boolean supports(Job pJob) {
		return pJob instanceof DownloadJob;
	}

	/**
	 * Returns the lower limit for the file size. 
	 * 
	 * @return
	 */
	public long getMinSize() {
		return mConfig.getMinSize();
	}

	/**
	 * Sets the lower limit for the file size. 
	 * Set to -1 to disable limit.
	 * 
	 * @param pMinSize
	 */
	public void setMinSize(long pMinSize) {
		mConfig.setMinSize(pMinSize);
	}

	/**
	 * Returns the upper limit for the file size.
	 * 
	 * @return
	 */
	public long getMaxSize() {
		return mConfig.getMaxSize();
	}

	/**
	 * Sets the upper limit for the file size. 
	 * Set to -1 to disable limit.
	 * 
	 * @param pMaxSize
	 */
	public void setMaxSize(long pMaxSize) {
		mConfig.setMaxSize(pMaxSize);
	}

	/**
	 * Returns if an file should be accepted when it's length is not known.
	 * 
	 * @return
	 */
	public boolean isAcceptWhenLengthNotSet() {
		return mConfig.isAcceptWhenLengthNotSet();
	}

	/**
	 * Sets if an file should be accepted when it's length is not known.
	 * 
	 * @param pAcceptWhenLengthNotSet
	 */
	public void setAcceptWhenLengthNotSet(boolean pAcceptWhenLengthNotSet) {
		mConfig.setAcceptWhenLengthNotSet(pAcceptWhenLengthNotSet);
	}

}
