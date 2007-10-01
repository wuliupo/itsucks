/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: DownloadJobFilter.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 */

package de.phleisch.app.itsucks.filter;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DownloadJob;

/**
 * This filter is able to filter DownloadJobs by the url of the job and many more 
 * parameter.
 * 
 * Possible filter criteria are:
 * 
 * - Follow only relative links
 * - Skip already downloaded url's
 * - Recursion Depth
 * - Allowed Hostnames (regular expression)
 * - Allowed base URL
 * - Filetypes to be saved on disk (regular expression)
 * 
 * 
 * @author olli
 *
 */
public class DownloadJobFilter implements JobFilter, Serializable {

	private static final long serialVersionUID = 4776756022068287844L;

	private Set<URI> mAlreadyAddedUrls;
	
	private URL mURLPrefix = null;
	private boolean mAllowOnlyRelativeReferences = false;
	private int mMaxRecursionDepth = -1; // -1 = unlimited
	private String mAllowedHostNames[];
	private String mSaveToDisk[];
	
	public DownloadJobFilter() {
		super();
		mAlreadyAddedUrls = new HashSet<URI>();
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		
		DownloadJob downloadJob = (DownloadJob) pJob;
		int state = downloadJob.getState();
		URL url = downloadJob.getUrl();
		
		if(!mAlreadyAddedUrls.add(url.toURI())) {
			state = Job.STATE_ALREADY_PROCESSED;
		}
		
		if(mAllowOnlyRelativeReferences) {
			if(!downloadJob.getUrl().toString().startsWith(mURLPrefix.toString())) {
				state = Job.STATE_IGNORED;
			}
		}
		
		if(mMaxRecursionDepth > -1) {
			if(downloadJob.getDepth() > mMaxRecursionDepth) {
				state = Job.STATE_IGNORED;
			}
		}
		
		if(mAllowedHostNames != null) {
			boolean allowed = false;
			for (String serverName : mAllowedHostNames) {
				if(url.getHost().matches(serverName)) {
					allowed = true;
					break;
				}
			}
			if(!allowed) {
				state = Job.STATE_IGNORED;
			}
		}
		
		if(mSaveToDisk != null) {
			boolean save = false;
			for (String saveFilter : mSaveToDisk) {
				if(url.getPath().matches(saveFilter)) {
					save = true;
					break;
				}
			}
			
			downloadJob.setSaveToDisk(save);
		}
		
		downloadJob.setState(state);
		
		return pJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#supports(de.phleisch.app.itsucks.Job)
	 */
	public boolean supports(Job pJob) {
		return pJob instanceof DownloadJob;
	}

	/**
	 * Returns if only relative links should be followed.
	 * 
	 * @return
	 */
	public boolean isAllowOnlyRelativeReferences() {
		return mAllowOnlyRelativeReferences;
	}

	/**
	 * Sets if only relative links should be followed.
	 * 
	 * @param pAllowOnlyRelativeReferences
	 */
	public void setAllowOnlyRelativeReferences(boolean pAllowOnlyRelativeReferences) {
		mAllowOnlyRelativeReferences = pAllowOnlyRelativeReferences;
	}

	/**
	 * Returns a set of all already known urls.
	 * 
	 * @return
	 */
	public Set<URI> getAlreadyAddedUrls() {
		return mAlreadyAddedUrls;
	}

	/**
	 * Sets a set of all already known urls.
	 * 
	 * @param pAlreadyAddedUrls
	 */
	public void setAlreadyAddedUrls(Set<URI> pAlreadyAddedUrls) {
		mAlreadyAddedUrls = pAlreadyAddedUrls;
	}

	/**
	 * Gets the maximum allowed recursion depth.
	 * A value of -1 means infinitive depth.
	 * 
	 * @return
	 */
	public int getMaxRecursionDepth() {
		return mMaxRecursionDepth;
	}

	/**
	 * Sets the maximum allowed recursion depth.
	 * A value of -1 means infinitive depth.
	 * @param pMaxRecursionDepth
	 */
	public void setMaxRecursionDepth(int pMaxRecursionDepth) {
		mMaxRecursionDepth = pMaxRecursionDepth;
	}

	/**
	 * Gets the prefix URL filter.
	 * 
	 * @return
	 */
	public URL getURLPrefix() {
		return mURLPrefix;
	}

	/**
	 * Sets the prefix URL filter.
	 * 
	 * @param pBaseURL
	 */
	public void setURLPrefix(URL pURLPrefix) {
		mURLPrefix = pURLPrefix;
	}

	/**
	 * Sets the allowed host names (regular expressions).
	 * 
	 * @param pAllowedHostNames
	 */
	public void setAllowedHostNames(String pAllowedHostNames[]) {
		mAllowedHostNames = pAllowedHostNames;
	}
	
	/**
	 * Returns the allowed hostnames (regular expressions).
	 * 
	 * @return
	 */
	public String[] getAllowedHostNames() {
		return mAllowedHostNames;
	}

	/**
	 * Gets the file filters which should be saved to disk (regular expressions).
	 * 
	 * @return
	 */
	public String[] getSaveToDisk() {
		return mSaveToDisk;
	}

	/**
	 * Sets the file filters which should be saved to disk (regular expressions).
	 * 
	 * @return
	 */	
	public void setSaveToDisk(String[] pSaveToDisk) {
		mSaveToDisk = pSaveToDisk;
	}

}
