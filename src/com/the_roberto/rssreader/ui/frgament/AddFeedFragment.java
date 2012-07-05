package com.the_roberto.rssreader.ui.frgament;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.the_roberto.rssreader.R;
import com.the_roberto.rssreader.util.NetworkUtils;
import com.the_roberto.rssreader.util.UiUtils;

public class AddFeedFragment extends SherlockFragment {
	public interface OnStartDownloadListener {
		void onStartDownload(String url);
	}

	private OnStartDownloadListener downloadCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			downloadCallback = (OnStartDownloadListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.getLocalClassName()
					+ " must implement OnStartDownloadListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_addfeed, null);
		final EditText feedUrl = (EditText) view.findViewById(R.id.main_feedurl);
		Button submit = (Button) view.findViewById(R.id.main_submit);

		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				processInput(feedUrl.getText().toString());
				UiUtils.hideKeyBoard(getActivity(), feedUrl);
			}
		});

		return view;
	}

	private void processInput(String input) {
		if (input.length() == 0) {
			UiUtils.showToast(getActivity(), getString(R.string.add_feed_empty_url));
		} else if (!NetworkUtils.validateUrl(input)) {
			UiUtils.showToast(getActivity(), getString(R.string.add_feed_invalid_url));
		} else {
			downloadCallback.onStartDownload(input);
		}
	}

}
