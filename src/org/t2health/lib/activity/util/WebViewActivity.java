package org.t2health.lib.activity.util;


import org.t2health.lib.R;
import org.t2health.lib.activity.BaseNavigationActivity;
import org.t2health.lib.util.WebViewUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;

/**
 * A simple activity that will display HTML generated content.
 * @author robbiev
 *
 */
public class WebViewActivity extends BaseNavigationActivity {
//	private static final String TAG = T2WebViewActivity.class.getSimpleName();

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_CONTENT = "content";

	public static final String EXTRA_TITLE_ID = "titleId";
	public static final String EXTRA_CONTENT_ID = "contentId";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();

		int contentId = intent.getIntExtra(EXTRA_CONTENT_ID, -1);
		String contentString = intent.getStringExtra(EXTRA_CONTENT);

		if(contentString == null && contentId == -1) {
			this.finish();
		}

		if(contentId != -1) {
			contentString = getString(contentId);
		}

		this.setContentView(R.layout.webview_layout);

		WebView wv = (WebView)this.findViewById(R.id.webview);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		WebViewUtil.formatWebView(this, wv, contentString, Color.WHITE);
	}
}
