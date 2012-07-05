package com.the_roberto.rssreader.ui.frgament;

import java.net.UnknownHostException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.the_roberto.rssreader.R;
import com.the_roberto.rssreader.db.FeedItemTable;
import com.the_roberto.rssreader.exception.ServerNotFoundException;
import com.the_roberto.rssreader.receiver.DetachableResultReceiver;
import com.the_roberto.rssreader.receiver.DetachableResultReceiver.Receiver;
import com.the_roberto.rssreader.service.UpdateService;
import com.the_roberto.rssreader.ui.adapter.FeedAdapter;
import com.the_roberto.rssreader.util.PrefUtils;
import com.the_roberto.rssreader.util.PrefUtils.PREF_NAME;
import com.the_roberto.rssreader.util.PrefUtils.SETTINGS_NAME;
import com.the_roberto.rssreader.util.UpdateHelper;

public class FeedListFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor>, Receiver, OnItemClickListener {

	public interface OnFeedItemClickListener {
		void onFeedItemClick(long id);
	}

	public interface OnErrorListener {
		void onError();
	}

	public interface OnSettingsClickListener {
		void onSettingsClick();
	}

	private static final int FEED_LOADER = 1;
	private static final String EXTRAS_URL = "url";
	private String feedUrl;
	private OnFeedItemClickListener clickCallback;
	private OnErrorListener errorCallback;
	private OnSettingsClickListener settingsClickListener;
	private ListView feedList;
	private CursorAdapter feedAdapter;
	private DetachableResultReceiver receiver;
	private boolean isUpdateRunning;

	public static SherlockFragment newInstance(String url) {
		SherlockFragment f = new FeedListFragment();
		Bundle args = new Bundle();
		args.putString(EXTRAS_URL, url);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FeedList", "OnAttach");
		try {
			clickCallback = (OnFeedItemClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.getLocalClassName()
					+ " must implement OnFeedItemClickListener");
		}
		try {
			errorCallback = (OnErrorListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.getLocalClassName()
					+ " must implement OnErrorListener");
		}
		try {
			settingsClickListener = (OnSettingsClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.getLocalClassName()
					+ " must implement OnSettingsClickListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		feedUrl = getArguments().getString(EXTRAS_URL);
		if (feedUrl == null) {
			throw new RuntimeException(
					"Please provide extras for FeedListFragment with URL argument");
		}

		receiver = new DetachableResultReceiver(new Handler());
		receiver.setReceiver(this);
		updateFeed();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feedlist, null);
		feedList = (ListView) view.findViewById(R.id.fragment_feedlist_list);
		feedAdapter = new FeedAdapter(getSherlockActivity(), null);
		feedList.setAdapter(feedAdapter);
		feedList.setOnItemClickListener(this);
		View empty = view.findViewById(R.id.fragment_feedlist_empty);
		feedList.setEmptyView(empty);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		receiver.setReceiver(this);
		LoaderManager manager = getSherlockActivity().getSupportLoaderManager();
		manager.destroyLoader(FEED_LOADER);
		manager.initLoader(FEED_LOADER, null, this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		receiver.clearReceiver();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.feed_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_feed_refresh:
			updateFeed();
			break;
		case R.id.menu_feed_settings:
			settingsClickListener.onSettingsClick();
			break;
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		clickCallback.onFeedItemClick(id);
	}

	private void updateFeed() {
		if (!isUpdateRunning) {
			isUpdateRunning = true;
			Intent intent = new Intent(getSherlockActivity(), UpdateService.class);
			intent.putExtra(UpdateService.EXTRAS_RECEIVER, receiver);
			intent.putExtra(UpdateService.EXTRAS_URL, feedUrl);
			getSherlockActivity().startService(intent);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		switch (id) {
		case FEED_LOADER:
			return new CursorLoader(getSherlockActivity(), FeedItemTable.CONTENT_URI,
					null, null, null, FeedItemTable._ID + " DESC");
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
		case FEED_LOADER:
			feedAdapter.swapCursor(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		SherlockFragmentActivity activity = getSherlockActivity();
		switch (resultCode) {
		case UpdateService.STATUS_RUNNING:
			if (activity != null) {
				getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
			}
			break;
		case UpdateService.STATUS_FINISHED:

			// Update status flag
			isUpdateRunning = false;
			// Schedule AutoUpdate depending on Settings
			if ((Boolean) PrefUtils.getUserSetting(activity, SETTINGS_NAME.AUTO_UPDATE)) {
				UpdateHelper.scheduleUpdate(activity.getApplicationContext());
			}

			// Dismiss progress bar
			activity.setSupportProgressBarIndeterminateVisibility(false);

			// Now we know that URL is valid and can save it
			PrefUtils.savePrefParameter(activity, PREF_NAME.FEED_URL, feedUrl);

			if (resultData.getInt(UpdateService.EXTRAS_INSERTED_COUNT) != 0) {

				// Start refreshing of content
				LoaderManager manager = activity.getSupportLoaderManager();
				manager.destroyLoader(FEED_LOADER);
				manager.initLoader(FEED_LOADER, null, this);
			}
			break;
		case UpdateService.STATUS_ERROR:

			// Update status flag
			isUpdateRunning = false;

			// Dismiss progress bar
			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);

			Exception exception = (Exception) resultData
					.getSerializable(UpdateService.EXTRAS_EXCEPTION);
			try {
				throw exception;
			} catch (ServerNotFoundException e) {
				showErrorDialog("Server not found");
			} catch (XmlPullParserException e) {
				showErrorDialog("Not valid xml response");
			} catch (UnknownHostException e) {
				showErrorDialog("Unknown host. Please, check the url or the Internet connection");
			} catch (Exception e) {
				showErrorDialog("Smth was wrong");
				e.printStackTrace();
			}
		}

	}

	private void showErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
		builder.setTitle(R.string.feedlist_error_title);
		builder.setMessage(message);

		OnClickListener positiveButtonListener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateFeed();
			}
		};

		OnClickListener negativeButtonListener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				errorCallback.onError();
			}
		};

		builder.setPositiveButton(getString(R.string.feedlist_button_retry),
				positiveButtonListener);
		builder.setNegativeButton(getString(R.string.feedlist_button_cancel),
				negativeButtonListener);

		AlertDialog alert = builder.create();
		alert.show();
	}

}
