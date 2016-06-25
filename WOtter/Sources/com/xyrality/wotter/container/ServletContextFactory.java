package com.xyrality.wotter.container;

import javax.servlet.ServletContext;

import org.glassfish.hk2.api.Factory;

import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.Swagger;

class ServletContextFactory
		implements
		Factory<ServletContext> {

	private final ServletContext servletContext = new AttributeServletContext();

	public ServletContextFactory() {
		final Info info = new Info()
				.title("WOtter Server API")
				.description("WOtter service API - something clever")
				.contact(new Contact().email("dennis.bliefernicht@xyrality.com"))
	      ;

		final Swagger swagger = new Swagger().info(info);

		servletContext.setAttribute("swagger", swagger);
	}

	@Override
	public ServletContext provide() {
		return servletContext;
	}

	@Override
	public void dispose(ServletContext instance) {
	}
}
