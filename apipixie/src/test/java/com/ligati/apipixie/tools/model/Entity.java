package com.ligati.apipixie.tools.model;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class Entity {
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
