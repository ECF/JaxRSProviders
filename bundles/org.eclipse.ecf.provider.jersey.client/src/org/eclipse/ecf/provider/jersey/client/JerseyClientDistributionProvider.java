/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.client;

import java.util.Map;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientDistributionProvider;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(service = IRemoteServiceDistributionProvider.class)
public class JerseyClientDistributionProvider extends JaxRSClientDistributionProvider {
	public static final String CLIENT_PROVIDER_NAME = "ecf.jaxrs.jersey.client";
	public static final String SERVER_PROVIDER_NAME = "ecf.jaxrs.jersey.server";

	public JerseyClientDistributionProvider() {
		super(CLIENT_PROVIDER_NAME, new JaxRSContainerInstantiator(SERVER_PROVIDER_NAME, CLIENT_PROVIDER_NAME) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description,
					@SuppressWarnings("rawtypes") Map parameters, final Configuration configuration) {
				return new JaxRSClientContainer(configuration);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return new ClientConfig();
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindMessageBodyWriter(MessageBodyWriter instance, Map serviceProps) {
		super.bindMessageBodyWriter(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindMessageBodyWriter(MessageBodyWriter instance) {
		super.unbindMessageBodyWriter(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindMessageBodyReader(MessageBodyReader instance, Map serviceProps) {
		super.bindMessageBodyReader(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindMessageBodyReader(MessageBodyReader instance) {
		super.unbindMessageBodyReader(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindContextResolver(ContextResolver instance, Map serviceProps) {
		super.bindContextResolver(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindContextResolver(ContextResolver instance) {
		super.unbindContextResolver(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindExceptionMapper(ExceptionMapper instance, Map serviceProps) {
		super.bindExceptionMapper(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindExceptionMapper(ExceptionMapper instance) {
		super.unbindExceptionMapper(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindFeature(Feature instance, Map serviceProps) {
		super.bindFeature(instance, serviceProps);
	}

	protected void unbindFeature(Feature instance) {
		super.unbindFeature(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindReaderInterceptor(ReaderInterceptor instance, Map serviceProps) {
		super.bindReaderInterceptor(instance, serviceProps);
	}

	protected void unbindReaderInterceptor(ReaderInterceptor instance) {
		super.unbindReaderInterceptor(instance);
	}
	
	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindWriterInterceptor(WriterInterceptor instance, Map serviceProps) {
		super.bindWriterInterceptor(instance, serviceProps);
	}

	protected void unbindWriterInterceptor(WriterInterceptor instance) {
		super.unbindWriterInterceptor(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindClientRequestFilter(ClientRequestFilter instance, Map serviceProps) {
		super.bindClientRequestFilter(instance, serviceProps);
	}

	protected void unbindClientRequestFilter(ClientRequestFilter instance) {
		super.unbindClientRequestFilter(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindClientResponseFilter(ClientResponseFilter instance, Map serviceProps) {
		super.bindClientResponseFilter(instance, serviceProps);
	}

	protected void unbindClientResponseFilter(ClientResponseFilter instance) {
		super.unbindClientResponseFilter(instance);
	}


}