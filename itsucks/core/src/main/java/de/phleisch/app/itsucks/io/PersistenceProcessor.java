/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.phleisch.app.itsucks.Job;


public class PersistenceProcessor extends DataProcessor {

	private static Log mLog = LogFactory.getLog(PersistenceProcessor.class);
	
	private File mFile;
	private OutputStream mOut;
	
	private static final String ALLOWED_COMMON_CHARS = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
		"abcdefghijklmnopqrstuvwxyz" +
		"0123456789" +
		"-+_,;#'()&%$!=~ ";
	
	/**
	 * Whitelist for the allowed characters in an filename
	 */
	private static final String ALLOWED_FILENAME_CHARS = 
			ALLOWED_COMMON_CHARS +
			".";
	
	/**
	 * Whitelist for the allowed characters in an directory
	 */
	private static final String ALLOWED_DIRECTORY_CHARS = 
			ALLOWED_COMMON_CHARS +
			"/";
	
	public PersistenceProcessor() {
		super();
	}
	
	@Override
	public boolean supports(Job pJob) {
		if(pJob instanceof DownloadJob) {
			DownloadJob downloadJob = (DownloadJob) pJob;
			return downloadJob.isSaveToFile();
		}
		
		return false;
	}
	
	public void init() throws Exception {
		
		URL url = mDataRetriever.getUrl();
		
		DownloadJob downloadJob = (DownloadJob) mJob;
		
		File target_path = downloadJob.getSavePath();
		if(target_path == null) throw new IOException("No target path set in job");
		
		String hostname_part = url.getHost();
		if(url.getPort() != -1) {
			hostname_part += ":" + url.getPort();
		}
		
		String full_path = url.getPath();
		if(url.getQuery() != null) full_path += "?" + url.getQuery();

		String path = url.getPath();
		String filename = "index.html";
		if(!full_path.endsWith("/") && full_path.lastIndexOf('/') != -1) {
		
			path = 
				full_path.substring(0, full_path.lastIndexOf('/'));
			filename = full_path.substring(url.getPath().lastIndexOf('/') + 1);
		}
		
		//replace all invalid characters with a white list
		path = filterString(path, ALLOWED_DIRECTORY_CHARS);
		
		File local_path = new File(
			target_path + File.separator 
			+ hostname_part + File.separator
			+ path + File.separator);
		mLog.debug("creating path: " + local_path);
		
		if(!local_path.exists() && !local_path.mkdirs()) {
			throw new IOException("Cannot create folder(s): " + local_path);
		}

		//replace all invalid characters with a white list
		filename = filterString(filename, ALLOWED_FILENAME_CHARS);
		
		mFile = new File(local_path + File.separator + filename);
		mLog.debug("saving file: " + mFile);
		mOut = new BufferedOutputStream(new FileOutputStream(mFile));
	}

	private String filterString(String filename, String pAllowedFilenameChars) {
		
		//replace all invalid characters with a white list
		StringBuffer filteredString = new StringBuffer();
		for (int i = 0; i < filename.length(); i++) {
			
			char ch = filename.charAt(i);
			if(pAllowedFilenameChars.indexOf(ch) != -1) {
				filteredString.append(ch);
			} else {
				filteredString.append('_');
			}
		}
		
		return filteredString.toString();
	}

	@Override
	public void process(byte[] pBuffer, int pBytes) throws Exception {
		mOut.write(pBuffer, 0, pBytes);
	}

	@Override
	public void finish() throws Exception {
		super.finish();
		mOut.close();
	}


}
