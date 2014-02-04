package com.ligati.apipixie;

import com.ligati.apipixie.exception.APIParsingException;
import com.ligati.apipixie.http.APIHttpManager;
import com.ligati.apipixie.tools.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class APIService<T, K> {
	private static final Logger logger = Logger.getLogger(APIService.class);

	private final APIPixie pixie;
	private final String entityName;
	private final String entityUrl;
	private final APIHolder<T, K> holder;
	private final APIHttpManager http;

	public APIService(APIPixie pixie, Class<T> clazz, APIHttpManager http) {
		this.pixie = pixie;
		this.holder = new APIHolder<>(clazz, pixie.isFeatureEnabled(APIPixieFeature.FAIL_ON_UNKNOWN_PROPERTIES));
		this.http = http;
		this.entityName = clazz.getSimpleName();
		this.entityUrl = this.getEntityUrl(clazz);
	}

	private String getEntityUrl(Class<T> clazz) {
		String annotationUrl = AnnotationUtil.getEntityUrl(clazz);
		if (annotationUrl != null)
			return annotationUrl;
		return UrlUtil.plural(clazz.getSimpleName().toLowerCase());
	}

	public List<T> getAll() {
		logger.debug("Getting all the " + entityName);
		JSONArray array = this.http.getArray(this.buildUrl(""));
		return this.jsonArrayToEntities(array);
	}

	public T get(K id) {
		logger.debug("Getting the " + entityName + "#" + id);
		JSONObject json = this.http.getObject(this.buildUrl(id.toString()));
		return this.jsonObjectToEntity(json);
	}

	public T post(T entity) {
		logger.debug("Posting a " + entityName);
		JSONObject json = entityToJsonObject(entity);
		json = this.http.postObject(this.buildUrl(""), json);
		return this.jsonObjectToEntity(json);
	}

	public T put(T entity) {
		logger.debug("Putting a " + entityName);
		K id = this.holder.getId(entity);
		JSONObject json = entityToJsonObject(entity);
		json = this.http.putObject(this.buildUrl(id.toString()), json);
		return this.jsonObjectToEntity(json);
	}

	public void delete(T entity) {
		Preconditionner.checkNotNull(entity, "The given entity is null.");
		K id = this.holder.getId(entity);
		Preconditionner.checkNotNull(id, "The given entity has no id.");
		this.deleteById(id);
	}

	public void deleteById(K id) {
		logger.debug("Deleting the " + entityName + "#" + id);
		Preconditionner.checkNotNull(id, "The given id is null.");
		this.http.deleteObject(this.buildUrl(id.toString()));
	}

	private String buildUrl(String path) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.pixie.getAPIUrl());
		sb.append("/");
		sb.append(this.entityUrl);
		if (path != null && !path.isEmpty()) {
			sb.append("/");
			sb.append(path);
		}
		return sb.toString();
	}

	private List<T> jsonArrayToEntities(JSONArray array) {
		List<T> list = new LinkedList<>();
		for (int i = 0; i < array.length(); i++) {
			try {
				JSONObject json = array.getJSONObject(i);
				T entity = this.jsonObjectToEntity(json);
				list.add(entity);
			} catch (JSONException e) {
				if (logger.isDebugEnabled())
					e.printStackTrace();
				throw new APIParsingException("An error occurred while reading the json array", e);
			}
		}
		return list;
	}

	protected T jsonObjectToEntity(JSONObject json) {
		T entity = this.holder.create();
		for (String name : JSONObject.getNames(json)) {
			try {
				ComplexField complexField = this.holder.getComplexField(name);
				if (complexField != null)
					entity = this.processComplexFieldJsonToEntity(complexField, entity, json, name);
				else
					entity = this.holder.set(entity, name, json.get(name));
			} catch (JSONException | InstantiationException | IllegalAccessException e) {
				if (logger.isDebugEnabled())
					e.printStackTrace();
				throw new APIParsingException("An error occurred while reading the json property: " + name, e);
			}
		}
		return entity;
	}

	private T processComplexFieldJsonToEntity(ComplexField complexField, T entity, JSONObject json, String propertyName) throws InstantiationException, IllegalAccessException, JSONException {
		switch (complexField.getType()) {
			case BASIC_COLLECTION:
				return this.processBasicCollectionJsonToEntity(complexField, entity, json, propertyName);
			case ENTITY_COLLECTION:
				return this.processEntityCollectionJsonToEntity(complexField, entity, json, propertyName);
			case NESTED_ENTITY:
				return this.processNestedEntityJsonToEntity(complexField, entity, json, propertyName);
		}
		throw new NotImplementedException();
	}

	@SuppressWarnings("unchecked")
	private T processBasicCollectionJsonToEntity(ComplexField complexField, T entity, JSONObject json, String propertyName) throws InstantiationException, IllegalAccessException, JSONException {
		JSONArray array = json.getJSONArray(propertyName);
		Collection collection = complexField.getCollectionClass().newInstance();
		for (int i = 0; i < array.length(); i++)
			collection.add(array.get(i));
		return this.holder.set(entity, propertyName, collection);
	}

	@SuppressWarnings("unchecked")
	private T processEntityCollectionJsonToEntity(ComplexField complexField, T entity, JSONObject json, String propertyName) throws InstantiationException, IllegalAccessException, JSONException {
		JSONArray array = json.getJSONArray(propertyName);
		Collection collection = complexField.getCollectionClass().newInstance();
		APIService service = new APIService(pixie, complexField.getClazz(), http);
		for (int i = 0; i < array.length(); i++)
			collection.add(service.jsonObjectToEntity(array.getJSONObject(i)));
		return this.holder.set(entity, propertyName, collection);
	}

	@SuppressWarnings("unchecked")
	private T processNestedEntityJsonToEntity(ComplexField complexField, T entity, JSONObject json, String propertyName) throws InstantiationException, IllegalAccessException, JSONException {
		JSONObject nestedJSON = json.getJSONObject(propertyName);
		APIService service = new APIService(pixie, complexField.getClazz(), http);
		Object nestedEntity = service.jsonObjectToEntity(nestedJSON);
		return this.holder.set(entity, propertyName, nestedEntity);
	}

	protected JSONObject entityToJsonObject(T entity) {
		JSONObject json = new JSONObject();
		for (String propertyName : this.holder.getPropertiesNames())
			try {
				ComplexField complexField = this.holder.getComplexField(propertyName);
				if (complexField != null)
					json.put(propertyName, this.processComplexFieldEntityToJson(complexField, entity, propertyName));
				else
					json.put(propertyName, this.holder.get(entity, propertyName));
			} catch (JSONException e) {
				if (logger.isDebugEnabled())
					e.printStackTrace();
				throw new APIParsingException("An error occurred while reading the entity field: " + propertyName, e);
			}
		return json;
	}

	private Object processComplexFieldEntityToJson(ComplexField complexField, T entity, String propertyName) throws JSONException {
		switch (complexField.getType()) {
			case BASIC_COLLECTION:
				return this.processBasicCollectionEntityToJson(complexField, entity, propertyName);
			case ENTITY_COLLECTION:
				return this.processEntityCollectionEntityToJson(complexField, entity, propertyName);
			case NESTED_ENTITY:
				return this.processNestedEntityToJson(complexField, entity, propertyName);
		}
		throw new NotImplementedException();
	}

	private JSONArray processBasicCollectionEntityToJson(ComplexField complexField, T entity, String propertyName) {
		JSONArray array = new JSONArray();
		Object nestedObjects = this.holder.get(entity, propertyName);
		if (nestedObjects == null)
			return null;
		for (Object nestedObject : complexField.getCollectionClass().cast(nestedObjects))
			array.put(nestedObject);
		return array;
	}

	@SuppressWarnings("unchecked")
	private JSONArray processEntityCollectionEntityToJson(ComplexField complexField, T entity, String propertyName) {
		JSONArray array = new JSONArray();
		Object nestedEntities = this.holder.get(entity, propertyName);
		if (nestedEntities == null)
			return null;
		APIService service = new APIService(pixie, complexField.getClazz(), http);
		for (Object nestedEntity : complexField.getCollectionClass().cast(nestedEntities)) {
			JSONObject nestedJson = service.entityToJsonObject(nestedEntity);
			array.put(nestedJson);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	private JSONObject processNestedEntityToJson(ComplexField complexField, T entity, String propertyName) throws JSONException {
		Object nestedEntity = this.holder.get(entity, propertyName);
		if (nestedEntity == null)
			return null;
		APIService service = new APIService(pixie, complexField.getClazz(), http);
		return service.entityToJsonObject(nestedEntity);
	}
}
