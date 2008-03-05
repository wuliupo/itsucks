/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 09.02.2008
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.phleisch.app.itsucks.io.UrlDataRetriever;

public class HttpRetrieverResponseCodeBehaviour implements Serializable {

	private static final long serialVersionUID = 8752549289063445405L;

	public static enum Action {
		OK { 
			Integer getRetrieverAction() {return UrlDataRetriever.RESULT_RETRIEVAL_OK;} 
		},
		FAILED_BUT_RETRYABLE { 
			Integer getRetrieverAction() {return UrlDataRetriever.RESULT_RETRIEVAL_FAILED_BUT_RETRYABLE;} 
		},
		FAILED { 
			Integer getRetrieverAction() {return UrlDataRetriever.RESULT_RETRIEVAL_FAILED;} 
		};

		abstract Integer getRetrieverAction();
	}
	
	public static class ResponseCodeRange implements Comparable<ResponseCodeRange> {
		
		public static final int LOW_PRIORITY = 100;
		public static final int MEDIUM_PRIORITY = 200;
		public static final int HIGH_PRIORITY = 300;
		
		private int mResponseCodeFrom;
		private int mResponseCodeTo;
		private Action mAction;
		private int mPriority;
		private Long mTimeToWaitBetweenRetry;
		
		public ResponseCodeRange(int pResponseCodeFrom, int pResponseCodeTo, 
				Action pAction) {
			this(pResponseCodeFrom, pResponseCodeTo, pAction, MEDIUM_PRIORITY);
		}
		
		public ResponseCodeRange(int pResponseCodeFrom, int pResponseCodeTo, 
				Action pAction, int pPriority) {
			mResponseCodeFrom = pResponseCodeFrom;
			mResponseCodeTo = pResponseCodeTo;
			mAction = pAction;
			mPriority = pPriority;
		}

		public int getResponseCodeFrom() {
			return mResponseCodeFrom;
		}

		public int getResponseCodeTo() {
			return mResponseCodeTo;
		}

		public void setAction(Action pAction) {
			mAction = pAction;
		}

		public Action getAction() {
			return mAction;
		}

		public int getPriority() {
			return mPriority;
		}
		
		public Long getTimeToWaitBetweenRetry() {
			return mTimeToWaitBetweenRetry;
		}

		public void setTimeToWaitBetweenRetry(Long pTimeToWaitBetweenRetry) {
			mTimeToWaitBetweenRetry = pTimeToWaitBetweenRetry;
		}

		public int compareTo(final ResponseCodeRange pResponseCodeRange) {
			int result = 
				compareInt(this.getResponseCodeTo(), pResponseCodeRange.getResponseCodeTo());
			if(result == 0) {
				result = compareInt(getPriority(), pResponseCodeRange.getPriority());
			}
			
			return result;
		}
		
		private static final int compareInt(final int thisVal, final int anotherVal) {
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}

		@Override
		public boolean equals(Object pResponseCodeRange) {
			return compareTo((ResponseCodeRange) pResponseCodeRange) == 0;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((mAction == null) ? 0 : mAction.hashCode());
			result = prime * result + mPriority;
			result = prime * result + mResponseCodeFrom;
			result = prime * result + mResponseCodeTo;
			result = prime
					* result
					+ ((mTimeToWaitBetweenRetry == null) ? 0
							: mTimeToWaitBetweenRetry.hashCode());
			return result;
		}

	}

	protected SortedSet<ResponseCodeRange> mResponseCodeAction;
	
	public HttpRetrieverResponseCodeBehaviour() {
		mResponseCodeAction = new TreeSet<ResponseCodeRange>();
	}

	public ResponseCodeRange add(int pResponseCode, Action pAction, int pPriority) {
		ResponseCodeRange range = new ResponseCodeRange(pResponseCode, pResponseCode, pAction, pPriority);
		mResponseCodeAction.add(range);
		
		return range;
	}
	
	public ResponseCodeRange add(int pResponseCode, Action pAction) {
		ResponseCodeRange range = new ResponseCodeRange(pResponseCode, pResponseCode, pAction);
		mResponseCodeAction.add(range);
		
		return range;
	}
	
	public ResponseCodeRange add(int pResponseCodeFrom, int pResponseCodeTo, Action pAction) {
		ResponseCodeRange range = new ResponseCodeRange(pResponseCodeFrom, pResponseCodeTo, pAction);
		mResponseCodeAction.add(range);

		return range;
	}
	
	public ResponseCodeRange add(int pResponseCodeFrom, int pResponseCodeTo, Action pAction, int pPriority) {
		ResponseCodeRange range = new ResponseCodeRange(pResponseCodeFrom, pResponseCodeTo, pAction, pPriority);
		mResponseCodeAction.add(range);

		return range;
	}
	
	public boolean add(ResponseCodeRange pResponseCodeRange) {
		return mResponseCodeAction.add(pResponseCodeRange);
	}
	
	public boolean add(HttpRetrieverResponseCodeBehaviour pRetrieverReponseCodeBehaviour) {
		return mResponseCodeAction.addAll(pRetrieverReponseCodeBehaviour.mResponseCodeAction);
	}

	public boolean remove(ResponseCodeRange pResponseCodeRange) {
		return mResponseCodeAction.remove(pResponseCodeRange);
	}
	
	public ResponseCodeRange findConfigurationForResponseCode(int pResponseCode) {
		
		ResponseCodeRange result = null;
		
		for (ResponseCodeRange range : mResponseCodeAction) {
			
			if(range.getResponseCodeFrom() <= pResponseCode 
					&& range.getResponseCodeTo() >= pResponseCode) {
				result = range;
				break;
			}
		}
		
		return result;
	}
	
	public Set<ResponseCodeRange> getConfigurationList() {
		return Collections.unmodifiableSet(mResponseCodeAction);
	}
	
	public Action findActionForResponseCode(int pResponseCode) {
		Action action = null;
		
		ResponseCodeRange rangeForCode = findConfigurationForResponseCode(pResponseCode);
		if(rangeForCode != null) {
			action = rangeForCode.getAction();
		}
		
		return action;
	}

	public int size() {
		return mResponseCodeAction.size();
	}

}
