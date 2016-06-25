package com.xyrality.wotter.exceptions.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Mapper for {@link WebApplicationException}s, just forwards to the WAE. We need
 * this because otherwise the {@link ThrowableMapper} will try to process these.
 */
@Provider
public class WebApplicationExceptionMapper
		implements ExceptionMapper<WebApplicationException> {

	public Response toResponse(WebApplicationException ex) {
		return ex.getResponse();
	}
}
