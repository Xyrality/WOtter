package com.xyrality.wotter;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.webobjects.eoaccess.EOModelGroup;
import com.wounit.rules.MockEditingContext;
import com.xyrality.wotter.container.ERXECRequestContextHandler;

import er.extensions.eof.ERXEC;

/**
 * Context handler for injecting mock editing contexts into jersey requests instead of "real" editing contexts. The mock editing
 * context will be created based on the default model group
 */
public class MockECRequestContextHandler extends ERXECRequestContextHandler {
	private static ERXEC currentMockEditingContext = null;

	static {
		resetMockEC();
	}

	@Override
	protected ERXEC generateEditingContext() {
		return currentMockEditingContext;

	}

	/**
	 * Create a fresh mock editing context to use in all future invocations and injections
	 */
	public static synchronized void resetMockEC() {
		//noinspection ToArrayCallWithZeroLengthArrayArgument
		currentMockEditingContext = new MockEditingContext(EOModelGroup.defaultGroup().modelNames().toArray(new String[0]));
	}

	/**
	 * Retrieve the mock editing context current in use
	 * @return the EC
	 */
	public static synchronized ERXEC getCurrentMockEditingContext() {
		return currentMockEditingContext;
	}

	@Override
	public void cleanupERXECInitializerInContext(final ContainerRequestContext context) {
		// This only works with mock editing contexts, so we do not have any cleanup work.
		// Tests may also leave unsaved objects around, so we do not try to log them
	}

	/**
	 * Factory to generate MockECRequestContextHandler objects
	 */
	public static class MockECRequestContextHandlerFactory implements Factory<MockECRequestContextHandler> {
		@Override
		public MockECRequestContextHandler provide() {
			return new MockECRequestContextHandler();
		}

		@Override
		public void dispose(final MockECRequestContextHandler instance) {
			// nothing to do
		}
	}

	/**
	 * Helper method to create the binder for the application resource config. This binds the MockECRequestContextHandler class
	 * with a ranking of 100 as singleton object to override the original ERXECRequestContextHandler
	 * @return the binder
	 */
	public static AbstractBinder binderForResourceConfig() {
		return new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(MockECRequestContextHandlerFactory.class)
						.to(ERXECRequestContextHandler.class)
						.in(Singleton.class)
						.ranked(100);
			}
		};
	}
}
