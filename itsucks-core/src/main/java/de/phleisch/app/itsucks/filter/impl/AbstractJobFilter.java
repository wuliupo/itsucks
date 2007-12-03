/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 02.11.2007
 */

package de.phleisch.app.itsucks.filter.impl;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.job.Context;

public abstract class AbstractJobFilter implements JobFilter {

	protected Context mContext = null;

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context pContext) {
		mContext = pContext;
	}
	
}
