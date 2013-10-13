package com.ligati.apipixie.tools;

import com.ligati.apipixie.exception.APIParsingException;

public class UrlUtil {
	public static String plural(String str) {
		// TODO this method is no accurate and should be i18nable
		if (str == null || str.isEmpty())
			throw new APIParsingException(
					"Cannot get the plural form of a null or empty string.");
		if (str.endsWith("y"))
			return str.substring(0, str.length() - 1) + "ies";
		return str + "s";
	}
}
