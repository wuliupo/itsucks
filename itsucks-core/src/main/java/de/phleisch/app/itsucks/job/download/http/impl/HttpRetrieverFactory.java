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
import de.phleisch.app.itsucks.io.http.impl.HttpRetriever;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverConfiguration;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.impl.DataRetrieverFactory;

public class HttpRetrieverFactory implements DataRetrieverFactory {

	public static final String HTTP_BEHAVIOUR_CONFIG_PARAMETER = "HttpRetriever_AdditionalBehaviour";
	
	public UrlDataRetriever createDataRetriever(URL pUrl, Context pGroupContext,
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
			findParameter(HTTP_BEHAVIOUR_CONFIG_PARAMETER, 
					pParameterList);
		if(parameter != null) {
			HttpRetrieverResponseCodeBehaviour specialBehaviour = 
				(HttpRetrieverResponseCodeBehaviour) parameter.getValue();
			
			HttpRetrieverResponseCodeBehaviour actualBehaviour = 
				retriever.getResponseCodeBehaviour();
			
			actualBehaviour.add(specialBehaviour);
		}
		
		//test cookie
		ArrayList<String> cookies = new ArrayList<String>();
		cookies.add("sso_session_ser=pGCbC0kSqqOZPoAmqrXVGEsGhQ2Sizl56uAUcF8%2FGdlxxPcKZU3Nh7uOM2xHiPVI7sq%2F2vTL7ZG9XYTfj6dktR0XoeIjwJZ3lLQRWHejDBdoGgOoCN41m8qhMMAaMPw0SX2Z78V1DQrXkayedgi6SbSkwgIp4VoRe2fSCfUuIOxvfRh3CEdjXKVrYPUvj3bI-e359c3c6233f9bc7f25fab5a9bc42850");
		cookies.add("username=FWAL2kKciOfKgErt17p55Q%3D%3D");
		cookies.add("session_ser=pGCbC0kSqqOZPoAmqrXVGEsGhQ2Sizl56uAUcF8%2FGdlxxPcKZU3Nh7uOM2xHiPVI7sq%2F2vTL7ZG9XYTfj6dktR0XoeIjwJZ3lLQRWHejDBdoGgOoCN41m8qhMMAaMPw0SX2Z78V1DQrXkayedgi6SbSkwgIp4VoRe2fSCfUuIOxvfRh3CEdjXKVrYPUvj3bI-e359c3c6233f9bc7f25fab5a9bc42850");
//		cookies.add("__utma=191645736.1890521747.1164894501.1204901905.1206556782.19; __qca=1183738333-3134264-40151882; __utmz=191645736.1204897018.17.1.utmccn=(direct)|utmcsr=(direct)|utmcmd=(none); __utmb=191645736.2; __utmc=191645736.2; __qcb=472408243; session_ser=pGCbC0kSqqOZPoAmqrXVGEsGhQ2Sizl56uAUcF8%2FGdlxxPcKZU3Nh7uOM2xHiPVI7sq%2F2vTL7ZG9XYTfj6dktR0XoeIjwJZ3lLQRWHejDBdoGgOoCN41m8qhMMAaMPw0SX2Z78V1DQrXkayedgi6SbSkwgIp4VoRe2fSCfUuIOxvfRh3CEdjXKVrYPUvj3bI-e359c3c6233f9bc7f25fab5a9bc42850; username=FWAL2kKciOfKgErt17p55Q%3D%3D; sso_session_ser=pGCbC0kSqqOZPoAmqrXVGEsGhQ2Sizl56uAUcF8%2FGdlxxPcKZU3Nh7uOM2xHiPVI7sq%2F2vTL7ZG9XYTfj6dktR0XoeIjwJZ3lLQRWHejDBdoGgOoCN41m8qhMMAaMPw0SX2Z78V1DQrXkayedgi6SbSkwgIp4VoRe2fSCfUuIOxvfRh3CEdjXKVrYPUvj3bI-e359c3c6233f9bc7f25fab5a9bc42850");
		
		retriever.setCookieList(cookies);
		
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
