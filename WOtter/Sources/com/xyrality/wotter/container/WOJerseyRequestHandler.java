package com.xyrality.wotter.container;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;

import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WORequestHandler;
import com.webobjects.appserver.WOResponse;

class WOJerseyRequestHandler extends WORequestHandler {
	private static final Logger log = Logger.getLogger(WOJerseyRequestHandler.class);

	private volatile ApplicationHandler appHandler;
	private ERXECRequestContextHandler erxecHandler;

	WOJerseyRequestHandler(ResourceConfig resourceConfig) {
		appHandler = new ApplicationHandler(resourceConfig);
		erxecHandler = appHandler.getServiceLocator().getService(ERXECRequestContextHandler.class);
	}

	@Override
	public WOResponse handleRequest(final WORequest request) {
		try {
			return handleRequestCore(request);
		} catch (Throwable t) {
			log.error("Issue forwarding request to Jersey " + request, t);

			final WOResponse internalError = new WOResponse();
			internalError.setStatus(WOMessage.HTTP_STATUS_INTERNAL_ERROR);
			return internalError;
		}
	}

	private WOResponse handleRequestCore(final WORequest request) throws Exception {
		final String method = request.method();
		final URI restRequestUri = new URI(request.uri());

		final String baseUriString = request.uri().replaceAll("/" + WOJerseyApp.JERSEY_ACTION_REQUEST_KEY + "/(.*)$", "") + "/" + WOJerseyApp.JERSEY_ACTION_REQUEST_KEY + "/";
		final URI baseUri = new URI(baseUriString);

		// Build container request
		final ContainerRequest requestContext = new ContainerRequest(baseUri, restRequestUri, method, new UnauthenticatedWOSecurityContext(request.isSecure()), new MapPropertiesDelegate());
		erxecHandler.injectERXECInitializerIntoContext(requestContext);

		// pass request headers and content to context
		final byte[] requestContent = request.contentString().getBytes(StandardCharsets.UTF_8);
		requestContext.setEntityStream(new ByteArrayInputStream(requestContent));

		for (final String key : request.headerKeys()) {
			requestContext.getHeaders().add(key, request.headerForKey(key));
		}

		// Fetch result from Jersey
		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		final Future<ContainerResponse> containerResponseFuture = appHandler.apply(requestContext, byteStream);

		ContainerResponse containerResponse = null;
		try {
			containerResponse = containerResponseFuture.get();
		} catch (InterruptedException
				| ExecutionException e) {
			log.error("Exception while trying to receive response from Jersey", e);
		} finally {
			erxecHandler.cleanupERXECInitializerInContext(requestContext);
		}

		// bridge result back to WOResponse
		final WOResponse woResponse = new WOResponse();
		woResponse.setContent(byteStream.toByteArray());

		if (containerResponse != null) {
			final MultivaluedMap<String, String> responseHeaders = containerResponse.getStringHeaders();
			woResponse.setHeaders(responseHeaders);
			woResponse.setStatus(containerResponse.getStatus());
		}

		return woResponse;
	}

	/**
	 * Simple unauthenticated WO security context
	 */
	private static class UnauthenticatedWOSecurityContext implements SecurityContext {

		private final boolean isSecure;

		UnauthenticatedWOSecurityContext(final boolean isSecure) {
			this.isSecure = isSecure;
		}

		@Override
		public Principal getUserPrincipal() {
			return null;
		}

		@Override
		public boolean isUserInRole(final String role) {
			return false;
		}

		@Override
		public boolean isSecure() {
			return isSecure;
		}

		@Override
		public String getAuthenticationScheme() {
			return null;
		}
	}
}
