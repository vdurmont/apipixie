package com.ligati.apipixie;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ligati.apipixie.exception.APIParsingException;
import com.ligati.apipixie.http.APIHttpManager;
import com.ligati.apipixie.tools.APIHolder;
import com.ligati.apipixie.tools.AnnotationUtil;
import com.ligati.apipixie.tools.UrlUtil;

public class APIService<T, K> {
	private static final Logger logger = Logger.getLogger(APIService.class);

	private final APIPixie pixie;
	private final String entityName;
	private final String entityUrl;
	private final APIHolder<T> holder;
	private final APIHttpManager http;

	public APIService(APIPixie pixie, Class<T> clazz, APIHttpManager http) {
		this.pixie = pixie;
		this.holder = new APIHolder<T>(clazz);
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
		return this.jsonArrayToEntity(array);
	}

	public T get(K id) {
		logger.debug("Getting the " + entityName + "#" + id);
		JSONObject json = this.http.getObject(this.buildUrl(id.toString()));
		return this.jsonObjectToEntity(json);
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

	private List<T> jsonArrayToEntity(JSONArray array) {
		List<T> list = new LinkedList<>();
		for (int i = 0; i < array.length(); i++) {
			try {
				JSONObject json = array.getJSONObject(i);
				T entity = this.jsonObjectToEntity(json);
				list.add(entity);
			} catch (JSONException e) {
				throw new APIParsingException(
						"An error occurred while reading the json array", e);
			}
		}
		return list;
	}

	private T jsonObjectToEntity(JSONObject json) {
		T entity = this.holder.create();
		for (String name : JSONObject.getNames(json)) {
			try {
				entity = this.holder.set(entity, name, json.get(name));
			} catch (JSONException e) {
				throw new APIParsingException(
						"An error occurred while reading the json property: "
								+ name, e);
			}
		}
		return entity;
	}
}
