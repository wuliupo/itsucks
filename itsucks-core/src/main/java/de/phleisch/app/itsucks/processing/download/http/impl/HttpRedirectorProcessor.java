/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 11.06.2007
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;

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
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessorChain;
import de.phleisch.app.itsucks.processing.ProcessingException;
import de.phleisch.app.itsucks.processing.impl.AbstractDataProcessor;

/**
 * This data processor analyzes the http request and searches
 * for any 'Location' header fields.
 * 
 * @author olli
 *
 */
public class HttpRedirectorProcessor extends AbstractDataProcessor implements ApplicationContextAware {

	private static Log mLog = LogFactory.getLog(HttpRedirectorProcessor.class);
	
	private ApplicationContext mContext;
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#supports(de.phleisch.app.itsucks.Job)
	 */
	@Override
	public boolean supports(Job pJob) {
		
		if(pJob instanceof UrlDownloadJob) {
			DownloadJob downloadJob = (DownloadJob) pJob;
			
			Metadata metadata = downloadJob.getDataRetriever().getMetadata();
			
			if(metadata instanceof HttpMetadata) {
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#needsDataAsWholeChunk()
	 */
	public boolean needsDataAsWholeChunk() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#init()
	 */
	@Override
	public void init() throws ProcessingException {
		super.init();
		
		DataRetriever dataRetriever = getProcessorChain().getDataRetriever();
		HttpMetadata metadata = (HttpMetadata)dataRetriever.getMetadata();
		
		URI baseURI;
		try {
			baseURI = dataRetriever.getUrl().toURI();
		} catch (URISyntaxException e) {
			throw new ProcessingException("Error converting URL to URI: " + dataRetriever.getUrl(), e);
		}
		
		//get URL's from header
		String[] location = metadata.getResponseHeaderField("Location");
		
		HashSet<URI> urlList = new HashSet<URI>();
		
		if(location != null && location.length > 0) {
			for (int i = 0; i < location.length; i++) {
				URI uri = null;
				try {
					uri = baseURI.resolve(location[i]);
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

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#process(byte[], int)
	 */
	public DataChunk process(DataChunk pDataChunk) {
		return pDataChunk;
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
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext pContext) throws BeansException {
		mContext = pContext;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#isConsumer()
	 */
	public boolean isConsumer() {
		return false;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.impl.AbstractDataProcessor#canResume()
	 */
	@Override
	public boolean canResume() {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.impl.AbstractDataProcessor#resumeAt(long)
	 */
	@Override
	public void resumeAt(long pByteOffset) {
	}
	
}
