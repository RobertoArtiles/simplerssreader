package com.the_roberto.rssreader.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class UiUtils {
	private static final int MESSAGE_DURATION = Toast.LENGTH_SHORT;

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, MESSAGE_DURATION).show();
	}

	public static void hideKeyBoard(Context context, EditText editText) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

}
