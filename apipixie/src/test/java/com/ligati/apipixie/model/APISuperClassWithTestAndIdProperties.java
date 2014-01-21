package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APISuperClass;

@APISuperClass
public class APISuperClassWithTestAndIdProperties {
	private Long id;
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
