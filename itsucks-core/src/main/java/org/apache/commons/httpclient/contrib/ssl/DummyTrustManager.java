/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 22.11.2009
 */

package org.apache.commons.httpclient.contrib.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class DummyTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] pChain, String pAuthType)
			throws CertificateException {
	}

	@Override
	public void checkServerTrusted(X509Certificate[] pChain, String pAuthType)
			throws CertificateException {
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[]{};
	}

}
