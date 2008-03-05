/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 24.11.2007
 */

package de.phleisch.app.itsucks.job.download;

import java.io.File;

import de.phleisch.app.itsucks.io.Metadata;
import de.phleisch.app.itsucks.io.UrlDataRetriever;
import de.phleisch.app.itsucks.job.Job;

public interface DownloadJob extends Job {

	/**
	 * Gets the recursive depth of the job.
	 * @return
	 */
	public abstract int getDepth();

	/**
	 * @return true if this file should be saved as file.
	 */
	public abstract boolean isSaveToDisk();

	/**
	 * Returns the base save path for saving downloaded files.
	 * 
	 * @return
	 */
	public abstract File getSavePath();

	/**
	 * @return the data retriever used to download from the url.
	 */
	public abstract UrlDataRetriever getDataRetriever();

	/**
	 * Returns the current download progress.
	 * @return
	 */
	public abstract float getProgress();

	/**
	 * Returns the maximum count of retries when an retryable error occurs.
	 * @return
	 */
	public abstract int getMaxRetryCount();

	/**
	 * Returns the actual retry count.
	 * @return
	 */
	public abstract int getRetryCount();

	/**
	 * Returns the count of bytes which are downloaded
	 * @return
	 */
	public abstract long getBytesDownloaded();

	/**
	 * Gets the metadata of the data retriever.
	 * @return
	 */
	public abstract Metadata getMetadata();

}