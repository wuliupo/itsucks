/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 07.02.2008
 */

package de.phleisch.app.itsucks.job.download.impl;

import java.io.File;
import java.net.URL;
import java.util.List;

import de.phleisch.app.itsucks.context.Context;
import de.phleisch.app.itsucks.io.ResumeUrlDataRetriever;
import de.phleisch.app.itsucks.io.UrlDataRetriever;
import de.phleisch.app.itsucks.job.JobParameter;

public interface DataRetrieverFactory {

//	public DataRetriever createDataRetriever(String pProtocol, Context pContext);

	public UrlDataRetriever createDataRetriever(URL pUrl, Context pGroupContext,
			List<JobParameter> pParameterList);
	
	public ResumeUrlDataRetriever createResumeDataRetriever(UrlDataRetriever pDataRetriever, File pFile);
}
