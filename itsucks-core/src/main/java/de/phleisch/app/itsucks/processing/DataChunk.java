/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 05.01.2008
 */

package de.phleisch.app.itsucks.processing;

public class DataChunk {

	private byte[] mData;
	private int mSize;
	private boolean mComplete;
	
	public DataChunk(byte[] pData, int pSize, boolean pComplete) {
		mData = pData;
		mSize = pSize;
		mComplete = pComplete;
	}
	
	public byte[] getData() {
		return mData;
	}
	
	public int getSize() {
		return mSize;
	}	
	
	public boolean isComplete() {
		return mComplete;
	}
}
