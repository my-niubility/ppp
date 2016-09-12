package com.nbl.controller.test;

import java.io.Serializable;

public class Person implements Serializable {
	
	private static final long serialVersionUID = -6557671551950486336L;

	private String name;
	
	private String address;
	
	private long age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}
	
	
	
}
