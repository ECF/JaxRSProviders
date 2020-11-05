/*******************************************************************************
* Copyright (c) 2020 Patrick Paulin and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Patrick Paulin - initial implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.EndpointDescriptionParser;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescriptionReader;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.osgi.service.remoteserviceadmin.EndpointDescription;

/**
 * An endpoint reader that sets JAX-RS defaults. These defaults are consistent
 * across most clients and should not need to be set in each EDEF file.
 * Currently, the only properties that are required in an EDEF file are:
 * <ul>
 * <li>ecf.endpoint.id (REST endpoint URL)
 * <li>objectClass (interface for REST service)
 * </ul>
 * All other properties can be omitted, though they may be set if desired.
 * <p>
 * Note that a reader must be registered as an OSGi service. It is assumed that
 * subclasses of this reader will do the actual registration.
 */
@SuppressWarnings("restriction")
public class JaxRSClientEndpointDescriptionReader extends EndpointDescriptionReader {

	@Override
	public EndpointDescription[] readEndpointDescriptions(InputStream input, Map<String, Object> overrideProperties)
			throws IOException {

		/*
		 * Need to copy input so that we can pass an unconsumed input to superclass
		 * method. Otherwise input is consumed when retrieving base endpoint properties.
		 */
		byte[] inputAsByteArray = getByteArrayFromInput(input);

		Map<String, Object> baseProperties = getBaseEndpointProperties(new ByteArrayInputStream(inputAsByteArray));
		addDefaultsToOverridesIfNeeded(baseProperties, overrideProperties);

		return super.readEndpointDescriptions(new ByteArrayInputStream(inputAsByteArray), overrideProperties);
	}

	/**
	 * Add default property values for JAX-RS clients. Classes that override this
	 * method should call the superclass method.
	 */
	protected void addDefaultsToOverridesIfNeeded(Map<String, Object> baseProperties,
			Map<String, Object> overrideProperties) {
		/* JAX-RS defaults */
		setDefaultPropertyIfNecessary(baseProperties, overrideProperties,
				org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE,
				JaxRSNamespace.NAME);
		setDefaultPropertyIfNecessary(baseProperties, overrideProperties, "remote.intents.supported",
				new String[] { "passByValue", "exactlyOnce", "ordered", "jaxrs" });
		setDefaultPropertyIfNecessary(baseProperties, overrideProperties,
				org.eclipse.ecf.remoteservice.Constants.OSGI_SERVICE_INTENTS,
				new String[] { org.eclipse.ecf.remoteservice.Constants.OSGI_ASYNC_INTENT });

		/* Defaults that perhaps shouldn't be required for JAX-RS clients */
		setDefaultPropertyIfNecessary(baseProperties, overrideProperties,
				org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, UUID.randomUUID().toString());
		setDefaultPropertyIfNecessary(baseProperties, overrideProperties,
				org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, Long.valueOf(0));
		setDefaultPropertyIfNecessary(baseProperties, overrideProperties,
				org.eclipse.ecf.remoteservice.Constants.ENDPOINT_REMOTESERVICE_FILTER, "(objectClass=*)");
	}

	protected void setDefaultPropertyIfNecessary(Map<String, Object> baseProperties,
			Map<String, Object> overrideProperties, String key, Object defaultValue) {
		if (!baseProperties.containsKey(key) && !overrideProperties.containsKey(key)) {
			overrideProperties.put(key, defaultValue);
		}
	}

	private Map<String, Object> getBaseEndpointProperties(InputStream input) throws IOException {
		EndpointDescriptionParser parser = new EndpointDescriptionParser();
		parser.parse(input);

		List<EndpointDescriptionParser.EndpointDescription> endpointDescriptions = parser.getEndpointDescriptions();

		/*
		 * Returning properties for first endpoint if multiple. Not sure how to apply
		 * defaults if more than one endpoint in file
		 */
		if (endpointDescriptions.size() > 0) {
			return endpointDescriptions.get(0).getProperties();
		}
		return new HashMap<String, Object>();
	}

	private byte[] getByteArrayFromInput(InputStream input) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int n = 0;
		while ((n = input.read(buffer)) >= 0)
			baos.write(buffer, 0, n);
		input.close();
		
		return baos.toByteArray();
	}
}