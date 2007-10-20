/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.10.2007
 */

package de.phleisch.app.itsucks.processing;

import de.phleisch.app.itsucks.Job;
import de.phleisch.app.itsucks.io.DownloadJob;

public class FilterBadCharsProcessor extends AbstractDataProcessor {

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#supports(de.phleisch.app.itsucks.Job)
	 */
	@Override
	public boolean supports(Job pJob) {
		return (pJob instanceof DownloadJob);
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#isConsumer()
	 */
	public boolean isConsumer() {
		return true;
	}

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#needsDataAsWholeChunk()
	 */
	public boolean needsDataAsWholeChunk() {
		return false;
	}

	public byte[] process(byte[] pBuffer, int pBytes) throws Exception {

		for (byte b : pBuffer) {
			if(b > 127) {
				throw new Exception("Bad char found, aborting download");
			}
		}
		
		return pBuffer;
	}

}
