/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
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

import de.phleisch.app.itsucks.constants.ApplicationConstants;
import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.io.http.impl.HttpMetadata;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.DownloadJob;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.DataProcessorChain;
import de.phleisch.app.itsucks.processing.impl.AbstractDataParser;


public class HtmlParser extends AbstractDataParser implements ApplicationContextAware, DataProcessor {
	
	private static final String REGEXP_PREFIX = "exp_"; 
	
	private static Log mLog = LogFactory.getLog(HtmlParser.class);
	private static Pattern[] mPatterns = null;
	
	private ApplicationContext mContext;
	private URI mBaseURI;
	//private StringBuilder mData;

	private String mEncoding;
	
	public HtmlParser() {
		super();
	}
	
	@Override
	public boolean supports(Job pJob) {
		if(pJob instanceof UrlDownloadJob) {
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
		
		initPatterns();
		
		DataRetriever dataRetriever = getProcessorChain().getDataRetriever();
		
		mBaseURI = dataRetriever.getUrl().toURI();
		
		HttpMetadata metadata = (HttpMetadata)dataRetriever.getMetadata();
		
		//check if the encoding used in the html page is supported, if not, use the system encoding
		String encoding = metadata.getEncoding();
		if(encoding != null) {
			if(Charset.isSupported(encoding)) {
				mEncoding = encoding;
			} else {
				mLog.warn("Unsupported encoding: " + encoding + ". System encoding used");
				mEncoding = null;
			}
		} else {
			mEncoding = null;
		}
		
	}
	
	@Override
	public void finish() throws Exception {
		super.finish();

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
			
			DownloadJobFactory jobFactory = (DownloadJobFactory) mContext.getBean("JobFactory");
			DataProcessorChain processorChain = getProcessorChain();
			
			UrlDownloadJob job = jobFactory.createDownloadJob();
			
			job.setUrl(url);
			job.setParent((UrlDownloadJob)processorChain.getJob());
			processorChain.getJobManager().addJob(job);
		}
	}
	
	public byte[] process(byte[] pBuffer, int pBytes) throws Exception {
		
		String convertedChunk;
		if(mEncoding != null) {
			convertedChunk = new String(pBuffer, 0, pBytes, mEncoding);
		} else {
			convertedChunk = new String(pBuffer, 0, pBytes);
		}
		
		//remove all line breaks
		convertedChunk = convertedChunk.replaceAll("[\r\n]", " "); 
		
		//extract the url's
		URI[] uris = extractURLs(convertedChunk);
		
		//add the jobs to the job manager
		addNewJobs(uris);
		
		return pBuffer;
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
					match = match.replaceAll(" ", "%20"); //try to fix broken url's
					uri = mBaseURI.resolve(new URI(match));
				} catch(Exception ex) {
					mLog.warn("Resolving of base url failed: " +
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

	public void setApplicationContext(ApplicationContext pContext) throws BeansException {
		mContext = pContext;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#needsDataAsWholeChunk()
	 */
	public boolean needsDataAsWholeChunk() {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#isConsumer()
	 */
	public boolean isConsumer() {
		return true;
	}


}
