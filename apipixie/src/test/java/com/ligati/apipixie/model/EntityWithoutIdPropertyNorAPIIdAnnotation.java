package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class EntityWithoutIdPropertyNorAPIIdAnnotation {
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}