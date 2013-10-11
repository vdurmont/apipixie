package com.ligati.apipixie.tools;

import com.ligati.apipixie.annotation.APIEntity;
import com.ligati.apipixie.exception.APIConfigurationException;

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
}
