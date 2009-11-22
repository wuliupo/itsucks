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
import java.io.SequenceInputStream;

import de.phleisch.app.itsucks.io.DataRetriever;

public class SequenceRetriever extends FilterDataRetriever {
	
	protected DataRetriever mFirst;
	protected DataRetriever mSecond;
	
	private InputStream mIn;
	protected long mBytesToSkip;

	public SequenceRetriever(DataRetriever pPrimaryRetriever, DataRetriever pFirst, DataRetriever pSecond) {
		mDataRetriever = pPrimaryRetriever;
		mFirst = pFirst;
		mSecond = pSecond;
	}

	public InputStream getDataAsInputStream() throws IOException {
		
		if(mIn == null) {
			mIn = new SequenceInputStream(
					mFirst.getDataAsInputStream(), 
					mSecond.getDataAsInputStream());
			
			if(mBytesToSkip > 0) {
				mIn.skip(mBytesToSkip);
			}
			
		}
		return mIn;
	}
	
	@Override
	public void connect() throws IOException {
		mFirst.connect();
		mSecond.connect();
	}
	
	@Override
	public boolean isConnected() throws IOException {
		return mDataRetriever.isConnected();
	}	

	@Override
	public void disconnect() throws IOException {
		mFirst.disconnect();
		mSecond.disconnect(); //hm, not optimal, when an exception is thrown, the second is not disconnected
	}
	
	@Override
	public void setBytesToSkip(long pBytesToSkip) {
		mBytesToSkip = pBytesToSkip;
	}
	
	@Override
	public long getBytesSkipped() {
		return mBytesToSkip;
	}

	@Override
	public long getContentLenght() throws IOException {
		return mFirst.getContentLenght() + mSecond.getContentLenght();
	}

	@Override
	public boolean isDataAvailable() throws IOException {
		return mFirst.isDataAvailable() | mSecond.isDataAvailable();
	}


}
