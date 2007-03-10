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

public class DownloadJobFilter implements JobFilter, Serializable {

	private static final long serialVersionUID = 4776756022068287844L;

	private Set<URI> mAlreadyAddedUrls;
	
	private URL mBaseURL = null;
	private boolean mAllowOnlyRelativeReferences = false;
	private int mMaxRecursionDepth = -1; // -1 = unlimited
	private String mAllowedHostNames[];
	private String mSaveToFileFilter[];
	
	public DownloadJobFilter() {
		super();
		mAlreadyAddedUrls = new HashSet<URI>();
	}

	public Job filter(Job pJob) throws Exception {
		
		DownloadJob downloadJob = (DownloadJob) pJob;
		int state = downloadJob.getState();
		
		if(!mAlreadyAddedUrls.add(downloadJob.getUrl().toURI())) {
			state = Job.STATE_ALREADY_PROCESSED;
		}
		
		if(mAllowOnlyRelativeReferences) {
			if(!downloadJob.getUrl().toString().startsWith(mBaseURL.toString())) {
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
				if(downloadJob.getUrl().getHost().matches(serverName)) {
					allowed = true;
					break;
				}
			}
			if(!allowed) {
				state = Job.STATE_IGNORED;
			}
		}
		
		if(mSaveToFileFilter != null) {
			boolean save = false;
			for (String saveFilter : mSaveToFileFilter) {
				if(downloadJob.getUrl().getPath().matches(saveFilter)) {
					save = true;
					break;
				}
			}
			
			downloadJob.setSaveToFile(save);
		}
		
		downloadJob.setState(state);
		
		return pJob;
	}

	public boolean supports(Job pJob) {
		return pJob instanceof DownloadJob;
	}

	public boolean isAllowOnlyRelativeReferences() {
		return mAllowOnlyRelativeReferences;
	}

	public void setAllowOnlyRelativeReferences(boolean pAllowOnlyRelativeReferences) {
		mAllowOnlyRelativeReferences = pAllowOnlyRelativeReferences;
	}

	public Set<URI> getAlreadyAddedUrls() {
		return mAlreadyAddedUrls;
	}

	public void setAlreadyAddedUrls(Set<URI> pAlreadyAddedUrls) {
		mAlreadyAddedUrls = pAlreadyAddedUrls;
	}

	public int getMaxRecursionDepth() {
		return mMaxRecursionDepth;
	}

	public void setMaxRecursionDepth(int pMaxRecursionDepth) {
		mMaxRecursionDepth = pMaxRecursionDepth;
	}

	public URL getBaseURL() {
		return mBaseURL;
	}

	public void setBaseURL(URL pBaseURL) {
		mBaseURL = pBaseURL;
	}

	public void setAllowedHostNames(String pAllowedHostNames[]) {
		mAllowedHostNames = pAllowedHostNames;
	}
	
	public String[] getAllowedHostNames() {
		return mAllowedHostNames;
	}

	public String[] getSaveToFileFilter() {
		return mSaveToFileFilter;
	}

	public void setSaveToFileFilter(String[] pSaveToFileFilter) {
		mSaveToFileFilter = pSaveToFileFilter;
	}

}
