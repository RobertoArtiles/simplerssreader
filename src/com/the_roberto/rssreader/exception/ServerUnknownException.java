package com.the_roberto.rssreader.exception;

public class ServerUnknownException extends Exception {
	private static final long serialVersionUID = 7150222582369878512L;

	public ServerUnknownException(String url, int statusCode) {
		super("Url: " + url + "   Status Code: " + statusCode);
	}
}