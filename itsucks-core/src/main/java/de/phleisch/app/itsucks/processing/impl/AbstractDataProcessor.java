/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 * 
 * $Id$
 */

package de.phleisch.app.itsucks.processing.impl;

import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.processing.DataProcessor;
import de.phleisch.app.itsucks.processing.DataProcessorChain;

/**
 * Basic implementation of an data processor.
 * 
 * @author olli
 *
 */
public abstract class AbstractDataProcessor implements DataProcessor {

	protected DataProcessorChain mChain;

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#supports(de.phleisch.app.itsucks.Job)
	 */
	public abstract boolean supports(Job pJob);
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#init()
	 */
	public void init() throws Exception {}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#finish()
	 */
	public void finish() throws Exception {}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#abort()
	 */
	public void abort() {}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#rollback()
	 */
	public void rollback() {}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#canResume()
	 */
	public boolean canResume() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#resumeAt(long)
	 */
	public void resumeAt(long pByteOffset) {
		throw new IllegalArgumentException();
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#setProcessorChain(de.phleisch.app.itsucks.processing.DataProcessorChain)
	 */
	public void setProcessorChain(DataProcessorChain pChain) {
		mChain = pChain;
	}
	
	public DataProcessorChain getProcessorChain() {
		return mChain;
	}
	
}
