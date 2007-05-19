/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.io.AbstractDataRetriever;
import de.phleisch.app.itsucks.io.DataProcessor;

@Deprecated
public class HttpRetriever extends AbstractDataRetriever {

	private static Log mLog = LogFactory.getLog(HttpRetriever.class);
	
	private URLConnection mConnection;
	private HttpMetadata mMetadata;
	
	public HttpRetriever() {
		super();
	}
	
	public void connect() throws IOException {
		 mConnection = mUrl.openConnection();
		 mLog.debug("Connected to: " + mConnection.getURL());
		 
		//build metadata
	
		mMetadata = new HttpMetadata();
		mMetadata.setContentType(mConnection.getContentType());
		mMetadata.setStatusCode(0);
	}
	

	public boolean isDataAvailable() throws Exception {
		return true;
	}
	
	public void retrieve() throws Exception {
		try {
			download();
		} catch (Exception e) {
			mLog.error("Error downloading url: " + mUrl, e);
			throw e;
		} finally {
			disconnect();
		}
	}
	
	public void disconnect() {
		if(mConnection != null) {
			try {
				mConnection.getInputStream().close();
				//mConnection.getOutputStream().close();
			} catch (IOException e) {
				mLog.error(e);
			}
			mConnection = null;
		}
		
	}
	
	private void download() throws Exception {
		
		InputStream input = mConnection.getInputStream(); 

		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			DataProcessor processor = it.next();
			processor.init();
		}
		
		//100k buffer
		byte buffer[] = new byte[100000];
		int bytesRead;
		
		while(input.available() > 0) {
			bytesRead = input.read(buffer);
			
			for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
				DataProcessor processor = it.next();
				processor.process(buffer, bytesRead);
			}
		}
		
		for (Iterator<DataProcessor> it = mDataProcessors.iterator(); it.hasNext();) {
			DataProcessor processor = it.next();
			
			processor.finish();
		}
		
	}
	
	public HttpMetadata getMetadata() {
		return mMetadata;
	}

	public long getBytesRetrieved() {
		return -1;
	}

	public float getProgress() {
		return -1;
	}

	public void abort() {
	}

	public void setBytesToSkip(long pBytesToSkip) {
		throw new IllegalStateException("Not implemented.");
	}

	public long getBytesSkipped() {
		throw new IllegalStateException("Not implemented.");
	}

	public int getResultCode() {
		return RESULT_RETRIEVAL_OK;
	}
	
}
