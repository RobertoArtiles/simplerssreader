package com.the_roberto.rssreader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {
	private static String SHARED_PREF = "shared_pref";

	public enum PREF_NAME {
		FEED_URL, FEED_TITLE, FEED_DESCRIPTION, LAST_GUID
	}

	public enum SETTINGS_NAME {
		AUTO_UPDATE, TIME_INTERVAL, NUMBER_ITEMS, CLEAN_CACHE, CHANGE_FEED
	}

	public static void savePrefParameter(Context context, PREF_NAME prefName, String value) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(prefName.name(), value);

		editor.commit();
	}

	public static String getPrefParameter(Context context, PREF_NAME prefName) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF,
				Context.MODE_PRIVATE);
		return prefs.getString(prefName.name(), null);
	}

	public static Object getUserSetting(Context context, SETTINGS_NAME settingsName) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		switch (settingsName) {
		case AUTO_UPDATE:
			return settings.getBoolean(settingsName.name(), false);
		case TIME_INTERVAL:
		case NUMBER_ITEMS:
			return Integer.parseInt(settings.getString(settingsName.name(), "0"));
		default:
			return settings.getString(settingsName.name(), null);
		}
	}
	
}
