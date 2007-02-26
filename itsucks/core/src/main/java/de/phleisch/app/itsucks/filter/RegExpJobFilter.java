/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.filter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DownloadJob;

/**
 * @author olli
 *
 */
public class RegExpJobFilter implements JobFilter {
	
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
		URL url = ((DownloadJob)pJob).getUrl();

		synchronized (mFilterRules) {
		
			for (RegExpFilterRule rule : mFilterRules) {
				Pattern p = rule.getPattern();
				
				Matcher m = p.matcher(url.toString());
		        if(m.find()) {
		        	//mLog.trace("URL: " + url.toString() + " matches to rule: " + p);
		        	
		        	//change the priority of the job
		        	if(rule.getMatchPriorityChange() != 0) {
		        		pJob.setPriority(pJob.getPriority() + rule.getMatchPriorityChange());
		        	}
		        	
		        	//only change accept if the rule changes it
		        	if(rule.getMatchAccept() != null) {
		        		accept = rule.getMatchAccept();
		        	}
		        } else {
		        	
		        	//change the priority of the job
		        	if(rule.getNoMatchPriorityChange() != 0) {
		        		pJob.setPriority(pJob.getPriority() + rule.getNoMatchPriorityChange());
		        	}
		        	
		        	//only change accept if the rule changes it
		        	if(rule.getNoMatchAccept() != null) {
		        		accept = rule.getNoMatchAccept();
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
	
	public static class RegExpFilterRule {

		private String mName;
		
		private Pattern mPattern;
		private int mMatchPriorityChange;
		private Boolean mMatchAccept;
		private int mNoMatchPriorityChange;
		private Boolean mNoMatchAccept;
		
		public RegExpFilterRule(String pPattern, Boolean pAccept, int pPriorityChange) {
			setPattern(pPattern);
			setMatchAccept(pAccept);
			setMatchPriorityChange(pPriorityChange);
		}
		
		public RegExpFilterRule(String pPattern, Boolean pMatchAccept, int pMatchPriorityChange, Boolean pNoMatchAccept, int pNoMatchPriorityChange) {
			setPattern(pPattern);
			setMatchAccept(pMatchAccept);
			setMatchPriorityChange(pMatchPriorityChange);
			setNoMatchAccept(pNoMatchAccept);
			setNoMatchPriorityChange(pNoMatchPriorityChange);
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
		
		/**
		 * 
		 * @param pAccept true == accept the job, false == discard the job, null == leave the previous value
		 */
		public void setMatchAccept(Boolean pAccept) {
			mMatchAccept = pAccept;
		}

		public int getMatchPriorityChange() {
			return mMatchPriorityChange;
		}

		public void setMatchPriorityChange(int pPriorityChange) {
			mMatchPriorityChange = pPriorityChange;
		}

		public Boolean getMatchAccept() {
			return mMatchAccept;
		}

		@Override
		public String toString() {
			return toHtmlString();
		}
		
		public String toTextString() {
			return (mName != null ? "Name: '" + mName + "'\n" : "") +
					"Pattern: '" + getPattern() + "' \n" +
					"Match: " + 
					"Accept: '" + (getMatchAccept() == null ? "no change" : getMatchAccept()) + "', " +
					"PrioChg: '" + getMatchPriorityChange() + "' \n" +
					"No Match: " +
					"Accept: '" + (getNoMatchAccept() == null ? "no change" : getNoMatchAccept()) + "', " +
					"PrioChg: '" + getNoMatchPriorityChange() + "'";
		}
		
		public String toHtmlString() {
			return "<html>" +
					(mName != null ? "Name: '" + mName + "'<br>\n" : "") +
					"Pattern: '" + getPattern() + "' <br>\n" +
					"Match: " + 
					"Accept: '" + (getMatchAccept() == null ? "no change" : getMatchAccept()) + "', " +
					"PrioChg: '" + getMatchPriorityChange() + "' " +
					"<br>\nNo Match: " +
					"Accept: '" + (getNoMatchAccept() == null ? "no change" : getNoMatchAccept()) + "', " +
					"PrioChg: '" + getNoMatchPriorityChange() + "'</html>";
		}

		public Boolean getNoMatchAccept() {
			return mNoMatchAccept;
		}

		public void setNoMatchAccept(Boolean pNoMatchAccept) {
			mNoMatchAccept = pNoMatchAccept;
		}

		public int getNoMatchPriorityChange() {
			return mNoMatchPriorityChange;
		}

		public void setNoMatchPriorityChange(int pNoMatchPriorityChange) {
			mNoMatchPriorityChange = pNoMatchPriorityChange;
		}

		public String getName() {
			return mName;
		}

		public void setName(String pName) {
			mName = pName;
		}
		
	}
	
}
