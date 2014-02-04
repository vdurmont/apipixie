package com.ligati.apipixie.example.nested;

import com.ligati.apipixie.APIPixie;
import com.ligati.apipixie.APIService;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
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
			// Creating the pixie instance
			APIPixie pixie = new APIPixie("http://localhost:1337");

			// Create a user
			APIService<User, Long> userService = pixie.getService(User.class);
			User user = new User();
			user.setName("Vincent DURMONT");
			user.setEmail("vdurmont@gmail.com");
			user = userService.post(user);
			System.out.println("Created user: " + user);

			// Create a project
			APIService<Project, Long> projectService = pixie.getService(Project.class);
			Project project = new Project();
			project.setOwner(user);
			project.setTitle("My awesome project");

			// Create some tags
			List<String> tags = new LinkedList<>();
			tags.add("test");
			tags.add("java");
			project.setTags(tags);

			// Create some issues
			List<Issue> issues = new LinkedList<>();

			Issue issue1 = new Issue();
			issue1.setAuthor(user);
			issue1.setText("the first issue!");
			issues.add(issue1);

			Issue issue2 = new Issue();
			issue2.setAuthor(user);
			issue2.setText("the second issue!");
			issues.add(issue2);

			project.setIssues(issues);
			project = projectService.post(project);
			System.out.println("Created project: " + project);

			System.out.println("List of users: " + userService.getAll());
			System.out.println("List of projects: " + projectService.getAll());
			APIService<Issue, Long> issueService = pixie.getService(Issue.class);
			System.out.println("List of issues: " + issueService.getAll());

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Stopping the server
		server.stop();
		connection.close();
	}
}
