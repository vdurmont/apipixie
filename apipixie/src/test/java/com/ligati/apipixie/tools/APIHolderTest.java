package com.ligati.apipixie.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.exception.APIParsingException;
import com.ligati.apipixie.model.Entity;
import com.ligati.apipixie.model.EntityWithoutAnnotation;
import com.ligati.apipixie.model.EntityWithoutDefaultConstructor;
import com.ligati.apipixie.model.EntityWithoutGetter;
import com.ligati.apipixie.model.EntityWithoutSetter;

@RunWith(JUnit4.class)
public class APIHolderTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void create_returns_an_instance() {
		// GIVEN

		// WHEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);
		Entity instance = holder.create();

		// THEN
		assertNotNull(instance);
	}

	@Test
	public void create_if_no_default_constructor_fails() {
		// GIVEN
		Class<?> clazz = EntityWithoutDefaultConstructor.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz).create();
	}

	@Test
	public void construct_without_annotation_fails() {
		// GIVEN
		Class<?> clazz = EntityWithoutAnnotation.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz);
	}

	@Test
	public void construct_without_setter_fails() {
		// GIVEN
		Class<?> clazz = EntityWithoutSetter.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz);
	}

	@Test
	public void construct_without_getter_fails() {
		// GIVEN
		Class<?> clazz = EntityWithoutGetter.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz);
	}

	@Test
	public void set_with_null_entity_fails() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.set(null, "text", "my text");
	}

	@Test
	public void set_with_null_name_fails() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.set(entity, null, "my text");
	}

	@Test
	public void set_with_empty_name_fails() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.set(entity, "", "my text");
	}

	@Test
	public void set_sets_the_value() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);
		Entity entity = new Entity();
		String text = "my text";

		// WHEN
		holder.set(entity, "text", text);

		// THEN
		assertEquals(text, entity.getText());
	}

	@Test
	public void get_with_null_entity_fails() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.get(null, "text");
	}

	@Test
	public void get_with_null_name_fails() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.get(entity, null);
	}

	@Test
	public void get_with_empty_name_fails() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.get(entity, "");
	}

	@Test
	public void get_a_null_field_returns_null() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);
		Entity entity = new Entity();

		// WHEN
		Object obj = holder.get(entity, "text");

		// THEN
		assertNull(obj);
	}

	@Test
	public void get_a_field_returns_its_value() {
		// GIVEN
		APIHolder<Entity> holder = new APIHolder<>(Entity.class);
		Entity entity = new Entity();
		String text = "my text";
		entity.setText(text);

		// WHEN
		Object obj = holder.get(entity, "text");

		// THEN
		String str = (String) obj;
		assertEquals(text, str);
	}
}
