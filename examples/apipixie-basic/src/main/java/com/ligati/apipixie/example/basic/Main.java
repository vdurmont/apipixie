package com.ligati.apipixie.example.basic;

import com.ligati.apipixie.APIPixie;
import com.ligati.apipixie.APIService;
import com.ligati.apipixie.exception.APIHTTPException;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

public class Main {
	public static void main(String[] args) throws Exception {
		// Starting the server
		Container container = new DistantAPISimulator();
		Server server = new ContainerServer(container);
		Connection connection = new SocketConnection(server);
		SocketAddress address = new InetSocketAddress(1337);
		connection.connect(address);

		try {
			// Creating the pixie instance and getting the service
			APIPixie pixie = new APIPixie("http://localhost:1337");
			APIService<Message, Long> service = pixie.getService(Message.class);

			// Create
			Message message = new Message();
			message.setText("my text");
			message = service.post(message);

			// GetAll
			List<Message> messages = service.getAll();
			System.out.println("All messages: " + messages);

			// Get
			message = service.get(message.getId());
			System.out.println("Message: " + message);

			// Update
			message.setText("new text");
			message = service.put(message);
			System.out.println("Message: " + message);

			// Delete and try to get
			service.delete(message);
			try {
				service.get(message.getId());
			} catch (APIHTTPException e) {
				if (e.getStatusCode() == 404)
					System.out.println("The message has been deleted.");
				else throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Stopping the server
		server.stop();
		connection.close();
	}
}
