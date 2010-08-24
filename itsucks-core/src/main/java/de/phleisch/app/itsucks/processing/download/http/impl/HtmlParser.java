/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;

import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.io.UrlDataRetriever;
import de.phleisch.app.itsucks.io.http.impl.HttpMetadata;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.DownloadJob;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.DataProcessorChain;
import de.phleisch.app.itsucks.processing.DataProcessorInfo;
import de.phleisch.app.itsucks.processing.ProcessingException;
import de.phleisch.app.itsucks.processing.impl.AbstractDataParser;


public class HtmlParser extends AbstractDataParser implements DataProcessor {
	
	private static Log mLog = LogFactory.getLog(HtmlParser.class);
	
	private DownloadJobFactory mDownloadJobFactory;
	private URI mBaseURI;
	private String mEncoding;
	private UrlExtractor mUrlExtractor;
	
	public HtmlParser() {
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
		
		UrlDownloadJob job = (UrlDownloadJob) getProcessorChain().getJob();
		UrlDataRetriever dataRetriever = job.getDataRetriever();
		
		try {
			mBaseURI = dataRetriever.getUrl().toURI();
		} catch (URISyntaxException e) {
			throw new ProcessingException("Error converting URL to URI: " + dataRetriever.getUrl(), e);
		}
		
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
		
		mUrlExtractor = new UrlExtractor(mBaseURI);
	}
	
	@Override
	public void finish() {
		super.finish();

	}

	private void addNewJobs(URI[] uris) {
		for (int i = 0; i < uris.length; i++) {
			URI referenceURI = uris[i];
			
			URL url;
			try {
				url = referenceURI.toURL();
			} catch (MalformedURLException ex) {
				mLog.warn("Parsed bad link: " + referenceURI + " BaseURI: " + mBaseURI);
				continue;
			}
			
			DataProcessorChain processorChain = getProcessorChain();
			
			UrlDownloadJob job = mDownloadJobFactory.createDownloadJob();
			
			job.setUrl(url);
			job.setParent((UrlDownloadJob)processorChain.getJob());
			processorChain.getJobManager().addJob(job);
		}
	}
	
	public DataChunk process(DataChunk pDataChunk) {
		
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
		
		//extract the url's
		URI[] uris = mUrlExtractor.extractURLs(convertedChunk);
		
		//add the jobs to the job manager
		addNewJobs(uris);
		
		return pDataChunk;
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
				DataProcessorInfo.ResumeSupport.RESUME_NOT_SUPPORTED,
				DataProcessorInfo.ProcessorType.CONSUMER,
				DataProcessorInfo.StreamingSupport.DATA_AS_WHOLE_CHUNK_NEEDED
		);
	}

	@Inject
	public void setDownloadJobFactory(DownloadJobFactory pDownloadJobFactory) {
		mDownloadJobFactory = pDownloadJobFactory;
	}

}
