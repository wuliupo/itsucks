/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 15.06.2007
 */

package de.phleisch.app.itsucks.persistence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.phleisch.app.itsucks.JobFactory;

public interface JobSerialization {

	public abstract void serialize(SerializableJobList pJobList,
			File pTargetFile) throws IOException;

	public abstract SerializableJobList deserialize(File pTargetFile)
			throws IOException, ClassNotFoundException;

	public abstract SerializableJobList deserialize(InputStream pInputStream)
			throws IOException, ClassNotFoundException;

	public abstract void setJobFactory(JobFactory pJobFactory);

}