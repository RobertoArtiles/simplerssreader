package com.the_roberto.rssreader.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderContract {

    public static final String AUTHORITY = "com.the_roberto.rssreader";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public interface ItemsColumns extends BaseColumns {
    	String TABLE_NAME = "feed_items";
    	String COLUMN_GUID = "guid";
        String COLUMN_LINK = "link";
        String COLUMN_TITLE = "title";
        String COLUMN_DESCRIPTION = "description";
        String COLUMN_DATE = "date";
        String COLUMN_IS_NEW = "isnew";
    }
    
    // Private constructor to ensure nobody instantiates this
    private ProviderContract() {
    }
}
