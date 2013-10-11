package com.ligati.apipixie.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.exception.APIParsingException;

public class APIHolder<T> {
	private final Class<T> clazz;
	private final Map<String, Method> setters;
	private final Map<String, Method> getters;

	public APIHolder(Class<T> clazz) {
		this.clazz = clazz;
		AnnotationUtil.getEntityAnnotation(clazz);
		this.setters = new HashMap<>();
		this.getters = new HashMap<>();
		this.extractMethods(clazz);
	}

	private void extractMethods(Class<T> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			String fieldName = this.getFieldNameForMethod(field);

			// Setter
			Class<?> type = field.getType();
			String setterName = "set" + fieldName;
			try {
				Method setter = clazz.getMethod(setterName, type);
				this.setters.put(field.getName(), setter);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new APIConfigurationException(
						"An error occurred while searching for the setter: "
								+ setterName + "(" + type + ")", e);
			}

			// Getter
			String getterName = "get" + fieldName;
			try {
				Method getter = clazz.getMethod(getterName);
				this.getters.put(field.getName(), getter);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new APIConfigurationException(
						"An error occurred while searching for the getter: "
								+ getterName + "()", e);
			}
		}
	}

	private String getFieldNameForMethod(Field field) {
		String fieldName = field.getName();
		// Capitalize the first letter
		String name = fieldName.substring(0, 1).toUpperCase();
		// Add the following letters
		if (fieldName.length() > 1)
			name += fieldName.substring(1);
		return name;
	}

	public T create() {
		T entity = null;
		try {
			entity = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new APIConfigurationException(
					"An error occurred while calling the default constructor of the entity "
							+ clazz, e);
		}
		return entity;
	}

	public T set(T entity, String name, Object value) {
		if (entity == null)
			throw new APIParsingException("Null entity");
		Method setter = this.setters.get(name);
		if (setter == null)
			throw new APIParsingException("Unknown property: " + name);
		else
			try {
				setter.invoke(entity, value);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new APIParsingException("Unknown property: " + name, e);
			}
		return entity;
	}

	public Object get(T entity, String name) {
		if (entity == null)
			throw new APIParsingException("Null entity");
		Method getter = this.getters.get(name);
		if (getter == null)
			throw new APIParsingException("Unknown property: " + name);
		else
			try {
				return getter.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new APIParsingException("Unknown property: " + name, e);
			}
	}
}
