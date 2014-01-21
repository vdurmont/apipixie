package com.ligati.apipixie.tools;

import com.ligati.apipixie.exception.APIPixieException;

import java.util.LinkedList;
import java.util.List;

public class TypeUtil {
	private static final List<Class<?>> BASIC_CLASSES = getClasses();

	private static List<Class<?>> getClasses() {
		List<Class<?>> classes = new LinkedList<>();
		classes.add(String.class);
		classes.add(Float.class);
		classes.add(Long.class);
		classes.add(Integer.class);
		classes.add(Double.class);
		classes.add(Character.class);
		classes.add(Boolean.class);
		classes.add(Short.class);
		classes.add(Byte.class);
		return classes;
	}

	public static boolean isBasicType(Class<?> type) {
		if (type == null)
			throw new APIPixieException("Null type given to the method TypeUtil#isBasicType");
		return BASIC_CLASSES.contains(type);
	}
}
