package com.ligati.apipixie;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
import com.ligati.apipixie.model.EntityWithUrl;
import com.ligati.apipixie.tools.AnnotationUtil;

@RunWith(JUnit4.class)
public class APIServiceTest {
	private APIPixie pixie;
	private APIHttpManager http;

	private static Long IDS = 1L;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() {
		this.pixie = mock(APIPixie.class);
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
		APIService<Entity, Long> service = new APIService<>(pixie,
				Entity.class, http);

		// WHEN
		List<Entity> entities = service.getAll();

		// THEN
		assertEquals(2, entities.size());
		assertEquals(text1, entities.get(0).getText());
		assertEquals(text2, entities.get(1).getText());
	}

	@Test
	public void if_no_specific_config_the_url_is_the_apiURL_plus_the_name_of_the_entity_in_plural_form() {
		// GIVEN
		String apiUrl = "http://myapi.com";
		when(pixie.getAPIUrl()).thenReturn(apiUrl);
		when(this.http.getArray(anyString())).thenReturn(new JSONArray());

		APIService<Entity, Long> service = new APIService<>(pixie,
				Entity.class, http);

		// WHEN
		service.getAll();

		// THEN
		String url = apiUrl + "/entities";
		verify(this.http).getArray(url);
	}

	@Test
	public void if_the_annotation_specifies_an_url_the_requestUrl_should_be_the_apiURL_plus_the_custom_url() {
		// GIVEN
		String apiUrl = "http://myapi.com";
		when(pixie.getAPIUrl()).thenReturn(apiUrl);
		when(this.http.getArray(anyString())).thenReturn(new JSONArray());

		APIService<EntityWithUrl, Long> service = new APIService<>(pixie,
				EntityWithUrl.class, http);

		// WHEN
		service.getAll();

		// THEN
		String url = apiUrl + "/"
				+ AnnotationUtil.getEntityUrl(EntityWithUrl.class);
		verify(this.http).getArray(url);
	}

	@Test
	public void get_returns_the_entity() throws JSONException {
		// GIVEN
		String text = "my text";
		JSONObject json = generateEntityJSON(text);
		Long id = json.getLong("id");

		when(this.http.getObject(anyString())).thenReturn(json);
		APIService<Entity, Long> service = new APIService<>(pixie,
				Entity.class, http);

		// WHEN
		Entity entity = service.get(id);

		// THEN
		assertEquals(id, entity.getId());
		assertEquals(text, entity.getText());
	}

	private static JSONObject generateEntityJSON(String text)
			throws JSONException {
		JSONObject entity = new JSONObject();
		entity.put("id", IDS++);
		entity.put("text", text);
		return entity;
	}
}
