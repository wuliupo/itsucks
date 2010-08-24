/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.07.2010
 */

package de.phleisch.app.itsucks.processing.download.http.impl;

import com.google.inject.Provider;

import de.phleisch.app.itsucks.processing.DataProcessor;

public class FilterFileSizeProcessorProvider implements Provider<DataProcessor> {

	@Override
	public DataProcessor get() {
		return new FilterFileSizeProcessor();
	}

}
