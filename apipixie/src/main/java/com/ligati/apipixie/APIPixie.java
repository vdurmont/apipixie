package com.ligati.apipixie;

import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.http.APIHttpManager;
import com.ligati.apipixie.http.DefaultAPIHttpManager;
import com.ligati.apipixie.tools.Preconditionner;

import java.util.HashMap;
import java.util.Map;

public class APIPixie {
	private String apiUrl;
	private APIHttpManager httpManager;
	private Map<APIPixieFeature, Boolean> features;

	public APIPixie(String apiUrl) {
		this(apiUrl, new DefaultAPIHttpManager());
	}

	public APIPixie(String apiUrl, APIHttpManager httpManager) {
		Preconditionner.checkNotNullConfig(httpManager, "You have to provide a non-null APIHttpManager.");
		this.setAPIUrl(apiUrl);
		this.httpManager = httpManager;
		this.features = new HashMap<>();
	}

	public void configure(APIPixieFeature feature, boolean enabled) {
		Preconditionner.checkNotNullConfig(feature, "You have to provide a non-null APIPixieFeature.");
		this.features.put(feature, enabled);
	}

	public boolean isFeatureEnabled(APIPixieFeature feature) {
		Boolean result = this.features.get(feature);
		if (result == null)
			return feature.getDefaultValue();
		return result;
	}

	public <T, K> APIService<T, K> getService(Class<T> clazz) {
		return new APIService<>(this, clazz, this.httpManager);
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
