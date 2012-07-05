package com.the_roberto.rssreader.exception;

public class ServerTimoutException extends Exception {
	private static final long serialVersionUID = 7616043564003179970L;

	public ServerTimoutException(String url) {
		super("Gateway Timeout: " + url);
	}
}