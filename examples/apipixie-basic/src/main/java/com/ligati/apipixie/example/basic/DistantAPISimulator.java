package com.ligati.apipixie.example.basic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class DistantAPISimulator implements Container {
	private Map<Long, Msg> database;
	private Long nextId;

	public DistantAPISimulator() {
		this.nextId = 1L;
		this.database = new HashMap<>();
	}

	public void handle(Request request, Response response) {
		try {
			String path = request.getPath().getPath();
			String method = request.getMethod();

			if ("/messages".equals(path) && "GET".equals(method))
				this.getAll(response);
			else if ("/messages".equals(path) && "POST".equals(method))
				this.post(request, response);
			else if (isMsgAccess(path) && "GET".equals(method))
				this.get(getId(path), response);
			else if (isMsgAccess(path) && "PUT".equals(method))
				this.put(getId(path), request, response);
			else if (isMsgAccess(path) && "DELETE".equals(method))
				this.delete(getId(path), response);
			else
				this.notFound(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isMsgAccess(String path) {
		if (path == null || !path.startsWith("/messages/"))
			return false;
		try {
			getId(path);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static Long getId(String path) {
		return Long.valueOf(path.replace("/messages/", "").replace("/", ""));
	}

	private void getAll(Response response) throws Exception {
		JSONArray arr = new JSONArray();
		for (Msg msg : database.values())
			arr.put(msg.toJSON());
		print(Status.OK, response, arr.toString());
	}

	private void get(Long id, Response response) throws Exception {
		Msg msg = database.get(id);
		if (msg == null)
			notFound(response);
		else
			print(Status.OK, response, msg.toJSON().toString());
	}

	private void put(Long id, Request request, Response response) throws Exception {
		Msg msg = database.get(id);
		if (msg == null)
			notFound(response);
		else {
			Msg replacement = new Msg(new JSONObject(request.getContent()));
			// Make sure the id is not changed
			replacement.setId(id);
			database.put(id, replacement);
			print(Status.OK, response, msg.toJSON().toString());
		}
	}

	private void delete(Long id, Response response) throws Exception {
		Msg msg = database.get(id);
		if (msg == null)
			notFound(response);
		else {
			database.remove(id);
			print(Status.NO_CONTENT, response, null);
		}
	}

	private void post(Request request, Response response) throws Exception {
		Msg msg = new Msg(new JSONObject(request.getContent()));
		msg.setId(nextId);
		nextId++;
		database.put(msg.getId(), msg);
		print(Status.CREATED, response, msg.toJSON().toString());
	}

	private void notFound(Response response) throws Exception {
		JSONObject error = new JSONObject();
		error.put("error", "Not found");
		print(Status.NOT_FOUND, response, error.toString());
	}

	private void print(Status status, Response response, String str) throws Exception {
		PrintStream body = response.getPrintStream();
		response.setStatus(status);
		response.setValue("Content-Type", "application/json");
		if (str != null)
			body.println(str);
		body.close();
	}

	private class Msg {
		private Long id;
		private String text;

		public Msg(JSONObject json) throws JSONException {
			if (json.has("id"))
				id = json.getLong("id");
			if (json.has("text"))
				text = json.getString("text");
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("text", text);
			return json;
		}
	}
}

