package com.wechat.entity;

public class ClickButton extends MoreButton {

	private String type = "click";
	private String key;
	
	public ClickButton(String name, String key) {
		super(name);
		this.key = key;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
