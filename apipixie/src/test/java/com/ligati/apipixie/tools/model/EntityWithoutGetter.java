package com.ligati.apipixie.tools.model;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class EntityWithoutGetter {
	@SuppressWarnings("unused")
	private String text;

	public void setText(String text) {
		this.text = text;
	}
}
