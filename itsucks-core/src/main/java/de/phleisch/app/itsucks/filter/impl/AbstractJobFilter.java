/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 02.11.2007
 */

package de.phleisch.app.itsucks.filter.impl;

import de.phleisch.app.itsucks.context.EventContext;
import de.phleisch.app.itsucks.filter.JobFilter;

public abstract class AbstractJobFilter implements JobFilter {

	protected EventContext mContext = null;

	public EventContext getContext() {
		return mContext;
	}

	public void setContext(EventContext pContext) {
		mContext = pContext;
	}
	
}
