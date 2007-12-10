/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui.job.ifc;

import de.phleisch.app.itsucks.persistence.SerializableJobList;

public interface AddDownloadJobCapable {

//	public abstract void addDownload(DownloadJob pDownload,
//			List<JobFilter> pFilterList);

	public abstract void addDownload(SerializableJobList pJob);

}