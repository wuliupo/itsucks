/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 05.10.2007
 */

package de.phleisch.app.itsucks.gui.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FieldValidator {

	private List<String> mErrors;
	
	public FieldValidator() {
		mErrors = new ArrayList<String>();
	}
	
	public boolean assertNotNull(Object pValue, String pMessage) {
		boolean result = true;
		
		if(pValue == null) {
			result = false;
		}
		
		if(!result) addError(pMessage);
		return result;
	}

	public boolean assertNotEmpty(String pValue, String pMessage) {
		boolean result = true;
		
		if(pValue == null || pValue.length() < 1) {
			result = false;
		}
		
		if(!result) addError(pMessage);
		return result;
	}
	
	public boolean assertInteger(String pValue, String pMessage) {
		boolean result = true;
		
		if(pValue == null) {
			result = false;
		} else {
			try {
				Integer.parseInt(pValue);
			} catch(NumberFormatException ex) {
				result = false;
			}
		}
		
		if(!result) addError(pMessage);
		return result;
	}
	
	public boolean assertURL(String pValue, String pMessage) {
		boolean result = true;
		
		if(pValue == null) {
			result = false;
		} else {
			try {
				new URL(pValue);
			} catch(MalformedURLException ex) {
				result = false;
			}
		}
		
		if(!result) addError(pMessage);
		return result;
	}
	
	public void addError(String pMessage) {
		mErrors.add(pMessage);
		
	}
	
	public List<String> getErrors() {
		return new ArrayList<String>(mErrors);
	}
}
