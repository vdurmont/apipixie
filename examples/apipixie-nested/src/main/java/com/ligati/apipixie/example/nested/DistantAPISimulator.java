package com.ligati.apipixie.example.nested;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DistantAPISimulator implements Container {
	private Map<Long, Usr> usrDB;
	private Map<Long, Prjct> prjctDB;
	private Map<Long, Iss> issDB;
	private Long nextId;

	public DistantAPISimulator() {
		this.nextId = 1L;
		this.usrDB = new HashMap<>();
		this.prjctDB = new HashMap<>();
		this.issDB = new HashMap<>();
	}

	public void handle(Request request, Response response) {
		try {
			String path = request.getPath().getPath();
			String method = request.getMethod();

			if ("/users".equals(path) && "POST".equals(method))
				this.createUser(request, response);
			else if ("/projects".equals(path) && "POST".equals(method))
				this.createProject(request, response);
			else if ("/users".equals(path) && "GET".equals(method))
				this.getAllUsers(response);
			else if ("/issues".equals(path) && "GET".equals(method))
				this.getAllIssues(response);
			else if ("/projects".equals(path) && "GET".equals(method))
				this.getAllProjects(response);
			else
				this.notFound(response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void getAllUsers(Response response) throws Exception {
		JSONArray array = new JSONArray();
		for (Long id : this.usrDB.keySet())
			array.put(this.usrDB.get(id).toJSON());
		print(Status.OK, response, array.toString());
	}

	private void getAllProjects(Response response) throws Exception {
		JSONArray array = new JSONArray();
		for (Long id : this.prjctDB.keySet())
			array.put(this.prjctDB.get(id).toJSON());
		print(Status.OK, response, array.toString());
	}

	private void getAllIssues(Response response) throws Exception {
		JSONArray array = new JSONArray();
		for (Long id : this.issDB.keySet())
			array.put(this.issDB.get(id).toJSON());
		print(Status.OK, response, array.toString());
	}

	private void createUser(Request request, Response response) throws Exception {
		Usr usr = new Usr(new JSONObject(request.getContent()));
		usr.setId(nextId);
		nextId++;
		usrDB.put(usr.getId(), usr);
		print(Status.CREATED, response, usr.toJSON().toString());
	}

	private void createProject(Request request, Response response) throws Exception {
		Prjct prjct = new Prjct(new JSONObject(request.getContent()));
		prjct.setId(nextId);
		nextId++;
		prjctDB.put(prjct.getId(), prjct);
		for (Iss iss : prjct.getIssues())
			this.storeIssue(iss);
		print(Status.CREATED, response, prjct.toJSON().toString());
	}

	private void storeIssue(Iss issue) throws Exception {
		issue.setId(nextId);
		nextId++;
		issDB.put(issue.getId(), issue);
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

	private class Usr {
		private Long id;
		private String name;
		private String email;

		private Usr(JSONObject json) throws JSONException {
			if (json.has("id"))
				this.id = json.getLong("id");
			if (json.has("name"))
				this.name = json.getString("name");
			if (json.has("email"))
				this.email = json.getString("email");
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
			json.put("name", name);
			json.put("email", email);
			return json;
		}
	}

	private class Prjct {
		private Long id;
		private Usr owner;
		private String title;
		private List<String> tags;
		private List<Iss> issues;

		private Prjct(JSONObject json) throws JSONException {
			if (json.has("id"))
				this.id = json.getLong("id");
			if (json.has("owner"))
				this.owner = new Usr(json.getJSONObject("owner"));
			if (json.has("title"))
				this.title = json.getString("title");
			if (json.has("tags")) {
				List<String> tags = new LinkedList<>();
				for (int i = 0; i < json.getJSONArray("tags").length(); i++)
					tags.add(json.getJSONArray("tags").getString(i));
				this.tags = tags;
			}
			if (json.has("issues")) {
				List<Iss> issues = new LinkedList<>();
				for (int i = 0; i < json.getJSONArray("issues").length(); i++)
					issues.add(new Iss(json.getJSONArray("issues").getJSONObject(i)));
				this.issues = issues;
			}
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public List<Iss> getIssues() {
			return issues;
		}

		public JSONObject toJSON() throws JSONException {
			JSONObject json = new JSONObject();
			json.put("id", id);
			json.put("owner", owner == null ? null : owner.toJSON());
			json.put("title", title);
			json.put("tags", tags);
			JSONArray issuesJson = null;
			if (issues != null) {
				issuesJson = new JSONArray();
				for (Iss iss : issues)
					issuesJson.put(iss.toJSON());
			}
			json.put("issues", issuesJson);
			return json;
		}
	}

	private class Iss {
		private Long id;
		private Usr author;
		private String text;

		public Iss(JSONObject json) throws JSONException {
			if (json.has("id"))
				this.id = json.getLong("id");
			if (json.has("author"))
				this.author = new Usr(json.getJSONObject("author"));
			if (json.has("text"))
				this.text = json.getString("text");
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
			json.put("author", author == null ? null : author.toJSON());
			json.put("text", text);
			return json;
		}
	}
}

