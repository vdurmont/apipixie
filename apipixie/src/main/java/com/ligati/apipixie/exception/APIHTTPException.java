package com.ligati.apipixie.exception;

public class APIHTTPException extends APIPixieException {
	private static final long serialVersionUID = 4264389495539416937L;

	private Integer statusCode;
	private String reasonPhrase;
	private String content;
	private String url;

	public APIHTTPException(int statusCode, String reasonPhrase, String content, String url) {
		super("A " + statusCode
				+ " status code was returned when performing a request on '"+url+"'");
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
		this.content = content;
		this.url = url;
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
}
