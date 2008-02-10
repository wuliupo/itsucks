/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 09.02.2008
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.util.SortedSet;
import java.util.TreeSet;

import de.phleisch.app.itsucks.io.DataRetriever;

public class HttpRetrieverResponseCodeBehaviour {

	public static enum Action {
		OK { 
			Integer getRetrieverAction() {return DataRetriever.RESULT_RETRIEVAL_OK;} 
		},
		FAILED_BUT_RETRYABLE { 
			Integer getRetrieverAction() {return DataRetriever.RESULT_RETRIEVAL_FAILED_BUT_RETRYABLE;} 
		},
		FAILED { 
			Integer getRetrieverAction() {return DataRetriever.RESULT_RETRIEVAL_FAILED;} 
		};

		abstract Integer getRetrieverAction();
	}
	
	public static class ResponseCodeRange implements Comparable<ResponseCodeRange> {
		private int mResponseCodeFrom;
		private int mResponseCodeTo;
		private Action mAction;
		
		public ResponseCodeRange(int pResponseCodeFrom, int pResponseCodeTo, Action pAction) {
			mResponseCodeFrom = pResponseCodeFrom;
			mResponseCodeTo = pResponseCodeTo;
			mAction = pAction;
		}

		public int getResponseCodeFrom() {
			return mResponseCodeFrom;
		}

		public int getResponseCodeTo() {
			return mResponseCodeTo;
		}

		public Action getAction() {
			return mAction;
		}

		public int compareTo(ResponseCodeRange pResponseCodeRange) {
			int thisVal = this.getResponseCodeTo();
			int anotherVal = pResponseCodeRange.getResponseCodeTo();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
	}

	protected SortedSet<ResponseCodeRange> mResponseCodeAction;
	
	public HttpRetrieverResponseCodeBehaviour() {
		mResponseCodeAction = new TreeSet<ResponseCodeRange>();
	}

	public boolean add(int pResponseCode, Action pAction) {
		return mResponseCodeAction.add(
				new ResponseCodeRange(pResponseCode, pResponseCode, pAction));
	}
	
	public boolean add(int pResponseCodeFrom, int pResponseCodeTo, Action pAction) {
		return mResponseCodeAction.add(
				new ResponseCodeRange(pResponseCodeFrom, pResponseCodeTo, pAction));
	}
	
	public boolean add(ResponseCodeRange pResponseCodeRange) {
		return mResponseCodeAction.add(pResponseCodeRange);
	}

	public boolean remove(ResponseCodeRange pResponseCodeRange) {
		return mResponseCodeAction.remove(pResponseCodeRange);
	}
	
	public ResponseCodeRange findRangeForCode(int pResponseCode) {
		
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
	
	public Action findActionForResponseCode(int pResponseCode) {
		Action action = null;
		
		ResponseCodeRange rangeForCode = findRangeForCode(pResponseCode);
		if(rangeForCode != null) {
			action = rangeForCode.getAction();
		}
		
		return action;
	}	

}
