/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 18.05.2008
 */

package de.phleisch.app.itsucks.io.http.impl;

import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;

public class ConfigurationAuthProvider implements CredentialsProvider {

	List<HttpAuthenticationCredentials> mAuthenticationCredentials;
	
	public ConfigurationAuthProvider(
			List<HttpAuthenticationCredentials> pAuthenticationCredentials) {
		mAuthenticationCredentials = pAuthenticationCredentials;
	}

	public Credentials getCredentials(
            final AuthScheme authscheme, 
            final String host, 
            int port, 
            boolean proxy)
			throws CredentialsNotAvailableException {
		
		if(proxy) {
			//uhoh, this shouldn't happen
			throw new IllegalStateException("Got request for proxy authentication");
		}

		Credentials serverCredentials = null;
		
		for (HttpAuthenticationCredentials credentials : mAuthenticationCredentials) {
			if(credentials.getHost().equalsIgnoreCase(host)) {
				
				serverCredentials = new UsernamePasswordCredentials(
						credentials.getUser(), 
						credentials.getPassword());
				
				break;
			}
		}
		
		if(serverCredentials == null) {
			throw new CredentialsNotAvailableException();
		}
			
		return serverCredentials;
	}
	
}
