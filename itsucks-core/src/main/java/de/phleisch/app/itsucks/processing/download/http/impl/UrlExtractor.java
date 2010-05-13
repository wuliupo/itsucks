/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.01.2008
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.constants.ApplicationConstants;

public class UrlExtractor {

	protected static final String REGEXP_PREFIX = "exp_"; 
	
	protected static final Log mLog = LogFactory.getLog(UrlExtractor.class);
	protected static Pattern[] mPatterns = null;

	protected URI mBaseURI;
	
	public UrlExtractor(URI pBaseURI) {
		mBaseURI = pBaseURI;
		
		initPatterns();
	}
	
	
	protected synchronized void initPatterns() {
		if(mPatterns == null) {
			mPatterns = loadPatterns(ApplicationConstants.HTTP_PARSER_CONFIG_FILE);
		}
	}

	public URI[] extractURLs(String pData) {
		
		//remove all line breaks
		pData = pData.replaceAll("[\r\n]", " "); 
		
		HashSet<URI> urlList = new HashSet<URI>();
		mLog.debug("Extracting URL's from " + mBaseURI);
		//mLog.debug("Site data: " + pData);
		
		for (int i = 0; i < mPatterns.length; i++) {
			Pattern pattern = mPatterns[i];
			Matcher matcher = pattern.matcher(pData);
			while(matcher.find()) {
				String match = matcher.group(1);
				
				//mLog.debug("Got hit '" + pattern.pattern() + "' on '" + line + "' -> " + match);
				//mLog.debug("Got hit: '" + pattern.pattern() + "' : " + match);
				
				URI uri = null;
				try {
					match = match.trim(); //remove trailing spaces
					match = prepareLink(match);
					
					//allocate new array to prevent reuse of the string which the data is from. 
					match = new String(match.toCharArray());
					
					uri = mBaseURI.resolve(new URI(match));
				} catch(Exception ex) {
					mLog.warn("Resolving of base url failed: " +
							"Match: " + match + " BaseURI: " + mBaseURI);
					mLog.trace("Resolving of base url failed: " +
							"Match: " + match + " BaseURI: " + mBaseURI, ex);
				}
				if(uri != null) {
					//mLog.debug("Add uri: " + uri);
					urlList.add(uri);
				}
			}
		}
		
		mLog.debug("Finished Extracting URL's from " + mBaseURI);
			
		return urlList.toArray(new URI[urlList.size()]);
	}
	
	/**
	 * http://www.ietf.org/rfc/rfc2396.txt
	 * @param pMatch
	 * @return
	 */
	private String prepareLink(String pMatch) {
		String result = pMatch;

		//TODO, folgende ersetzen:
		// " " | "<" | ">" | "#" | "%" | <"> | "{" | "}" | "|" | "\" | "^" | "[" | "]" | "`"
		
		//try to fix broken url's
		//space is not allowed in URI's, replace them with %20
		result = result.replaceAll(" ", "%20"); 
		
		//replace html codes
		result = result.replaceAll("&amp;", "&");
		result = result.replaceAll("&lt;", "%3C"); //<
		result = result.replaceAll("<", "%3C"); //<
		result = result.replaceAll("&gt;", "%3E"); //>
		result = result.replaceAll(">", "%3E"); //>
		result = result.replaceAll("&quot;", "%22"); //"
		
		return result;
	}


	protected Pattern[] loadPatterns(String propertyName) {
		
		mLog.debug("Reading Patterns from file: " + propertyName);
		
		URL resource = this.getClass().getClassLoader().getResource(propertyName);
		if(resource == null) {
			mLog.error("Cannot load patterns from file: " + propertyName);
			return new Pattern[0];
		}
		
		Properties patternFile = new Properties();
		
		try {
			patternFile.load(resource.openStream());
		} catch (IOException e) {
			mLog.error("Cannot load patterns from file: " + propertyName, e);
		}
		
		ArrayList<Pattern> regExpList = new ArrayList<Pattern>();
		
		int offset = 1;
		while(true) {
			
			String regexp = (String) patternFile.get(REGEXP_PREFIX + offset);
			if(regexp != null) {
				
				Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
				regExpList.add(pattern);
			} else {
				break;
			}
			
			offset++;
		}
		
		return regExpList.toArray(new Pattern[regExpList.size()]);
	}
	
}
