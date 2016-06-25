package com.xyrality.wotter.filters;

import java.lang.annotation.Annotation;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.xyrality.wotter.rest.v1.model.ETagResource;

import io.swagger.jaxrs.PATCH;

/**
 * Response filter for etag, automatically attaches the ETag HTTP header
 * for all resources that implement {@link ETagResource}
 */
@Provider
public class ETagResponseFilter implements ContainerResponseFilter {

	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String ENTITY_TAG = "ETag";

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
		if (responseContext.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
			return;
		}

		for (Annotation annotation : resourceInfo.getResourceMethod().getDeclaredAnnotations()) {
			if ((annotation instanceof GET) || (annotation instanceof PUT) || (annotation instanceof PATCH)) {
				final Object entity = responseContext.getEntity();
				if (entity != null && entity instanceof ETagResource) {
					final String eTag = ((ETagResource)entity).geteTag();
					responseContext.getHeaders().add(ENTITY_TAG, eTag);
				}
				responseContext.getHeaders().add(CACHE_CONTROL, "no-cache");
				return;
			}
		}
	}

}
