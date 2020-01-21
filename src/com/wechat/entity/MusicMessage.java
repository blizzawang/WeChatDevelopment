package com.wechat.entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class MusicMessage extends BaseMessage {

	private Music music;

	public Music getMusic() {
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}

	public MusicMessage(Map<String, String> reqMap, Music music) {
		super(reqMap);
		setMessageType("music");
		this.music = music;
	}
}
