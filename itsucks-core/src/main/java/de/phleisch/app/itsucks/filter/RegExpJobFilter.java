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
 * This filter is very flexible because of it's feature to filter 
 * download job attributes by regular expressions.
 * 
 * @author olli
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
	
	/**
	 * Sets if unfiltered jobs (no rule matches) should let passed.
	 * If set to false jobs which does not matches at least one rule is rejected.
	 * 
	 * Default value is true (let unfiltered jobs pass).
	 * 
	 * @param pValue
	 */
	public void setLetUnfilteredJobsPass(boolean pValue) {
		mLetUnfilteredJobsPass = pValue;
	}
	
	/**
	 * Returns if unfiltered jobs (no rule matches) should let passed.
	 * If set to false jobs which does not matches at least one rule is rejected.
	 * 
	 * @param pValue
	 */
	public boolean isLetUnfilteredJobsPass() {
		return mLetUnfilteredJobsPass;
	}
	
	/**
	 * Adds an filter rule.
	 * 
	 * @param pRule
	 */
	public void addFilterRule(RegExpFilterRule pRule) {
		synchronized (mFilterRules) {
			mFilterRules.add(pRule);
		}
	}
	
	/**
	 * Removes an filter rule.
	 * 
	 * @param pRule
	 */
	public void removeFilterRule(RegExpFilterRule pRule) {
		synchronized (mFilterRules) {
			mFilterRules.remove(pRule);
		}
	}
	
	/**
	 * Returns a list of all registered filter rules.
	 * 
	 * @return
	 */
	public List<RegExpFilterRule> getFilterRules() {
		synchronized (mFilterRules) {
			return new ArrayList<RegExpFilterRule>(mFilterRules);
		}
	}
	
	/**
	 * This is a single regular expression rule.
	 * 
	 * Actions can be set for a match and a no match of the expression.
	 * The regular expression is the default java regexp syntax.
	 * 
	 * @author olli
	 *
	 */
	public static class RegExpFilterRule implements Serializable {

		private static final long serialVersionUID = -9012670101396671089L;

		private String mName = null;
		private Pattern mPattern = null;

		private RegExpFilterAction mMatchAction = null;
		private RegExpFilterAction mNoMatchAction = null;
		
		public RegExpFilterRule() {
			this("", new RegExpFilterAction(), new RegExpFilterAction());
		}		
		
		/**
		 * Creates a new rule with the given pattern.
		 * No actions are registered for match/no match of the pattern.
		 * 
		 * @param pPattern
		 */
		public RegExpFilterRule(String pPattern) {
			this(pPattern, new RegExpFilterAction(), new RegExpFilterAction());
		}		
		
		/**
		 * Creates a new rule with the given pattern and given match/no match actions.
		 * 
		 * @param pPattern
		 * @param pMatchAction
		 * @param pNoMatchAction
		 */
		public RegExpFilterRule(String pPattern, 
				RegExpFilterAction pMatchAction, RegExpFilterAction pNoMatchAction) {
			
			setPattern(pPattern);
			setMatchAction(pMatchAction);
			setNoMatchAction(pNoMatchAction); 
		}
		
		/**
		 * Creates a new rule with the given pattern and parameters.
		 * 
		 * @param pPattern The regular expression pattern.
		 * @param pAccept Should the job accepted in case of an match of the pattern. 
		 * 	True == accept
		 *  False == reject
		 *  NULL == no change
		 * @param pPriorityChange Should the priority changed in case of match.  
		 */
		public RegExpFilterRule(String pPattern, Boolean pAccept, int pPriorityChange) {
			setPattern(pPattern);
			
			RegExpFilterAction action = new RegExpFilterAction();
			action.setAccept(pAccept);
			action.setPriorityChange(pPriorityChange);
			setMatchAction(action);
			
			setNoMatchAction(new RegExpFilterAction()); 
		}
		
		/**
		 * Creates a new rule with the given pattern and parameters.
		 * 
		 * @param pPattern The regular expression pattern.
		 * @param pAccept Should the job accepted in case of an match of the pattern. 
		 * 	True == accept
		 *  False == reject
		 *  NULL == no change
		 */
		public RegExpFilterRule(String pPattern, Boolean pAccept) {
			this(pPattern, pAccept, 0);
		}
		
		/**
		 * Creates a new rule with the given pattern and parameters.
		 * 
		 * @param pPattern The regular expression pattern.
		 * @param pPriorityChange Should the priority changed in case of match.
		 */   
		public RegExpFilterRule(String pPattern, int pPriorityChange) {
			this(pPattern, null, pPriorityChange);
		}
		
		/**
		 * Sets the given regular expression pattern.
		 * 
		 * @param pPattern
		 */
		public void setPattern(String pPattern) {
			mPattern = Pattern.compile(pPattern, 
					Pattern.CASE_INSENSITIVE);
		}
		
		/**
		 * Sets the regular expression pattern.
		 * 
		 * @return
		 */
		public Pattern getPattern() {
			return mPattern;
		}
		
		@Override
		public String toString() {
			return toTextString();
		}
		
		/**
		 * Returns a string containing all information about this filter.
		 * Text format.
		 * 
		 * @return
		 */
		public String toTextString() {
			return (mName != null ? "Name: '" + mName + "'\n" : "") +
					"Pattern: '" + getPattern() + "' \n" +
					"Match: " + mMatchAction + "\n" + 
					"No Match: " + mNoMatchAction;
		}

		/**
		 * Returns the name of the filter.
		 * 
		 * @return
		 */
		public String getName() {
			return mName;
		}

		/**
		 * Sets the name of the filter.
		 * 
		 * @param pName
		 */
		public void setName(String pName) {
			mName = pName;
		}

		/**
		 * Returns the action in case of an match.
		 * 
		 * @return
		 */
		public RegExpFilterAction getMatchAction() {
			return mMatchAction;
		}

		/**
		 * Sets the action in case of an match.
		 * 
		 * @param pMatchAction
		 */
		public void setMatchAction(RegExpFilterAction pMatchAction) {
			mMatchAction = pMatchAction;
		}

		/**
		 * Returns the action in case of no match.
		 * 
		 * @return
		 */		
		public RegExpFilterAction getNoMatchAction() {
			return mNoMatchAction;
		}

		/**
		 * Sets the action in case of no match.
		 * 
		 * @param pMatchAction
		 */		
		public void setNoMatchAction(RegExpFilterAction pNoMatchAction) {
			mNoMatchAction = pNoMatchAction;
		}
		
	}
	
	/**
	 * This class saves the changes which are applied to a filtered job. 
	 * 
	 * @author olli
	 */
	public static class RegExpFilterAction implements Serializable {
		
		private static final long serialVersionUID = 2661477258757092821L;
		
		private int mPriorityChange = 0;
		private Boolean mAccept = null;
		private Map<String, JobParameter> mJobParameter;
		
		/**
		 * Creates a new action.
		 * Per default no changes to an job is set.
		 */
		public RegExpFilterAction() {
			mJobParameter = new HashMap<String, JobParameter>();
		}
		
		/**
		 * Creates a new action.
		 * 
		 * @param pAccept Accept the filtered job
		 * 	True == accept
		 *  False == reject
		 *  NULL == no change
		 * @param pPriorityChange Priority change of the job.
		 */
		public RegExpFilterAction(Boolean pAccept, int pPriorityChange) {
			this();
			mAccept = pAccept;
			mPriorityChange = pPriorityChange;
		}
		
		/**
		 * Sets if the job should be accepted/rejected.
		 * 
		 * @param pAccept true == accept the job, false == discard the job, null == leave the previous value
		 */
		public void setAccept(Boolean pAccept) {
			mAccept = pAccept;
		}

		/**
		 * Returns if the job should be accepted/rejected.
		 * 
		 * @return
		 */
		public Boolean getAccept() {
			return mAccept;
		}		
		
		/**
		 * Gets the priority change.
		 * 
		 * @return
		 */
		public int getPriorityChange() {
			return mPriorityChange;
		}

		/**
		 * Sets the priority change.
		 * 
		 * @param pPriorityChange
		 */
		public void setPriorityChange(int pPriorityChange) {
			mPriorityChange = pPriorityChange;
		}
		
		/**
		 * Returns true if any job parameter are defined. 
		 * @return
		 */
		public boolean hasJobParameter() {
			return mJobParameter.size() > 0;
		}
		
		/**
		 * Adds an job parameter which is added to the job.
		 * 
		 * @param pJobParameter
		 */
		public void addJobParameter(JobParameter pJobParameter) {
			if(pJobParameter != null) {
				mJobParameter.put(pJobParameter.getKey(), pJobParameter);
			}
		}

		/**
		 * Removes the given job parameter.
		 * 
		 * @param pJobParameterKey
		 */
		public void removeJobParameter(String pJobParameterKey) {
			if(pJobParameterKey != null) {
				mJobParameter.remove(pJobParameterKey);
			}
		}
		
		/**
		 * Gets a job parameter.
		 * 
		 * @param pJobParameter
		 * @return
		 */
		public JobParameter getJobParameter(String pJobParameterKey) {
			return mJobParameter.get(pJobParameterKey);
		}
		
		/**
		 * Gets the job parameter list.
		 * 
		 * @return
		 */
		public List<JobParameter> getJobParameterList() {
			return new ArrayList<JobParameter>(mJobParameter.values());
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return 
				"Accept: '" + (getAccept() == null ? "no change" : getAccept()) + "', " +
				"PrioChg: '" + getPriorityChange() + "'";
		}

	}
	
}
