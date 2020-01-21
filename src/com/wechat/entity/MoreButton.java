package com.wechat.entity;

public abstract class MoreButton {
	
	private String name;

	public MoreButton(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
