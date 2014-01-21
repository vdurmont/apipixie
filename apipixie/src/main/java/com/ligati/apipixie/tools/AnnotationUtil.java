package com.ligati.apipixie.tools;

import com.ligati.apipixie.annotation.APIEntity;
import com.ligati.apipixie.exception.APIConfigurationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationUtil {
	protected static APIEntity getEntityAnnotation(Class<?> clazz) {
		APIEntity annotation = clazz.getAnnotation(APIEntity.class);
		if (annotation == null)
			throw new APIConfigurationException("The class " + clazz
					+ " is not an APIEntity.");
		return annotation;
	}

	public static <T> String getEntityUrl(Class<T> clazz) {
		APIEntity annotation = getEntityAnnotation(clazz);
		if (annotation == null)
			return null;
		else {
			String url = annotation.url();
			return url.isEmpty() ? null : url;
		}
	}

	public static boolean hasAnnotation(Field field, Class<?> annotationClazz) {
		Annotation[] annotations = field.getDeclaredAnnotations();
		return hasAnnotation(annotations, annotationClazz);
	}

	public static boolean hasAnnotation(Class<?> clazz, Class<?> annotationClazz) {
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		return hasAnnotation(annotations, annotationClazz);
	}

	private static boolean hasAnnotation(Annotation[] annotations, Class<?> annotationClazz) {
		for (Annotation annotation : annotations)
			if (annotationClazz.equals(annotation.annotationType()))
				return true;
		return false;
	}
}
