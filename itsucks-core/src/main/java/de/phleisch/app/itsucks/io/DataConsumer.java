/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.11.2007
 */

package de.phleisch.app.itsucks.io;

public interface DataConsumer {

	/**
	 * Processes the given data chunk.
	 * 
	 * @param pBuffer
	 * @param pBytes
	 * @throws Exception
	 */
	public abstract void process(byte[] pBuffer, int pBytes) throws Exception;

	/**
	 * Returns if the consumer supports
	 * resuming.
	 * 
	 * @return
	 */
	public abstract boolean canResume();
	
}
