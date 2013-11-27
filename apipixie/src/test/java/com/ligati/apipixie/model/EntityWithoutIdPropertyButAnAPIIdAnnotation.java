package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APIEntity;
import com.ligati.apipixie.annotation.APIId;

@APIEntity
public class EntityWithoutIdPropertyButAnAPIIdAnnotation {
	@APIId
	private String firstProperty;

	public String getFirstProperty() {
		return firstProperty;
	}

	public void setFirstProperty(String firstProperty) {
		this.firstProperty = firstProperty;
	}
}
