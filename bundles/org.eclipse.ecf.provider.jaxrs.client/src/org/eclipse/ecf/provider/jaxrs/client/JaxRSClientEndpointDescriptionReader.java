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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.EndpointDescriptionParser;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescriptionReader;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;

/**
 * An extension of the default ECF Remote Services endpoint reader. This
 * extension does the following.
 * <p>
 * First, it set defaults for JAX-RS clients. These defaults are consistent
 * across most clients and should not need to be set in each EDEF file.
 * Currently, the only properties that are required in an EDEF file are:
 * <ul>
 * <li>ecf.endpoint.id (REST endpoint URL)
 * <li>objectClass (interface for REST service)
 * </ul>
 * All other properties can be omitted, though they may be set if desired.
 * <p>
 * Second, the reader allows clients to define custom profiles identified by a
 * string name (e.g. "test", "prod"). These profiles can be referenced in the
 * EDEF file for a given property, like this:
 * 
 * <pre>
 * {@code
 * <property key="%test.ecf.endpoint.id" value="http://path.to.test.service" />
 * <property key="%prod.ecf.endpoint.id" value="http://path.to.prod.service" />
 * }
 * </pre>
 * 
 * The profile itself can be specified at runtime using the command line
 * argument:
 * <p>
 * {@code -Dorg.eclipse.ecf.provider.jaxrs.client.profile=test}
 * <p>
 * Note that a reader must be registered as an OSGi service. It is assumed that
 * subclasses of this reader will do the actual registration.
 */
@SuppressWarnings("restriction")
public class JaxRSClientEndpointDescriptionReader extends EndpointDescriptionReader {

	private static final String JAXRS_PROFILE_PREFIX_CHARACTER = "%";
	private static final String JAXRS_PROFILE_PROPERTY = "org.eclipse.ecf.provider.jaxrs.client.profile";

	@Override
	public EndpointDescription[] readEndpointDescriptions(InputStream input) throws IOException {

		List<EndpointDescriptionParser.EndpointDescription> baseEndpointDescriptions = getBaseEndpointDescriptions(
				input);
		List<org.osgi.service.remoteserviceadmin.EndpointDescription> finalEndpointDescriptions = new ArrayList<org.osgi.service.remoteserviceadmin.EndpointDescription>();

		for (EndpointDescriptionParser.EndpointDescription baseEndpointDescription : baseEndpointDescriptions) {
			Map<String, Object> baseProperties = baseEndpointDescription.getProperties();
			Map<String, Object> profiledProperties = addProfiledProperties(baseProperties);
			Map<String, Object> finalizedProperties = addDefaultProperties(profiledProperties);

			finalEndpointDescriptions.add(new EndpointDescription(finalizedProperties));
		}

		return finalEndpointDescriptions.toArray(new EndpointDescription[finalEndpointDescriptions.size()]);
	}

	/**
	 * Add default property values for JAX-RS clients. Classes that override this
	 * method should call the superclass method.
	 * 
	 * @param baseProperties
	 * @return properties with defaults added
	 */
	protected Map<String, Object> addDefaultProperties(Map<String, Object> baseProperties) {
		Map<String, Object> propertiesIncludingDefaults = new HashMap<>();
		propertiesIncludingDefaults.putAll(baseProperties);

		if (!propertiesIncludingDefaults.containsKey(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID)) {
			propertiesIncludingDefaults.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID,
					UUID.randomUUID().toString());
		}

		if (!propertiesIncludingDefaults.containsKey(
				org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE)) {
			propertiesIncludingDefaults.put(
					org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE,
					JaxRSNamespace.NAME);
		}

		if (!propertiesIncludingDefaults.containsKey(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID)) {
			propertiesIncludingDefaults.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, Long.valueOf(0));
		}

		if (!propertiesIncludingDefaults
				.containsKey(org.eclipse.ecf.remoteservice.Constants.ENDPOINT_REMOTESERVICE_FILTER)) {
			propertiesIncludingDefaults.put(org.eclipse.ecf.remoteservice.Constants.ENDPOINT_REMOTESERVICE_FILTER,
					"(objectClass=*)");
		}

		if (!propertiesIncludingDefaults.containsKey("remote.intents.supported")) {
			baseProperties.put("remote.intents.supported",
					new String[] { "passByValue", "exactlyOnce", "ordered", "jaxrs" });
		}

		if (!propertiesIncludingDefaults.containsKey(org.eclipse.ecf.remoteservice.Constants.OSGI_SERVICE_INTENTS)) {
			propertiesIncludingDefaults.put(org.eclipse.ecf.remoteservice.Constants.OSGI_SERVICE_INTENTS,
					new String[] { org.eclipse.ecf.remoteservice.Constants.OSGI_ASYNC_INTENT });
		} else {
			String[] serviceIntents = (String[]) propertiesIncludingDefaults
					.get(org.eclipse.ecf.remoteservice.Constants.OSGI_SERVICE_INTENTS);
			List<String> serviceIntentsAsList = Arrays.asList(serviceIntents);

			if (!serviceIntentsAsList.contains(org.eclipse.ecf.remoteservice.Constants.OSGI_ASYNC_INTENT)) {
				serviceIntentsAsList.add(org.eclipse.ecf.remoteservice.Constants.OSGI_ASYNC_INTENT);
				String[] updatedServiceIntents = serviceIntentsAsList.toArray(new String[serviceIntentsAsList.size()]);
				propertiesIncludingDefaults.put(org.eclipse.ecf.remoteservice.Constants.OSGI_SERVICE_INTENTS,
						updatedServiceIntents);
			}
		}

		return propertiesIncludingDefaults;
	}

	private List<EndpointDescriptionParser.EndpointDescription> getBaseEndpointDescriptions(InputStream input)
			throws IOException {
		EndpointDescriptionParser parser = new EndpointDescriptionParser();
		parser.parse(input);

		return parser.getEndpointDescriptions();
	}

	/**
	 * Compare the keys to see if the active profile is included as a prefix (e.g.
	 * %test.ecf.endpoint.id). Properties containing valid prefixes are used to
	 * override any default values. Properties with prefixes that do not match the
	 * active profile are removed.
	 * 
	 * @param mergedProperties
	 */
	private Map<String, Object> addProfiledProperties(Map<String, Object> mergedProperties) {
		String profile = System.getProperty(JAXRS_PROFILE_PROPERTY);

		Map<String, Object> finalizedProperties = new HashMap<>();

		/*
		 * Load all properties without prefixes, including defaults that may be
		 * overridden
		 */
		for (String key : mergedProperties.keySet()) {
			if (!key.startsWith(JAXRS_PROFILE_PREFIX_CHARACTER)) {
				finalizedProperties.put(key, mergedProperties.get(key));
			}
		}

		/* Load properties that match the current profile */
		if (profile != null) {
			String validPrefix = JAXRS_PROFILE_PREFIX_CHARACTER + profile + ".";
			for (String key : mergedProperties.keySet()) {
				if (key.startsWith(validPrefix)) {
					Object value = mergedProperties.get(key);
					String actualKey = key.replace(validPrefix, new String());
					finalizedProperties.put(actualKey, value); // may overwrite default value
				}
			}
		}
		return finalizedProperties;
	}
}
