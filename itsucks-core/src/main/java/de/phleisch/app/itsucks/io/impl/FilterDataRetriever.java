/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 17.11.2009
 */

package de.phleisch.app.itsucks.io.impl;

import java.io.IOException;
import java.io.InputStream;

import de.phleisch.app.itsucks.io.DataRetriever;
import de.phleisch.app.itsucks.io.Metadata;

public abstract class FilterDataRetriever implements DataRetriever {

	protected volatile DataRetriever mDataRetriever;

	public void abort() {
		mDataRetriever.abort();
	}

	public void connect() throws IOException {
		mDataRetriever.connect();
	}

	public void disconnect() throws IOException {
		mDataRetriever.disconnect();
	}

	public long getBytesSkipped() {
		return mDataRetriever.getBytesSkipped();
	}

	public long getContentLenght() throws IOException {
		return mDataRetriever.getContentLenght();
	}

	public InputStream getDataAsInputStream() throws IOException {
		return mDataRetriever.getDataAsInputStream();
	}

	public Metadata getMetadata() {
		return mDataRetriever.getMetadata();
	}

	public boolean isDataAvailable() throws IOException {
		return mDataRetriever.isDataAvailable();
	}

	public void setBytesToSkip(long pBytesToSkip) {
		mDataRetriever.setBytesToSkip(pBytesToSkip);
	}

}
