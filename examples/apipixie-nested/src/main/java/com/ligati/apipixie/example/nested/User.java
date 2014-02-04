package com.ligati.apipixie.example.nested;

import com.ligati.apipixie.annotation.APIEntity;

@APIEntity
public class User {
	private Long id;
	private String name;
	private String email;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("User{");
		sb.append("id=").append(id);
		sb.append(", name='").append(name).append('\'');
		sb.append(", email='").append(email).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
