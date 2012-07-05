package com.the_roberto.rssreader.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;

import com.the_roberto.rssreader.db.FeedItemTable;
import com.the_roberto.rssreader.provider.ProviderContract;
import com.the_roberto.rssreader.util.PrefUtils.PREF_NAME;
import com.the_roberto.rssreader.util.PrefUtils.SETTINGS_NAME;

public class XmlProcessor {
	private static final String PUB_DATE = "pubDate";
	private static final String DESCRIPTION = "description";
	private static final String CHANNEL = "channel";
	private static final String LINK = "link";
	private static final String TITLE = "title";
	private static final String ITEM = "item";
	private static final String GUID = "guid";
	private static final int NEW_FLAG = 1;

	/***
	 * @return count of inserted rows
	 */
	public static int parse(Context context, ContentResolver resolver, InputStream rssFeed)
			throws XmlPullParserException, IOException, RemoteException,
			OperationApplicationException {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(rssFeed, "UTF-8");
			int eventType = parser.getEventType();
			boolean done = false;
			RssItem item = null;
			ArrayList<ContentProviderOperation> insertOperations = null;
			ContentProviderOperation operation = null;

			boolean isChanelLevel = true;
			boolean isFirstItem = true;
			String guid = PrefUtils.getPrefParameter(context, PREF_NAME.LAST_GUID);
			while (eventType != XmlPullParser.END_DOCUMENT && !done) {
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					insertOperations = new ArrayList<ContentProviderOperation>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase(ITEM)) {
						item = new RssItem();
						isChanelLevel = false;
					} else if (item != null && !isChanelLevel) {
						if (name.equalsIgnoreCase(LINK)) {
							item.setLink(parser.nextText().trim());
						} else if (name.equalsIgnoreCase(DESCRIPTION)) {
							item.setDescription(parser.nextText().trim());
						} else if (name.equalsIgnoreCase(PUB_DATE)) {
							item.setPubDate(parser.nextText());
						} else if (name.equalsIgnoreCase(TITLE)) {
							item.setTitle(parser.nextText().trim());
						} else if (name.equalsIgnoreCase(GUID)) {
							item.setGuid(parser.nextText().trim());
						}

					}

					if (isChanelLevel) {
						if (name.equalsIgnoreCase(DESCRIPTION)) {
							PrefUtils.savePrefParameter(context,
									PREF_NAME.FEED_DESCRIPTION, parser.nextText().trim());
						} else if (name.equalsIgnoreCase(TITLE)) {
							PrefUtils.savePrefParameter(context, PREF_NAME.FEED_TITLE,
									parser.nextText().trim());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();

					if (name.equalsIgnoreCase(ITEM) && item != null) {

						if (guid != null && item.getGuid().equalsIgnoreCase(guid)) {
							done = true;
						} else {
							if (isFirstItem) {
								PrefUtils.savePrefParameter(context, PREF_NAME.LAST_GUID,
										item.getGuid());
								isFirstItem = false;
							}
							operation = ContentProviderOperation
									.newInsert(FeedItemTable.CONTENT_URI)
									.withValue(FeedItemTable.COLUMN_DATE,
											item.getPubDate())
									.withValue(FeedItemTable.COLUMN_DESCRIPTION,
											item.getDescription())
									.withValue(FeedItemTable.COLUMN_GUID, item.getGuid())
									.withValue(FeedItemTable.COLUMN_IS_NEW, NEW_FLAG)
									.withValue(FeedItemTable.COLUMN_LINK, item.getLink())
									.withValue(FeedItemTable.COLUMN_TITLE,
											item.getTitle()).build();
							insertOperations.add(operation);
						}

					} else if (name.equalsIgnoreCase(CHANNEL)) {
						done = true;
					}
					break;
				}
				eventType = parser.next();
			}
			Collections.reverse(insertOperations);

			controlItemsCountLimits(context, resolver, insertOperations.size());

			resolver.applyBatch(ProviderContract.AUTHORITY, insertOperations);
			return insertOperations.size();
		} finally {
			if (rssFeed != null) {
				try {
					rssFeed.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static void controlItemsCountLimits(Context context,
			ContentResolver resolver, int size) {
		int count = resolver.query(FeedItemTable.CONTENT_URI, null, null, null, null)
				.getCount();
		if (count > (Integer) PrefUtils.getUserSetting(context,
				SETTINGS_NAME.NUMBER_ITEMS) && size != 0) {
			resolver.delete(FeedItemTable.CONTENT_URI, null, null);
			Log.d("CacheGC", "Old items were deleted");
		}
	}

}
