package com.ligati.apipixie.http;

import org.json.JSONArray;
import org.json.JSONObject;

public class DefaultAPIHttpManager implements APIHttpManager {
	@Override
	public JSONArray getArray(String url) {
		return APIHttpUtil.performGet(JSONArray.class, url);
	}

	@Override
	public JSONObject getObject(String url) {
		return APIHttpUtil.performGet(JSONObject.class, url);
	}

	@Override
	public JSONObject putObject(String url, JSONObject json) {
		return APIHttpUtil.performPut(JSONObject.class, url, json);
	}

	@Override
	public JSONObject postObject(String url, JSONObject json) {
		return APIHttpUtil.performPost(JSONObject.class, url, json);
	}

	@Override
	public void deleteObject(String url) {
		APIHttpUtil.performDelete(url);
	}
}
