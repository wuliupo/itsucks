/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 26.02.2007
 */

package de.phleisch.app.itsucks.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This interface specifies an DataRetriever. DataRetriever are used to provided
 * an extended input stream for the processor chain.
 * 
 * @author olli
 */
public interface DataRetriever {

	/**
	 * This type of notification will be send if the progress changes.
	 */
	public final static Integer NOTIFICATION_PROGRESS = 100;
	
	//control retriever
	/**
	 * Advise the Retriever to connect to the data source.
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException;
	
	/**
	 * Returns true if the data source contains any data to be read.
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean isDataAvailable() throws IOException;
	
	/**
	 * Returns an input stream for the retrieved data. 
	 * @return
	 * @throws IOException 
	 */
	public InputStream getDataAsInputStream() throws IOException;
	
	/**
	 * Disconnect from the data source.
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException;
	
	/**
	 * Aborts the current retrieving.
	 */
	public void abort();
	
	/**
	 * Returns the metadata of the data source connection.
	 * 
	 * @return
	 */
	public Metadata getMetadata();
	
	/**
	 * Returns the content length in bytes.
	 * 
	 * @return
	 */
	public long getContentLenght() throws IOException;

	//resume interface
	/**
	 * Skip the given bytes (seek) when reading the data source.
	 * 
	 * @param pBytesToSkip
	 */
	public void setBytesToSkip(long pBytesToSkip);
	
	/**
	 * Returns the skipped bytes.
	 * 
	 * @return
	 */
	public long getBytesSkipped();
	
}