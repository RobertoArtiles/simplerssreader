package com.the_roberto.rssreader.util;

public class RssItem {
	private String pubDate;
	private String description;
	private String link;
	private String title;
	private String guid;

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	@Override
	public String toString() {
		return "RssItem [pubDate=" + pubDate + ", description=" + description + ", link="
				+ link + ", title=" + title + ", guid=" + guid + "]";
	}
}
