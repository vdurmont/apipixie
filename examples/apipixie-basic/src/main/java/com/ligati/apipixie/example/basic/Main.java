package com.ligati.apipixie.example.basic;

import java.util.List;

import com.ligati.apipixie.APIPixie;
import com.ligati.apipixie.APIService;

public class Main {
	public static void main(String[] args) {
		// Creating the pixie instance and getting the service
		APIPixie pixie = new APIPixie("http://localhost:1337");
		APIService<Message, Long> service = pixie.getService(Message.class);

		// GetALL
		List<Message> messages = service.getAll();
		System.out.println("All messages: " + messages);

		// Get #1
		Message message = service.get(1L);
		System.out.println("Message#1: " + message);

		// Put #1
		message.setText("new text");
		message = service.put(message);
		System.out.println("Message#1: " + message);
	}
}
