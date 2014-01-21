package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APIEntity;

import java.util.List;

@APIEntity
public class EntityWithBasicCollectionNotAPICollection {
	private Long id;
	private List<String> strings;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}
}
