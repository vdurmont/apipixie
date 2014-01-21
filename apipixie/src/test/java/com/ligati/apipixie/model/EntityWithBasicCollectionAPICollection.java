package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APICollection;
import com.ligati.apipixie.annotation.APIEntity;

import java.util.ArrayList;

@APIEntity
public class EntityWithBasicCollectionAPICollection {
	private Long id;
	@APICollection(mappedClass = String.class)
	private ArrayList<String> strings;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArrayList<String> getStrings() {
		return strings;
	}

	public void setStrings(ArrayList<String> strings) {
		this.strings = strings;
	}
}
