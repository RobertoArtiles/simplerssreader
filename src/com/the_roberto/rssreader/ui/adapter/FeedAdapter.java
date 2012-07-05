package com.the_roberto.rssreader.ui.adapter;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.the_roberto.rssreader.R;
import com.the_roberto.rssreader.db.FeedItemTable;

public class FeedAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	private HashMap<Integer, Integer> idMap;

	public FeedAdapter(Activity activity, Cursor cursor) {
		super(activity, cursor, false);
		inflater = activity.getLayoutInflater();
		idMap = new HashMap<Integer, Integer>();
	}

	private class ViewHolder {
		ImageView dot;
		TextView title;
		TextView date;
	}

	@Override
	public long getItemId(int position) {
		return idMap.get(position);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		idMap.put(cursor.getPosition(), cursor.getInt(FeedItemTable.Query._ID));
		ViewHolder holder = (ViewHolder) view.getTag();
		if (cursor.getInt(FeedItemTable.Query.COLUMN_IS_NEW) == 0) {
			holder.dot.setVisibility(View.INVISIBLE);
		} else {
			holder.dot.setVisibility(View.VISIBLE);
		}
		holder.title.setText(cursor.getString(FeedItemTable.Query.COLUMN_TITLE));
		holder.date.setText(cursor.getString(FeedItemTable.Query.COLUMN_DATE));
	}

	@Override
	public View newView(Context arg0, Cursor cursor, ViewGroup arg2) {
		View view = inflater.inflate(R.layout.list_item_feed, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.dot = (ImageView) view.findViewById(R.id.list_item_feed_dot);
		viewHolder.title = (TextView) view.findViewById(R.id.list_item_feed_title);
		viewHolder.date = (TextView) view.findViewById(R.id.list_item_feed_date);
		view.setTag(viewHolder);

		return view;
	}
}
