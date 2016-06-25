package com.xyrality.wotter.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.google.common.net.HttpHeaders;

import io.swagger.models.HttpMethod;

/**
 * Attach some generic CORS headers.
 *
 * ATTENTION! This may not be secure and open your application to XSS attacks,
 * this should therefore only be used in development.
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {
	@Override
	public void filter(final ContainerRequestContext request, final ContainerResponseContext response) throws IOException {
		response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, request.getHeaderString(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS));
		response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

		response.getHeaders().add(
				HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
				HttpMethod.GET.name()+", "+
				HttpMethod.POST.name()+", "+
				HttpMethod.PUT.name()+", "+
				HttpMethod.PATCH.name()+", "+
				HttpMethod.DELETE.name()+", "+
				HttpMethod.OPTIONS.name()+", "+
				HttpMethod.HEAD.name());
	}
}
