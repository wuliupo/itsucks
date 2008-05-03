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
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.http.impl.HttpRetrieverFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class CookieFilter 
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	private static final long serialVersionUID = 399630859261954159L;

	protected List<Cookie> mCookies; 
	
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
		
		List<Cookie> cookieList = null; 
		UrlDownloadJob downloadJob = (UrlDownloadJob) pJob;
		URL url = downloadJob.getUrl();
		
		for (Cookie cookie : mCookies) {
			if(checkCookie(url, cookie)) {
				if(cookieList == null) {
					cookieList = new ArrayList<Cookie>(5);
				}
				cookieList.add(cookie);
			}
		}
		
		if(cookieList != null) {
			downloadJob.setParameter(
					new JobParameter(HttpRetrieverFactory.HTTP_COOKIE_CONFIG_PARAMETER, cookieList));
		}
		
		return pJob;
	}

	private boolean checkCookie(URL pUrl, Cookie pCookie) {
		
		String domain = pCookie.getDomain();
		boolean match;
		
		if(domain.startsWith(".")) {
			match = pUrl.getHost().toLowerCase().endsWith(domain.toLowerCase().substring(1));
		} else {
			match = pUrl.getHost().equalsIgnoreCase(domain);
		}
		
		if(match) {
			match = pUrl.getPath().toLowerCase().startsWith(pCookie.getPath().toLowerCase());
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
				result = "http".equalsIgnoreCase(url.getProtocol())
					|| "https".equalsIgnoreCase(url.getProtocol());
			}
		}
		
		return result;
	}

	public boolean addCookie(final Cookie pCookie) {
		return mCookies.add(pCookie);
	}
	
	public boolean removeCookie(final Cookie pCookie) {
		return mCookies.remove(pCookie);
	}

}
