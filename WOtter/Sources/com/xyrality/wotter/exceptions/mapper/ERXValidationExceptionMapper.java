package com.xyrality.wotter.exceptions.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.xyrality.wotter.rest.v1.model.ErrorDTO;

import er.extensions.appserver.ERXApplication;
import er.extensions.validation.ERXValidationException;

/**
 * Mapper for {@link ERXValidationException}, giving and internal error for most of them
 * but a specific message for exceeding the maximum length of an attribute.
 */
@Provider
public class ERXValidationExceptionMapper
		implements ExceptionMapper<ERXValidationException> {

	public Response toResponse(ERXValidationException ex) {
		if (ex.type().equals(ERXValidationException.ExceedsMaximumLengthException)) {
			final int characterLength = ex.eoObject().valueForKey(ex.propertyKey()).toString().length();
			final int characterLimit = ex.attribute().width();

			return Response.status(Response.Status.BAD_REQUEST)
					.type(MediaType.APPLICATION_JSON_TYPE)
					.entity(new ErrorDTO("Wot too long (%d characters) exceeding the maximum length of %d characters", characterLength, characterLimit))
					.build();
		} else {
			ERXApplication.log.error("Unexpected validation exception", ex);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.build();
		}
	}
}
