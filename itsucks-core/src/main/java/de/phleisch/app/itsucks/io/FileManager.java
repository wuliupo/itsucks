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
		"-+_.,;#'()&%$!=~ ";
	
	/**
	 * Whitelist for the allowed characters in an filename
	 */
	private static final String ALLOWED_FILENAME_CHARS = 
			ALLOWED_COMMON_CHARS +
			"";
	
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
	public File buildSavePath(final URL mUrl, final String pFilename) {
		
		String hostname_part = mUrl.getHost();
		if(mUrl.getPort() != -1) {
			hostname_part += ":" + mUrl.getPort();
		}
		
		String path = mUrl.getPath();
		if(!path.endsWith("/") && path.lastIndexOf('/') != -1) {
			path = path.substring(0, path.lastIndexOf('/'));
		}
		
		//replace all invalid characters with a white list
		path = filterString(path, ALLOWED_DIRECTORY_CHARS);
		
		File local_path = new File(
			mBasePath + File.separator 
			+ hostname_part + File.separator
			+ path + File.separator);

		//replace all invalid characters with a white list
		String filename = filterString(pFilename, ALLOWED_FILENAME_CHARS);
		
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
		
		String result = filteredString.toString(); 
		
		// Set an '_' at the beginning when an path starts with an dot. 
		// This is for security reasons.
		if(result.startsWith(".")) {
			result = '_' + result;
		}
		
		return result;
	}
}
