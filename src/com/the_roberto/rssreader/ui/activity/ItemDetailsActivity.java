package com.the_roberto.rssreader.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.the_roberto.rssreader.R;
import com.the_roberto.rssreader.db.FeedItemTable;

public class ItemDetailsActivity extends SherlockFragmentActivity implements
		LoaderCallbacks<Cursor> {
	public static final String EXTRAS_ID = "extras_id";
	private static final int ITEM_LOADER = 1;
	private long itemId;
	private String titleText;
	private String dateText;
	private String urlText;

	private TextView title;
	private TextView date;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itemdetails);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			itemId = extras.getLong(EXTRAS_ID);
		} else {
			throw new RuntimeException("Please provide intent with ID extras");
		}

		title = (TextView) findViewById(R.id.activity_itemdetails_title);
		date = (TextView) findViewById(R.id.activity_itemdetails_date);
		webView = (WebView) findViewById(R.id.activity_itemdetails_webview);
		webView.setBackgroundColor(Color.TRANSPARENT);
		webView.getSettings().setBuiltInZoomControls(true);

		getSupportLoaderManager().initLoader(ITEM_LOADER, null, this);
		webView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int progress) {
				setSupportProgress(progress * 100);
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.item_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		case R.id.menu_item_share:
			Intent shareIntent = prepareIntent();
			startActivity(Intent.createChooser(shareIntent, "Share with"));
			break;
		}
		return true;
	}

	private Intent prepareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TITLE, titleText);
		shareIntent.putExtra(Intent.EXTRA_TEXT, urlText);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, dateText);
		return shareIntent;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (id) {
		case ITEM_LOADER:

			return new CursorLoader(this, FeedItemTable.CONTENT_URI, null,
					FeedItemTable._ID + "=?", new String[] { Long.toString(itemId) },
					null);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case ITEM_LOADER:
			if (cursor.moveToFirst()) {
				titleText = cursor.getString(FeedItemTable.Query.COLUMN_TITLE);
				dateText = cursor.getString(FeedItemTable.Query.COLUMN_DATE);
				urlText = cursor.getString(FeedItemTable.Query.COLUMN_LINK);

				title.setText(titleText);
				date.setText(cursor.getString(FeedItemTable.Query.COLUMN_DATE));
				webView.loadDataWithBaseURL(null,
						cursor.getString(FeedItemTable.Query.COLUMN_DESCRIPTION),
						"text/html", "utf-8", null);
				ContentValues values = new ContentValues();
				values.put(FeedItemTable.COLUMN_IS_NEW, 0);
				getContentResolver().update(FeedItemTable.CONTENT_URI, values,
						FeedItemTable._ID + "=?", new String[] { Long.toString(itemId) });
			} else {
				throw new RuntimeException("Please provide Intent with valid ID:");
			}
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

}
