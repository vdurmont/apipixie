package com.ligati.apipixie.example.nested;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class Issue {
	private Long id;
	private User author;
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Issue{");
		sb.append("id=").append(id);
		sb.append(", author=").append(author);
		sb.append(", text='").append(text).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
