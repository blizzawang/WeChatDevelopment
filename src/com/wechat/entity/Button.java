package com.wechat.entity;

import java.util.ArrayList;
import java.util.List;

public class Button {
	
	private List<MoreButton> button = new ArrayList<MoreButton>();
	

	public List<MoreButton> getButton() {
		return button;
	}

	public void setButton(List<MoreButton> button) {
		this.button = button;
	}
}
