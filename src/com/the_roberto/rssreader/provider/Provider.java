package com.the_roberto.rssreader.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.the_roberto.rssreader.db.DataBaseHelper;
import com.the_roberto.rssreader.db.FeedItemTable;
import com.the_roberto.rssreader.exception.UnknownUriException;
import com.the_roberto.rssreader.util.SelectionBuilder;

public class Provider extends ContentProvider {
	private DataBaseHelper dbHelper;
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int ITEMS = 100;
	private static final int SINGLE_ITEM = 101;

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = ProviderContract.AUTHORITY;
		matcher.addURI(authority, FeedItemTable.TABLE_NAME, ITEMS);
		matcher.addURI(authority, FeedItemTable.TABLE_NAME + "/*", SINGLE_ITEM);
		return matcher;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DataBaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		int match = sUriMatcher.match(uri);
		switch (match) {
		case ITEMS:
			return FeedItemTable.CONTENT_TYPE;
		case SINGLE_ITEM:
			return FeedItemTable.CONTENT_ITEM_TYPE;
		default:
			throw new UnknownUriException(uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(getTable(uri));
		return builder.query(db, projection, selection, selectionArgs, null, null,
				sortOrder);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.insertOrThrow(getTable(uri), null, values);
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SelectionBuilder builder = new SelectionBuilder();
		int retVal = builder.table(getTable(uri)).where(selection, selectionArgs)
				.delete(db);
		return retVal;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SelectionBuilder builder = new SelectionBuilder();
		int retVal = builder.table(getTable(uri)).where(selection, selectionArgs)
				.update(db, values);
		return retVal;
	}

	private String getTable(final Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case ITEMS:
			return FeedItemTable.TABLE_NAME;
		case SINGLE_ITEM:
			return FeedItemTable.TABLE_NAME;
		default:
			throw new UnknownUriException(uri);
		}
	}
}
