package com.the_roberto.rssreader.exception;


public class ServerNotFoundException extends Exception {
	private static final long serialVersionUID = 7616043564003179970L;

	public ServerNotFoundException(String url) {
		super("Server not found URI: " + url);
	}
}
