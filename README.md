API PIXIE
==================

API PIXIE is a Java library which enables you to set up a communication with a distant REST API in minutes.

## Why API Pixie?

If you frequently establish communications between distant APIs and your system, you will have to handle a lot of boilerplate code to create the HTTP requests, manage the status codes and the format of the answer, and finaly map the content to your very own Java objects.

API Pixie enables you to avoid all those problems and concentrate on your code!

## Quickstart

### Installation

Just add the maven dependency to your project:

```
<dependency>
	<groupId>com.ligati</groupId>
	<artifactId>apipixie</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

This dependency ~~is~~ will be available on the [Maven Central Repository](http://search.maven.org/).

### Annotate your model

Annotate the classes representing the distant API Objects with `@APIEntity`.

```
@APIEntity
public class Message {
	private Long id;
	private String text;

	// Getters and setters
}
```

### Code!

```
// Create an APIPixie instance
APIPixie pixie = new APIPixie("http://api.mydistantservice.com");

// Retrieve an APIService for your object
APIService<Message> msgService = pixie.getService(Message.class);

// Start communicating with the distant API
Message msg = msgService.get(42L);

// Now do stuff with the object!
String text = msg.getText();
```