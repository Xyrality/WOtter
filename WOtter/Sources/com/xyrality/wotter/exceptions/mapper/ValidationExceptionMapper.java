package com.xyrality.wotter.exceptions.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.ValidationException;

import com.xyrality.wotter.rest.v1.model.ErrorDTO;

@Provider
public class ValidationExceptionMapper
		implements
		ExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException exception) {
		return Response.status(Response.Status.BAD_REQUEST)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(new ErrorDTO("invalid request parameters"))
				.build();
	}

}
