package com.xyrality.wotter.exceptions.mapper;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.xyrality.wotter.rest.v1.model.ErrorDTO;

@Provider
public class JerseyClientErrorMapper implements
		ExceptionMapper<ClientErrorException> {

	@Override
	public Response toResponse(ClientErrorException exception) {
		return Response.status(exception.getResponse().getStatus())
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(new ErrorDTO(exception.getMessage()))
				.build();
	}
}
