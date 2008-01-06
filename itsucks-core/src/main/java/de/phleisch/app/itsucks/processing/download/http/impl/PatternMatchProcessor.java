/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.io.http.impl.HttpMetadata;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.DownloadJob;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.processing.AbortProcessingException;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.ProcessingException;
import de.phleisch.app.itsucks.processing.impl.AbstractDataParser;


public class PatternMatchProcessor extends AbstractDataParser implements ApplicationContextAware, DataProcessor {
	
	private static Log mLog = LogFactory.getLog(PatternMatchProcessor.class);
	private static Pattern[] mPatterns = null;
	
	private ApplicationContext mContext;
	private String mEncoding;
	
	public PatternMatchProcessor() {
		super();
	}
	
	@Override
	public boolean supports(Job pJob) {
		if(pJob instanceof UrlDownloadJob) {
			DownloadJob downloadJob = (DownloadJob) pJob;
			
			Metadata metadata = downloadJob.getDataRetriever().getMetadata();
			
			if(metadata instanceof HttpMetadata &&
					((HttpMetadata)metadata).getContentType().startsWith("text/")) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void init() throws ProcessingException {
		super.init();
		
//		if(true) throw new AbortProcessingException("Test abort!");
		
//		initPatterns();
		
		DataRetriever dataRetriever = getProcessorChain().getDataRetriever();
		
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
	public void finish() {
		super.finish();

	}

	
	public DataChunk process(DataChunk pDataChunk) throws ProcessingException {
		
		String convertedChunk;
		if(mEncoding != null) {
			try {
				convertedChunk = new String(pDataChunk.getData(), 0, pDataChunk.getSize(), mEncoding);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Enconding not supported: " + mEncoding, e);
			}
		} else {
			convertedChunk = new String(pDataChunk.getData(), 0, pDataChunk.getSize());
		}
		
//		if(true) throw new AbortProcessingException("Test abort!");
		
		//remove all line breaks
//		convertedChunk = convertedChunk.replaceAll("[\r\n]", " "); 
		
//		//extract the url's
//		URI[] uris = extractURLs(convertedChunk);
//		
//		//add the jobs to the job manager
//		addNewJobs(uris);
		
		return pDataChunk;
	}

//	protected Pattern[] loadPatterns(String propertyName) {
//		
//		mLog.debug("Reading Patterns from file: " + propertyName);
//		
//		URL resource = this.getClass().getClassLoader().getResource(propertyName);
//		if(resource == null) {
//			mLog.error("Cannot load patterns from file: " + propertyName);
//			return new Pattern[0];
//		}
//		
//		Properties patternFile = new Properties();
//		
//		try {
//			patternFile.load(resource.openStream());
//		} catch (IOException e) {
//			mLog.error("Cannot load patterns from file: " + propertyName, e);
//		}
//		
//		ArrayList<Pattern> regExpList = new ArrayList<Pattern>();
//		
//		int offset = 1;
//		while(true) {
//			
//			String regexp = (String) patternFile.get(REGEXP_PREFIX + offset);
//			if(regexp != null) {
//				
//				Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
//				regExpList.add(pattern);
//			} else {
//				break;
//			}
//			
//			offset++;
//		}
//		
//		return regExpList.toArray(new Pattern[regExpList.size()]);
//	}

	protected boolean matchPattern(CharSequence pData, Pattern pPattern) throws IOException {
			Matcher matcher = pPattern.matcher(pData);
			return matcher.matches();
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
		return false;
	}


}
