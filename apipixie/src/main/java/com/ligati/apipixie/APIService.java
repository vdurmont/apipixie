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

public class APIService<T> {
	private static final Logger logger = Logger.getLogger(APIService.class);

	private final String entityName;
	private final APIHolder<T> holder;
	private final APIHttpManager http;

	public APIService(Class<T> clazz, APIHttpManager http) {
		this.holder = new APIHolder<T>(clazz);
		this.http = http;
		this.entityName = clazz.getSimpleName();
	}

	public List<T> getAll() {
		logger.debug("Getting all the " + entityName);
		JSONArray array = this.http.getArray("");
		return this.jsonArrayToEntity(array);
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
