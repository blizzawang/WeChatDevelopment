package com.wechat.entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class VoiceMessage extends BaseMessage {

	@XStreamAlias("MediaId")
	private String mediaId;
	
	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public VoiceMessage(Map<String, String> reqMap,String mediaId) {
		super(reqMap);
		setMessageType("voice");
		this.mediaId = mediaId;
	}
}
