package com.ligati.apipixie.http;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ligati.apipixie.exception.APIHTTPException;

public class DefaultAPIHttpManager implements APIHttpManager {
	@Override
	public JSONArray getArray(String url) {
		return performGet(JSONArray.class, url);
	}

	@Override
	public JSONObject getObject(String url) {
		return performGet(JSONObject.class, url);
	}

	private static <T> T performGet(Class<T> clazz, String url) {
		HttpGet get = new HttpGet(url);
		return performRequest(get, clazz);
	}

	private static <T> T performRequest(HttpUriRequest req, Class<T> clazz) {
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse res = client.execute(req);
			analyzeStatusCode(res);
			// case of DELETE for 204 http status codes
			if (clazz == null)
				return null;
			InputStream stream = res.getEntity().getContent();
			String jsonStr = IOUtils.toString(stream);
			return clazz.getConstructor(String.class).newInstance(jsonStr);
		} catch (IOException | NoSuchMethodException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | SecurityException e) {
			throw new APIHTTPException(
					"An unexpected exception occurred while performing a request",
					e);
		}
	}

	private static void analyzeStatusCode(HttpResponse res) {
		int statusCode = res.getStatusLine().getStatusCode();
		if (100 <= statusCode && statusCode < 200) {
			// TODO do something?
		} else if (300 <= statusCode && statusCode < 400) {
			// TODO do something?
		} else if (400 <= statusCode) {
			String content = null;
			try {
				content = IOUtils.toString(res.getEntity().getContent());
			} catch (Exception e) {
			}
			throw new APIHTTPException(statusCode, res.getStatusLine()
					.getReasonPhrase(), content);
		}
	}
}
