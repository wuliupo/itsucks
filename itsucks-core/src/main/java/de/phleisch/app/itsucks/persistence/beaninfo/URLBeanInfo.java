/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 24.06.2007
 */

package de.phleisch.app.itsucks.persistence.beaninfo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLBeanInfo extends SimpleBeanInfo {

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
		
		try {
			PropertyDescriptor toExternalForm = 
				new PropertyDescriptor("toExternalForm",
						URL.class.getMethod("toExternalForm", new Class<?>[0]), null);
			
			descriptors.add(toExternalForm);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return descriptors.toArray(new PropertyDescriptor[descriptors.size()]);
	}
	
	
	
}
