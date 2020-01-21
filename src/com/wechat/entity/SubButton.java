package com.wechat.entity;

import java.util.ArrayList;
import java.util.List;

public class SubButton extends MoreButton {
	
	private List<MoreButton> sub_button = new ArrayList<MoreButton>();

	public SubButton(String name) {
		super(name);
	}
	
	public List<MoreButton> getSub_button() {
		return sub_button;
	}

	public void setSub_button(List<MoreButton> sub_button) {
		this.sub_button = sub_button;
	}
}
