package com.ligati.apipixie.tools;

import com.ligati.apipixie.annotation.APICollection;
import com.ligati.apipixie.annotation.APIEntity;
import com.ligati.apipixie.exception.APIConfigurationException;

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
		String url = annotation.url();
		return url.isEmpty() ? null : url;
	}

	protected static APICollection getCollectionAnnotation(Field field) {
		APICollection annotation = field.getAnnotation(APICollection.class);
		if (annotation == null)
			throw new APIConfigurationException("The field " + field.getName()
					+ " is not an APICollection.");
		return annotation;
	}

	public static boolean isAPIEntityCollection(Field field) {
		APICollection annotation = getCollectionAnnotation(field);
		Class<?> collectionType = annotation.mappedClass();
		if (TypeUtil.isBasicType(collectionType))
			return false;
		getEntityAnnotation(collectionType); // We check that the class is correctly annotated.
		return true;
	}

	public static boolean referencesAPIEntity(Field field) {
		APIEntity annotation = field.getType().getAnnotation(APIEntity.class);
		return annotation != null;
	}
}
