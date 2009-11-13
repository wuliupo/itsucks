/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 09.02.2008
 */

package de.phleisch.app.itsucks.job.download.http.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.phleisch.app.itsucks.context.Context;
import de.phleisch.app.itsucks.io.UrlDataRetriever;
import de.phleisch.app.itsucks.io.http.impl.Cookie;
import de.phleisch.app.itsucks.io.http.impl.HttpRetriever;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.impl.DataRetrieverFactory;

public class HttpRetrieverFactory implements DataRetrieverFactory {

	public static final String HTTP_BEHAVIOUR_CONFIG_PARAMETER = "HttpRetriever_AdditionalBehaviour";
	public static final String HTTP_COOKIE_CONFIG_PARAMETER = "HttpRetriever_CookieConfig";
	public static final String HTTP_REFERER_CONFIG_PARAMETER = "RefererURL";

	protected static final String DEFAULT_USER_AGENT = "Mozilla/5.0";
	
	@SuppressWarnings("unchecked")
	public UrlDataRetriever createDataRetriever(URL pUrl, Context pGroupContext,
			List<JobParameter> pParameterList) {
		
		//get configuration from the context
		HttpRetrieverConfiguration httpRetrieverConfiguration = 
			getHttpRetrieverConfigurationFromContext(pGroupContext);
		
		if(httpRetrieverConfiguration == null) {
			//Configuration not defined, set it to the context to allow sharing the httpClient 
			//instance between different HttpRetrievers
			httpRetrieverConfiguration = createDefaultConfiguration();
			setHttpRetrieverConfigurationToContext(httpRetrieverConfiguration, pGroupContext);
		}
		
		HttpRetriever retriever = 
			new HttpRetriever(pUrl, httpRetrieverConfiguration);
				
		//set referer
		if(httpRetrieverConfiguration.isSendReferer()) {
			JobParameter referer = findParameter(HTTP_REFERER_CONFIG_PARAMETER, pParameterList);
			if(referer != null) {
				retriever.setReferer((URL) referer.getValue());
			}
		}
		
		JobParameter parameterBehaviour = 
			findParameter(HTTP_BEHAVIOUR_CONFIG_PARAMETER, 
					pParameterList);
		if(parameterBehaviour != null) {
			HttpRetrieverResponseCodeBehaviour specialBehaviour = 
				(HttpRetrieverResponseCodeBehaviour) parameterBehaviour.getValue();
			
			HttpRetrieverResponseCodeBehaviour actualBehaviour = 
				retriever.getResponseCodeBehaviour();
			
			actualBehaviour.add(specialBehaviour);
		}
		
		JobParameter parameterCookie = 
			findParameter(HTTP_COOKIE_CONFIG_PARAMETER,
					pParameterList);
		
		if(parameterCookie != null) {
			
			List<Cookie> cookieList = (List<Cookie>) parameterCookie.getValue();
			List<String> convertedCookies = new ArrayList<String>();
			
			for (Cookie cookie : cookieList) {
				convertedCookies.add(cookie.getName() + "=" + cookie.getValue());
			}
			
			retriever.setCookieList(convertedCookies);
		}
		
		return retriever;
	}

	protected static HttpRetrieverConfiguration createDefaultConfiguration() {
		
		HttpRetrieverConfiguration defaultConfiguration = 
			new HttpRetrieverConfiguration();
		
		defaultConfiguration.setUserAgent(DEFAULT_USER_AGENT);
		defaultConfiguration.setSendReferer(true);
		
		return defaultConfiguration;
	}
	
	protected JobParameter findParameter(String pString, List<JobParameter> pParameterList) {
		
		for (JobParameter jobParameter : pParameterList) {
			if(pString.equals(jobParameter.getKey())) {
				return jobParameter;
			}
		}
		
		return null;
	}

	protected HttpRetrieverConfiguration getHttpRetrieverConfigurationFromContext(
			Context jobContext) {
		
		HttpRetrieverConfiguration configuration = 
			(HttpRetrieverConfiguration) jobContext.getContextParameter(
					HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION);
		return configuration;
	}
	
	protected void setHttpRetrieverConfigurationToContext(
			HttpRetrieverConfiguration pConfiguration, Context jobContext) {
		
			jobContext.setContextParameter(
					HttpRetrieverConfiguration.CONTEXT_PARAMETER_HTTP_RETRIEVER_CONFIGURATION,
					pConfiguration);
	}
	
}
