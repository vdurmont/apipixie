package com.ligati.apipixie;

import com.ligati.apipixie.exception.APIUsageException;
import com.ligati.apipixie.http.APIHttpManager;
import com.ligati.apipixie.model.Entity;
import com.ligati.apipixie.model.EntityWithBasicCollectionAPICollection;
import com.ligati.apipixie.model.EntityWithEntityCollectionAPICollection;
import com.ligati.apipixie.model.EntityWithUrl;
import com.ligati.apipixie.tools.AnnotationUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

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
		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

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

		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

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

		APIService<EntityWithUrl, Long> service = new APIService<>(pixie, EntityWithUrl.class, http);

		// WHEN
		service.getAll();

		// THEN
		String url = apiUrl + "/" + AnnotationUtil.getEntityUrl(EntityWithUrl.class);
		verify(this.http).getArray(url);
	}

	@Test
	public void get_returns_the_entity() throws JSONException {
		// GIVEN
		String text = "my text";
		JSONObject json = generateEntityJSON(text);
		Long id = json.getLong("id");

		when(this.http.getObject(anyString())).thenReturn(json);
		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

		// WHEN
		Entity entity = service.get(id);

		// THEN
		assertEquals(id, entity.getId());
		assertEquals(text, entity.getText());
	}

	@Test
	public void put_updates_and_returns_the_entity() throws JSONException {
		// GIVEN
		String text = "my text";
		JSONObject json = generateEntityJSON(text);
		Long id = json.getLong("id");

		when(this.http.putObject(anyString(), any(JSONObject.class))).thenReturn(json);
		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

		Entity modified = new Entity();
		modified.setId(id);
		modified.setText(text);

		// WHEN
		Entity entity = service.put(modified);

		// THEN
		assertEquals(id, entity.getId());
		assertEquals(text, entity.getText());

		verify(this.http).putObject(anyString(), any(JSONObject.class));
	}

	@Test
	public void post_creates_and_returns_the_entity_with_its_id() throws JSONException {
		// GIVEN
		String text = "my text";
		Entity entity = new Entity();
		entity.setText(text);

		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

		Long id = 42L;
		JSONObject json = generateEntityJSON(text);
		json.put("id", id);
		when(this.http.postObject(anyString(), any(JSONObject.class))).thenReturn(json);

		// WHEN
		Entity created = service.post(entity);

		// THEN
		assertEquals(id, created.getId());
		assertEquals(text, created.getText());
		verify(this.http).postObject(anyString(), any(JSONObject.class));
	}

	@Test
	public void delete_deletes_the_entity() throws JSONException {
		// GIVEN
		Long id = 42L;
		String text = "my text";
		Entity entity = new Entity();
		entity.setId(id);
		entity.setText(text);

		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

		// WHEN
		service.delete(entity);

		// THEN
		verify(this.http).deleteObject(anyString());
	}

	@Test
	public void delete_if_null_entity_fails() throws JSONException {
		// GIVEN
		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

		// THEN
		verifyZeroInteractions(this.http);
		this.expectedEx.expect(APIUsageException.class);

		// WHEN
		service.delete(null);
	}

	@Test
	public void delete_if_no_id_fails() throws JSONException {
		// GIVEN
		Entity entity = new Entity();
		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

		// THEN
		verifyZeroInteractions(this.http);
		this.expectedEx.expect(APIUsageException.class);

		// WHEN
		service.delete(entity);
	}

	@Test
	public void delete_with_id_deletes_the_entity() throws JSONException {
		// GIVEN
		Long id = 42L;

		APIService<Entity, Long> service = new APIService<>(pixie, Entity.class, http);

		// WHEN
		service.deleteById(id);

		// THEN
		verify(this.http).deleteObject(anyString());
	}

	private static JSONObject generateEntityJSON(String text) throws JSONException {
		JSONObject entity = new JSONObject();
		entity.put("id", IDS++);
		entity.put("text", text);
		return entity;
	}

	@Test
	public void jsonObjectToEntity_with_an_array_and_a_basic_collection_maps_the_array() throws JSONException {
		// GIVEN
		APIService<EntityWithBasicCollectionAPICollection, Long> service = new APIService<>(pixie, EntityWithBasicCollectionAPICollection.class, http);

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		array.put("first");
		array.put("second");
		json.put("strings", array);


		// WHEN
		EntityWithBasicCollectionAPICollection entity = service.jsonObjectToEntity(json);

		// THEN
		assertEquals(2, entity.getStrings().size());
		assertEquals("first", entity.getStrings().get(0));
		assertEquals("second", entity.getStrings().get(1));
	}

	@Test
	public void jsonObjectToEntity_with_an_array_and_an_APIEntity_collection_maps_the_array() throws JSONException {
		// GIVEN
		APIService<EntityWithEntityCollectionAPICollection, Long> service = new APIService<>(pixie, EntityWithEntityCollectionAPICollection.class, http);

		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();
		JSONObject entity1 = new JSONObject();
		entity1.put("id", 42);
		array.put(entity1);
		JSONObject entity2 = new JSONObject();
		entity2.put("id", 43);
		array.put(entity2);
		json.put("entities", array);


		// WHEN
		EntityWithEntityCollectionAPICollection entity = service.jsonObjectToEntity(json);

		// THEN
		assertEquals(2, entity.getEntities().size());
		assertEquals(new Long(42), entity.getEntities().get(0).getId());
		assertEquals(new Long(43), entity.getEntities().get(1).getId());
	}
}
