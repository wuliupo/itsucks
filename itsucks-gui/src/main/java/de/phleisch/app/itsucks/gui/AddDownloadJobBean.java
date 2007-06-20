/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.gui;

import java.util.ArrayList;
import java.util.List;

import de.phleisch.app.itsucks.filter.JobFilter;
import de.phleisch.app.itsucks.io.DownloadJob;

public class AddDownloadJobBean {
	
	private DownloadJob mDownload;
	private List<JobFilter> mFilterList;
	
	public AddDownloadJobBean() {
		mFilterList = new ArrayList<JobFilter>();
	}
	
	public DownloadJob getDownload() {
		return mDownload;
	}
	public void setDownload(DownloadJob pDownload) {
		mDownload = pDownload;
	}
	public List<JobFilter> getFilterList() {
		return mFilterList;
	}
	public void setFilterList(List<JobFilter> pFilterList) {
		mFilterList = pFilterList;
	}
	public void addFilter(JobFilter pAdvancedFilter) {
		
		List<JobFilter> newList = new ArrayList<JobFilter>(getFilterList());
		newList.add(pAdvancedFilter);
		
		setFilterList(newList);
	}
	
}
