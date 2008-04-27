/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 26.04.2008
 */

package de.phleisch.app.itsucks.gui.job.ifc;

import java.util.List;

import de.phleisch.app.itsucks.persistence.SerializableJobPackage;

public interface EditJobCapable {

	public void loadJobPackage(SerializableJobPackage pJobPackage);
	
	public void saveJobPackage(SerializableJobPackage pJobPackage);
	
	public List<String> validateFields();
}
