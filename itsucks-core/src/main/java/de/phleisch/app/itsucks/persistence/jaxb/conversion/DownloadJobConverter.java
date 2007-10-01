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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.phleisch.app.itsucks.JobFactory;
import de.phleisch.app.itsucks.JobParameter;
import de.phleisch.app.itsucks.filter.DownloadJobFilter;
import de.phleisch.app.itsucks.filter.MaxLinksToFollowFilter;
import de.phleisch.app.itsucks.filter.RegExpJobFilter;
import de.phleisch.app.itsucks.filter.RegExpJobFilter.RegExpFilterAction;
import de.phleisch.app.itsucks.filter.RegExpJobFilter.RegExpFilterRule;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.persistence.jaxb.ObjectFactory;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedDownloadJob;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedDownloadJobFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedJobParameter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedMaxLinksToFollowFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedRegExpJobFilter;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedRegExpJobFilterAction;
import de.phleisch.app.itsucks.persistence.jaxb.SerializedRegExpJobFilterRule;

public class DownloadJobConverter extends AbstractBeanConverter {

	private ObjectFactory mBeanFactory = null;
	private JobFactory mJobFactory;
	
	public Object convertBeanToClass(Object pBean) throws Exception {
		
		if(pBean instanceof SerializedDownloadJob) {
			return convertSerializedDownloadJobToClass((SerializedDownloadJob)pBean);
		} if(pBean instanceof SerializedDownloadJobFilter) {
			return convertSerializedDownloadJobFilterToClass((SerializedDownloadJobFilter) pBean);
		} if(pBean instanceof SerializedMaxLinksToFollowFilter) {
			return convertSerializedMaxLinksToFollowFilterToClass((SerializedMaxLinksToFollowFilter) pBean);
		} if(pBean instanceof SerializedRegExpJobFilter) {
			return convertSerializedRegExpJobFilterToClass((SerializedRegExpJobFilter) pBean);
		}
		
		throw new IllegalArgumentException("Unsupported bean type given: " + pBean.getClass());
	}

	private DownloadJob convertSerializedDownloadJobToClass(SerializedDownloadJob pJob) 
			throws Exception {
		
		DownloadJob job = mJobFactory.createDownloadJob();
		
		job.setName(pJob.getName());
		job.setId(pJob.getId().intValue());
		job.setDepth(pJob.getDepth());
		job.setIgnoreFilter(pJob.isIgnoreFilter());
		job.setMaxRetryCount(pJob.getMaxRetryCount());
		job.setMinTimeBetweenRetry(pJob.getMinTimeBetweenRetry());
		//job.setParent(pParent) TODO implement
		job.setPriority(pJob.getPriority());
		job.setSavePath(pJob.getSavePath() != null ? new File(pJob.getSavePath()) : null);
		job.setSaveToDisk(pJob.isSaveToFile());
		job.setState(pJob.getState());
		job.setUrl(new URL(pJob.getUrl()));
		
		
		for (SerializedJobParameter serializedJobParameter : pJob.getParameter()) {
			job.addParameter(convertSerializedJobParameterToClass(serializedJobParameter));
		}
		
		return job;
	}

	private DownloadJobFilter convertSerializedDownloadJobFilterToClass(SerializedDownloadJobFilter pFilter) 
			throws Exception {
		
		DownloadJobFilter downloadJobFilter = new DownloadJobFilter();
		
		downloadJobFilter.setAllowOnlyRelativeReferences(pFilter.isAllowOnlyRelativeReferences());
		downloadJobFilter.setURLPrefix(pFilter.getBaseUrl() != null ? new URL(pFilter.getBaseUrl()) : null);
		downloadJobFilter.setMaxRecursionDepth(pFilter.getMaxRecursionDepth());

		List<String> saveToFileFilter = pFilter.getSaveToFileFilter();
		downloadJobFilter.setSaveToDisk(saveToFileFilter.toArray(new String[saveToFileFilter.size()]));

		
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

	public Object convertClassToBean(Object pObject) {
		
		if(pObject instanceof DownloadJob) {
			return convertDownloadJobToBean((DownloadJob)pObject);
		} if(pObject instanceof DownloadJobFilter) {
			return convertDownloadJobFilterToBean((DownloadJobFilter) pObject);
		} if(pObject instanceof MaxLinksToFollowFilter) {
			return convertMaxLinksToFollowFilterToBean((MaxLinksToFollowFilter) pObject);
		} if(pObject instanceof RegExpJobFilter) {
			return convertRegExpJobFilterToBean((RegExpJobFilter) pObject);
		}
			
		throw new IllegalArgumentException("Unsupported bean type given: " + pObject.getClass());
	}

	private SerializedDownloadJob convertDownloadJobToBean(DownloadJob pJob) {
		
		SerializedDownloadJob serializedJob = mBeanFactory.createSerializedDownloadJob();
		
		serializedJob.setId(new Long(pJob.getId()));
		serializedJob.setName(pJob.getName());
		serializedJob.setPriority(pJob.getPriority());
		serializedJob.setState(pJob.getState());
		serializedJob.setIgnoreFilter(pJob.isIgnoreFilter());
		serializedJob.setSaveToFile(pJob.isSaveToDisk());
		serializedJob.setSavePath(pJob.getSavePath().toString());
		
		//download job specific fields
		serializedJob.setUrl(pJob.getUrl().toExternalForm());
		serializedJob.setParentJobId(pJob.getParent() != null ? new Long(pJob.getParent().getId()) : null);
		serializedJob.setDepth(pJob.getDepth());
		serializedJob.setMaxRetryCount(pJob.getMaxRetryCount());
		serializedJob.setMinTimeBetweenRetry(new Long(pJob.getMinTimeBetweenRetry()));
		
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
		
		SerializedDownloadJobFilter serializedDownloadJobFilter = mBeanFactory.createSerializedDownloadJobFilter();
		
		serializedDownloadJobFilter.setAllowOnlyRelativeReferences(pFilter.isAllowOnlyRelativeReferences());
		serializedDownloadJobFilter.setMaxRecursionDepth(pFilter.getMaxRecursionDepth());
		serializedDownloadJobFilter.setBaseUrl(pFilter.getURLPrefix() != null ? pFilter.getURLPrefix().toExternalForm() : null);
		
		for (String allowedHostname : pFilter.getAllowedHostNames()) {
			serializedDownloadJobFilter.getAllowedHostName().add(allowedHostname);
		}
		
		for (String saveToFile : pFilter.getSaveToDisk()) {
			serializedDownloadJobFilter.getSaveToFileFilter().add(saveToFile);
		}
		
		for (URI alreadyAddedURI : pFilter.getAlreadyAddedUrls()) {
			serializedDownloadJobFilter.getAlreadyAddedUri().add(alreadyAddedURI.toString());
		}
		
		return serializedDownloadJobFilter;
	}

	private SerializedMaxLinksToFollowFilter convertMaxLinksToFollowFilterToBean(MaxLinksToFollowFilter pFilter) {
		
		SerializedMaxLinksToFollowFilter maxLinksToFollowFilter = mBeanFactory.createSerializedMaxLinksToFollowFilter();
		maxLinksToFollowFilter.setMaxLinksToFollow(pFilter.getMaxLinksToFollow());
		
		return maxLinksToFollowFilter;
	}
	
	private SerializedRegExpJobFilter convertRegExpJobFilterToBean(RegExpJobFilter pFilter) {
		
		SerializedRegExpJobFilter regExpJobFilter = mBeanFactory.createSerializedRegExpJobFilter();

		regExpJobFilter.setLetUnfilteredJobsPass(pFilter.isLetUnfilteredJobsPass());
		
		for (RegExpFilterRule filterRule : pFilter.getFilterRules()) {
			
			SerializedRegExpJobFilterRule serializedFilterRule = mBeanFactory.createSerializedRegExpJobFilterRule();
			serializedFilterRule.setName(filterRule.getName());
			serializedFilterRule.setPattern(filterRule.getPattern().pattern());
			
			serializedFilterRule.setMatchAction(convertRegExpFilterActionToBean(filterRule.getMatchAction()));
			serializedFilterRule.setNoMatchAction(convertRegExpFilterActionToBean(filterRule.getNoMatchAction()));			
			
			regExpJobFilter.getSerializedRegExpJobFilterRule().add(serializedFilterRule);
		}
		
		return regExpJobFilter;
	}
	
	private SerializedRegExpJobFilterAction convertRegExpFilterActionToBean(RegExpFilterAction pMatchAction) {
		
		SerializedRegExpJobFilterAction serializedFilterAction = mBeanFactory.createSerializedRegExpJobFilterAction();
		
		serializedFilterAction.setAccept(pMatchAction.getAccept());
		serializedFilterAction.setPriorityChange(pMatchAction.getPriorityChange());
		
		for (JobParameter jobParameter : pMatchAction.getJobParameterList()) {
			serializedFilterAction.getParameter().add(convertJobParameterToBean(jobParameter));
		}
		
		return serializedFilterAction;
	}

	public void setBeanFactory(ObjectFactory pBeanFactory) {
		mBeanFactory = pBeanFactory;
	}

	public void setJobFactory(JobFactory pJobFactory) {
		mJobFactory = pJobFactory;
	}

}

