package com.ligati.apipixie;

import com.ligati.apipixie.http.DefaultAPIHttpManager;

public class APIPixie {
	public <T> APIService<T> getService(Class<T> clazz) {
		return new APIService<>(clazz, new DefaultAPIHttpManager());
	}
}
