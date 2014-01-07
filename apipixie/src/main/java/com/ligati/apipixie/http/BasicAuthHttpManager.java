package com.ligati.apipixie.http;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;

public class BasicAuthHttpManager implements APIHttpManager {
	private final Header auth;

	public BasicAuthHttpManager(String username, String password) {
		byte[] credentials = (username + ":" + password).getBytes();
		String encoded = new String(Base64.encodeBase64(credentials));
		this.auth = new BasicHeader("Authorization", "Basic " + encoded);
	}

	private Header getAuthHeader() {
		return auth;
	}

	@Override
	public JSONArray getArray(String url) {
		return APIHttpUtil.performGet(JSONArray.class, url, getAuthHeader());
	}

	@Override
	public JSONObject getObject(String url) {
		return APIHttpUtil.performGet(JSONObject.class, url, getAuthHeader());
	}

	@Override
	public JSONObject putObject(String url, JSONObject json) {
		return APIHttpUtil.performPut(JSONObject.class, url, json, getAuthHeader());
	}

	@Override
	public JSONObject postObject(String url, JSONObject json) {
		return APIHttpUtil.performPost(JSONObject.class, url, json, getAuthHeader());
	}

	@Override
	public void deleteObject(String url) {
		APIHttpUtil.performDelete(url, getAuthHeader());
	}
}
