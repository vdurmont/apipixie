package com.ligati.apipixie.tools;

import com.ligati.apipixie.APIPixieFeature;
import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.exception.APIUsageException;

public class Preconditionner {

	public static void checkNotNull(Object obj, String msg) {
		if (obj == null)
			throw new APIUsageException(msg);
	}

	public static void checkNotNullConfig(Object obj, String msg) {
		if (obj == null)
			throw new APIConfigurationException(msg);
	}
}
