package com.xyrality.wotter.exceptions.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import er.extensions.appserver.ERXApplication;

/**
 * Generic handler for all {@link Throwable}s that are not caugh by other handlers
 */
@Provider
public class ThrowableMapper
		implements ExceptionMapper<Throwable> {

	public Response toResponse(Throwable ex) {
		ERXApplication.log.error("Encountered internal error", ex);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.build();
	}
}
