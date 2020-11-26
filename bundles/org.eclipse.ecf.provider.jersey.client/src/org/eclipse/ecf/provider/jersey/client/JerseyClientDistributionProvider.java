/*******************************************************************************
* Copyright (c) 2020 Composent, Inc. and others. All rights reserved. This
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
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(service = IRemoteServiceDistributionProvider.class)
public class JerseyClientDistributionProvider extends AbstractJerseyClientDistributionProvider {
	public static final String CLIENT_PROVIDER_NAME = "ecf.jaxrs.jersey.client";
	public static final String SERVER_PROVIDER_NAME = "ecf.jaxrs.jersey.server";

	public JerseyClientDistributionProvider() {
		super(SERVER_PROVIDER_NAME, CLIENT_PROVIDER_NAME);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindMessageBodyWriter(MessageBodyWriter instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindMessageBodyWriter(MessageBodyWriter instance) {
		removeJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindMessageBodyReader(MessageBodyReader instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindMessageBodyReader(MessageBodyReader instance) {
		removeJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindContextResolver(ContextResolver instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindContextResolver(ContextResolver instance) {
		removeJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindExceptionMapper(ExceptionMapper instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindExceptionMapper(ExceptionMapper instance) {
		removeJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindFeature(Feature instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	protected void unbindFeature(Feature instance) {
		removeJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindReaderInterceptor(ReaderInterceptor instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	protected void unbindReaderInterceptor(ReaderInterceptor instance) {
		removeJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindWriterInterceptor(WriterInterceptor instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	protected void unbindWriterInterceptor(WriterInterceptor instance) {
		removeJaxComponent(instance);
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