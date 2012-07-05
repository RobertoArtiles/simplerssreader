package com.the_roberto.rssreader.util;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.the_roberto.rssreader.receiver.ScheduledUpdateReceicer;
import com.the_roberto.rssreader.util.PrefUtils.SETTINGS_NAME;

public class UpdateHelper {

	private static final int REQUEST_CODE = 0;

	public static void scheduleUpdate(Context context, int interval) {
		Intent intent = new Intent(context, ScheduledUpdateReceicer.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
				intent, 0);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		if (interval == 0) {
			interval = (Integer) PrefUtils.getUserSetting(context,
					SETTINGS_NAME.TIME_INTERVAL);
		}
		calendar.add(Calendar.SECOND, interval);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), interval * 1000, pendingIntent);
	}

	public static void scheduleUpdate(Context context) {
		scheduleUpdate(context, 0);
	}

	public static void cancelUpdate(Context context) {
		Intent intent = new Intent(context, ScheduledUpdateReceicer.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
				intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

}
