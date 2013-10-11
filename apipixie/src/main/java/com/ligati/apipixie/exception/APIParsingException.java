package com.ligati.apipixie.exception;

public class APIParsingException extends APIPixieException {
	private static final long serialVersionUID = -8590543966152737836L;

	public APIParsingException(String msg) {
		super(msg);
	}

	public APIParsingException(String msg, Exception e) {
		super(msg, e);
	}
}
