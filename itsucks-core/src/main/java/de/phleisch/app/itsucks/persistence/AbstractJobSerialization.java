/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.06.2007
 */

package de.phleisch.app.itsucks.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.phleisch.app.itsucks.JobFactory;

public abstract class AbstractJobSerialization implements JobSerialization {

	protected JobFactory mJobFactory;
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#serialize(de.phleisch.app.itsucks.persistence.SerializableJobList, java.io.File)
	 */
	public void serialize(SerializableJobList pJobList, File pTargetFile) throws IOException {
		
		FileOutputStream output = new FileOutputStream(pTargetFile);
		BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
		
		serialize(pJobList, bufferedOutput);
	}
	
	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#deserialize(java.io.File)
	 */
	public SerializableJobList deserialize(File pTargetFile) throws IOException, ClassNotFoundException {

		FileInputStream input = new FileInputStream(pTargetFile);
		BufferedInputStream bufferedInput = new BufferedInputStream(input);

		SerializableJobList list = deserialize(bufferedInput);
		
		return list;
	}	

	/* (non-Javadoc)
	 * @see de.phleisch.app.itsucks.persistence.JobSerialization#setJobFactory(de.phleisch.app.itsucks.JobFactory)
	 */
	public void setJobFactory(JobFactory pJobFactory) {
		mJobFactory = pJobFactory;
	}

}
