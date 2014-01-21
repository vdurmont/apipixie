package com.ligati.apipixie.http;

import com.ligati.apipixie.exception.APIHTTPException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

public class APIHttpUtil {
	private static final Logger logger = Logger.getLogger(APIHttpUtil.class);

	public static <T> T performGet(Class<T> clazz, String url, Header... headers) {
		HttpGet get = new HttpGet(url);
		return performRequest(get, clazz, headers);
	}

	public static <T> T performPut(Class<T> clazz, String url, JSONObject json, Header... headers) {
		HttpPut put = new HttpPut(url);
		put.setEntity(new StringEntity(json.toString(), Charset.forName("UTF-8")));
		put.setHeader("Content-Type", "application/json");
		return performRequest(put, clazz, headers);
	}

	public static <T> T performPost(Class<T> clazz, String url, JSONObject json, Header... headers) {
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(json.toString(), Charset.forName("UTF-8")));
		post.setHeader("Content-Type", "application/json");
		return performRequest(post, clazz, headers);
	}

	public static <T> T performDelete(String url, Header... headers) {
		HttpDelete delete = new HttpDelete(url);
		return performRequest(delete, null, headers);
	}

	private static <T> T performRequest(HttpUriRequest req, Class<T> clazz, Header... headers) {
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			addHeaders(req, headers);
			HttpResponse res = client.execute(req);
			analyzeStatusCode(res, req);
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

	private static void addHeaders(HttpUriRequest req, Header... headers) {
		if (headers != null)
			for (Header header : headers)
				req.addHeader(header);
	}

	private static void analyzeStatusCode(HttpResponse res, HttpUriRequest req) {
		String url = req.getURI().toString();
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
					.getReasonPhrase(), content, url, req);
		}
	}
}
