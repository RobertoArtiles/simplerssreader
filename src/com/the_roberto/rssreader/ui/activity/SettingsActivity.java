package com.the_roberto.rssreader.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.the_roberto.rssreader.R;
import com.the_roberto.rssreader.db.FeedItemTable;
import com.the_roberto.rssreader.util.UpdateHelper;
import com.the_roberto.rssreader.util.PrefUtils;
import com.the_roberto.rssreader.util.PrefUtils.PREF_NAME;

public class SettingsActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Preference cleanCachePref = findPreference(PrefUtils.SETTINGS_NAME.CLEAN_CACHE
				.name());
		cleanCachePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				new AsyncTask<Void, Void, Void>() {
					private ProgressDialog progress;

					protected void onPreExecute() {
						progress = ProgressDialog.show(SettingsActivity.this, null,
								getString(R.string.pref_deleting_message));
					};

					protected Void doInBackground(Void... params) {
						PrefUtils.savePrefParameter(getApplicationContext(),
								PREF_NAME.LAST_GUID, "");
						getContentResolver()
								.delete(FeedItemTable.CONTENT_URI, null, null);
						return null;
					};

					@Override
					protected void onPostExecute(Void result) {
						progress.dismiss();
					}

				}.execute();

				return true;
			}
		});

		Preference changeFeedPref = findPreference(PrefUtils.SETTINGS_NAME.CHANGE_FEED
				.name());

		changeFeedPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				new AsyncTask<Void, Void, Void>() {
					private ProgressDialog progress;

					protected void onPreExecute() {
						progress = ProgressDialog.show(SettingsActivity.this, null,
								getString(R.string.pref_deleting_message));
					};

					protected Void doInBackground(Void... params) {
						PrefUtils.savePrefParameter(getApplicationContext(),
								PREF_NAME.LAST_GUID, "");
						PrefUtils.savePrefParameter(getApplicationContext(),
								PREF_NAME.FEED_URL, "");
						getContentResolver()
								.delete(FeedItemTable.CONTENT_URI, null, null);
						return null;
					};

					@Override
					protected void onPostExecute(Void result) {
						progress.dismiss();
						Intent intent = new Intent(SettingsActivity.this,
								MainActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
										| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}

				}.execute();

				return true;
			}
		});

		Preference autoUpdate = findPreference(PrefUtils.SETTINGS_NAME.AUTO_UPDATE.name());
		autoUpdate.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if ((Boolean) newValue) {
					UpdateHelper.scheduleUpdate(getApplicationContext());
				} else {
					UpdateHelper.cancelUpdate(getApplicationContext());
				}
				return true;
			}
		});

		Preference timeInterval = findPreference(PrefUtils.SETTINGS_NAME.TIME_INTERVAL
				.name());
		timeInterval.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				UpdateHelper.scheduleUpdate(getApplicationContext(),
						Integer.parseInt((String) newValue));
				return true;
			}
		});
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
		}
		return true;
	}
}
