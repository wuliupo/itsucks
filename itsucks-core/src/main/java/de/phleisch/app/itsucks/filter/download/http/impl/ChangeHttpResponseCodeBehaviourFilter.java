/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 14.10.2007
 */

package de.phleisch.app.itsucks.filter.download.http.impl;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.filter.impl.AbstractJobFilter;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

/**
 * TODO javadoc
 * 
 * @author olli
 *
 */
public class ChangeHttpResponseCodeBehaviourFilter 
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	private static final long serialVersionUID = 7630921381984994229L;
	
	public static final String HTTP_BEHAVIOUR_CONFIG_PARAMETER = "HttpRetriever_AdditionalBehaviour";

	public static class HttpResponseCodeBehaviourHostConfig implements Serializable {
		
		private static final long serialVersionUID = 2888428738451176299L;
		
		private String mHostnameRegexp;
		private HttpRetrieverResponseCodeBehaviour mResponseCodeBehaviour;
		
		public HttpResponseCodeBehaviourHostConfig(String pHostnameRegexp,
				HttpRetrieverResponseCodeBehaviour pResponseCodeBehaviour) {
			mHostnameRegexp = pHostnameRegexp;
			mResponseCodeBehaviour = pResponseCodeBehaviour;
		}

		public String getHostnameRegexp() {
			return mHostnameRegexp;
		}
		public void setHostnameRegexp(String pHostnameRegexp) {
			mHostnameRegexp = pHostnameRegexp;
		}
		public HttpRetrieverResponseCodeBehaviour getResponseCodeBehaviour() {
			return mResponseCodeBehaviour;
		}
		public void setResponseCodeBehaviour(
				HttpRetrieverResponseCodeBehaviour pResponseCodeBehaviour) {
			mResponseCodeBehaviour = pResponseCodeBehaviour;
		}
		
	}
	
	protected List<HttpResponseCodeBehaviourHostConfig> mConfigList = 
		new ArrayList<HttpResponseCodeBehaviourHostConfig>();
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		
		UrlDownloadJob job = (UrlDownloadJob) pJob;
		
		for (HttpResponseCodeBehaviourHostConfig config : mConfigList) {
			
			Pattern pattern = compilePattern(config.getHostnameRegexp());
			Matcher matcher = pattern.matcher(job.getUrl().getHost());
			
			if(matcher.matches()) {
				pJob.addParameter(
						new JobParameter(HTTP_BEHAVIOUR_CONFIG_PARAMETER, 
								config.getResponseCodeBehaviour()));
			}
			
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
				result = "http".equalsIgnoreCase(url.getProtocol());
			}
		}
		
		return result;
	}
	
	public boolean addConfig(String pHostnameRegExp, HttpRetrieverResponseCodeBehaviour pConfig) {
		return mConfigList.add(new HttpResponseCodeBehaviourHostConfig(pHostnameRegExp, pConfig));
	}
	
	public boolean addConfig(HttpResponseCodeBehaviourHostConfig pConfig) {
		return mConfigList.add(pConfig);
	}
	
	private static Pattern compilePattern(final String pPattern) {
		return Pattern.compile(pPattern, 
				Pattern.CASE_INSENSITIVE);
	}

}
