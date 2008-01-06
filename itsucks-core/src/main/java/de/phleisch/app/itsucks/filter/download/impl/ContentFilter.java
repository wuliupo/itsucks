/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 06.01.2008
 */

package de.phleisch.app.itsucks.filter.download.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.phleisch.app.itsucks.filter.impl.AbstractJobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class ContentFilter extends AbstractJobFilter implements Serializable {
	
	private static final long serialVersionUID = -6691442606571156612L;

	public static final String CONTENT_FILTER_CONFIG_LIST_PARAMETER = "ContentFilterConfigList";

	private List<ContentFilterConfig> mConfigList;
	
	public static class ContentFilterConfig implements Serializable {
		
		private static final long serialVersionUID = 5415048157348669335L;

		public enum Action {
			NO_ACTION,
			REJECT,
		}
		
		private Pattern mPattern;
		private Action mMatchAction;
		private Action mNoMatchAction;
		
		public ContentFilterConfig(String pPattern, Action pMatchAction,
				Action pNoMatchAction) {
			
			this(Pattern.compile(pPattern, 
					Pattern.CASE_INSENSITIVE), pMatchAction, pNoMatchAction);
		}
		
		public ContentFilterConfig(Pattern pPattern, Action pMatchAction,
				Action pNoMatchAction) {
			super();
			mPattern = pPattern;
			mMatchAction = pMatchAction;
			mNoMatchAction = pNoMatchAction;
		}
		
		public Pattern getPattern() {
			return mPattern;
		}
		
		public Action getMatchAction() {
			return mMatchAction;
		}
		
		public Action getNoMatchAction() {
			return mNoMatchAction;
		}
	}
	
	public ContentFilter() {
		mConfigList = new ArrayList<ContentFilterConfig>();
	}
	
	public void addContentFilterConfig(ContentFilterConfig pConfig) {
		mConfigList.add(pConfig);
	}
	
	public Job filter(Job pJob) throws Exception {
		
		pJob.addParameter(new JobParameter(CONTENT_FILTER_CONFIG_LIST_PARAMETER, mConfigList));
		
		return pJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#supports(de.phleisch.app.itsucks.job.Job)
	 */
	public boolean supports(Job pJob) {
		return pJob instanceof UrlDownloadJob;
	}

}
