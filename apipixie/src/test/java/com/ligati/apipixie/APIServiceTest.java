package com.ligati.apipixie;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ligati.apipixie.http.APIHttpManager;
import com.ligati.apipixie.model.Entity;

@RunWith(JUnit4.class)
public class APIServiceTest {
	private APIHttpManager http;

	private static Long IDS = 1L;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() {
		this.http = mock(APIHttpManager.class);
	}

	@Test
	public void getAll_returns_a_list() throws JSONException {
		// GIVEN
		String text1 = "my text 1";
		String text2 = "my text 2";

		JSONArray array = new JSONArray();
		JSONObject entity1 = generateEntityJSON(text1);
		JSONObject entity2 = generateEntityJSON(text2);
		array.put(entity1);
		array.put(entity2);

		when(this.http.getArray(anyString())).thenReturn(array);
		APIService<Entity> service = new APIService<>(Entity.class, http);

		// WHEN
		List<Entity> entities = service.getAll();

		// THEN
		assertEquals(2, entities.size());
		assertEquals(text1, entities.get(0).getText());
		assertEquals(text2, entities.get(1).getText());
	}

	private static JSONObject generateEntityJSON(String text)
			throws JSONException {
		JSONObject entity = new JSONObject();
		entity.put("id", IDS++);
		entity.put("text", text);
		return entity;
	}
}
