package com.ligati.apipixie.http;

import org.json.JSONArray;
import org.json.JSONObject;

public interface APIHttpManager {
	public JSONArray getArray(String url);

	public JSONObject getObject(String url);
}
