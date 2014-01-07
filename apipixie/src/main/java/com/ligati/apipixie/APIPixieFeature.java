package com.ligati.apipixie;

public enum APIPixieFeature {
	FAIL_ON_UNKNOWN_PROPERTIES(false);

	private boolean defaultValue;

	private APIPixieFeature(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}
}
