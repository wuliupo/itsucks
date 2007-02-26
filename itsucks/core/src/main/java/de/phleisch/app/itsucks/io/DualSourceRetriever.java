/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 26.02.2007
 */

package de.phleisch.app.itsucks.io;

import java.io.File;
import java.net.URL;
import java.util.Observer;

public class DualSourceRetriever implements DataRetriever {

	private DataRetriever mDataRetriever;
	private File mLocalFile;
	
	public DualSourceRetriever(DataRetriever pDataRetriever, File pFile) {
		
		mDataRetriever = pDataRetriever;
		
		//mLog.error("File: " + file + " already exists with size: " + file.length());
	}

	public void abort() {
		// TODO Auto-generated method stub
		
	}

	public void addDataProcessor(DataProcessor pDataProcessor) {
		// TODO Auto-generated method stub
		
	}

	public void addObserver(Observer pO) {
		// TODO Auto-generated method stub
		
	}

	public void connect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void deleteObserver(Observer pO) {
		// TODO Auto-generated method stub
		
	}

	public void disconnect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public long getBytesDownloaded() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	public URL getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDataAvailable() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public void retrieve() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void setUrl(URL pUrl) {
		// TODO Auto-generated method stub
		
	}

}
