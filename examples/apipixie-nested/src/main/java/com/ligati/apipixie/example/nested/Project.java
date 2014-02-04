package com.ligati.apipixie.example.nested;

import com.ligati.apipixie.annotation.APICollection;
import com.ligati.apipixie.annotation.APIEntity;

import java.util.List;

@APIEntity
public class Project {
	private Long id;
	private String title;
	private User owner;
	@APICollection(mappedClass = String.class)
	private List<String> tags;
	@APICollection(mappedClass = Issue.class)
	private List<Issue> issues;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Project{");
		sb.append("id=").append(id);
		sb.append(", title='").append(title).append('\'');
		sb.append(", owner=").append(owner);
		sb.append(", tags=").append(tags);
		sb.append(", issues=").append(issues);
		sb.append('}');
		return sb.toString();
	}
}