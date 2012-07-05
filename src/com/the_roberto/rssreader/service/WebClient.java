package com.the_roberto.rssreader.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

import com.the_roberto.rssreader.exception.ServerNotFoundException;
import com.the_roberto.rssreader.exception.ServerUnknownException;
import com.the_roberto.rssreader.util.NetworkUtils;

public class WebClient {
	private static final int CONNECTION_TIMEOUT = 10000;
	private static WebClient self;
	private HttpClient client;

	public static WebClient getInstance() {
		if (self == null) {
			self = new WebClient();
		}
		return self;
	}

	private WebClient() {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(httpParameters,
				schReg);

		client = new DefaultHttpClient(conMgr, httpParameters);
	}

	public InputStream execute(Context context, String url)
			throws ServerNotFoundException,
			ServerUnknownException, IllegalStateException, IOException {
		HttpGet httpGet = new HttpGet(NetworkUtils.makeHttpUrl(url));
		httpGet.addHeader("Accept", "application/xml");
		httpGet.addHeader("Content-Type", "application/xml");

		HttpResponse response = client.execute(httpGet);

		int statusCode = response.getStatusLine().getStatusCode();

		switch (statusCode) {
		case HttpStatus.SC_OK:
			return response.getEntity().getContent();
		case HttpStatus.SC_NOT_FOUND:
			throw new ServerNotFoundException(url);
		case HttpStatus.SC_GATEWAY_TIMEOUT:
			throw new ServerNotFoundException(url);
		default:
			throw new ServerUnknownException(url, statusCode);
		}

	}
	
}
