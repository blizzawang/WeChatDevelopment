package com.wechat.entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class TextMessage extends BaseMessage {
	
	@XStreamAlias("Content")
	private String content;

	public TextMessage(Map<String, String> reqMap,String content) {
		super(reqMap);
		//设置消息类型
		setMessageType("text");
		//发送的文本消息内容
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
