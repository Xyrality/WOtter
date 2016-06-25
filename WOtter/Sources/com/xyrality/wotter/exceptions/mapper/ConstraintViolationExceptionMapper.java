package com.xyrality.wotter.exceptions.mapper;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.xyrality.wotter.rest.v1.model.ErrorDTO;

/**
 * This mapper is responsible for catching validation failures issued by bean validation failures
 * It will return a 400 Bad Request
 */
@Provider
public class ConstraintViolationExceptionMapper implements
		ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		final String message = exception.getConstraintViolations().stream()
			.map(cv -> cv.getInvalidValue().toString() + " " + cv.getMessage())
			.collect(Collectors.joining(", "));
		return Response.status(Response.Status.BAD_REQUEST)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(new ErrorDTO("invalid request parameters: " + message))
				.build();
	}
}
