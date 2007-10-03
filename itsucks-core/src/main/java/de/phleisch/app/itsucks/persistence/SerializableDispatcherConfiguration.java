/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 03.10.2007
 */

package de.phleisch.app.itsucks.persistence;

public class SerializableDispatcherConfiguration {
	private Integer mWorkerThreads;
	private Integer mDispatchDelay;
	
	public Integer getWorkerThreads() {
		return mWorkerThreads;
	}
	public void setWorkerThreads(Integer pWorkerThreads) {
		mWorkerThreads = pWorkerThreads;
	}
	public Integer getDispatchDelay() {
		return mDispatchDelay;
	}
	public void setDispatchDelay(Integer pDispatchDelay) {
		mDispatchDelay = pDispatchDelay;
	}
}
