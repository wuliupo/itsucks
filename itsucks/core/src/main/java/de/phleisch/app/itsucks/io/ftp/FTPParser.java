/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.ftp;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DataParser;


public class FTPParser extends DataParser {

	public FTPParser() {
		super();
	}

	@Override
	public void process(byte[] pBuffer, int pBytes) throws Exception {
	}

	@Override
	public boolean supports(Job pJob) {
		return false;
	}

}
