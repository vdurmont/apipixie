package com.ligati.apipixie.exception;

public class APIConfigurationException extends APIPixieException {
	private static final long serialVersionUID = 4588555541950970144L;

	public APIConfigurationException(String msg) {
		super(msg);
	}

	public APIConfigurationException(String msg, Exception e) {
		super(msg, e);
	}
}
