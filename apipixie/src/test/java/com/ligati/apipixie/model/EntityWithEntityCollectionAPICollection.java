package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APICollection;
import com.ligati.apipixie.annotation.APIEntity;

import java.util.List;

@APIEntity
public class EntityWithEntityCollectionAPICollection {
	private Long id;
	@APICollection(mappedClass = Entity.class)
	private List<Entity> entities;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
}
