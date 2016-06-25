package com.xyrality.wotter.exceptions.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.webobjects.eoaccess.EOObjectNotAvailableException;
import com.xyrality.wotter.rest.v1.model.ErrorDTO;

/**
 * This mapper is issued when an object could not be found inside the database
 * It will be mapped to a 404 Not Found
 */
@Provider
public class EOObjectNotAvailableMapper
		implements ExceptionMapper<EOObjectNotAvailableException> {

	public Response toResponse(EOObjectNotAvailableException ex) {
		return Response.status(Response.Status.NOT_FOUND)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(new ErrorDTO("Object not found"))
				.build();
	}
}
