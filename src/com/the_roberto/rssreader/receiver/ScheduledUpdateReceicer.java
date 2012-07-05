package com.the_roberto.rssreader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.the_roberto.rssreader.service.UpdateService;
import com.the_roberto.rssreader.util.PrefUtils;
import com.the_roberto.rssreader.util.PrefUtils.PREF_NAME;

public class ScheduledUpdateReceicer extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
			updateFeed(context);
			Log.d("Scheduled Update", "performed");
	}

	private void updateFeed(Context context) {
		Intent intent = new Intent(context, UpdateService.class);
		String feedUrl = PrefUtils.getPrefParameter(context, PREF_NAME.FEED_URL);
		if (feedUrl != null && feedUrl.length() != 0) {
			intent.putExtra(UpdateService.EXTRAS_URL, feedUrl);
			context.startService(intent);
		}
	}

}
