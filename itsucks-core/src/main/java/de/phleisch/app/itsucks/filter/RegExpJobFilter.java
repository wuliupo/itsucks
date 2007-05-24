/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.filter;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.JobParameter;
import de.phleisch.app.itsucks.io.DownloadJob;

/**
 * @author olli
 *
 */
public class RegExpJobFilter implements JobFilter, Serializable {
	
	private static final long serialVersionUID = 8668860787246814610L;

	@SuppressWarnings("unused")
	private static Log mLog = LogFactory.getLog(RegExpJobFilter.class);

	/**
	 * List with all filter rules
	 */
	private List<RegExpFilterRule> mFilterRules;
	
	/**
	 * true == if no filter matches, dont't change the job
	 * false == if no filter matches, discard the job
	 */
	private boolean mLetUnfilteredJobsPass = true;   
	
	public RegExpJobFilter() {
		mFilterRules = new ArrayList<RegExpFilterRule>();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.chaoscrawler.filter.JobFilter#filter(de.phleisch.app.chaoscrawler.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		
		//check state of job, only process open jobs
		if(pJob.getState() != Job.STATE_OPEN && 
				pJob.getState() != Job.STATE_IGNORED) return pJob;
		
		Boolean accept = null;
		final URL url = ((DownloadJob)pJob).getUrl();

		synchronized (mFilterRules) {
		
			for (RegExpFilterRule rule : mFilterRules) {
				Pattern p = rule.getPattern();
				
				Matcher m = p.matcher(url.toString());
				RegExpFilterAction action;
				
		        if(m.find()) {
		        	//mLog.trace("URL: " + url.toString() + " matches to rule: " + p);
		        	action = rule.getMatchAction();
		        } else {
		        	action = rule.getNoMatchAction();
		        }
		        	
	        	//change the priority of the job
	        	if(action.getPriorityChange() != 0) {
	        		pJob.setPriority(pJob.getPriority() + action.getPriorityChange());
	        	}
	        	
	        	//only change accept if the rule action changes it
	        	if(action.getAccept() != null) {
	        		accept = action.getAccept();
	        	}
		        
	        	//if the action has parameter add them to the job
	        	if(action.hasJobParameter()) {
	        		List<JobParameter> jobParameterList = action.getJobParameterList();
	        		for (JobParameter parameter : jobParameterList) {
						pJob.addParameter(parameter);
					}
	        	}
	        	
	        	
			}
		}
		
		//if an result
		if(accept != null) {
			pJob.setState(accept ? Job.STATE_OPEN : Job.STATE_IGNORED);
			
		} else if(!mLetUnfilteredJobsPass) {
			// ignore jobs which aren't filtered
			pJob.setState(Job.STATE_IGNORED);
		}
		
		return pJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.chaoscrawler.filter.JobFilter#supports(de.phleisch.app.chaoscrawler.Job)
	 */
	public boolean supports(Job pJob) {
		return pJob instanceof DownloadJob;
	}
	
	public void setLetUnfilteredJobsPass(boolean pValue) {
		mLetUnfilteredJobsPass = pValue;
	}
	
	public void addFilterRule(RegExpFilterRule pRule) {
		synchronized (mFilterRules) {
			mFilterRules.add(pRule);
		}
	}
	
	public void removeFilterRule(RegExpFilterRule pRule) {
		synchronized (mFilterRules) {
			mFilterRules.remove(pRule);
		}
	}
	
	public List<RegExpFilterRule> getFilterRules() {
		synchronized (mFilterRules) {
			return new ArrayList<RegExpFilterRule>(mFilterRules);
		}
	}
	
	public static class RegExpFilterRule implements Serializable {

		private static final long serialVersionUID = -6511044935647503309L;

		private String mName = null;
		private Pattern mPattern = null;

		private RegExpFilterAction mMatchAction = null;
		private RegExpFilterAction mNoMatchAction = null;
		
		public RegExpFilterRule(String pPattern) {
			setPattern(pPattern);
			setMatchAction(new RegExpFilterAction());
			setNoMatchAction(new RegExpFilterAction()); 
		}
		
		public RegExpFilterRule(String pPattern, Boolean pAccept, int pPriorityChange) {
			setPattern(pPattern);
			
			RegExpFilterAction action = new RegExpFilterAction();
			action.setAccept(pAccept);
			action.setPriorityChange(pPriorityChange);
			setMatchAction(action);
			
			setNoMatchAction(new RegExpFilterAction()); 
		}
		
		public RegExpFilterRule(String pPattern, Boolean pAccept) {
			this(pPattern, pAccept, 0);
		}
		
		public RegExpFilterRule(String pPattern, int pPriorityChange) {
			this(pPattern, null, pPriorityChange);
		}
		
		public void setPattern(String pPattern) {
			mPattern = Pattern.compile(pPattern, 
					Pattern.CASE_INSENSITIVE);
		}
		
		public Pattern getPattern() {
			return mPattern;
		}
		
		@Override
		public String toString() {
			return toHtmlString();
		}
		
		public String toTextString() {
			return (mName != null ? "Name: '" + mName + "'\n" : "") +
					"Pattern: '" + getPattern() + "' \n" +
					"Match: " + mMatchAction + "\n" + 
					"No Match: " + mNoMatchAction;
		}
		
		public String toHtmlString() {
			return "<html>" +
					(mName != null ? "Name: '" + mName + "'<br>\n" : "") +
					"Pattern: '" + getPattern() + "' <br>\n" +
					"Match: " + mMatchAction + 
					"<br>\nNo Match: " + mNoMatchAction +
					"</html>";
		}

		public String getName() {
			return mName;
		}

		public void setName(String pName) {
			mName = pName;
		}

		public RegExpFilterAction getMatchAction() {
			return mMatchAction;
		}

		public void setMatchAction(RegExpFilterAction pMatchAction) {
			mMatchAction = pMatchAction;
		}

		public RegExpFilterAction getNoMatchAction() {
			return mNoMatchAction;
		}

		public void setNoMatchAction(RegExpFilterAction pNoMatchAction) {
			mNoMatchAction = pNoMatchAction;
		}
		
	}
	
	public static class RegExpFilterAction implements Serializable {
		
		private static final long serialVersionUID = 3892411450565605281L;
		
		private int mPriorityChange = 0;
		private Boolean mAccept = null;
		private Map<String, JobParameter> mJobParameter;
		
		public RegExpFilterAction() {
			mJobParameter = new HashMap<String, JobParameter>();
		}
		
		public RegExpFilterAction(Boolean pAccept, int pPriorityChange) {
			this();
			mAccept = pAccept;
			mPriorityChange = pPriorityChange;
		}
		
		/**
		 * 
		 * @param pAccept true == accept the job, false == discard the job, null == leave the previous value
		 */
		public void setAccept(Boolean pAccept) {
			mAccept = pAccept;
		}

		public int getPriorityChange() {
			return mPriorityChange;
		}

		public void setPriorityChange(int pPriorityChange) {
			mPriorityChange = pPriorityChange;
		}

		public Boolean getAccept() {
			return mAccept;
		}
		
		public boolean hasJobParameter() {
			return mJobParameter.size() > 0;
		}
		
		public void addJobParameter(JobParameter pJobParameter) {
			if(pJobParameter != null) {
				mJobParameter.put(pJobParameter.getKey(), pJobParameter);
			}
		}

		public JobParameter getJobParameter(String pJobParameter) {
			return mJobParameter.get(pJobParameter);
		}
		
		public List<JobParameter> getJobParameterList() {
			return new ArrayList<JobParameter>(mJobParameter.values());
		}
		
		@Override
		public String toString() {
			return 
				"Accept: '" + (getAccept() == null ? "no change" : getAccept()) + "', " +
				"PrioChg: '" + getPriorityChange() + "'";
		}

	}
	
}
