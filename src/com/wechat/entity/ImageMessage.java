package com.wechat.entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class ImageMessage extends BaseMessage {
	
	@XStreamAlias("MediaId")
	private String mediaId;

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}


	public ImageMessage(Map<String, String> reqMap,String mediaId) {
		super(reqMap);
		this.setMessageType("image");
		this.mediaId = mediaId;
	}
}
