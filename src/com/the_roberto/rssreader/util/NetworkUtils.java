package com.the_roberto.rssreader.util;

import java.util.regex.Matcher;

import android.util.Patterns;
import android.webkit.URLUtil;

public class NetworkUtils {
	
	public static boolean validateUrl(String url) {
		Matcher m = Patterns.WEB_URL.matcher(url);
		return m.lookingAt();
	}

	public static String makeHttpUrl(String url) {
		if (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)) {
			return "http://".concat(url);
		}
		return url;
	}
}
