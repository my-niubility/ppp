package com.nbl.controller.test;

import org.hibernate.validator.constraints.NotEmpty;

public class RequestBodyTest {
	
	@NotEmpty
	private String name;
	
	private String message;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
