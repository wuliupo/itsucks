/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 30.01.2008
 */

package de.phleisch.app.itsucks.processing;

public class DataProcessorInfo {

	public static enum ResumeSupport {
		RESUME_NOT_SUPPORTED,
		RESUME_SUPPORTED,
	}
	
	public static enum ProcessorType {
		CONSUMER,
		FILTER,
	}
	
	public static enum StreamingSupport {
		STREAMING_SUPPORTED,
		DATA_AS_WHOLE_CHUNK_NEEDED,
	}
	
	protected ResumeSupport mResumeSupport;
	protected ProcessorType mProcessorType;
	protected StreamingSupport mStreamingSupport;

	public DataProcessorInfo(
			ResumeSupport pResumeSupport,
			ProcessorType pProcessorType,
			StreamingSupport pStreamingSupport) {
		
		mResumeSupport = pResumeSupport;
		mProcessorType = pProcessorType;
		mStreamingSupport = pStreamingSupport;
	}
	
	/**
	 * Supports this data processor resuming?
	 */
	public ResumeSupport getResumeSupport() {
		return mResumeSupport;
	}
	
	/**
	 * 'Consumes' (saves, parses etc.) this processor to the data or is it
	 * only an filter?
	 * If an chain contains no consumer, the data is not processed.
	 */
	public ProcessorType getProcessorType() {
		return mProcessorType;
	}
	
	/**
	 * Asks the processor if it needs the data from the data retriever
	 * in one chunk and not in multiple pieces.
	 * 
	 * @return
	 */
	public StreamingSupport getStreamingSupport() {
		return mStreamingSupport;
	}
	
}
