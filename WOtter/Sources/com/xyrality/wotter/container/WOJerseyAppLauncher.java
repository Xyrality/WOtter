package com.xyrality.wotter.container;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.stream.Collectors;

import org.glassfish.jersey.server.ResourceConfig;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

/**
 * Launcher helper class. This checks if the application actually is run with two parameters,
 * swagger.json/yaml and a filename. If so, just dump the swagger file instead of actually launching
 * the application
 */
public class WOJerseyAppLauncher {
	public static void main(final String[] argv, final Runnable main, final Class<?>... apiClasses) {
		if (argv.length == 2 && argv[0].startsWith("swagger.")) {
			mainDumpSwaggerAPI(argv[0], argv[1], apiClasses);
		} else {
			main.run();
		}
	}

	private static void mainDumpSwaggerAPI(final String resourceName, final String outputFile, final Class<?>[] apiClasses) {
		final String apiPackagesString = Joiner.on(',').join(
				Lists.newArrayList(apiClasses).stream()
						.map(Class::getPackage)
						.map(Package::getName)
						.collect(Collectors.toSet()));

		WOJerseyApp.createSwaggerConfiguration(apiPackagesString, "localhost", "DummyServer", ".woa");
		final ResourceConfig config = WOJerseyApp.createJerseyApp();
		final WOJerseyRequestHandler jerseyRequestHandler = new WOJerseyRequestHandler(config);
		final WORequest request = new WORequest("GET", "/XYRALITY/WebObjects/DummyServer.woa/rs/" + resourceName, "HTTP/1.0", Maps.newHashMap(), null, Maps.newHashMap());
		final WOResponse response = jerseyRequestHandler.handleRequest(request);

		final File swaggerOutputFile = new File(outputFile);
		try (final FileOutputStream fos = new FileOutputStream(swaggerOutputFile);
				final OutputStreamWriter outWriter = new OutputStreamWriter(fos)) {
			outWriter.write(response.contentString());
			System.out.println("Dumped " + resourceName + " to " + outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
