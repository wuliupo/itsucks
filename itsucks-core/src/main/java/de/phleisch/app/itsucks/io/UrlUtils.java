/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.11.2009
 */

package de.phleisch.app.itsucks.io;

import java.net.URL;

public class UrlUtils {

	/**
	 * Returns the filename of an URL or null if the URL ends with an slash.
	 * 
	 *  http://server.com/a/foo.bin -> method returns "foo.bin"
	 *  http://server.com/a/ -> method returns null
	 *  http://server.com -> method returns null
	 *  
	 * @param pUrl
	 * @return The filename or null
	 */
	public static String getFilenameFromUrl(final URL pUrl) {
		
		String full_path = pUrl.getPath();
		if(pUrl.getQuery() != null) full_path += "?" + pUrl.getQuery();

		String filename = null;
		if(!full_path.endsWith("/") && full_path.lastIndexOf('/') != -1) {
			filename = full_path.substring(pUrl.getPath().lastIndexOf('/') + 1);
		}
		
		return filename;
	}
	
}
