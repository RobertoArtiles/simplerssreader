package com.the_roberto.rssreader.exception;
import android.net.Uri;

public class UnknownUriException extends UnsupportedOperationException {
	private static final long serialVersionUID = 7616043564003179970L;

	public UnknownUriException(Uri uri) {
		super("Unknown URI: " + uri);
	}
}
