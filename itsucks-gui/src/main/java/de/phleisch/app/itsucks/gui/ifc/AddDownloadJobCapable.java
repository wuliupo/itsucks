/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui.ifc;

import java.util.List;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.io.DownloadJob;

public interface AddDownloadJobCapable {

	public abstract void addDownload(DownloadJob pDownload,
			List<JobFilter> pFilterList);

}