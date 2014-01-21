package com.ligati.apipixie.tools;

import com.ligati.apipixie.exception.APIConfigurationException;
import com.ligati.apipixie.exception.APIParsingException;
import com.ligati.apipixie.model.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class APIHolderTest {
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void create_returns_an_instance() {
		// GIVEN

		// WHEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
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
		new APIHolder<>(clazz, false).create();
	}

	@Test
	public void construct_without_annotation_fails() {
		// GIVEN
		Class<?> clazz = EntityWithoutAnnotation.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz, false);
	}

	@Test
	public void construct_without_setter_fails() {
		// GIVEN
		Class<?> clazz = EntityWithoutSetter.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz, false);
	}

	@Test
	public void construct_without_getter_fails() {
		// GIVEN
		Class<?> clazz = EntityWithoutGetter.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz, false);
	}

	@Test
	public void construct_without_property_id_nor_APIId_annotation_fails() {
		// GIVEN
		Class<?> clazz = EntityWithoutIdPropertyNorAPIIdAnnotation.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz, false);
	}

	@Test
	public void construct_with_several_APIId_annotation_fails() {
		// GIVEN
		Class<?> clazz = EntityWithSeveralAPIIdAnnotations.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz, false);
	}

	@Test
	public void construct_without_id_property_and_with_an_APIId_annotation_is_ok() {
		// GIVEN
		Class<?> clazz = EntityWithoutIdPropertyButAnAPIIdAnnotation.class;

		// WHEN
		new APIHolder<>(clazz, false);

		// THEN
		// It's ok
	}

	@Test
	public void construct_with_id_property_in_parent_class_APISuperClass_is_ok() {
		// GIVEN
		Class<?> clazz = EntityExtendingAPISuperClassWithAnIdProperty.class;

		// WHEN
		new APIHolder<>(clazz, false);

		// THEN
		// It's ok
	}

	@Test
	public void construct_with_duplicate_property_in_parent_class_APISuperClass_fails() {
		// GIVEN
		Class<?> clazz = EntityExtendingAPISuperClassWithDuplicationOfProperty.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz, false);
	}

	@Test
	public void construct_with_id_property_in_parent_class_not_APISuperClass_fails() {
		// GIVEN
		Class<?> clazz = EntityExtendingNotAPISuperClassWithAnIdProperty.class;

		// THEN
		this.expectedEx.expect(APIConfigurationException.class);

		// WHEN
		new APIHolder<>(clazz, false);
	}

	@Test
	public void set_with_null_entity_fails() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.set(null, "text", "my text");
	}

	@Test
	public void set_with_null_name_fails() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.set(entity, null, "my text");
	}

	@Test
	public void set_with_empty_name_fails() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.set(entity, "", "my text");
	}

	@Test
	public void set_with_unknown_property_name_and_FAIL_ON_UNKNOWN_PROPERTIES_configuration_to_true_fails() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, true);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.set(entity, "unknownproperty", "my text");
	}

	@Test
	public void set_with_unknown_property_name_and_FAIL_ON_UNKNOWN_PROPERTIES_configuration_to_false_doesnt_fail() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
		Entity entity = new Entity();

		// WHEN
		holder.set(entity, "unknownproperty", "my text");

		// THEN
		// Nothing happened.
	}

	@Test
	public void set_sets_the_value() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
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
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.get(null, "text");
	}

	@Test
	public void get_with_null_name_fails() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.get(entity, null);
	}

	@Test
	public void get_with_empty_name_fails() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
		Entity entity = new Entity();

		// THEN
		this.expectedEx.expect(APIParsingException.class);

		// WHEN
		holder.get(entity, "");
	}

	@Test
	public void get_a_null_field_returns_null() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
		Entity entity = new Entity();

		// WHEN
		Object obj = holder.get(entity, "text");

		// THEN
		assertNull(obj);
	}

	@Test
	public void get_a_field_returns_its_value() {
		// GIVEN
		APIHolder<Entity, Long> holder = new APIHolder<>(Entity.class, false);
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
