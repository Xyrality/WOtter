package com.xyrality.wotter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Before;
import org.junit.Test;

import com.xyrality.wotter.container.ObjectMapperProvider;
import com.xyrality.wotter.eo.Account;
import com.xyrality.wotter.rest.v1.model.WotDTO;

@SuppressWarnings("unchecked")
public class WotsApiTest extends JerseyTest {

	public static class WOtterTestApplication extends com.xyrality.wotter.Application {
		@Override
		protected void configureAdditionalFeatures(final ResourceConfig config) {
			super.configureAdditionalFeatures(config);
			config.register(MockECRequestContextHandler.binderForResourceConfig());
		}
	}

	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
		return new WOJerseyTestContainerFactory(WOtterTestApplication.class);
	}

	@Override
	protected Application configure() {
		return new ResourceConfig();
	}

	@Before
	public void setupWotsApiTest() {
		MockECRequestContextHandler.resetMockEC();
		client().register(ObjectMapperProvider.class);
	}

	@Test
	public void testListEmptyWots() {
		final List<WotDTO> wotsList = target("/v1/wots")
				.request()
				.get(List.class);
		assertTrue(wotsList.isEmpty());
	}

	@Test
	public void testPutWot() {
		Account.createAccount(MockECRequestContextHandler.getCurrentMockEditingContext(), "tester");

		final WotDTO sourceWot = new WotDTO();
		sourceWot.setText("TEST");
		final Entity<WotDTO> sourceWotEntity = Entity.entity(sourceWot, MediaType.APPLICATION_JSON_TYPE);

		client().register(HttpAuthenticationFeature.basic("tester", "tester"));
		final Response createNewWot = target("/v1/wots")
				.request()
				.put(sourceWotEntity);

		final WotDTO wotDTO = createNewWot.readEntity(WotDTO.class);
		assertEquals("tester", wotDTO.getAuthor());
		assertEquals("TEST", wotDTO.getText());
		assertTrue(Duration.between(wotDTO.getPostTime(), Instant.now()).getSeconds() < 60);
	}

	@Test
	public void testPutUnauthenticated() {
		final WotDTO sourceWot = new WotDTO();
		sourceWot.setText("TEST");
		final Entity<WotDTO> sourceWotEntity = Entity.entity(sourceWot, MediaType.APPLICATION_JSON_TYPE);

		final Response createWotResponse = target("/v1/wots")
				.request()
				.put(sourceWotEntity);

		assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, createWotResponse.getStatus());
	}
}
