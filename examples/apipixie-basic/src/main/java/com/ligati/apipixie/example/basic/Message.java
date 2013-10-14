package com.ligati.apipixie.example.basic;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class Message {
	private Long id;
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Message [id=");
		builder.append(id);
		builder.append(", text=");
		builder.append(text);
		builder.append("]");
		return builder.toString();
	}
}
