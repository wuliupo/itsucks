/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 09.02.2008
 */

package de.phleisch.app.itsucks.job.download.http.impl;

import java.net.URL;
import java.util.List;

import de.phleisch.app.itsucks.context.Context;
import de.phleisch.app.itsucks.filter.download.http.impl.ChangeHttpResponseCodeBehaviourFilter;
import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.http.impl.HttpRetriever;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.impl.DataRetrieverFactory;

public class HttpRetrieverFactory implements DataRetrieverFactory {

	public DataRetriever createDataRetriever(URL pUrl, Context pGroupContext,
			List<JobParameter> pParameterList) {

		HttpRetriever retriever = new HttpRetriever();
		
		//get configuration from the context
		HttpRetrieverConfiguration httpRetrieverConfiguration = 
			getHttpRetrieverConfigurationFromContext(pGroupContext);
		
		if(httpRetrieverConfiguration == null) {
			//Configuration not defined, set it to the context to allow sharing the httpClient 
			//instance between different HttpRetrievers
			httpRetrieverConfiguration = retriever.getConfiguration();
			setHttpRetrieverConfigurationToContext(httpRetrieverConfiguration, pGroupContext);
		} else {
			retriever.setConfiguration(httpRetrieverConfiguration);
		}
		
		JobParameter parameter = 
			findParameter(ChangeHttpResponseCodeBehaviourFilter.HTTP_BEHAVIOUR_CONFIG_PARAMETER, 
					pParameterList);
		if(parameter != null) {
			HttpRetrieverResponseCodeBehaviour specialBehaviour = 
				(HttpRetrieverResponseCodeBehaviour) parameter.getValue();
			
			HttpRetrieverResponseCodeBehaviour actualBehaviour = 
				retriever.getResponseCodeBehaviour();
			
			actualBehaviour.add(specialBehaviour);
		}
		
		retriever.setUrl(pUrl);
		
		return retriever;
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
