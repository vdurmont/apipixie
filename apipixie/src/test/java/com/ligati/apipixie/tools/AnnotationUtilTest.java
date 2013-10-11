package com.ligati.apipixie.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ligati.apipixie.annotation.APIEntity;
import com.ligati.apipixie.exception.APIConfigurationException;

@RunWith(JUnit4.class)
public class AnnotationUtilTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void getEntityUrl_returns_the_url() {
		// GIVEN
		Class<?> clazz = AnnotationEntityTestWithUrl.class;

		// WHEN
		String url = AnnotationUtil.getEntityUrl(clazz);

		// THEN
		assertEquals("myUrl", url);
	}

	@Test
	public void getEntityUrl_without_url_returns_null() {
		// GIVEN
		Class<?> clazz = AnnotationEntityTestWithoutUrl.class;

		// WHEN
		String url = AnnotationUtil.getEntityUrl(clazz);

		// THEN
		assertNull(url);
	}

	@Test
	public void getEntityUrl_without_annotation_fails() {
		// GIVEN
		Class<?> clazz = AnnotationEntityTestNotAnnotated.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		AnnotationUtil.getEntityUrl(clazz);
	}

	@APIEntity(url = "myUrl")
	private class AnnotationEntityTestWithUrl {
	}

	@APIEntity
	private class AnnotationEntityTestWithoutUrl {
	}

	private class AnnotationEntityTestNotAnnotated {
	}
}
