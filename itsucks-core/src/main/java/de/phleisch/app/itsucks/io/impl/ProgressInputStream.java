/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.01.2008
 */

package de.phleisch.app.itsucks.io.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;

public class ProgressInputStream extends InputStream {

	protected long mUpdateThreshold = 102400; //100kb
	
	protected InputStream mIn;
	protected long mDataLength;
	protected long mDataRead;
	protected long mDataReadSum;
	
	protected float mProgress;
	
    /**
     * Used to handle the listener list for property change events.
     *
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see #firePropertyChangeListener
     */
    protected PropertyChangeSupport mAccessibleChangeSupport;
	
	public ProgressInputStream(final InputStream pStream, final long pContentLength) {
		mIn = pStream;
		mDataLength = pContentLength;
		mDataRead = 0;
		mDataReadSum = 0;
		
		mAccessibleChangeSupport = new PropertyChangeSupport(this);
	}
	
	@Override
	public int available() throws IOException {
		return mIn.available();
	}

	@Override
	public void close() throws IOException {
		mIn.close();
	}

	@Override
	public synchronized void mark(final int pReadlimit) {
		mIn.mark(pReadlimit);
	}

	@Override
	public boolean markSupported() {
		return mIn.markSupported();
	}

	@Override
	public int read(final byte[] pB, final int pOff, final int pLen) throws IOException {
		int bytes = mIn.read(pB, pOff, pLen);
		
		mDataRead += bytes;
		if(mDataRead > mUpdateThreshold) {
			updateProgress();
		}
		return bytes;
	}

	@Override
	public int read(final byte[] pB) throws IOException {
		int bytes = mIn.read(pB);
		
		mDataRead += bytes;
		if(mDataRead > mUpdateThreshold) {
			updateProgress();
		}
		
		return bytes;
	}

	@Override
	public synchronized void reset() throws IOException {
		mIn.reset();
	}

	@Override
	public long skip(final long pN) throws IOException {
		long bytes = mIn.skip(pN);
		
		mDataRead += bytes;
		if(mDataRead > mUpdateThreshold) {
			updateProgress();
		}
		
		return bytes;
	}

	@Override
	public int read() throws IOException {
		mDataRead ++;
		if(mDataRead > mUpdateThreshold) {
			updateProgress();
		}
		
		return mIn.read();
	}
	
	protected void updateProgress() {
		
		mDataReadSum += mDataRead;
		mDataRead = 0;
		
		if(mDataLength > 0) {
			setProgress((float)mDataReadSum / mDataLength);
		}
	}

	public long getUpdateThreshold() {
		return mUpdateThreshold;
	}

	public void setUpdateThreshold(final long pUpdateThreshold) {
		mUpdateThreshold = pUpdateThreshold;
	}

	public InputStream getWrappedInputStream() {
		return mIn;
	}

	public long getDataRead() {
		return mDataRead + mDataReadSum;
	}

	protected void setProgress(final float pProgress) {
		final float oldValue = mProgress;
		mProgress = pProgress;
		
		//inform listener
		mAccessibleChangeSupport.firePropertyChange(
				"progress", 
				Float.valueOf(oldValue), 
				Float.valueOf(pProgress));
	}
	
	public float getProgress() {
		return mProgress;
	}

	public void addPropertyChangeListener(PropertyChangeListener pListener) {
		mAccessibleChangeSupport.addPropertyChangeListener(pListener);
	}

	public void removePropertyChangeListener(PropertyChangeListener pListener) {
		mAccessibleChangeSupport.removePropertyChangeListener(pListener);
	}
	
}
