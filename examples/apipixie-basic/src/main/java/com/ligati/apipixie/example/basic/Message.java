package com.ligati.apipixie.example.basic;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class Message {
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Message [text=");
		builder.append(text);
		builder.append("]");
		return builder.toString();
	}
}
