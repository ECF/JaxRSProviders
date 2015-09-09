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

import java.util.Dictionary;
import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.AbstractContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.eclipse.ecf.remoteservice.IRemoteFilter;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceCallPolicy;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceID;
import org.eclipse.ecf.remoteservice.IRemoteServiceListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.equinox.concurrent.future.IExecutor;
import org.eclipse.equinox.concurrent.future.IFuture;
import org.eclipse.equinox.concurrent.future.ThreadsExecutor;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public abstract class JaxRSServerContainer extends AbstractContainer implements IRemoteServiceContainerAdapter {

	public static final String SERVLET_PROPERTIES_PARAM = ".servletProperties"; // expected
																				// value
																				// type=Dictionary
	public static final String SERVLET_HTTPCONTEXT_PARAM = ".servletHttpContext"; // expected
																					// value
																					// type
																					// =
																					// HttpContext

	protected static final String SLASH = "/";

	private final String urlContext;
	private final String alias;
	private final ID serverID;

	private JaxRSRemoteServiceContainerAdapter adapterImpl;

	public JaxRSServerContainer(String urlContext, String alias, IExecutor executor) {
		Assert.isNotNull(urlContext);
		this.urlContext = urlContext;
		// remove any leading slashes on alias
		// Then make sure it starts with slash
		this.alias = alias;
		// Create serverID
		this.serverID = JaxRSNamespace.INSTANCE.createInstance(new Object[] { this.urlContext + this.alias });
		if (executor == null)
			executor = new ThreadsExecutor();
		this.adapterImpl = new JaxRSRemoteServiceContainerAdapter(this, executor);
	}

	public JaxRSServerContainer(String urlContext, String alias) {
		this(urlContext, alias, null);
	}

	protected String getAlias() {
		return alias;
	}

	protected String getUrlContext() {
		return urlContext;
	}

	@SuppressWarnings("rawtypes")
	protected Dictionary createServletProperties(IRemoteServiceRegistration registration, Object serviceObject,
			Dictionary properties) {
		return getKeyEndsWithPropertyValue(properties, SERVLET_PROPERTIES_PARAM, Dictionary.class);
	}

	protected String createServletAlias(IRemoteServiceRegistration registration, Object serviceObject,
			@SuppressWarnings("rawtypes") Dictionary properties) {
		return getAlias() + SLASH + registration.getID().getContainerRelativeID();
	}

	protected abstract Servlet createServlet(IRemoteServiceRegistration registration, Object serviceObject,
			@SuppressWarnings("rawtypes") Dictionary properties);

	@SuppressWarnings("unchecked")
	private <T> T getKeyEndsWithPropertyValue(@SuppressWarnings("rawtypes") Dictionary input, String keyEndsWith,
			Class<T> valueType) {
		for (@SuppressWarnings("rawtypes")
		Enumeration e = input.keys(); e.hasMoreElements();) {
			Object k = e.nextElement();
			if (k instanceof String) {
				String key = (String) k;
				if (key.endsWith(keyEndsWith)) {
					Object v = input.get(key);
					if (valueType.isInstance(v))
						return (T) v;
				}
			}
		}
		return null;
	}

	protected HttpContext createServletContext(IRemoteServiceRegistration registration, Object service,
			@SuppressWarnings("rawtypes") Dictionary properties) {
		return getKeyEndsWithPropertyValue(properties, SERVLET_HTTPCONTEXT_PARAM, HttpContext.class);
	}

	protected abstract HttpService getHttpService();

	protected void registerJaxRSResource(String servletAlias, Servlet servlet,
			@SuppressWarnings("rawtypes") Dictionary servletProperties, HttpContext servletContext)
					throws RuntimeException {
		try {
			getHttpService().registerServlet(servletAlias, servlet, servletProperties, servletContext);
		} catch (ServletException | NamespaceException e) {
			throw new RuntimeException("Cannot register servlet with alias=" + getAlias(), e);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error registering servlet with alias=" + getAlias(), e);
		}
	}

	protected void unregisterResource(String servletAlias) {
		getHttpService().unregister(servletAlias);
	}

	@Override
	public Namespace getConnectNamespace() {
		return serverID.getNamespace();
	}

	@Override
	public void connect(ID targetID, IConnectContext connectContext) throws ContainerConnectException {
		throw new ContainerConnectException("Cannot connect to JaxRSServerContainer");
	}

	@Override
	public ID getConnectedID() {
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		synchronized (this) {
			if (adapterImpl != null) {
				adapterImpl.dispose();
				adapterImpl = null;
			}
		}
	}

	@Override
	public void disconnect() {
	}

	@Override
	public ID getID() {
		return serverID;
	}

	@Override
	public void addRemoteServiceListener(IRemoteServiceListener listener) {
		adapterImpl.addRemoteServiceListener(listener);
	}

	@Override
	public void removeRemoteServiceListener(IRemoteServiceListener listener) {
		adapterImpl.removeRemoteServiceListener(listener);
	}

	@Override
	public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service,
			@SuppressWarnings("rawtypes") Dictionary properties) {
		return adapterImpl.registerRemoteService(clazzes, service, properties);
	}

	@Override
	public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter)
			throws InvalidSyntaxException, ContainerConnectException {
		return adapterImpl.getRemoteServiceReferences(target, idFilter, clazz, filter);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IFuture asyncGetRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) {
		return adapterImpl.asyncGetRemoteServiceReferences(target, clazz, filter);
	}

	@Override
	public IRemoteServiceReference[] getRemoteServiceReferences(ID[] idFilter, String clazz, String filter)
			throws InvalidSyntaxException {
		return adapterImpl.getRemoteServiceReferences(idFilter, clazz, filter);
	}

	@Override
	public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter)
			throws InvalidSyntaxException, ContainerConnectException {
		return adapterImpl.getRemoteServiceReferences(target, clazz, filter);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IFuture asyncGetRemoteServiceReferences(ID[] idFilter, String clazz, String filter) {
		return adapterImpl.asyncGetRemoteServiceReferences(idFilter, clazz, filter);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IFuture asyncGetRemoteServiceReferences(ID target, String clazz, String filter) {
		return adapterImpl.asyncGetRemoteServiceReferences(target, clazz, filter);
	}

	@Override
	public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter)
			throws InvalidSyntaxException {
		return adapterImpl.getAllRemoteServiceReferences(clazz, filter);
	}

	@Override
	public Namespace getRemoteServiceNamespace() {
		return adapterImpl.getRemoteServiceNamespace();
	}

	@Override
	public IRemoteServiceID getRemoteServiceID(ID containerID, long containerRelativeID) {
		return adapterImpl.getRemoteServiceID(containerID, containerRelativeID);
	}

	@Override
	public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceID) {
		return adapterImpl.getRemoteServiceReference(serviceID);
	}

	@Override
	public IRemoteService getRemoteService(IRemoteServiceReference reference) {
		return adapterImpl.getRemoteService(reference);
	}

	@Override
	public boolean ungetRemoteService(IRemoteServiceReference reference) {
		return adapterImpl.ungetRemoteService(reference);
	}

	@Override
	public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException {
		return adapterImpl.createRemoteFilter(filter);
	}

	@Override
	public void setConnectContextForAuthentication(IConnectContext connectContext) {
		adapterImpl.setConnectContextForAuthentication(connectContext);
	}

	@Override
	public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
		return adapterImpl.setRemoteServiceCallPolicy(policy);
	}

}
