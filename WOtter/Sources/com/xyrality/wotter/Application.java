package com.xyrality.wotter;

import org.glassfish.jersey.server.ResourceConfig;

import com.webobjects.appserver.WOSession;
import com.xyrality.wotter.container.WOJerseyApp;
import com.xyrality.wotter.container.WOJerseyAppLauncher;
import com.xyrality.wotter.eo.Account;
import com.xyrality.wotter.filters.AccountAuthenticationFilter;
import com.xyrality.wotter.rest.WOtterAPIDefinitionV1;

import er.extensions.appserver.ERXApplication;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;

public class Application extends WOJerseyApp {

	private static final Class[] API_CLASSES = new Class[] { WOtterAPIDefinitionV1.class };

	public static void main(final String... args) {
		WOJerseyAppLauncher. main(args, () -> {
			ERXApplication.main(args, Application.class);
		}, API_CLASSES);
	}

	public Application() {
		startJersey();
	}

	@Override
	protected void configureAdditionalFeatures(final ResourceConfig config) {
		config
				.register(new AuthValueFactoryProvider.Binder<>(Account.class))
				.register(new AuthDynamicFeature(new AccountAuthenticationFilter()))
		;
	}

	@Override
	protected String swaggerResourcePackages() {
		return WOtterAPIDefinitionV1.class.getPackage().getName();
	}

	@Override
	protected Class _sessionClass() {
		return WOSession.class;
	}
}
