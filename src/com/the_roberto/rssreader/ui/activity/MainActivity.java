package com.the_roberto.rssreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.the_roberto.rssreader.R;
import com.the_roberto.rssreader.ui.frgament.AddFeedFragment;
import com.the_roberto.rssreader.ui.frgament.AddFeedFragment.OnStartDownloadListener;
import com.the_roberto.rssreader.ui.frgament.FeedListFragment;
import com.the_roberto.rssreader.ui.frgament.FeedListFragment.OnErrorListener;
import com.the_roberto.rssreader.ui.frgament.FeedListFragment.OnFeedItemClickListener;
import com.the_roberto.rssreader.ui.frgament.FeedListFragment.OnSettingsClickListener;
import com.the_roberto.rssreader.util.PrefUtils;
import com.the_roberto.rssreader.util.PrefUtils.PREF_NAME;
import com.the_roberto.rssreader.util.PrefUtils.SETTINGS_NAME;

public class MainActivity extends SherlockFragmentActivity implements
		OnStartDownloadListener, OnFeedItemClickListener, OnErrorListener,
		OnSettingsClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		setSupportProgressBarIndeterminateVisibility(false);
		String feedUrl = PrefUtils.getPrefParameter(this, PREF_NAME.FEED_URL);
		PrefUtils.getUserSetting(this, SETTINGS_NAME.NUMBER_ITEMS);
		if (feedUrl != null && feedUrl.length() != 0) {
			showFeedListFragment(feedUrl);
		} else {
			showAddFeedFragment();
		}
	}

	@Override
	public void onStartDownload(String url) {
		showFeedListFragment(url);
	}

	@Override
	public void onFeedItemClick(long id) {
		Intent intent = new Intent(this, ItemDetailsActivity.class);
		intent.putExtra(ItemDetailsActivity.EXTRAS_ID, id);
		startActivity(intent);
	}

	private void showAddFeedFragment() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, new AddFeedFragment());
		transaction.commit();
	}

	private void showFeedListFragment(String url) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, FeedListFragment.newInstance(url));
		transaction.commit();
	}

	@Override
	public void onError() {
		showAddFeedFragment();
	}

	@Override
	public void onSettingsClick() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
}