package com.ligati.apipixie.example.basic;

import java.util.List;

import com.ligati.apipixie.APIPixie;
import com.ligati.apipixie.APIService;

public class Main {
	public static void main(String[] args) {
		// Creating the pixie instance and getting the service
		APIPixie pixie = new APIPixie();
		pixie.setAPIUrl("http://localhost:1337");
		APIService<Message> service = pixie.getService(Message.class);

		// GetALL
		List<Message> messages = service.getAll();
		System.out.println("All messages: " + messages);
	}
}
