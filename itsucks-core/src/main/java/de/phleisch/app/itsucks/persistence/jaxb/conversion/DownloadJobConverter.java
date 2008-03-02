/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.07.2007
 */

package de.phleisch.app.itsucks.persistence.jaxb.conversion;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.phleisch.app.itsucks.filter.download.http.impl.ChangeHttpResponseCodeBehaviourFilter;
import de.phleisch.app.itsucks.filter.download.http.impl.ChangeHttpResponseCodeBehaviourFilter.HttpResponseCodeBehaviourHostConfig;
import de.phleisch.app.itsucks.filter.download.impl.ContentFilter;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.filter.download.impl.FileSizeFilter;
import de.phleisch.app.itsucks.filter.download.impl.MaxLinksToFollowFilter;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter;
import de.phleisch.app.itsucks.filter.download.impl.TimeLimitFilter;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter.RegExpFilterAction;
import de.phleisch.app.itsucks.filter.download.impl.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour;
import de.phleisch.app.itsucks.io.http.impl.HttpRetrieverResponseCodeBehaviour.ResponseCodeRange;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.DownloadJob;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.persistence.jaxb.ObjectFactory;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedChangeHttpResponseCodeBehaviourFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedContentFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedContentFilterConfig;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedDownloadJob;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedDownloadJobFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedFileSizeFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedHttpResponseCodeBehaviourHostConfig;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedHttpRetrieverResponseCodeBehaviour;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedJobParameter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedMaxLinksToFollowFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedRegExpJobFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedRegExpJobFilterAction;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedRegExpJobFilterRule;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedTimeLimitFilter;

public class DownloadJobConverter extends AbstractBeanConverter {

	private ObjectFactory mBeanFactory = new ObjectFactory();
	private DownloadJobFactory mJobFactory;
	
	public Object convertBeanToClass(Object pBean) throws Exception {
		
		//TODO replace this with an map
		
		if(pBean instanceof SerializedDownloadJob) {
			return convertSerializedDownloadJobToClass((SerializedDownloadJob)pBean);
		} if(pBean instanceof SerializedDownloadJobFilter) {
			return convertSerializedDownloadJobFilterToClass((SerializedDownloadJobFilter) pBean);
		} if(pBean instanceof SerializedMaxLinksToFollowFilter) {
			return convertSerializedMaxLinksToFollowFilterToClass((SerializedMaxLinksToFollowFilter) pBean);
		} if(pBean instanceof SerializedRegExpJobFilter) {
			return convertSerializedRegExpJobFilterToClass((SerializedRegExpJobFilter) pBean);
		} if(pBean instanceof SerializedFileSizeFilter) {
			return convertSerializedFileSizeFilterToClass((SerializedFileSizeFilter) pBean);
		} if(pBean instanceof SerializedChangeHttpResponseCodeBehaviourFilter) {
			return convertSerializedChangeHttpResponseCodeBehaviourFilterToClass((SerializedChangeHttpResponseCodeBehaviourFilter) pBean);			
		} if(pBean instanceof SerializedTimeLimitFilter) {
			return convertSerializedTimeLimitFilterToClass((SerializedTimeLimitFilter) pBean);
		} if(pBean instanceof SerializedContentFilter) {
			return convertSerializedContentFilterToClass((SerializedContentFilter) pBean);
		}
		
		
		throw new IllegalArgumentException("Unsupported bean type given: " + pBean.getClass());
	}


	private DownloadJob convertSerializedDownloadJobToClass(SerializedDownloadJob pJob) 
			throws Exception {
		
		UrlDownloadJob job = mJobFactory.createDownloadJob();
		
		job.setName(pJob.getName());
		job.setId(pJob.getId().intValue());
		job.setDepth(pJob.getDepth());
		job.setIgnoreFilter(pJob.isIgnoreFilter());
		job.setMaxRetryCount(pJob.getMaxRetryCount());
		job.setMinTimeBetweenRetry(pJob.getMinTimeBetweenRetry());
		//job.setParent(pParent) TODO implement
		job.setPriority(pJob.getPriority());
		job.setSavePath(pJob.getSavePath() != null ? new File(pJob.getSavePath()) : null);
		job.setSaveToDisk(pJob.isSaveToDisk());
		job.setState(pJob.getState());
		job.setUrl(new URL(pJob.getUrl()));
		
		for (SerializedJobParameter serializedJobParameter : pJob.getParameter()) {
			job.setParameter(convertSerializedJobParameterToClass(serializedJobParameter));
		}
		
		return job;
	}

	private DownloadJobFilter convertSerializedDownloadJobFilterToClass(SerializedDownloadJobFilter pFilter) 
			throws Exception {
		
		DownloadJobFilter downloadJobFilter = new DownloadJobFilter();
		
		downloadJobFilter.setURLPrefix(pFilter.getUrlPrefix() != null ? new URL(pFilter.getUrlPrefix()) : null);
		downloadJobFilter.setMaxRecursionDepth(pFilter.getMaxRecursionDepth());

		List<String> saveToDisk = pFilter.getSaveToDisk();
		downloadJobFilter.setSaveToDisk(saveToDisk.toArray(new String[saveToDisk.size()]));
		
		List<String> allowedHostName = pFilter.getAllowedHostName();
		downloadJobFilter.setAllowedHostNames(allowedHostName.toArray(new String[allowedHostName.size()]));
		
		List<String> alreadyAddedUri = pFilter.getAlreadyAddedUri();
		Set<URI> alreadyAddedUriSet = new HashSet<URI>();
		for (String string : alreadyAddedUri) {
			alreadyAddedUriSet.add(new URI(string));
		}
		downloadJobFilter.setAlreadyAddedUrls(alreadyAddedUriSet);
		
		return downloadJobFilter;
	}
	
	private MaxLinksToFollowFilter convertSerializedMaxLinksToFollowFilterToClass(SerializedMaxLinksToFollowFilter pFilter) {
		
		MaxLinksToFollowFilter maxLinksToFollowFilter = new MaxLinksToFollowFilter();
		
		maxLinksToFollowFilter.setMaxLinksToFollow(pFilter.getMaxLinksToFollow());
		
		return maxLinksToFollowFilter;
	}
	
	private RegExpJobFilter convertSerializedRegExpJobFilterToClass(SerializedRegExpJobFilter pFilter) {
		
		RegExpJobFilter regExpJobFilter = new RegExpJobFilter();
		
		regExpJobFilter.setLetUnfilteredJobsPass(pFilter.isLetUnfilteredJobsPass());
		
		for (SerializedRegExpJobFilterRule serializedFilterRule : 
				pFilter.getSerializedRegExpJobFilterRule()) {
			
			regExpJobFilter.addFilterRule(convertSerializedRegExpJobFilterRuleToClass(serializedFilterRule));
		}
		
		return regExpJobFilter;
	}

	private RegExpFilterRule convertSerializedRegExpJobFilterRuleToClass(SerializedRegExpJobFilterRule pSerializedFilterRule) {
		
		RegExpFilterRule filterRule = new RegExpJobFilter.RegExpFilterRule();
		
		filterRule.setName(pSerializedFilterRule.getName());
		filterRule.setDescription(pSerializedFilterRule.getDescription());
		filterRule.setPattern(pSerializedFilterRule.getPattern());
		
		filterRule.setMatchAction(convertSerializedRegExpJobFilterActionToClass(pSerializedFilterRule.getMatchAction()));
		filterRule.setNoMatchAction(convertSerializedRegExpJobFilterActionToClass(pSerializedFilterRule.getNoMatchAction()));
		
		return filterRule;
	}

	private RegExpFilterAction convertSerializedRegExpJobFilterActionToClass(SerializedRegExpJobFilterAction pSerializedAction) {
		
		RegExpFilterAction filterAction = new RegExpJobFilter.RegExpFilterAction();
		
		filterAction.setAccept(pSerializedAction.isAccept());
		filterAction.setPriorityChange(pSerializedAction.getPriorityChange());
		
		for (SerializedJobParameter serializedJobParameter : pSerializedAction.getParameter()) {
			filterAction.addJobParameter(convertSerializedJobParameterToClass(serializedJobParameter));
		}
		
		return filterAction;
	}

	private JobParameter convertSerializedJobParameterToClass(SerializedJobParameter pSerializedJobParameter) {
		
		return new JobParameter(pSerializedJobParameter.getKey(), pSerializedJobParameter.getValue());
	}
	
	private Object convertSerializedFileSizeFilterToClass(
			SerializedFileSizeFilter pBean) {
		
		FileSizeFilter fileSizeFilter = new FileSizeFilter();
		
		fileSizeFilter.setMinSizeAsText(pBean.getMinFileSize());
		fileSizeFilter.setMaxSizeAsText(pBean.getMaxFileSize());
		fileSizeFilter.setAcceptWhenLengthNotSet(
				pBean.isAcceptWhenLengthNotSet());
		
		return fileSizeFilter;
	}
	
	private Object convertSerializedChangeHttpResponseCodeBehaviourFilterToClass(
			SerializedChangeHttpResponseCodeBehaviourFilter pBean) {
		
		ChangeHttpResponseCodeBehaviourFilter httpResponseCodeFilter =
			new ChangeHttpResponseCodeBehaviourFilter();
		
		List<SerializedHttpResponseCodeBehaviourHostConfig> serializedHostConfigList = 
			pBean.getSerializedHttpResponseCodeBehaviourHostConfig();
		
		for (SerializedHttpResponseCodeBehaviourHostConfig serializedHostConfig : 
				serializedHostConfigList) {
			
			HttpRetrieverResponseCodeBehaviour responseCodeBehaviour =
				new HttpRetrieverResponseCodeBehaviour();
			
			for (SerializedHttpRetrieverResponseCodeBehaviour 
					serializedHttpRetrieverResponseCodeBehaviour : 
						serializedHostConfig.getSerializedHttpRetrieverResponseCodeBehaviour()) {
				
				ResponseCodeRange responseCodeRange = responseCodeBehaviour.add(
						serializedHttpRetrieverResponseCodeBehaviour.getResponseCodeFrom(), 
						serializedHttpRetrieverResponseCodeBehaviour.getResponseCodeTo(), 
						HttpRetrieverResponseCodeBehaviour.Action.valueOf(
								serializedHttpRetrieverResponseCodeBehaviour.getAction()),
						serializedHttpRetrieverResponseCodeBehaviour.getPriority());
				
				responseCodeRange.setTimeToWaitBetweenRetry(
						serializedHttpRetrieverResponseCodeBehaviour.getTimeToWaitBetweenRetry());
				
			}
			
			HttpResponseCodeBehaviourHostConfig hostConfig = 
				new HttpResponseCodeBehaviourHostConfig(
						serializedHostConfig.getHostname(),
						responseCodeBehaviour);
			
			httpResponseCodeFilter.addConfig(hostConfig);
		}
		
		return httpResponseCodeFilter;
	}	

	private Object convertSerializedTimeLimitFilterToClass(
			SerializedTimeLimitFilter pBean) {
		
		TimeLimitFilter timeLimitFilter = new TimeLimitFilter();
		
		timeLimitFilter.setTimeLimitAsText(pBean.getTimeLimit());
		
		return timeLimitFilter;
	}
	
	private Object convertSerializedContentFilterToClass(
			SerializedContentFilter pBean) {
		
		ContentFilter contentFilter = new ContentFilter();
		
		for(SerializedContentFilterConfig serializedConfig : pBean.getSerializedContentFilterConfig()) {
			
			ContentFilter.ContentFilterConfig config = 
				new ContentFilter.ContentFilterConfig(serializedConfig.getPattern(), 
						ContentFilter.ContentFilterConfig.Action.valueOf(serializedConfig.getMatchAction()),
						ContentFilter.ContentFilterConfig.Action.valueOf(serializedConfig.getNoMatchAction()));
			
			config.setName(serializedConfig.getName());
			config.setDescription(serializedConfig.getDescription());
			
			contentFilter.addContentFilterConfig(config);
		}
		
		return contentFilter;
	}
	
	public Object convertClassToBean(Object pObject) {
		
		if(pObject instanceof UrlDownloadJob) {
			return convertDownloadJobToBean((UrlDownloadJob)pObject);
		} if(pObject instanceof DownloadJobFilter) {
			return convertDownloadJobFilterToBean((DownloadJobFilter) pObject);
		} if(pObject instanceof MaxLinksToFollowFilter) {
			return convertMaxLinksToFollowFilterToBean((MaxLinksToFollowFilter) pObject);
		} if(pObject instanceof RegExpJobFilter) {
			return convertRegExpJobFilterToBean((RegExpJobFilter) pObject);
		} if(pObject instanceof FileSizeFilter) {
			return convertFileSizeFilterToBean((FileSizeFilter) pObject);
		} if(pObject instanceof ChangeHttpResponseCodeBehaviourFilter) {
			return convertChangeHttpResponseCodeBehaviourFilterToBean((ChangeHttpResponseCodeBehaviourFilter) pObject);
		} if(pObject instanceof TimeLimitFilter) {
			return convertTimeLimitFilterToBean((TimeLimitFilter) pObject);
		} if(pObject instanceof ContentFilter) {
			return convertContentFilterToBean((ContentFilter) pObject);
		}
			
		throw new IllegalArgumentException("Unsupported bean type given: " + pObject.getClass());
	}

	private SerializedDownloadJob convertDownloadJobToBean(UrlDownloadJob pJob) {
		
		SerializedDownloadJob serializedJob = mBeanFactory.createSerializedDownloadJob();
		
		serializedJob.setId(Long.valueOf(pJob.getId()));
		serializedJob.setName(pJob.getName());
		serializedJob.setPriority(pJob.getPriority());
		serializedJob.setState(pJob.getState());
		serializedJob.setIgnoreFilter(pJob.isIgnoreFilter());
		serializedJob.setSaveToDisk(pJob.isSaveToDisk());
		serializedJob.setSavePath(pJob.getSavePath().toString());
		
		//download job specific fields
		serializedJob.setUrl(pJob.getUrl().toExternalForm());
		serializedJob.setParentJobId(pJob.getParent() != null ? Long.valueOf(pJob.getParent().getId()) : null);
		serializedJob.setDepth(pJob.getDepth());
		serializedJob.setMaxRetryCount(pJob.getMaxRetryCount());
		serializedJob.setMinTimeBetweenRetry(Long.valueOf(pJob.getMinTimeBetweenRetry()));
		
		for (JobParameter jobParameter : pJob.getParameterList()) {
			serializedJob.getParameter().add(convertJobParameterToBean(jobParameter));
		}
		
		return serializedJob;
	}
	
	private SerializedJobParameter convertJobParameterToBean(JobParameter pJobParameter) {
		
		SerializedJobParameter parameter = mBeanFactory.createSerializedJobParameter();
		parameter.setKey(pJobParameter.getKey());
		parameter.setValue(pJobParameter.getValue());
		
		//TODO when not an simple type, do an conversion here
		
		return parameter;
	}

	private SerializedDownloadJobFilter convertDownloadJobFilterToBean(DownloadJobFilter pFilter) {
		
		SerializedDownloadJobFilter serializedDownloadJobFilter = 
			mBeanFactory.createSerializedDownloadJobFilter();
		
		serializedDownloadJobFilter.setMaxRecursionDepth(pFilter.getMaxRecursionDepth());
		serializedDownloadJobFilter.setUrlPrefix(pFilter.getURLPrefix() != null ? pFilter.getURLPrefix().toExternalForm() : null);
		
		for (String allowedHostname : pFilter.getAllowedHostNames()) {
			serializedDownloadJobFilter.getAllowedHostName().add(allowedHostname);
		}
		
		for (String saveToFile : pFilter.getSaveToDisk()) {
			serializedDownloadJobFilter.getSaveToDisk().add(saveToFile);
		}
		
		for (URI alreadyAddedURI : pFilter.getAlreadyAddedUrls()) {
			serializedDownloadJobFilter.getAlreadyAddedUri().add(alreadyAddedURI.toString());
		}
		
		return serializedDownloadJobFilter;
	}

	private SerializedMaxLinksToFollowFilter convertMaxLinksToFollowFilterToBean(MaxLinksToFollowFilter pFilter) {
		
		SerializedMaxLinksToFollowFilter maxLinksToFollowFilter = 
			mBeanFactory.createSerializedMaxLinksToFollowFilter();
		
		maxLinksToFollowFilter.setMaxLinksToFollow(pFilter.getMaxLinksToFollow());
		
		return maxLinksToFollowFilter;
	}
	
	private SerializedRegExpJobFilter convertRegExpJobFilterToBean(RegExpJobFilter pFilter) {
		
		SerializedRegExpJobFilter regExpJobFilter = 
			mBeanFactory.createSerializedRegExpJobFilter();

		regExpJobFilter.setLetUnfilteredJobsPass(pFilter.isLetUnfilteredJobsPass());
		
		for (RegExpFilterRule filterRule : pFilter.getFilterRules()) {
			
			SerializedRegExpJobFilterRule serializedFilterRule = 
				mBeanFactory.createSerializedRegExpJobFilterRule();
			
			serializedFilterRule.setName(filterRule.getName());
			serializedFilterRule.setDescription(filterRule.getDescription());
			serializedFilterRule.setPattern(filterRule.getPattern().pattern());
			
			serializedFilterRule.setMatchAction(convertRegExpFilterActionToBean(filterRule.getMatchAction()));
			serializedFilterRule.setNoMatchAction(convertRegExpFilterActionToBean(filterRule.getNoMatchAction()));			
			
			regExpJobFilter.getSerializedRegExpJobFilterRule().add(serializedFilterRule);
		}
		
		return regExpJobFilter;
	}
	
	private SerializedRegExpJobFilterAction convertRegExpFilterActionToBean(RegExpFilterAction pMatchAction) {
		
		SerializedRegExpJobFilterAction serializedFilterAction = 
			mBeanFactory.createSerializedRegExpJobFilterAction();
		
		serializedFilterAction.setAccept(pMatchAction.getAccept());
		serializedFilterAction.setPriorityChange(pMatchAction.getPriorityChange());
		
		for (JobParameter jobParameter : pMatchAction.getJobParameterList()) {
			serializedFilterAction.getParameter().add(convertJobParameterToBean(jobParameter));
		}
		
		return serializedFilterAction;
	}
	
	private Object convertFileSizeFilterToBean(FileSizeFilter pFileSizeFilter) {
		
		SerializedFileSizeFilter serializedFileSizeFilter = 
			mBeanFactory.createSerializedFileSizeFilter();
		
		if(pFileSizeFilter.getMinSizeAsText() != null) { 
			serializedFileSizeFilter.setMinFileSize(pFileSizeFilter.getMinSizeAsText());
		} else {
			serializedFileSizeFilter.setMinFileSize(String.valueOf(pFileSizeFilter.getMinSize()));
		}
		
		if(pFileSizeFilter.getMaxSizeAsText() != null) {
			serializedFileSizeFilter.setMaxFileSize(pFileSizeFilter.getMaxSizeAsText());
		} else {
			serializedFileSizeFilter.setMaxFileSize(String.valueOf(pFileSizeFilter.getMaxSize()));
		}
		
		serializedFileSizeFilter.setAcceptWhenLengthNotSet(
				pFileSizeFilter.isAcceptWhenLengthNotSet());
		
		return serializedFileSizeFilter;
	}
	
	private Object convertChangeHttpResponseCodeBehaviourFilterToBean(
			ChangeHttpResponseCodeBehaviourFilter pChangeHttpResponseCodeBehaviourFilter) {
		
		SerializedChangeHttpResponseCodeBehaviourFilter serializedChangeHttpResponseCodeBehaviourFilter = 
			mBeanFactory.createSerializedChangeHttpResponseCodeBehaviourFilter();

		List<SerializedHttpResponseCodeBehaviourHostConfig> serializedHostConfigList = 
			serializedChangeHttpResponseCodeBehaviourFilter.getSerializedHttpResponseCodeBehaviourHostConfig();
		
		for (HttpResponseCodeBehaviourHostConfig hostConfig : pChangeHttpResponseCodeBehaviourFilter.getConfigList()) {

			SerializedHttpResponseCodeBehaviourHostConfig serializedHostConfig = 
				mBeanFactory.createSerializedHttpResponseCodeBehaviourHostConfig();

			serializedHostConfig.setHostname(hostConfig.getHostnameRegexp());
//			serializedHostConfig.setQueueBehaviour(hostConfig.getQueueBehaviour().name());
			
			List<SerializedHttpRetrieverResponseCodeBehaviour> serializedResponseCodeBehaviourList = 
				serializedHostConfig.getSerializedHttpRetrieverResponseCodeBehaviour();
			
			HttpRetrieverResponseCodeBehaviour responseCodeBehaviour = hostConfig.getResponseCodeBehaviour();
			for (ResponseCodeRange responseCodeRange : responseCodeBehaviour.getConfigurationList()) {
				
				SerializedHttpRetrieverResponseCodeBehaviour serializedResponseCodeBehaviour = 
					mBeanFactory.createSerializedHttpRetrieverResponseCodeBehaviour();
				
				serializedResponseCodeBehaviour.setResponseCodeFrom(responseCodeRange.getResponseCodeFrom());
				serializedResponseCodeBehaviour.setResponseCodeTo(responseCodeRange.getResponseCodeTo());
				serializedResponseCodeBehaviour.setAction(responseCodeRange.getAction().name());
				serializedResponseCodeBehaviour.setPriority(responseCodeRange.getPriority());
				serializedResponseCodeBehaviour.setTimeToWaitBetweenRetry(responseCodeRange.getTimeToWaitBetweenRetry());

				serializedResponseCodeBehaviourList.add(serializedResponseCodeBehaviour);
			}
			
			serializedHostConfigList.add(serializedHostConfig);
		}
		
		return serializedChangeHttpResponseCodeBehaviourFilter;
	}

	private Object convertTimeLimitFilterToBean(TimeLimitFilter pTimeLimitFilter) {
		
		SerializedTimeLimitFilter serializedTimeLimitFilter = 
			mBeanFactory.createSerializedTimeLimitFilter();
		
		if(pTimeLimitFilter.getTimeLimitAsText() != null) { 
			serializedTimeLimitFilter.setTimeLimit(pTimeLimitFilter.getTimeLimitAsText());
		} else {
			serializedTimeLimitFilter.setTimeLimit(String.valueOf(pTimeLimitFilter.getTimeLimit() * 1000));
		}
		
		return serializedTimeLimitFilter;
	}

	private Object convertContentFilterToBean(ContentFilter pContentFilter) {
		
		SerializedContentFilter serializedContentFilter = 
			mBeanFactory.createSerializedContentFilter();
		
		for(ContentFilter.ContentFilterConfig config : pContentFilter.getContentFilterConfigList()) {
			
			SerializedContentFilterConfig serializedConfig = 
				mBeanFactory.createSerializedContentFilterConfig();
			
			serializedConfig.setName(config.getName());
			serializedConfig.setDescription(config.getDescription());
			serializedConfig.setPattern(config.getPattern().pattern());	
			serializedConfig.setMatchAction(config.getMatchAction().name());
			serializedConfig.setNoMatchAction(config.getNoMatchAction().name());
			
			serializedContentFilter.getSerializedContentFilterConfig().add(serializedConfig);
		}
		
		return serializedContentFilter;
	}
	
	public void setJobFactory(DownloadJobFactory pJobFactory) {
		mJobFactory = pJobFactory;
	}
	
	public List<Class<?>> getSupportedBeanConverter() {
		
		Class<?>[] supportedBeanConvertClasses = new Class<?>[] {
			SerializedDownloadJob.class,
			SerializedDownloadJobFilter.class,
			SerializedMaxLinksToFollowFilter.class,
			SerializedRegExpJobFilter.class,
			SerializedFileSizeFilter.class,
			SerializedChangeHttpResponseCodeBehaviourFilter.class,
			SerializedTimeLimitFilter.class,
			SerializedContentFilter.class,
		};
		
		return Arrays.asList(supportedBeanConvertClasses);
	}

	public List<Class<?>> getSupportedClassConverter() {
		
		Class<?>[] supportedClassConvertClasses = new Class<?>[] {
			UrlDownloadJob.class,
			DownloadJobFilter.class,
			MaxLinksToFollowFilter.class,
			RegExpJobFilter.class,
			FileSizeFilter.class,
			ChangeHttpResponseCodeBehaviourFilter.class,
			TimeLimitFilter.class,
			ContentFilter.class,
		};
		
		return Arrays.asList(supportedClassConvertClasses);
	}

}

