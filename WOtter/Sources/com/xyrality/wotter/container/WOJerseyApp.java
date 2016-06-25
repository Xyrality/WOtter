package com.xyrality.wotter.container;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.google.common.annotations.VisibleForTesting;
import com.webobjects.appserver.WOApplication;

import er.extensions.appserver.ERXApplication;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Scheme;

public abstract class WOJerseyApp extends ERXApplication {

	static final String JERSEY_ACTION_REQUEST_KEY = "rs";

	private static ResourceConfig config;

	protected WOJerseyApp() {
		createSwaggerConfiguration();
		config = createJerseyApp();
		config.packages(getClass().getPackage().getName());
		configureAdditionalFeatures(config);
	}

	/**
	 * Configure the jersey request handler for the /rs/ path
	 */
	protected void startJersey() {
		final WOJerseyRequestHandler jerseyRequestHandler = new WOJerseyRequestHandler(config);
		WOApplication.application().registerRequestHandler(jerseyRequestHandler, JERSEY_ACTION_REQUEST_KEY);
	}

	/**
	 * @return a comma separated list of packages to scan for jersey resources.
	 */
	protected abstract String swaggerResourcePackages();

	/**
	 * Create the bean configuration required for swagger to scan the resource packages with the given configuration
	 *
	 * @param resourcePackages base package name for resources
	 * @param hostString the hostname/port string
	 * @param applicationName the application name
	 * @param applicationExtension the application extension
	 * @return a BeanConfig object to use for swagger
	 */
	static void createSwaggerConfiguration(final String resourcePackages, final String hostString, final String applicationName, final String applicationExtension) {
		final BeanConfig beanConfig = new BeanConfig();
		beanConfig.setResourcePackage(resourcePackages);
		beanConfig.setHost(hostString);
		beanConfig.setHost("127.0.0.1:8091");
		beanConfig.setBasePath("/cgi-bin/WebObjects/" + applicationName + applicationExtension + "/" + JERSEY_ACTION_REQUEST_KEY);
		beanConfig.setSchemes(new String[] { Scheme.HTTP.name(), Scheme.HTTPS.name() });
		beanConfig.setSchemes(new String[] { Scheme.HTTP.name() });
		beanConfig.setScan(true);
	}

	/**
	 * Create the swagger configuration for the running WOApplication
	 */
	private void createSwaggerConfiguration() {
		final String woHostAddress = hostAddress().getHostAddress() + ":" + port();
		createSwaggerConfiguration(swaggerResourcePackages(), woHostAddress, name(), applicationExtension());
	}

	/**
	 * Create and configure the Jersey application configuration
	 * @return the jersey configuration
	 */
	@VisibleForTesting
	public static ResourceConfig createJerseyApp() {
		return new ResourceConfig()
				// Swagger
				.packages(ApiListingResource.class.getPackage().getName())
				.register(SwaggerSerializers.class)

				// ERXEC generation and handling
				.register(ERXECRequestContextHandler.binderForResourceConfig())
				.register(ERXECFactory.binderForResourceConfig())

				// Jackson de-/serialization
				.register(JacksonFeature.class)
				.register(ObjectMapperProvider.class)
				;
	}

	protected void configureAdditionalFeatures(final ResourceConfig config) {
		// to override in subclasses
	}
}
