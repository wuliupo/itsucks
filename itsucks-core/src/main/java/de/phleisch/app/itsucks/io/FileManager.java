/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 26.02.2007
 */

package de.phleisch.app.itsucks.io;

import java.io.File;
import java.net.URL;

/**
 * Handy utility class to build the target path of an file to be downloaded
 * and remove characters not allowed in the filename.
 * 
 * @author olli
 *
 */
public class FileManager {

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

	/**
	 * The base target path for all urls
	 */
	private File mBasePath;
	
	public FileManager(File pTargetPath) {
		
		if(pTargetPath == null) {
			throw new IllegalArgumentException("No base path set!");
		}
		
		mBasePath = pTargetPath;
	}

	/**
	 * Builds a file path for the url to where the data can be saved. 
	 * @param mUrl
	 * @return
	 */
	public File buildSavePath(final URL mUrl) {
		
		String hostname_part = mUrl.getHost();
		if(mUrl.getPort() != -1) {
			hostname_part += ":" + mUrl.getPort();
		}
		
		String full_path = mUrl.getPath();
		if(mUrl.getQuery() != null) full_path += "?" + mUrl.getQuery();

		String path = mUrl.getPath();
		String filename = "index.html";
		if(!full_path.endsWith("/") && full_path.lastIndexOf('/') != -1) {
		
			path = 
				full_path.substring(0, full_path.lastIndexOf('/'));
			filename = full_path.substring(mUrl.getPath().lastIndexOf('/') + 1);
		}
		
		//replace all invalid characters with a white list
		path = filterString(path, ALLOWED_DIRECTORY_CHARS);
		
		File local_path = new File(
			mBasePath + File.separator 
			+ hostname_part + File.separator
			+ path + File.separator);

		//replace all invalid characters with a white list
		filename = filterString(filename, ALLOWED_FILENAME_CHARS);
		
		return new File(local_path + File.separator + filename);
	}

	/**
	 * Replaces every char which is not allowed with an '_'.
	 * @param filename
	 * @param pAllowedFilenameChars
	 * @return
	 */
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
}
