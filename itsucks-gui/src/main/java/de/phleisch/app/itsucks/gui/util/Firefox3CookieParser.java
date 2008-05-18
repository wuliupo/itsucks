/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 04.05.2008
 */

package de.phleisch.app.itsucks.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.phleisch.app.itsucks.io.http.impl.Cookie;

public class Firefox3CookieParser implements CookieParser {

	/*
		Feld 1
		    Name des Cookies
		Feld 2
		    Cookie-Daten 
		Feld 3
		    Domainname
		Feld 4
		    Pfad für den das Cookie gültig ist
	 */	
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.gui.util.CookieParser#parseCookies(java.lang.String)
	 */
	public List<Cookie> parseCookies(final String pData) {
		
		List<Cookie> result = new ArrayList<Cookie>();
		
		String data = pData.trim();
		//remove \r\n at the end
		data = data.replaceAll("[\n]*$", "");
		
		StringTokenizer tokenizer = new StringTokenizer(data, "\t\n");

		if((tokenizer.countTokens() % 4) > 0) {
			throw new IllegalArgumentException("Bad data given, count of tokens is invalid");
		}

		int line = 0;
		while(tokenizer.hasMoreTokens()) {
			line ++;
			Cookie cookie = new Cookie();
			
			for (int i = 0; i < 4; i++) {
				String nextToken = tokenizer.nextToken();
				if(nextToken == null) {
					throw new RuntimeException("Parser error, token " + i + " in " + line + " is emtpy");
				}
				
				readField(i, nextToken, cookie);
				
				
			}
			
			result.add(cookie);
		}
		
		return result;
	}

	
	private void readField(int pField, String pValue, Cookie pCookie) {
		

		switch(pField) {
		case 0: //name
			pCookie.setName(pValue);
			break;
			
		case 1: //data
			pCookie.setValue(pValue);
			break;
			
		case 2: //domain name
			pCookie.setDomain(pValue);
			break;
			
		case 3: //path
			pCookie.setPath(pValue);
			break;

		default:
			throw new IllegalArgumentException("Got bad field offset: " + pField);
			
		}
		
	}
	
}
