package com.the_roberto.rssreader.service;

import java.io.InputStream;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.the_roberto.rssreader.R;
import com.the_roberto.rssreader.ui.activity.MainActivity;
import com.the_roberto.rssreader.util.XmlProcessor;

public class UpdateService extends IntentService {
	private static final String NAME = UpdateService.class.getSimpleName();
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_ERROR = 2;
	public static final int STATUS_FINISHED = 3;

	public static final String EXTRAS_REQUEST = "request";
	public static final String EXTRAS_RECEIVER = "receiver";
	public static final String EXTRAS_RESPONSE = "response";
	public static final String EXTRAS_URL = "url";
	public static final String EXTRAS_EXCEPTION = "exception";
	public static final String EXTRAS_INSERTED_COUNT = "EXTRAS_INSERTED_COUNT";

	private WebClient client;

	public UpdateService() {
		super(NAME);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		client = WebClient.getInstance();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		ResultReceiver receiver = extras.getParcelable(EXTRAS_RECEIVER);
		if (receiver != null) {
			receiver.send(STATUS_RUNNING, null);
		}

		try {
			InputStream response = client.execute(getApplicationContext(),
					intent.getStringExtra(EXTRAS_URL));
			int insertedItemsCount = XmlProcessor.parse(this, getContentResolver(),
					response);

			if (receiver != null) {
				Bundle result = new Bundle();
				result.putInt(EXTRAS_INSERTED_COUNT, insertedItemsCount);
				receiver.send(STATUS_FINISHED, result);
			} else if (insertedItemsCount != 0) {
				createNotification(insertedItemsCount);
			}
		} catch (Exception e) {
			Bundle bundle = new Bundle();
			if (receiver != null) {
				bundle.putSerializable(EXTRAS_EXCEPTION, e);
				receiver.send(STATUS_ERROR, bundle);
			}
			e.printStackTrace();
		}
	}

	public void createNotification(int count) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new Builder(getApplicationContext());
		builder.setAutoCancel(true);
		builder.setDefaults(Notification.DEFAULT_ALL);
		builder.setSmallIcon(R.drawable.ic_launcher);
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		builder.setContentIntent(pendingIntent);
		builder.setContentTitle("Simple RssReader - New Items");
		String countEnding;
		if (count == 1) {
			countEnding = " item";
		} else {
			countEnding = " items";
		}
		builder.setContentText(count + countEnding);

		notificationManager.notify(0, builder.getNotification());

	}
}
