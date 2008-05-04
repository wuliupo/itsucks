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

public class IECookieParser implements CookieParser {

	/*
		Zeile 1
		    Name des Cookies
		Zeile 2
		    Cookie-Daten
		Zeile 3
		    Domain bzw. Host und Pfad für den das Cookie gültig ist.
		Zeile 4
		    Unbekannt
		Zeile 5
		    Gültigkeit (niederwertigeres DWord der FileTime-Struktur)
		Zeile 6
		    Gültigkeit (höherwertigeres DWord der FileTime-Struktur)
		Zeile 7
		    Erstellungszeit (niederwertigeres DWord der FileTime-Struktur)
		Zeile 8
		    Erstellungszeit (höherwertigeres DWord der FileTime-Struktur)
		Zeile 9
		    Stern-Zeichen (*) zur Trennung 
	 */	
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.gui.util.CookieParser#parseCookies(java.lang.String)
	 */
	public List<Cookie> parseCookies(final String pData) {
		
		List<Cookie> result = new ArrayList<Cookie>();
		
		String data = pData.trim();
		//remove \r\n at the end
		data = data.replaceAll("[\n]*$", "");
		
		StringTokenizer tokenizer = new StringTokenizer(data, "\n");

		if((tokenizer.countTokens() % 9) > 0) {
			throw new IllegalArgumentException("Bad data given, count of tokens is invalid");
		}

		int line = 0;
		while(tokenizer.hasMoreTokens()) {
			line ++;
			Cookie cookie = new Cookie();
			
			for (int i = 0; i < 9; i++) {
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
		
		case 2: //domain name + path
			int index = pValue.indexOf('/');
			if(index < 0) {
				throw new IllegalArgumentException("Bad domain/path value: " + pValue);
			}
			pCookie.setDomain(pValue.substring(0, index));
			pCookie.setPath(pValue.substring(index));
			break;
			
		case 3: 
		case 4:
		case 5:
		case 6:		
		case 7:			
			break;
			
		case 8:
			if(!pValue.equals("*")) {
				throw new IllegalArgumentException("Line end invalid: " + pValue);
			}
			break;


		default:
			throw new IllegalArgumentException("Got bad field offset: " + pField);
		}
		
	}
	
}
