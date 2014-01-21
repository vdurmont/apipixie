package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APISuperClass;

@APISuperClass
public class APISuperClassWithId {
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
