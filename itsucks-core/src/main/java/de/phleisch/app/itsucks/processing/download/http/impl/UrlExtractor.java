/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 28.01.2008
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import de.phleisch.app.itsucks.constants.ApplicationConstants;

public class UrlExtractor {

	protected static final String REGEXP_SEARCH_PREFIX = "exp_";
	protected static final String REGEXP_EXCLUDE_PREFIX = "excl_";
	
	protected static final Log mLog = LogFactory.getLog(UrlExtractor.class);
	protected static PatternConfig[] mSearchPatterns = null;
	protected static Pattern[] mExcludePatterns = null;
	//From http://www.ietf.org/rfc/rfc2396.txt
	protected static final Pattern mAllowedURICharsPattern = 
		Pattern.compile("[a-z0-9;/?:@&=+$,\\-_\\.!~*'\\(\\)%]", Pattern.CASE_INSENSITIVE);

	protected URI mBaseURI;
	
	public UrlExtractor(URI pBaseURI) {
		mBaseURI = pBaseURI;
		
		initPatterns();
	}
	
	
	protected synchronized void initPatterns() {
		if(mSearchPatterns == null) {
			loadPatterns(ApplicationConstants.HTTP_PARSER_CONFIG_FILE);
		}
	}

	public URI[] extractURLs(String pData) {
		
		//remove all line breaks
		pData = pData.replaceAll("[\r\n]", " "); 
		
		HashSet<URI> urlList = new HashSet<URI>();
		mLog.debug("Extracting URL's from " + mBaseURI);
		//mLog.debug("Site data: " + pData);
		
		for (int i = 0; i < mSearchPatterns.length; i++) {
			PatternConfig pattern = mSearchPatterns[i];
			Matcher matcher = pattern.getPattern().matcher(pData);
			while(matcher.find()) {
				String match = matcher.group(pattern.getResultGroup());
				
				//mLog.debug("Got hit '" + pattern.pattern() + "' on '" + line + "' -> " + match);
				mLog.debug("Got hit: " + match);
				
				URI uri = null;
				try {
					match = match.trim(); //remove trailing spaces
					
					match = decodeHtmlExpressions(match);
					
					if(isMatchExcluded(match)) {
						continue;
					}
					
					match = encodeURIChars(match); 
					
					//allocate new array to prevent reuse of the string which the data is from. 
					match = new String(match.toCharArray());
					
					uri = mBaseURI.resolve(new URI(match));
				} catch(URISyntaxException se) {
					mLog.error("Error parsing URI: " + match, se);
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
	 * Checks if the match is excluded by a pattern.
	 * @param pMatch
	 * @return
	 */
	protected boolean isMatchExcluded(String pMatch) {
		
		for (int i = 0; i < mExcludePatterns.length; i++) {
			Pattern pattern = mExcludePatterns[i];
			Matcher matcher = pattern.matcher(pMatch);
			if(matcher.find()) {
				return true;
			}
		}
		
		return false;
	}


	protected String decodeHtmlExpressions(String link) {
		//replace html codes
		link = link.replaceAll("&amp;", "&");
		link = link.replaceAll("&lt;", "<"); //<
		link = link.replaceAll("&gt;", ">"); //>
		link = link.replaceAll("&quot;", "\""); //"
		
		return link;
	}

	/**
	 * http://www.ietf.org/rfc/rfc2396.txt
	 * @param pLink
	 * @return
	 */
	protected String encodeURIChars(String pLink) {
		
		String link = StringUtils.trimLeadingWhitespace(pLink);
		
		//encode chars for url
		StringBuilder url = new StringBuilder();
		
		char[] linkChars = new char[link.length()];
		link.getChars(0, link.length(), linkChars, 0);
		
		for(char linkChar : linkChars) {
			String linkChunk = String.valueOf(linkChar);
			
			if(mAllowedURICharsPattern.matcher(linkChunk).find()) {
				//char is allowed
				url.append(linkChar);
			} else {
				//encode char
				byte[] charBytes;
				try {
					charBytes = linkChunk.getBytes("ASCII");
				} catch (UnsupportedEncodingException e) {
					mLog.error(e);
					throw new IllegalStateException(e);
				}
				
				for(byte b : charBytes) {
					url.append('%');
					url.append(Integer.toHexString(b).toUpperCase());
				}
			}
		}
		
		return url.toString();
	}

	protected void loadPatterns(String propertyName) {
		
		mLog.debug("Reading Patterns from file: " + propertyName);
		
		URL resource = this.getClass().getClassLoader().getResource(propertyName);
		if(resource == null) {
			mLog.error("Cannot load patterns from file: " + propertyName);
			throw new IllegalStateException("Cannot load patterns from file: " + propertyName);
		}
		
		Properties patternFile = new Properties();
		
		try {
			patternFile.load(resource.openStream());
		} catch (IOException e) {
			mLog.error("Cannot load patterns from file: " + propertyName, e);
		}
		
		//load search patterns
		ArrayList<PatternConfig> searchRegExpList = new ArrayList<PatternConfig>();
		int offset = 1;
		while(true) {
			
			String regexp = (String) patternFile.get(REGEXP_SEARCH_PREFIX + offset);
			String resultGroup = (String) patternFile.get(REGEXP_SEARCH_PREFIX + offset + ".group");
			if(regexp != null) {
				Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
				searchRegExpList.add(new PatternConfig(pattern, Integer.parseInt(resultGroup)));
			} else {
				break;
			}
			
			offset++;
		}
		
		mSearchPatterns = searchRegExpList.toArray(new PatternConfig[searchRegExpList.size()]);
		
		
		//load exclude patterns
		ArrayList<Pattern> excludeRegExpList = new ArrayList<Pattern>();
		offset = 1;
		while(true) {
			String regexp = (String) patternFile.get(REGEXP_EXCLUDE_PREFIX + offset);
			if(regexp != null) {
				excludeRegExpList.add(Pattern.compile(regexp, Pattern.CASE_INSENSITIVE));
			} else {
				break;
			}
			offset++;
		}
		
		mExcludePatterns = excludeRegExpList.toArray(new Pattern[excludeRegExpList.size()]);
	}
	
	protected class PatternConfig {
		private Pattern mPattern;
		private int mResultGroup;
		
		protected PatternConfig(Pattern pPattern, int pResultGroup) {
			mPattern = pPattern;
			mResultGroup = pResultGroup;
		}

		public Pattern getPattern() {
			return mPattern;
		}

		public int getResultGroup() {
			return mResultGroup;
		}
	}
}
