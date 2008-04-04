/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.03.2008
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.util.List;

/**
 * @author olli
 *
 * http://www.w3.org/Protocols/rfc2109/rfc2109.txt
 *
 */
public class CookieHelper {

	
	public String buildCookieString(List<String> pCookieList) {
		
		StringBuilder cookieString = new StringBuilder();
		
		boolean first = true;
		for (String cookie : pCookieList) {
			if(!first) {
				cookieString.append("; ");
			}
			cookieString.append(cookie);
			first = false;
		}
		
		return cookieString.toString();
	}
	
	
}
