package com.xyrality.wotter;

import java.net.URI;

import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import com.webobjects.appserver.WOApplication;

public class WOJerseyTestContainerFactory implements TestContainerFactory {
	private final Class<? extends WOApplication> applicationClass;

	public WOJerseyTestContainerFactory(final Class<? extends WOApplication> applicationClass) {
		this.applicationClass = applicationClass;
	}

	@Override
	public TestContainer create(final URI baseUri, final DeploymentContext deploymentContext) {
		return new WOJerseyTestContainer(applicationClass);
	}
}
