/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package com.mycorp.examples.student.remoteservice.host;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.mycorp.examples.student.StudentService;

public class Activator implements BundleActivator {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void start(BundleContext context) throws Exception {

		Dictionary serviceProps = new Hashtable();
		serviceProps.put("service.exported.interfaces", "*");
		serviceProps.put("service.exported.configs", "ecf.jaxrs.jersey.server");
		serviceProps.put("ecf.jaxrs.jersey.server.alias", "/jersey/is/good");
		Properties systemProperties = System.getProperties();
		for(Enumeration systemPropNames = systemProperties.propertyNames(); systemPropNames.hasMoreElements(); ) {
			String systemPropName = (String) systemPropNames.nextElement();
			if (systemPropName.startsWith("ecf.")) 
				serviceProps.put(systemPropName, systemProperties.getProperty(systemPropName));
		}
		context.registerService(StudentService.class, new StudentResource(), serviceProps);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
