package com.xyrality.wotter;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.spi.TestContainer;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

import er.extensions.ERXExtensions;

/**
 * Test container for Jersey test framework, launches the WOApplication
 * subclass and retrieves the Jersey base URL. Note that this only starts
 * the application once, even if requested multiple times as WO does not
 * like to be restarted multiple times within one process. Use per-test
 * processes or per test-class.
 */
public class WOJerseyTestContainer implements TestContainer {

	private final Class<? extends WOApplication> applicationClass;
	private final Object launchLock = new Object();
	private static volatile boolean applicationWasInitialized = false;

	/**
	 * Create a new test container given the application class
	 * @param applicationClass the application class
	 */
	WOJerseyTestContainer(final Class<? extends WOApplication> applicationClass) {
		this.applicationClass = applicationClass;
	}

	@Override
	public ClientConfig getClientConfig() {
		return null;
	}

	@Override
	public URI getBaseUri() {
		try {
			final WOApplication app = Application.application();
			return new URI(app.directConnectURL() + "/rs/");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void start() {
		if (applicationWasInitialized) {
			return;
		}

		// we need this to properly initialize the local direct connect port
		WOApplication._wasMainInvoked = true;

		// register foro notification and launch in background task
		NSNotificationCenter.defaultCenter().addObserver(this, new NSSelector("didFinishLaunching", new Class[] { NSNotification.class }), WOApplication.ApplicationDidFinishLaunchingNotification, null);
		new Thread(() -> {
			ERXExtensions.initApp(applicationClass, new String[] { "-WOAutoOpenInBrowser", "false" });
		}).start();

		// wait until app is launched
		try {
			synchronized (launchLock) {
				launchLock.wait(10L * 1000L);
				if (!applicationWasInitialized) {
					fail("Couldn't initialize application within timeout");
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Callback invoked by WO when the application is launched, declares the
	 * container started
	 * @param notification the WO notification, not used
	 */
	public void didFinishLaunching(final NSNotification notification) {
		synchronized (launchLock) {
			applicationWasInitialized = true;
			launchLock.notify();
		}
	}

	@Override
	public void stop() {
		// intentionally left blank
	}
}
