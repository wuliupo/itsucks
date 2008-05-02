/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 02.05.2008
 */

package de.phleisch.app.itsucks.filter.download.http.impl;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.impl.AbstractJobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class CookieFilter 
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	private static final long serialVersionUID = 399630859261954159L;

	protected List<Cookie> mCookies; 
	
	public static class Cookie {

		private String mName = "";
		private String mValue = "";
		private String mDomain = "";
		private String mPath = "";
		public String getName() {
			return mName;
		}
		public void setName(String pName) {
			mName = pName;
		}
		public String getValue() {
			return mValue;
		}
		public void setValue(String pValue) {
			mValue = pValue;
		}
		public String getDomain() {
			return mDomain;
		}
		public void setDomain(String pDomain) {
			mDomain = pDomain;
		}
		public String getPath() {
			return mPath;
		}
		public void setPath(String pPath) {
			mPath = pPath;
		}
	}
	
	public CookieFilter() {
		mCookies = new ArrayList<Cookie>();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.job.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		
		if(mCookies.size() == 0) {
			return pJob;
		}
		
		List<Cookie> cookieList = new ArrayList<Cookie>(); 
		UrlDownloadJob downloadJob = (UrlDownloadJob) pJob;
		URL url = downloadJob.getUrl();
		
		for (Cookie cookie : mCookies) {
			if(checkCookie(url, cookie)) {
				cookieList.add(cookie);
			}
		}
		
		return pJob;
	}

	private boolean checkCookie(URL pUrl, Cookie pCookie) {
		
		String domain = pCookie.getDomain();
		boolean match = true;
		
		if(domain.startsWith(".")) {
			match |= pUrl.getHost().toLowerCase().endsWith(domain.toLowerCase());
		} else {
			match |= pUrl.getHost().equalsIgnoreCase(domain);
		}
		
		if(match) {
			match |= pUrl.getPath().toLowerCase().startsWith(pCookie.getPath().toLowerCase());
		}
		
		return match;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#supports(de.phleisch.app.itsucks.Job)
	 */
	public boolean supports(Job pJob) {
		boolean result = false;
		
		if(pJob instanceof UrlDownloadJob) {
			UrlDownloadJob job = (UrlDownloadJob) pJob;
			URL url = job.getUrl();
			if(url != null) {
				result = "http".equalsIgnoreCase(url.getProtocol());
			}
		}
		
		return result;
	}


}
