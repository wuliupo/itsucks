/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.05.2007
 */

package de.phleisch.app.itsucks.job;

import java.io.Serializable;

/**
 * The JobParameter is a generic parameter which can be given to an job.
 * Also filter can add specific JobParameter when filtering jobs and a condition is met.
 * 
 * If more attributes are needed, extend from this class.
 * 
 * @author olli
 *
 */
public class JobParameter implements Serializable {

	private static final long serialVersionUID = -6335474374019021707L;
	
	private String mKey;
	private Object mValue;

	public JobParameter(String pKey, Object pValue) {
		mKey = pKey;
		mValue = pValue;
	}
	
	public String getKey() {
		return mKey;
	}
	
	public void setKey(String pKey) {
		mKey = pKey;
	}
	
	public Object getValue() {
		return mValue;
	}
	
	public void setValue(Object pValue) {
		mValue = pValue;
	}
	
	
	
}
