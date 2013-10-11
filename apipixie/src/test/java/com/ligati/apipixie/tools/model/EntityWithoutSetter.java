package com.ligati.apipixie.tools.model;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class EntityWithoutSetter {
	private String text;

	public String getText() {
		return text;
	}
}
