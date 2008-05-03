/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 29.04.2008
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GZIPAwareGetMethod extends GetMethod {

	private static Log mLog = LogFactory.getLog(GZIPAwareGetMethod.class);
	
    public GZIPAwareGetMethod() {
        super();
    }

    public GZIPAwareGetMethod(String uri) {
        super(uri);
    }

    @Override
	protected void processResponseBody(HttpState pState, HttpConnection pConn) {
		super.processResponseBody(pState, pConn);

        Header contentEncodingHeader = getResponseHeader("Content-Encoding");

        if (contentEncodingHeader != null && contentEncodingHeader.getValue().equalsIgnoreCase("gzip")) {
        	
        	mLog.trace("Wrap gunzip around http stream.");
        	
            try {
				setResponseStream(new GZIPInputStream(getResponseStream()));
			} catch (IOException e) {
				throw new RuntimeException("Error wrapping input stream with gunzip stream", e);
			}
        }
		
	}

}
