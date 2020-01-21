package com.wechat.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class NewsMessage extends BaseMessage{

	@XStreamAlias("ArticleCount")
	private String atricleCount;
	@XStreamAlias("Articles")
	private List<Article> articles = new ArrayList<Article>();
	
	public String getAtricleCount() {
		return atricleCount;
	}
	public void setAtricleCount(String atricleCount) {
		this.atricleCount = atricleCount;
	}
	public List<Article> getArticles() {
		return articles;
	}
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
	
	public NewsMessage(Map<String, String> reqMap, String atricleCount, List<Article> articles) {
		super(reqMap);
		setMessageType("news");
		this.atricleCount = atricleCount;
		this.articles = articles;
	}
}
