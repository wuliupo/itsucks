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
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.io.http.HttpMetadata;

/**
 * This filter is able to filter DownloadJobs by the content length of the file. 
 * 
 * @author olli
 *
 */
public class FileSizeFilter implements JobFilter, Serializable {

	private static final long serialVersionUID = 7530902338022529741L;
	
	private long mMinSize = -1;
	private long mMaxSize = -1;
	private boolean mAcceptWhenLenghtNotSet = true;
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		
		DownloadJob downloadJob = (DownloadJob) pJob;
		if(downloadJob.isSaveToDisk()) {
			
			Metadata metadata = downloadJob.getDataRetriever().getMetadata();
			long contentLength = 0;
			
			if(metadata instanceof HttpMetadata) {
				HttpMetadata httpMetadata = (HttpMetadata) metadata;
				contentLength = httpMetadata.getContentLength();
			}
			
			if(contentLength > 0) {
				downloadJob.setSaveToDisk(checkContentLength(contentLength));
			}
			
		}
		
		return pJob;
	}

	private boolean checkContentLength(long pContentLength) {
		
		if(pContentLength <= 0) {
			return mAcceptWhenLenghtNotSet;
		}
		
		boolean accept = true;
		if(accept && mMinSize > -1) {
			accept = pContentLength >= mMinSize;
		}
		
		if(accept && mMaxSize > -1) {
			accept = pContentLength <= mMaxSize;
		}
		
		return accept;
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
		return mMinSize;
	}

	/**
	 * Sets the lower limit for the file size. 
	 * Set to -1 to disable limit.
	 * 
	 * @param pMinSize
	 */
	public void setMinSize(long pMinSize) {
		mMinSize = pMinSize;
	}

	/**
	 * Returns the upper limit for the file size.
	 * 
	 * @return
	 */
	public long getMaxSize() {
		return mMaxSize;
	}

	/**
	 * Sets the upper limit for the file size. 
	 * Set to -1 to disable limit.
	 * 
	 * @param pMaxSize
	 */
	public void setMaxSize(long pMaxSize) {
		mMaxSize = pMaxSize;
	}

	/**
	 * Returns if an file should be accepted when it's length is not known.
	 * 
	 * @return
	 */
	public boolean isAcceptWhenLenghtNotSet() {
		return mAcceptWhenLenghtNotSet;
	}

	/**
	 * Sets if an file should be accepted when it's length is not known.
	 * 
	 * @param pAcceptWhenLenghtNotSet
	 */
	public void setAcceptWhenLenghtNotSet(boolean pAcceptWhenLenghtNotSet) {
		mAcceptWhenLenghtNotSet = pAcceptWhenLenghtNotSet;
	}

}
