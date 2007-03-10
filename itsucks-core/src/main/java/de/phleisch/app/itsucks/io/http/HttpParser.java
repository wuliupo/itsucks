/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.ApplicationConstants;
import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DataParser;
import de.phleisch.app.itsucks.io.DownloadJob;
import de.phleisch.app.itsucks.io.Metadata;


public class HttpParser extends DataParser implements ApplicationContextAware {
	
	private static final String REGEXP_PREFIX = "exp_"; 
	
	private static Log mLog = LogFactory.getLog(HttpParser.class);
	private static Pattern[] mPatterns = null;
	
	private ApplicationContext mContext;
	private URI mBaseURI;
	private StringBuilder mData;
	
	public HttpParser() {
		super();
	}
	
	@Override
	public boolean supports(Job pJob) {
		if(pJob instanceof DownloadJob) {
			DownloadJob downloadJob = (DownloadJob) pJob;
			
			Metadata metadata = downloadJob.getDataRetriever().getMetadata();
			
			if(metadata instanceof HttpMetadata &&
					((HttpMetadata)metadata).getContentType().startsWith("text/html")) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		
		mData = new StringBuilder();
		initPatterns();
		mBaseURI = mDataRetriever.getUrl().toURI();
		
		//get URL's from header
		String[] location = 
			((HttpMetadata)mDataRetriever.getMetadata()).getHeaderField("Location");
		
		HashSet<URI> urlList = new HashSet<URI>();
		
		if(location != null && location.length > 0) {
			for (int i = 0; i < location.length; i++) {
				URI uri = null;
				try {
					uri = mBaseURI.resolve(location[i]);
				} catch(Exception ex) {
					mLog.warn(ex);
				}
				if(uri != null) {
					//mLog.debug("Add uri: " + uri);
					urlList.add(uri);
				}
			}
			
			addNewJobs(urlList.toArray(new URI[urlList.size()]));
		}
	}
	
	@Override
	public void finish() throws Exception {
		super.finish();

		URI[] uris = extractURLs(mData);
		
		addNewJobs(uris);
		
		mData = null;
	}

	private void addNewJobs(URI[] uris) {
		for (int i = 0; i < uris.length; i++) {
			URI referenceURI = uris[i];
			
			URL url;
			try {
				url = referenceURI.toURL();
			} catch (MalformedURLException ex) {
				mLog.warn("Parsed bad link: " + referenceURI);
				continue;
			}
			
			//hm, maybe a mJob.createChild() is better here...
			DownloadJob job = (DownloadJob) mContext.getBean("DownloadJob");
			job.setUrl(url);
			job.setParent((DownloadJob)this.getJob());
			mJobManager.addJob(job);
		}
	}
	

	@Override
	public void process(byte[] pBuffer, int pBytes) throws Exception {
		
		String convertedChunk = new String(pBuffer, 0, pBytes); //TODO add encoding
		convertedChunk = convertedChunk.replaceAll("[\r\n]", " "); //remove all line breaks
		mData.append(convertedChunk);  
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
	

	private synchronized void initPatterns() {
		if(mPatterns == null) {
			mPatterns = loadPatterns(ApplicationConstants.HTTP_PARSER_CONFIG_FILE);
		}
	}

	public URI[] extractURLs(CharSequence pData) throws IOException {
		
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
					uri = mBaseURI.resolve(match);
				} catch(Exception ex) {
					mLog.warn(ex);
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

	public void setApplicationContext(ApplicationContext pContext) throws BeansException {
		mContext = pContext;
	}


}
