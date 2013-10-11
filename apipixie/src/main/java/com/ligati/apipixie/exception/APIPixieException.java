package com.ligati.apipixie.exception;

public class APIPixieException extends RuntimeException {
	private static final long serialVersionUID = -7577335806750297287L;

	public APIPixieException(String msg) {
		super(msg);
	}

	public APIPixieException(String msg, Exception e) {
		super(msg, e);
	}
}
