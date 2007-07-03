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
import java.io.OutputStream;

import de.phleisch.app.itsucks.JobFactory;

/**
 * Interface for the job serialization.
 * This interface provides functionality to serialize and deserialize multiple jobs.
 * 
 * After an job is deserialized, the job factory is used to reinject the dependencies.
 * 
 * @author olli
 *
 */
public interface JobSerialization {

	/**
	 * Serializes the given job list to the target file.
	 * 
	 * @param pJobList
	 * @param pTargetFile
	 * @throws IOException
	 * @throws Exception 
	 */
	public abstract void serialize(SerializableJobList pJobList,
			File pTargetFile) throws Exception;

	/**
	 * Serializes the given job list to the given output stream.
	 * The list is only written to the stream, the stream will not be closed.
	 * 
	 * @param pJobList
	 * @param pOutputStream
	 * @throws IOException
	 * @throws Exception 
	 */
	public void serialize(SerializableJobList pJobList, OutputStream pOutputStream) 
			throws Exception;
	
	/**
	 * Deserializes a job list from the given file.
	 * 
	 * @param pTargetFile
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public abstract SerializableJobList deserialize(File pTargetFile)
			throws Exception;

	/**
	 * Deserializes a job list from the given input stream.
	 * 
	 * @param pInputStream
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public abstract SerializableJobList deserialize(InputStream pInputStream)
			throws Exception;

	/**
	 * Sets the factory to reinject the dependencies back to the deserialzed
	 * jobs.
	 * 
	 * @param pJobFactory
	 */
	public abstract void setJobFactory(JobFactory pJobFactory);

}