package com.xyrality.wotter.container;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

import er.extensions.eof.ERXEC;

/**
 * Factory to support injection of ERXEC objects based on the container request context.
 * This requires that the ERXECRequestContextHandler or one of its subclasses is registered
 * and in place to actually store an editing context within the request context object
 */
public class ERXECFactory implements Factory<ERXEC> {

	private final ContainerRequestContext context;

	@Inject
	public ERXECFactory(final ContainerRequestContext context) {
		this.context = context;
	}

	@Override
	public ERXEC provide() {
		return ERXECRequestContextHandler.ecForContext(context);
	}

	@Override
	public void dispose(final ERXEC instance) {
		// nothing to do, will be handled by global context handling
	}

	/**
	 * Helper to create the request-scoped binding between this factory and the ERXEC class
	 * @return the binder to supply to a resource configuration
	 */
	public static AbstractBinder binderForResourceConfig() {
		return new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(ERXECFactory.class).to(ERXEC.class).in(RequestScoped.class);
			}
		};
	}
}
