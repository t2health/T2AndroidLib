package org.t2health.lib.activity;


import org.t2health.lib.R;
import org.t2health.lib.util.WebViewUtil;

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
	/**
	 * The content text (presented as String or Resource ID).
	 */
	public static final String EXTRA_CONTENT = "content";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String contentString = this.getIntentText(getIntent(), EXTRA_CONTENT);
		if(contentString == null || contentString.length() == 0) {
			this.finish();
			return;
		}

		this.setContentView(R.layout.webview_activity);

		WebView wv = (WebView)this.findViewById(R.id.webview);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		WebViewUtil.formatWebViewText(this, wv, contentString, Color.WHITE);
	}
}
