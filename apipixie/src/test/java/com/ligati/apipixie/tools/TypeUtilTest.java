package com.ligati.apipixie.tools;

import com.ligati.apipixie.exception.APIPixieException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TypeUtilTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void isBasicType_with_null_type_fails() {
		// GIVEN

		// THEN
		this.expectedEx.expect(APIPixieException.class);

		// WHEN
		TypeUtil.isBasicType(null);
	}
}