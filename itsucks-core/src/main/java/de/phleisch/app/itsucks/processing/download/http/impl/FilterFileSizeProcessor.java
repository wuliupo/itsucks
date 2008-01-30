/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 20.10.2007
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import de.phleisch.app.itsucks.filter.download.impl.FileSizeFilter;
import de.phleisch.app.itsucks.filter.download.impl.FileSizeFilter.FileSizeConfig;
import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.io.http.impl.HttpMetadata;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessorInfo;
import de.phleisch.app.itsucks.processing.impl.AbstractDataProcessor;

public class FilterFileSizeProcessor extends AbstractDataProcessor {

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#supports(de.phleisch.app.itsucks.Job)
	 */
	@Override
	public boolean supports(Job pJob) {
		
		if(!(pJob instanceof UrlDownloadJob) 
				|| pJob.getParameter(FileSizeFilter.FILE_SIZE_CONFIG_PARAMETER) == null) {
			return false;
		}

		FileSizeConfig fileSizeConfig = (FileSizeConfig)
			pJob.getParameter(FileSizeFilter.FILE_SIZE_CONFIG_PARAMETER).getValue();
		
		UrlDownloadJob downloadJob = (UrlDownloadJob) pJob;
		if(downloadJob.isSaveToDisk()) {
			
			Metadata metadata = downloadJob.getDataRetriever().getMetadata();
			long contentLength = 0;
			
			if(metadata instanceof HttpMetadata) {
				HttpMetadata httpMetadata = (HttpMetadata) metadata;
				contentLength = httpMetadata.getContentLength();
			}
			
			downloadJob.setSaveToDisk(checkContentLength(contentLength, fileSizeConfig));
		}

		return true;
	}

	private boolean checkContentLength(long pContentLength, 
			FileSizeConfig pFileSizeConfig) {
		
		if(pContentLength <= 0) {
			return pFileSizeConfig.isAcceptWhenLengthNotSet();
		}
		
		boolean accept = true;
		if(accept && pFileSizeConfig.getMinSize() > -1) {
			accept = pContentLength >= pFileSizeConfig.getMinSize();
		}
		
		if(accept && pFileSizeConfig.getMaxSize() > -1) {
			accept = pContentLength <= pFileSizeConfig.getMaxSize();
		}
		
		return accept;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#process(byte[], int)
	 */
	public DataChunk process(DataChunk pDataChunk) {
		return pDataChunk;
	}
	
	public void resumeAt(long pByteOffset) {
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#getInfo()
	 */
	public DataProcessorInfo getInfo() {
		
		return new DataProcessorInfo(
				DataProcessorInfo.ResumeSupport.RESUME_SUPPORTED,
				DataProcessorInfo.ProcessorType.FILTER,
				DataProcessorInfo.StreamingSupport.STREAMING_SUPPORTED
		);
	}
	
}
