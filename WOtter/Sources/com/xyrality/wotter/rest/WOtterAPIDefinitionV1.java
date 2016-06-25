package com.xyrality.wotter.rest;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;
import io.swagger.models.auth.BasicAuthDefinition;

@SwaggerDefinition(
		basePath = "/cgi-bin/WebObjects/WOtter.woa/rs",
		consumes = {"application/json"},
		produces = {"application/json"},
		schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
		info = @Info(
				title = "The WOtter API",
				version = "1.0.0",
				contact = @Contact(
						name = "The WOtters",
						email = "backend@xyrality.com"
				),
				license = @License(
						name = "(c) Copyright 2016 XYRALITY GmbH, all rights reserved."
				),
				description = "" +
						"This is the WOtter server API. It describes what resources the clients can access to post and read Wots."
		)
)
public class WOtterAPIDefinitionV1
		implements
		ReaderListener {
	private static final String CREDENTIALS_AUTH = "credentialsAuth";

	@Override
	public void beforeScan(final Reader reader, final Swagger swagger) {
	}

	@Override
	public void afterScan(final Reader reader, final Swagger swagger) {
		// Add the authentication definition for basic auth
		final BasicAuthDefinition credentialsAuthDefinition = new BasicAuthDefinition();
		credentialsAuthDefinition.setDescription("" +
				"HTTP Basic authentication for posting to **WOtter** resources.\n" +
				"\n" +
				"It expects the base 64 encoded username and password."
				);

		swagger.addSecurityDefinition(CREDENTIALS_AUTH, credentialsAuthDefinition);
	}
}
