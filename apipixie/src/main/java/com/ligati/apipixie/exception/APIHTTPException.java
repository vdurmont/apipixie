package com.ligati.apipixie.exception;

public class APIHTTPException extends APIPixieException {
	private static final long serialVersionUID = 4264389495539416937L;

	private Integer statusCode;
	private String reasonPhrase;
	private String content;

	public APIHTTPException(int statusCode, String reasonPhrase, String content) {
		super("A " + statusCode
				+ " status code was returned when performing the request.");
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
		this.content = content;
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
}
