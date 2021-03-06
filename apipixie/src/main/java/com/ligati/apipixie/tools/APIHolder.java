package com.ligati.apipixie.tools;

import com.ligati.apipixie.annotation.APICollection;
import com.ligati.apipixie.annotation.APIEntity;
import com.ligati.apipixie.annotation.APIId;
import com.ligati.apipixie.annotation.APISuperClass;
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
	private final Map<String, ComplexField> complexFields;
	private final Set<String> propertiesNames;
	private final boolean failOnUnknownProperties;
	private Method idGetter;

	public APIHolder(Class<T> clazz, boolean failOnUnknownProperties) {
		this.clazz = clazz;
		this.failOnUnknownProperties = failOnUnknownProperties;
		AnnotationUtil.getEntityAnnotation(clazz);
		this.setters = new HashMap<>();
		this.getters = new HashMap<>();
		this.complexFields = new HashMap<>();
		this.propertiesNames = new HashSet<>();
		this.extractMethods(clazz);
	}

	private void extractMethods(Class<T> clazz) {
		Map<Field, Method> idCandidates = new HashMap<>();
		List<Field> fields = getFields(clazz);
		for (Field field : fields) {
			if (!TypeUtil.isBasicType(field.getType())) {
				// If it is not a basic type, there are 5 possibilities:
				ComplexField complexField = null;

				// 1) It's a collection (basic or APIEntity)
				boolean isAPICollection = field.isAnnotationPresent(APICollection.class);
				if (isAPICollection) {
					ComplexField.FieldType collectionType = AnnotationUtil.isAPIEntityCollection(field) ? ComplexField.FieldType.ENTITY_COLLECTION : ComplexField.FieldType.BASIC_COLLECTION;
					Class<?> mappedClass = AnnotationUtil.getCollectionAnnotation(field).mappedClass();
					complexField = new ComplexField(collectionType, mappedClass, (Class<? extends Collection>) field.getType());
				}

				// 3) It's a date
				// TODO Handle the dates (an @APITemporal annotation?)

				// 4) It's an enumerate
				// TODO Handle the enumerates (an @APIEnum annotation?)

				// 5) It's a nested APIEntity
				if (AnnotationUtil.referencesAPIEntity(field))
					complexField = new ComplexField(ComplexField.FieldType.NESTED_ENTITY, field.getType());

				// 6) It's an error!
				if (complexField == null)
					throw new APIConfigurationException("The field " + field.getName() + " is not a basic type but there is no annotation defining how it should be treated.");

				this.complexFields.put(field.getName(), complexField);
			}

			boolean isIdCandidate = "id".equals(field.getName()) || field.isAnnotationPresent(APIId.class);

			String fieldName = this.getFieldNameForMethod(field);
			this.propertiesNames.add(field.getName());

			// Setter
			Class<?> type = field.getType();
			String setterName = "set" + fieldName;
			try {
				Method setter = clazz.getMethod(setterName, type);
				this.setters.put(field.getName(), setter);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new APIConfigurationException("An error occurred while searching for the setter: " + setterName + "(" + type + ") in the class " + clazz, e);
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
						"An error occurred while searching for the getter: " + getterName + "() in the class " + clazz, e);
			}
		}

		this.resolveId(idCandidates);
	}

	private static List<Field> getFields(Class<?> clazz) {
		List<Field> fields = new LinkedList<>();
		Set<String> names = new HashSet<>();
		Class<?> tmp = clazz;
		while (tmp.isAnnotationPresent(APISuperClass.class) ||
				tmp.isAnnotationPresent(APIEntity.class)) {
			for (Field field : tmp.getDeclaredFields()) {
				fields.add(field);
				names.add(field.getName());
			}
			tmp = tmp.getSuperclass();
		}
		if (fields.size() != names.size()) {
			Set<String> duplicates = getDuplicatesProperties(fields);
			throw new APIConfigurationException("Duplicate propert" + (duplicates.size() > 1 ? "ies" : "y") + " in type hierarchy of class " + clazz + ": " + duplicates);
		}
		return fields;
	}

	private static Set<String> getDuplicatesProperties(List<Field> fields) {
		Set<String> duplicates = new HashSet<>();
		List<String> names = new LinkedList<>();
		for (Field field : fields) {
			String name = field.getName();
			if (names.contains(name))
				duplicates.add(name);
			names.add(name);
		}
		return duplicates;
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
					firstFieldHasAnnotation = field.isAnnotationPresent(APIId.class);
				} else {
					// Second element
					if (firstFieldHasAnnotation && field.isAnnotationPresent(APIId.class))
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
		if (name == null || name.isEmpty())
			throw new APIParsingException("Invalid property name (null or empty)");
		Method setter = this.setters.get(name);
		if (setter == null && this.failOnUnknownProperties)
			throw new APIParsingException("Unknown property: " + name);
		else if (setter != null)
			try {
				Method getter = this.getters.get(name);
				if (value instanceof Integer && Long.class.equals(getter.getReturnType()))
					setter.invoke(entity, Long.valueOf((int) value));
				else
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

	public ComplexField getComplexField(String name) {
		return this.complexFields.get(name);
	}
}
