package com.ligati.apipixie;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ligati.apipixie.model.Entity;

@RunWith(JUnit4.class)
public class APIPixieTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void getService_returns_the_associated_service() {
		// GIVEN
		APIPixie pixie = new APIPixie();

		// WHEN
		APIService<Entity> service = pixie.getService(Entity.class);

		// THEN
		assertNotNull(service);
	}
}
