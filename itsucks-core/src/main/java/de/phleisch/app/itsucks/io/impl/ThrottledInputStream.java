/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.01.2008
 */

package de.phleisch.app.itsucks.io.impl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ThrottledInputStream extends FilterInputStream {

	private final int mBytesPerSecond;

    public ThrottledInputStream(InputStream in, int pBytesPerSecond) {
        super(in);
        mBytesPerSecond = pBytesPerSecond;
    }

    public int read() throws IOException {
    	throw new UnsupportedOperationException();
    	
    	//pause(1,0);
        //return in.read();
    }

    // Also handles read(byte[])
    public int read(byte[] b, int off, int len) throws IOException {
    	
    	long startTime = System.currentTimeMillis();
    	int bytesRead = in.read(b, off, len);
    	
    	if(bytesRead > 0) {
    		pause(bytesRead, System.currentTimeMillis() - startTime);
    	}
    	
    	return bytesRead;
    }

    protected void pause(final int bytes, final long pTimeToSkip){
    	final long sleepTime = (bytes*1000)/mBytesPerSecond - pTimeToSkip;

    	//sleep under 5ms is too small
    	if(sleepTime <= 5) {
    		return;
    	}
    	
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }
    
}
