/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.11.2007
 */

package de.phleisch.app.itsucks.core;

import de.phleisch.app.itsucks.job.Job;

public interface WorkerThread {

	public final static int CMD_SHUTDOWN = 0;
	public final static int CMD_PROCESS_JOB = 1;
	public final static int CMD_RETURN_TO_POOL = 2;

	public abstract void abort();

	public abstract void start();

	public abstract void join(long pMillis) throws InterruptedException;

	public abstract void addCommand(int pCmd);

	public abstract Job getJob();

	public abstract void setJob(Job pJob);

}