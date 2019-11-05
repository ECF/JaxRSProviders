/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import java.net.URI;
import java.util.Map;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;

public abstract class JaxRSServerContainerInstantiator extends JaxRSContainerInstantiator {

	public static final String JAXRS_SERVER_CONFIG = "ecf.jaxrs.server";
	public static final String URL_PROTOCOL_PROP = "protocol";
	public static final String URL_PROTOCOL_DEFAULT = "http";
	public static final String URL_HOSTNAME_PROP = "hostname";
	public static final String URL_HOSTNAME_DEFAULT = "localhost";
	public static final String URL_PORT_PROP = "port";
	public static final String URL_HTTP_PORT_DEFAULT = "8080";
	public static final String URL_HTTPS_PORT_DEFAULT = "8443";
	public static final String URL_PATH_PREFIX_PROP = "pathPrefix";
	public static final String URL_PATH_PREFIX_DEFAULT = "/";
	public static final String URL_PREFIX_PROP = "urlPrefix";

	public static final String URL_INCLUDE_REMOTE_SERVICE_ID = "includeRemoteServiceId";

	public static final String URL_INCLUDE_REMOTE_SERVICE_ID_DEFAULT = "false";

	public JaxRSServerContainerInstantiator(String serverConfigTypeName) {
		super(serverConfigTypeName);
	}

	public JaxRSServerContainerInstantiator(String serverConfigTypeName, String clientConfigTypeName) {
		super(serverConfigTypeName, clientConfigTypeName);
	}

	public static String getDotProperty(String configName, String paramName) {
		return configName + "." + paramName;
	}

	protected String getSystemProperty(String configName, String key, String def) {
		return System.getProperty(getDotProperty(configName, key), def);
	}

	protected String getProtocol(Map<String, ?> params, String configName) {
		return super.getParameterValue(params, URL_PROTOCOL_PROP,
				getSystemProperty(configName, URL_PROTOCOL_PROP, URL_PROTOCOL_DEFAULT));
	}

	protected String getHostname(Map<String, ?> params, String configName) {
		return super.getParameterValue(params, URL_HOSTNAME_PROP,
				getSystemProperty(configName, URL_HOSTNAME_PROP, URL_HOSTNAME_DEFAULT));
	}

	protected String getSystemPort(Map<String, ?> params, String configName, boolean https) {
		if (https)
			return System.getProperty("org.osgi.service.http.port.secure", URL_HTTPS_PORT_DEFAULT);
		else
			return System.getProperty("org.osgi.service.http.port", URL_HTTP_PORT_DEFAULT);
	}

	protected String getSystemPathPrefix(Map<String, ?> params, String configName) {
		return getSystemProperty(configName, URL_PATH_PREFIX_PROP,
				getSystemProperty(JAXRS_SERVER_CONFIG, URL_PATH_PREFIX_PROP, URL_PATH_PREFIX_DEFAULT));
	}

	protected String getUrl(Map<String, ?> params, String configName) {
		// Look for system property: <configName>.urlPrefix
		String sysUp = getSystemProperty(configName, URL_PREFIX_PROP, null);
		// If found we use it unconditionally
		if (sysUp != null)
			return sysUp;
		// Look for parameter urlPrefix
		// Fix for https://github.com/ECF/JaxRSProviders/issues/9
		String propUp = getParameterValue(params, URL_PREFIX_PROP, null);
		// If found we use it unconditionally
		if (propUp != null)
			return propUp;
		else {
			// Get protocol, hostname,port,path to create uri
			String protocol = getProtocol(params, configName);
			String hostname = getHostname(params, configName);
			String port = getSystemPort(params, configName, protocol.equalsIgnoreCase("https"));
			String path = getSystemPathPrefix(params, configName);
			return protocol + "://" + hostname + ((!"".equals(port)) ? ":" + port : "") + path;
		}

	}

	protected boolean getIncludeRemoteServiceId(Map<String, ?> params, String configName) {
		String includeRemoteServiceIdVal = super.getParameterValue(params, URL_INCLUDE_REMOTE_SERVICE_ID,
				getSystemProperty(configName, URL_INCLUDE_REMOTE_SERVICE_ID, URL_INCLUDE_REMOTE_SERVICE_ID_DEFAULT));
		if (includeRemoteServiceIdVal == null)
			return Boolean.FALSE;
		return Boolean.valueOf(includeRemoteServiceIdVal);
	}

	protected URI getUri(Map<String, ?> params, String configName) throws ContainerCreateException {
		try {
			return new URI(getUrl(params, configName));
		} catch (Exception e) {
			throw new ContainerCreateException("Cannot create Jersey Server Container uri", e);
		}
	}

}
