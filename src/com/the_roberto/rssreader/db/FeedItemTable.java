package com.the_roberto.rssreader.db;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.the_roberto.rssreader.provider.ProviderContract;
import com.the_roberto.rssreader.provider.ProviderContract.ItemsColumns;

public class FeedItemTable implements ItemsColumns{
	public static final Uri CONTENT_URI =
			ProviderContract.CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rssreader.item";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rssreader.item";

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME
			+ "("
			+ _ID + " integer primary key autoincrement, "
			+ COLUMN_GUID + " text not null, "
			+ COLUMN_LINK + " text, "
			+ COLUMN_TITLE + " text, "
			+ COLUMN_DESCRIPTION + " text, "
			+ COLUMN_DATE + " text, "
			+ COLUMN_IS_NEW + " integer "
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(FeedItemTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
	
	public interface Query{
		int _ID = 0;
		int COLUMN_GUID = 1;
		int COLUMN_LINK = 2;
		int COLUMN_TITLE = 3;
		int COLUMN_DESCRIPTION = 4;
		int COLUMN_DATE = 5;
		int COLUMN_IS_NEW = 6;
	}
}
