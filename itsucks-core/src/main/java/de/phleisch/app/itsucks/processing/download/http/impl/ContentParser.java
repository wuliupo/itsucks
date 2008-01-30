/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.filter.download.impl.ContentFilter;
import de.phleisch.app.itsucks.filter.download.impl.ContentFilter.ContentFilterConfig;
import de.phleisch.app.itsucks.filter.download.impl.ContentFilter.ContentFilterConfig.Action;
import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.io.http.impl.HttpMetadata;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.JobParameter;
import de.phleisch.app.itsucks.job.download.DownloadJob;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.processing.AbortProcessingException;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.DataProcessorInfo;
import de.phleisch.app.itsucks.processing.ProcessingException;
import de.phleisch.app.itsucks.processing.impl.AbstractDataParser;


public class ContentParser extends AbstractDataParser implements DataProcessor {
	
	private static Log mLog = LogFactory.getLog(ContentParser.class);
	
	private List<ContentFilterConfig> mConfigList;
	private String mEncoding;
	
	public ContentParser() {
		super();
	}
	
	@Override
	public boolean supports(Job pJob) {
		
		if(pJob.getParameter(ContentFilter.CONTENT_FILTER_CONFIG_LIST_PARAMETER) != null
				&& pJob instanceof UrlDownloadJob) {
			
			DownloadJob downloadJob = (DownloadJob) pJob;
			
			Metadata metadata = downloadJob.getDataRetriever().getMetadata();
			
			if(metadata instanceof HttpMetadata &&
					((HttpMetadata)metadata).getContentType().startsWith("text/")) {
				return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init() throws ProcessingException {
		super.init();

		//load content filter list
		DownloadJob job = (DownloadJob) this.getProcessorChain().getJob();
		JobParameter parameter = job.getParameter(ContentFilter.CONTENT_FILTER_CONFIG_LIST_PARAMETER);
		mConfigList = (List<ContentFilterConfig>) parameter.getValue();

		DataRetriever dataRetriever = job.getDataRetriever();
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
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.impl.AbstractDataProcessor#finish()
	 */
	@Override
	public void finish() {
		super.finish();

	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#process(de.phleisch.app.itsucks.processing.DataChunk)
	 */
	public DataChunk process(DataChunk pDataChunk) throws ProcessingException {
		
		//abort immediately if no pattern is set
		if(mConfigList.size() == 0) {
			return pDataChunk;
		}
		
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
		
		//remove all line breaks
		convertedChunk = convertedChunk.replaceAll("[\r\n]", " "); 
		
		for (ContentFilterConfig config : mConfigList) {
			Matcher matcher = config.getPattern().matcher(convertedChunk);
			
			if(matcher.matches()) {
				executeAction(config.getMatchAction());
			} else {
				executeAction(config.getNoMatchAction());
			}
		}
		
		return pDataChunk;
	}

	private void executeAction(Action pAction) throws AbortProcessingException {
		
		if(pAction.equals(Action.REJECT)) {
			throw new AbortProcessingException("Processing stopped");
		}
		
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#resumeAt(long)
	 */
	public void resumeAt(long pByteOffset) {
		throw new IllegalArgumentException("Resume not supported.");
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#getInfo()
	 */
	public DataProcessorInfo getInfo() {
		
		return new DataProcessorInfo(
				DataProcessorInfo.ResumeSupport.NO_RESUME_SUPPORTED,
				DataProcessorInfo.ProcessorType.FILTER,
				DataProcessorInfo.StreamingSupport.NEED_DATA_AS_WHOLE_CHUNK
		);
	}

}
