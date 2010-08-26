/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 24.08.2010
 */

package de.phleisch.app.itsucks.plugin;

public interface Plugin {

	public String getName();
	public String getVersion();
	
	public void configure(PluginConfigurator pConfiguration);
	
}
