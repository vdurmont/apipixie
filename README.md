API PIXIE
==================

API PIXIE is a Java library which enables you to set up a communication with a distant REST API in minutes.

## Why API Pixie?

If you frequently establish communications between distant APIs and your system, you will have to handle a lot of boilerplate code to create the HTTP requests, manage the status codes and the format of the answer, and finaly map the content to your very own Java objects.

API Pixie enables you to avoid all those problems and concentrate on your code!

## Quickstart

### Installation

Just add the maven dependency to your project:

	<dependency>
		<groupId>com.ligati</groupId>
		<artifactId>apipixie</artifactId>
		<version>0.1</version>
	</dependency>

This dependency ~~is~~ **will be** available on the [Maven Central Repository](http://search.maven.org/). In the meantime, you can just add the jar into your classpath (or clone this repo and `mvn clean install` to have the jar in your .m2).

### Annotate your model

Annotate the classes representing the distant API Objects with `@APIEntity`.

	@APIEntity
	public class Message {
		private Long id;
		private String text;

		// Getters and setters
	}

### Code!

	// Create an APIPixie instance
	APIPixie pixie = new APIPixie("http://api.mydistantservice.com");

	// Retrieve an APIService for your object
	APIService<Message> msgService = pixie.getService(Message.class);

	// Start communicating with the distant API
	Message msg = msgService.get(42L);

	// Now do stuff with the object!
	String text = msg.getText();

## Example

An example is available in `./examples`.

Just run the main method in `./examples/apipixie-basic/src/main/java/com/ligati/apipixie/example/basic/Main.java`.
A fake distant API server is embedded in the Main class.

## Identifiers

Each entity must have an identifier. If your entity has an `id` property, it will be selected as the default identifier.
If you don't have this property or if you want to select a property, just annotate it with `@APIId`.

	@APIEntity
	public class Message {
	    @APIId
	    private String myCustomIdentifier;
		private String text;

		// Getters and setters
	}

## Requests

By default, the following requests will be made on the distant API:

* `get(id)`: `GET <API_URL>/<IDENTIFIER>`. Expects a 200 status code.
* `post(entity)`: `POST <API_URL>/` with a JSON body representing the entity. Expects a 201 status code.
* `put(entity)`: `PUT <API_URL>/<IDENTIFIER>` with a JSON body representing the entity. Expects a 200 status code.
* `delete(entity)` and `delete(id)`: `DELETE <API_URL>/<IDENTIFIER>`. Expects a 204 status code.

## Errors

Several exceptions can be thrown when using APIPixie. They all extend `java.lang.RuntimeException` so that you decide to catch them or let them propagate.

Here are the different types of exceptions and their meaning:

* `APIConfigurationException`: this exception is thrown when your configuration is not valid. Have you forgotten to annotate some entities? Did you miss some getters/setters? Read the error message to learn more!
* `APIHTTPException`: this error occurs when an unexpected status code is returned by the distant API. You can use the method `getStatusCode()` to learn more.
* `APIParsingException`: this exception can be thrown when APIPixie cannot parse a JSON to your entity and vice-versa.
* `APIUsageException`: this error occurs when your usage of APIPixie is invalid (example: call the put method with a null object).
* `APIPixieException`: mother of all the APIPixie exceptions, something went terribly wrong!

## Advanced configuration

### Custom HTTPManager

If you have specific needs on the way to execute the HTTP requests, just implement the `APIHttpManager` interface and build your APIPixie instance this way:

	APIPixie pixie = new APIPixie(myAPIUrl, new MyCustomHttpManager());

The default is `DefaultHttpManager`. We also provide a `BasicAuthHttpManager` which enables you to communicate with the API using [basic access authentication](http://en.wikipedia.org/wiki/Basic_access_authentication).

If you write your own APIHttpManager, do not hesitate to check out the `APIHttpUtil` class.

### Features

APIPixie relies on a set of features which define its behavior. Those features are in the class `APIPixieFeature` and have a default value for the basic usage. You can configure those features this way:

	APIPixie pixie = new APIPixie(myAPIUrl);
	pixie.configure(APIPixieFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

The current features are:

* `FAIL_ON_UNKNOWN_PROPERTIES`: whether or not APIPixie has to raise an exception when it receives an unknown property from the server. (Default: `false`)

### Inheritance

If you want to use inherited fields in your communication with the distant API, just annotate your parent classes with `@APISuperClass`. Here is a quick example:

	@APISuperClass
	public class MyParent {
		private Long id;
		// Getters & Setter
	}

	@APIEntity
	public class MyChild extends MyParent {
		// Code
	}

### Collections

If one of you attributes is a collection (lists, sets, etc.), you have to annotate it with `@APICollection` and give in `mappedClass` the class of the parameter. The parameter can be another APIEntity!

For example:

	@APICollection(mappedClass = String.class)
	private List<String> strings;

	@APICollection(mappedClass = Entity.class)
	private List<Entity> entities;

## Dependencies

APIPixie depends on some basic libraries. Check out the `pom.xml` file for the versions.

* `commons-io > commons-io` is used to read the web streams and convert them to Strings.
* `org.json > json` is used to manipulate all the JSON objects in APIPixie.
* `org.apache.httpcomponents > httpclient` is used for all the requests sent to the distant APIs.
* `org.slf4j > slf4j-log4j12` is used to log the actions made by APIPixie.

Some dependencies were added for the tests:

* `junit > junit`
* `org.mockito > mockito-core`

## Changelog

### Version 0.2 (in progress)

* Support for nested APIEntities
* Support for basic collections (Strings, Longs, etc.)
* Support for APIEntities collections
* Support for APISuperClass (inherited properties)

### Version 0.1

* First release, first implementation
* Support for APIEntity
* Support for custom APIId
* HTTP Basic Auth manager implementation
