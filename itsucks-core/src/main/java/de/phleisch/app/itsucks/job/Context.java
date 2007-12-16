/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.09.2007
 */

package de.phleisch.app.itsucks.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.phleisch.app.itsucks.event.EventDispatcher;

public class Context {

	private Map<Object, Object> mParameters = new ConcurrentHashMap<Object, Object>();
	private EventDispatcher mEventDispatcher = null;
	
	public void setContextParameter(Object pKey, Object pValue) {
		mParameters.put(pKey, pValue);
	}
	
	public Object getContextParameter(Object pKey) {
		return mParameters.get(pKey);
	}

	public void putAllContextParameter(Map<String, Object> contextParameter) {
		mParameters.putAll(contextParameter);
	}

	public EventDispatcher getEventDispatcher() {
		return mEventDispatcher;
	}

	public void setEventDispatcher(EventDispatcher pEventDispatcher) {
		mEventDispatcher = pEventDispatcher;
	}
	
}
