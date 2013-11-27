package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APIEntity;
import com.ligati.apipixie.annotation.APIId;

@APIEntity
public class EntityWithSeveralAPIIdAnnotations {
	@APIId
	private String firstProperty;
	@APIId
	private String secondProperty;

	public String getFirstProperty() {
		return firstProperty;
	}

	public void setFirstProperty(String firstProperty) {
		this.firstProperty = firstProperty;
	}

	public String getSecondProperty() {
		return secondProperty;
	}

	public void setSecondProperty(String secondProperty) {
		this.secondProperty = secondProperty;
	}
}
