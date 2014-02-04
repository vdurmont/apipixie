package com.ligati.apipixie.tools;

import java.util.*;

public class ComplexField {
	private FieldType type;
	private Class<?> clazz;
	private Class<? extends Collection> collectionClass;

	public ComplexField(FieldType type, Class<?> clazz) {
		this.type = type;
		this.clazz = clazz;
	}

	public ComplexField(FieldType type, Class<?> clazz, Class<? extends Collection> collectionClass) {
		this(type, clazz);
		this.setCollectionClass(collectionClass);
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends Collection> getCollectionClass() {
		return collectionClass;
	}

	public void setCollectionClass(Class<? extends Collection> collectionClass) {
		if (List.class.equals(collectionClass))
			this.collectionClass = LinkedList.class;
		else if (Set.class.equals(collectionClass))
			this.collectionClass = HashSet.class;
		else
			this.collectionClass = collectionClass;
	}

	public enum FieldType {
		BASIC_COLLECTION, ENTITY_COLLECTION, DATE, NESTED_ENTITY, ENUM
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("ComplexField{");
		sb.append("type=").append(type);
		sb.append(", clazz=").append(clazz);
		sb.append(", collectionClass=").append(collectionClass);
		sb.append('}');
		return sb.toString();
	}
}
