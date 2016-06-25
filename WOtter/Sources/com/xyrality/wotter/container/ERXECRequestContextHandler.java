package com.xyrality.wotter.container;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

import org.apache.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import er.extensions.eof.ERXEC;

/**
 * Request context handler to generate and store a per-request editing context within the container request context.
 * This stores an editing context without undo manager within the request context and provides methods to access
 * this EC directly. Subclasses can be created to create different editing contexts (see MockECRequestContextHandler).
 * This handler is resolved within the WOJerseyRequestHandler using injection so other implementations can easily
 * supersede this one by providing a higher rank (again see MockECRequestContextHandler)
 */
public class ERXECRequestContextHandler {
	private static final Logger LOG = Logger.getLogger(ERXECRequestContextHandler.class);

	public static final String ERXEC_PROPERTY_KEY = "PerRequestERXEC";

	/**
	 * Create a new editing context
	 * @return the editing context
	 */
	protected ERXEC generateEditingContext() {
		final ERXEC ec = (ERXEC)ERXEC.newEditingContext();
		ec.setUndoManager(null);
		return ec;
	}

	/**
	 * Retrieve the stored editing context from a given container request context
	 * @param context the container context
	 * @return the ERXEC
	 * @throws NullPointerException if no ERXEC has been stored in the provided context
	 */
	public static @NonNull ERXEC ecForContext(final ContainerRequestContext context) {
		final ERXEC erxec = (ERXEC)context.getProperty(ERXEC_PROPERTY_KEY);
		if (erxec == null) {
			throw new NullPointerException("No ERXEC property set in request context " + context);
		}
		return erxec;
	}

	/**
	 * Generates and injects (if required) an ERXEC into the given context
	 * @param context the context to inject into
	 */
	public void injectERXECInitializerIntoContext(final ContainerRequestContext context) {
		if (context.getProperty(ERXEC_PROPERTY_KEY) == null) {
			context.setProperty(ERXEC_PROPERTY_KEY, generateEditingContext());
		}
	}

	/**
	 * Does cleanup work on the EC stored in the given context. If debug logging is activated for this class,
	 * this also logs all unsaved changes within the EC
	 *
	 * @param context the context
	 */
	public void cleanupERXECInitializerInContext(final ContainerRequestContext context) {
		final ERXEC ec = ecForContext(context);
		context.removeProperty(ERXEC_PROPERTY_KEY);

		if (LOG.isDebugEnabled() && ec.hasChanges()) {
			final StringBuilder logMessage = new StringBuilder();

			logMessage.append("ERXEC to be disposed still has unsaved changes:\n");
			ec.insertedObjects().forEach(o -> logMessage.append("- INSERT ").append(o.toString()).append("\n"));
			ec.updatedObjects().forEach(o -> logMessage.append("- UPDATE ").append(o.toString()).append("\n"));
			ec.deletedObjects().forEach(o -> logMessage.append("- DELETE ").append(o.toString()).append("\n"));

			LOG.debug(logMessage.toString());
		}

		ec.dispose();
	}

	/**
	 * Factory to generate ERXECRequestContextHandler objects
	 */
	public static class ERXECRequestContextHandlerFactory implements Factory<ERXECRequestContextHandler> {
		@Override
		public ERXECRequestContextHandler provide() {
			return new ERXECRequestContextHandler();
		}

		@Override
		public void dispose(final ERXECRequestContextHandler instance) {
			// nothing to do
		}
	}

	/**
	 * Helper method to create the binder for the application resource config. This binds the ERXECRequestContextHandler class
	 * with as singleton object
	 * @return the binder
	 */
	public static AbstractBinder binderForResourceConfig() {
		return new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(ERXECRequestContextHandlerFactory.class)
						.to(ERXECRequestContextHandler.class)
						.in(Singleton.class);
			}
		};
	}
}
