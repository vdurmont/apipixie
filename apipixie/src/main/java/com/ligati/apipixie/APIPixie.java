package com.ligati.apipixie;

import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.http.DefaultAPIHttpManager;

public class APIPixie {
	private String apiUrl;

	public APIPixie(String apiUrl) {
		this.setAPIUrl(apiUrl);
	}

	public <T> APIService<T> getService(Class<T> clazz) {
		return new APIService<>(this, clazz, new DefaultAPIHttpManager());
	}

	public String getAPIUrl() {
		if (apiUrl == null)
			throw new APIConfigurationException(
					"You have to provide an URL for the API.");
		return this.apiUrl;
	}

	public void setAPIUrl(String apiUrl) {
		if (apiUrl == null || apiUrl.isEmpty())
			throw new APIConfigurationException(
					"You cannot define a null or empty url for the API.");
		else if (apiUrl.endsWith("/"))
			this.apiUrl = apiUrl.substring(0, apiUrl.length() - 1);
		else
			this.apiUrl = apiUrl;
	}
}
