/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 14.10.2007
 */

package de.phleisch.app.itsucks.filter;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.JobParameter;
import de.phleisch.app.itsucks.io.DownloadJob;

/**
 * This filter is able to filter DownloadJobs by the content length of the file. 
 * 
 * @author olli
 *
 */
public class FileSizeFilter 
		extends AbstractJobFilter
		implements JobFilter, Serializable {

	public static final String FILE_SIZE_CONFIG_PARAMETER = "FileSizeConfig";

	private static final long serialVersionUID = 7530902338022529741L;
	
	private FileSizeConfig mConfig = new FileSizeConfig();
	
	private String mMinSizeAsText;
	private String mMaxSizeAsText;
	
	public class FileSizeConfig implements Serializable {
		
		private static final long serialVersionUID = -6074309604039842435L;
		
		private long mMinSize = -1;
		private long mMaxSize = -1;
		private boolean mAcceptWhenLengthNotSet = true;
		
		public long getMinSize() {
			return mMinSize;
		}
		public void setMinSize(long pMinSize) {
			mMinSize = pMinSize;
		}
		public long getMaxSize() {
			return mMaxSize;
		}
		public void setMaxSize(long pMaxSize) {
			mMaxSize = pMaxSize;
		}
		public boolean isAcceptWhenLengthNotSet() {
			return mAcceptWhenLengthNotSet;
		}
		public void setAcceptWhenLengthNotSet(boolean pAcceptWhenLengthNotSet) {
			mAcceptWhenLengthNotSet = pAcceptWhenLengthNotSet;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#filter(de.phleisch.app.itsucks.Job)
	 */
	public Job filter(Job pJob) throws Exception {
		
		pJob.addParameter(new JobParameter(FILE_SIZE_CONFIG_PARAMETER, mConfig));
		
		return pJob;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.filter.JobFilter#supports(de.phleisch.app.itsucks.Job)
	 */
	public boolean supports(Job pJob) {
		return pJob instanceof DownloadJob;
	}

	/**
	 * Returns the lower limit for the file size. 
	 * 
	 * @return
	 */
	public long getMinSize() {
		return mConfig.getMinSize();
	}

	/**
	 * Sets the lower limit for the file size. 
	 * Set to -1 to disable limit.
	 * 
	 * @param pMinSize
	 */
	public void setMinSize(long pMinSize) {
		mConfig.setMinSize(pMinSize);
	}

	/**
	 * Sets the lower limit for the file size. 
	 * Set to -1 to disable limit.
	 * Valid units are KB, MB, GB. 
	 * 
	 * @param pValue
	 */
	public void setMinSizeAsText(String pMinSize) {
		setMinSize(parseTextValue(pMinSize));
		mMinSizeAsText = pMinSize;
	}

	/**
	 * Gets the lower limit for the file size. 
	 * Set to -1 to disable limit.
	 */
	public String getMinSizeAsText() {
		return mMinSizeAsText;
	}
	
	/**
	 * Returns the upper limit for the file size.
	 * 
	 * @return
	 */
	public long getMaxSize() {
		return mConfig.getMaxSize();
	}

	/**
	 * Sets the upper limit for the file size. 
	 * Set to -1 to disable limit.
	 * 
	 * @param pMaxSize
	 */
	public void setMaxSize(long pMaxSize) {
		mConfig.setMaxSize(pMaxSize);
	}

	/**
	 * Sets the upper limit for the file size. 
	 * Set to -1 to disable limit.
	 * Valid units are KB, MB, GB. 
	 * 
	 * @param pValue
	 */
	public void setMaxSizeAsText(String pMaxSize) {
		setMaxSize(parseTextValue(pMaxSize));
		mMaxSizeAsText = pMaxSize;
	}
	
	/**
	 * Gets the upper limit for the file size. 
	 * Set to -1 to disable limit.
	 */
	public String getMaxSizeAsText() {
		return mMaxSizeAsText;
	}
	
	/**
	 * Returns if an file should be accepted when it's length is not known.
	 * 
	 * @return
	 */
	public boolean isAcceptWhenLengthNotSet() {
		return mConfig.isAcceptWhenLengthNotSet();
	}

	/**
	 * Sets if an file should be accepted when it's length is not known.
	 * 
	 * @param pAcceptWhenLengthNotSet
	 */
	public void setAcceptWhenLengthNotSet(boolean pAcceptWhenLengthNotSet) {
		mConfig.setAcceptWhenLengthNotSet(pAcceptWhenLengthNotSet);
	}

	private long parseTextValue(String pMinSize) {
		String regExp = "^([-]?[0-9]{1,})[ ]*(KB|kb|MB|mb|GB|gb|$)$";
		
		Pattern pattern = null;
		try {
			pattern = Pattern.compile(regExp);
		} catch (PatternSyntaxException ex) {
			throw new RuntimeException("Bad regular expression given.", ex);
		}
		
		Matcher matcher = pattern.matcher(pMinSize);
		if(!matcher.matches()) {
			throw new IllegalArgumentException("Bad value given: " + pMinSize);
		}
		
		long value = Long.parseLong(matcher.group(1));
		String unit = matcher.group(2);
		if(unit != null && !"".equals(unit)) {
			
			if(unit.equalsIgnoreCase("KB")) {
				value *= 1024;
			} else if(unit.equalsIgnoreCase("MB")) {
				value *= 1024 * 1024;
			} else if(unit.equalsIgnoreCase("GB")) {
				value *= 1024 * 1024 * 1024;
			}
		}
		
		return value;
	}
	
}
