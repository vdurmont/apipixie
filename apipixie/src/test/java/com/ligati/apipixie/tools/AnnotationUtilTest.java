package com.ligati.apipixie.tools;

import com.ligati.apipixie.annotation.APIEntity;
import com.ligati.apipixie.annotation.APIId;
import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.model.HasAnnotation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

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

	@Test
	public void hasAnnotation_if_the_annotation_is_present_returns_true() {
		// GIVEN
		Class<?> annotationClazz = APIId.class;
		Field field = getField("first", HasAnnotation.class);

		// WHEN
		boolean hasAnnotation = AnnotationUtil.hasAnnotation(field, annotationClazz);

		// THEN
		assertTrue(hasAnnotation);
	}

	@Test
	public void hasAnnotation_if_the_annotation_is_not_present_returns_false() {
		// GIVEN
		Class<?> annotationClazz = APIId.class;
		Field field = getField("second", HasAnnotation.class);

		// WHEN
		boolean hasAnnotation = AnnotationUtil.hasAnnotation(field, annotationClazz);

		// THEN
		assertFalse(hasAnnotation);
	}

	private Field getField(String name, Class<?> clazz) {
		for (Field f : clazz.getDeclaredFields())
			if (name.equals(f.getName()))
				return f;
		return null;
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
