/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.ftp;

import de.phleisch.app.itsucks.io.AbstractDataRetriever;
import de.phleisch.app.itsucks.io.Metadata;


public class FTPRetriever extends AbstractDataRetriever {

	public FTPRetriever() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieve() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDataAvailable() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getBytesDownloaded() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		
	}

}
