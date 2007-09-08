/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 08.09.2007
 */

package de.phleisch.app.itsucks.gui.util;

import java.awt.Component;
import java.awt.Container;

public class SwingUtils {

	public static void setContainerAndChildrenEnabled(
			Container pContainer, boolean pEnabled) {
		
		pContainer.setEnabled(pEnabled);
		
		Component[] components = pContainer.getComponents();
		for (Component component : components) {
			
			if(component instanceof Container) {
				setContainerAndChildrenEnabled((Container)component, pEnabled);
			} else {
				component.setEnabled(pEnabled);
			}
		}
	}

}
