package com.ligati.apipixie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.model.Entity;

@RunWith(JUnit4.class)
public class APIPixieTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void getService_returns_the_associated_service() {
		// GIVEN
		APIPixie pixie = new APIPixie("http://myapi.com");

		// WHEN
		APIService<Entity, Long> service = pixie.getService(Entity.class);

		// THEN
		assertNotNull(service);
	}

	@Test
	public void setAPIUrl_if_null_apiUrl_fails() {
		// GIVEN
		APIPixie pixie = new APIPixie("http://myapi.com");

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		pixie.setAPIUrl(null);
	}

	@Test
	public void setAPIUrl_if_empty_apiUrl_fails() {
		// GIVEN
		APIPixie pixie = new APIPixie("http://myapi.com");

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		pixie.setAPIUrl("");
	}

	@Test
	public void setAPIUrl_defines_the_apiUrl() {
		// GIVEN
		APIPixie pixie = new APIPixie("http://myapi.com");
		String url = "http://myawesomeurl.com";

		// WHEN
		pixie.setAPIUrl(url);

		// THEN
		assertEquals(url, pixie.getAPIUrl());
	}
}
