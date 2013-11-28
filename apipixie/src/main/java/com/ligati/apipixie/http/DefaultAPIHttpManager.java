package com.ligati.apipixie.http;

import com.ligati.apipixie.exception.APIHTTPException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

public class DefaultAPIHttpManager implements APIHttpManager {
	private static final Logger logger = Logger.getLogger(DefaultAPIHttpManager.class);

	@Override
	public JSONArray getArray(String url) {
		return performGet(JSONArray.class, url);
	}

	@Override
	public JSONObject getObject(String url) {
		return performGet(JSONObject.class, url);
	}

	@Override
	public JSONObject putObject(String url, JSONObject json) {
		return performPut(JSONObject.class, url, json);
	}

	@Override
	public JSONObject postObject(String url, JSONObject json) {
		return performPost(JSONObject.class, url, json);
	}

	@Override
	public void deleteObject(String url) {
		performDelete(url);
	}

	private static <T> T performGet(Class<T> clazz, String url) {
		HttpGet get = new HttpGet(url);
		return performRequest(get, clazz);
	}

	private static <T> T performPut(Class<T> clazz, String url, JSONObject json) {
		HttpPut put = new HttpPut(url);
		put.setEntity(new StringEntity(json.toString(), Charset.forName("UTF-8")));
		put.setHeader("Content-Type", "application/json");
		return performRequest(put, clazz);
	}

	private static <T> T performPost(Class<T> clazz, String url, JSONObject json) {
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(json.toString(), Charset.forName("UTF-8")));
		post.setHeader("Content-Type", "application/json");
		return performRequest(post, clazz);
	}

	private static <T> T performDelete(String url) {
		HttpDelete delete = new HttpDelete(url);
		return performRequest(delete, null);
	}

	private static <T> T performRequest(HttpUriRequest req, Class<T> clazz) {
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse res = client.execute(req);
			analyzeStatusCode(res, req.getURI().toString());
			// case of DELETE for 204 http status codes
			if (clazz == null)
				return null;
			InputStream stream = res.getEntity().getContent();
			String jsonStr = IOUtils.toString(stream);
			logger.debug("String received from the API: " + jsonStr);
			return clazz.getConstructor(String.class).newInstance(jsonStr);
		} catch (IOException | NoSuchMethodException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | SecurityException e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
			throw new APIHTTPException("An unexpected exception occurred while performing a request.", e);
		}
	}

	private static void analyzeStatusCode(HttpResponse res, String url) {
		int statusCode = res.getStatusLine().getStatusCode();
		logger.debug("Status code received from the API: " + statusCode);
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
					.getReasonPhrase(), content, url);
		}
	}
}
