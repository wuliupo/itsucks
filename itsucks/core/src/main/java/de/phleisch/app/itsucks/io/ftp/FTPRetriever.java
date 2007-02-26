/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.ftp;

import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.Metadata;


public class FTPRetriever extends DataRetriever {

	public FTPRetriever() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void connect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void retrieve() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void disconnect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isDataAvailable() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected long getBytesDownloaded() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected float getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

}
