package com.ligati.apipixie.model;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class EntityWithNestedEntity {
	private Long id;
	private Entity entity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}
