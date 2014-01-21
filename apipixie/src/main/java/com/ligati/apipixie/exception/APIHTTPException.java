package com.ligati.apipixie.exception;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class APIHTTPException extends APIPixieException {
	private static final long serialVersionUID = 4264389495539416937L;

	private Integer statusCode;
	private String reasonPhrase;
	private String content;
	private String url;
	private HttpUriRequest sentRequest;

	public APIHTTPException(int statusCode, String reasonPhrase, String content, String url, HttpUriRequest sentRequest) {
		super("A " + statusCode
				+ " status code was returned when performing a request on '" + url + "'."
				+ " Investigate using getStatusCode, getReasonPhrase, getContent, getUrl, getSentRequest, getSentContent (PUT and POST only) on this exception.");
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
		this.content = content;
		this.url = url;
		this.sentRequest = sentRequest;
	}

	public APIHTTPException(String msg, Exception e) {
		super(msg);
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public String getContent() {
		return content;
	}

	public String getUrl() {
		return url;
	}

	public HttpUriRequest getSentRequest() {
		return sentRequest;
	}

	public JSONObject getSentContent() {
		if (sentRequest == null)
			throw new APIUsageException("We don't have any data on the sent request. Sorry.");
		if (sentRequest instanceof HttpEntityEnclosingRequest) {
			try {
				HttpEntityEnclosingRequest req = (HttpEntityEnclosingRequest) sentRequest;
				return new JSONObject(IOUtils.toString(req.getEntity().getContent()));
			} catch (IOException | JSONException e) {
				throw new APIParsingException("An unexpected error occurred while reading the request content.", e);
			}
		}
		throw new APIUsageException("No content was sent with this request.");
	}
}
