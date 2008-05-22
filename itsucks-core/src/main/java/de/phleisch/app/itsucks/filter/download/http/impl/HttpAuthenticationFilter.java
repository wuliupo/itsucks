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
import java.util.Collections;
import java.util.List;

import de.phleisch.app.itsucks.context.Context;
import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.impl.AbstractJobFilter;
import de.phleisch.app.itsucks.io.http.impl.HttpAuthenticationCredentials;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class HttpAuthenticationFilter 
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	private static final long serialVersionUID = 3100816812067651683L;

	protected List<HttpAuthenticationCredentials> mCredentials; 
	protected boolean mAuthenticationSet;
	
	public HttpAuthenticationFilter() {
		mCredentials = new ArrayList<HttpAuthenticationCredentials>();
		mAuthenticationSet = false;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.job.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		if(!mAuthenticationSet) {
			if(mCredentials.size() > 0) {
				
				Context context = getContext();
				
				HttpRetrieverConfiguration retrieverConfiguration = (HttpRetrieverConfiguration) context.getContextParameter(
						HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION);
				
				//if null, create retriever configuration
				if(retrieverConfiguration == null) {
					retrieverConfiguration = new HttpRetrieverConfiguration();
					context.setContextParameter(
						HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION, retrieverConfiguration);
				}
				
				//directly modification of retriever configuration is not nice, but the
				//httpclient interface only allows global authentication settings, not per method.
				retrieverConfiguration.setAuthenticationCredentials(getCredentials());
			}
			mAuthenticationSet = true;
		}
		
		return pJob;
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

	public boolean addCredentials(final HttpAuthenticationCredentials pCredentials) {
		return mCredentials.add(pCredentials);
	}
	
	public boolean removeCredentials(final HttpAuthenticationCredentials pCredentials) {
		return mCredentials.remove(pCredentials);
	}
	
	public List<HttpAuthenticationCredentials> getCredentials() {
		return Collections.unmodifiableList(mCredentials);
	}
	
}
