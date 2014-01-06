package com.ligati.apipixie.tools;

import com.ligati.apipixie.annotation.APIId;
import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.exception.APIParsingException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class APIHolder<T, K> {
	private static final Logger logger = Logger.getLogger(APIHolder.class);

	private final Class<T> clazz;
	private final Map<String, Method> setters;
	private final Map<String, Method> getters;
	private final Set<String> propertiesNames;
	private Method idGetter;

	public APIHolder(Class<T> clazz) {
		this.clazz = clazz;
		AnnotationUtil.getEntityAnnotation(clazz);
		this.setters = new HashMap<>();
		this.getters = new HashMap<>();
		this.propertiesNames = new HashSet<>();
		this.extractMethods(clazz);
	}

	private void extractMethods(Class<T> clazz) {
		Map<Field, Method> idCandidates = new HashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			boolean isIdCandidate = "id".equals(field.getName()) || AnnotationUtil.hasAnnotation(field, APIId.class);

			String fieldName = this.getFieldNameForMethod(field);
			this.propertiesNames.add(field.getName());

			// Setter
			Class<?> type = field.getType();
			String setterName = "set" + fieldName;
			try {
				Method setter = clazz.getMethod(setterName, type);
				this.setters.put(field.getName(), setter);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new APIConfigurationException(
						"An error occurred while searching for the setter: " + setterName + "(" + type + ")", e);
			}

			// Getter
			String getterName = "get" + fieldName;
			try {
				Method getter = clazz.getMethod(getterName);
				this.getters.put(field.getName(), getter);
				if (isIdCandidate)
					idCandidates.put(field, getter);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new APIConfigurationException(
						"An error occurred while searching for the getter: " + getterName + "()", e);
			}
		}

		this.resolveId(idCandidates);
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

	private void resolveId(Map<Field, Method> candidates) {
		if (candidates.size() == 0) {
			// No candidates => error
			throw new APIConfigurationException("No id property nor @APIId annotation found in the entity.");
		} else if (candidates.size() == 1) {
			// Only one candidate => we found it!
			Iterator<Method> ite = candidates.values().iterator();
			this.idGetter = ite.next();
		} else if (candidates.size() == 2) {
			// It may be an id and an annotation
			Field firstField = null;
			Boolean firstFieldHasAnnotation = null;
			for (Field field : candidates.keySet()) {
				if (firstField == null) {
					// First element
					firstField = field;
					firstFieldHasAnnotation = AnnotationUtil.hasAnnotation(field, APIId.class);
				} else {
					// Second element
					if (firstFieldHasAnnotation && AnnotationUtil.hasAnnotation(field, APIId.class))
						throw new APIConfigurationException("Too many candidates to be an id in the entity.");
					else if (firstFieldHasAnnotation)
						this.idGetter = candidates.get(firstField);
					else
						this.idGetter = candidates.get(field);
				}
			}
		} else {
			throw new APIConfigurationException("Too many candidates to be an id in the entity.");
		}
	}

	public T create() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
			throw new APIConfigurationException(
					"An error occurred while calling the default constructor of the entity "
							+ clazz, e);
		}
	}


	public T set(T entity, String name, Object value) {
		if (entity == null)
			throw new APIParsingException("Null entity");
		Method setter = this.setters.get(name);
		if (setter == null)
			throw new APIParsingException("Unknown property: " + name);
		else
			try {
				if (value instanceof Integer) {
					// Fix for long values
					Method getter = this.getters.get(name);
					if (Long.class.equals(getter.getReturnType()))
						setter.invoke(entity, Long.valueOf((int) value));
				} else
					setter.invoke(entity, value);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				if (logger.isDebugEnabled())
					e.printStackTrace();
				throw new APIParsingException("Error while setting the property: " + name, e);
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
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				if (logger.isDebugEnabled())
					e.printStackTrace();
				throw new APIParsingException("Unknown property: " + name, e);
			}
	}

	@SuppressWarnings("unchecked")
	public K getId(T entity) {
		try {
			return (K) this.idGetter.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
			throw new APIParsingException("Error while retrieving the entity id.", e);
		}
	}

	public Set<String> getPropertiesNames() {
		return this.propertiesNames;
	}
}
