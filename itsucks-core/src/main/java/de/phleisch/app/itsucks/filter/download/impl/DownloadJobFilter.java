/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id: DownloadJobFilter.java,v 1.1 2006-12-03 19:31:43 olli Exp $
 */

package de.phleisch.app.itsucks.filter.download.impl;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.impl.AbstractJobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

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
public class DownloadJobFilter 
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	private static final long serialVersionUID = 4776756022068287844L;

	private Set<URI> mAlreadyAddedUrls;
	
	private URL mURLPrefix = null;
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
		
		UrlDownloadJob downloadJob = (UrlDownloadJob) pJob;
		int state = downloadJob.getState();
		URL url = downloadJob.getUrl();
		
		if(!mAlreadyAddedUrls.add(url.toURI())) {
			state = Job.STATE_ALREADY_PROCESSED;
		}
		
		if(mURLPrefix != null) {
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
				
				Pattern pattern = Pattern.compile(serverName, 
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(url.getHost());
				
				if(matcher.matches()) {
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
				
				Pattern pattern = Pattern.compile(saveFilter, 
						Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(url.getPath());
				
				if(matcher.matches()) {
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
		return pJob instanceof UrlDownloadJob;
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
	 * URL's which do not begin with this prefix will be rejected. 
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
		mAllowedHostNames = Arrays.copyOf(pAllowedHostNames, pAllowedHostNames.length);
	}
	
	/**
	 * Returns the allowed hostnames (regular expressions).
	 * 
	 * @return
	 */
	public String[] getAllowedHostNames() {
		return Arrays.copyOf(mAllowedHostNames, mAllowedHostNames.length);
	}

	/**
	 * Gets the file filters which should be saved to disk (regular expressions).
	 * 
	 * @return
	 */
	public String[] getSaveToDisk() {
		return Arrays.copyOf(mSaveToDisk, mSaveToDisk.length);
	}

	/**
	 * Sets the file filters which should be saved to disk (regular expressions).
	 * 
	 * @return
	 */	
	public void setSaveToDisk(String[] pSaveToDisk) {
		mSaveToDisk = Arrays.copyOf(pSaveToDisk, pSaveToDisk.length);
	}

}
