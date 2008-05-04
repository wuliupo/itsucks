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

import de.phleisch.app.itsucks.filter.download.http.impl.Cookie;

public class MozillaCookieParser implements CookieParser {

	/*
		Feld 1
		    Domainname
		Feld 2
		    Gültigkeit (TRUE = für die gesamte Domain gültig, FALSE = nur für den angegebenen Pfad gültig)
		Feld 3
		    Pfad für den das Cookie gültig ist
		Feld 4
		    Sichere Verbindung erforderlich (TRUE = Ja, FALSE = Nein)
		Feld 5
		    Datum und Uhrzeit der Gültigkeit des Cookies. Diese Zeitangabe (UTC) ist im time_t-Format gespeichert, hier: 09.12.2006, 23:45:49 (UTC)
		Feld 6
		    Name des Cookies
		Feld 7
		    Cookie-Daten 
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

		if((tokenizer.countTokens() % 7) > 0) {
			throw new IllegalArgumentException("Bad data given, count of tokens is invalid");
		}

		int line = 0;
		while(tokenizer.hasMoreTokens()) {
			line ++;
			Cookie cookie = new Cookie();
			
			for (int i = 0; i < 7; i++) {
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
		case 0: //domain name
			pCookie.setDomain(pValue);
			break;
			
		case 1: 
			break;
			
		case 2: //path
			pCookie.setPath(pValue);
			break;
			
		case 3: //security
			break;
			
		case 4: //date
			break;
			
		case 5: //name
			pCookie.setName(pValue);
			break;
			
		case 6: //data
			pCookie.setValue(pValue);
			break;

		default:
			throw new IllegalArgumentException("Got bad field offset: " + pField);
			
		}
		
	}
	
}
