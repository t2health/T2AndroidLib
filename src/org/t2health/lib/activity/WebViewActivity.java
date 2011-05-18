package org.t2health.lib.activity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.t2health.lib.R;
import org.t2health.lib.util.WebViewUtil;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;

/**
 * A simple activity that will display HTML generated content.
 * @author robbiev
 * 
 * @TODO Make this activity take a URL too. Then use a conditional GET to
 * get the updated content. Cache the new content some place.
 *
 */
public class WebViewActivity extends BaseNavigationActivity {
	/**
	 * The content text (presented as String or Resource ID).
	 */
	public static final String EXTRA_CONTENT = "content";
	
	
	public static final String EXTRA_CONTENT_URL = "contentURL";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		String contentURL = intent.getStringExtra(EXTRA_CONTENT_URL);
		String contentString = this.getIntentText(intent, EXTRA_CONTENT);
		if(contentString == null || contentString.length() == 0) {
			this.finish();
			return;
		}

		this.setContentView(R.layout.webview_activity);

		contentString = getContentCache(contentURL, contentString);
		
		WebView wv = (WebView)this.findViewById(R.id.webview);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		WebViewUtil.formatWebViewText(this, wv, contentString, Color.WHITE);
	}
	
	private void updateContentCahe(String url, long lastChangedTime) {
		if(url == null || url.length() == 0 || !isAllowedToAccessInternet() || !isNetworkConnected()) {
			return;
		}
		//init variables
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
		Random rand = new Random();
		String eTag = "GLXK"+ rand.nextInt(10000);
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
			request.addHeader("If-None-Match", eTag);
			request.addHeader("If-Modified-Since", dateFormat.format(new Date(lastChangedTime)));
		
		// prepare the first call.
		boolean success = true;
		Date lastModified = null;
		HttpResponse response = null;
		try {
			response = client.execute(request);
			
			String lastModifiedString = response.getFirstHeader("Last-Modified").getValue();
			if(lastModifiedString != null) {
				try {
					lastModified = dateFormat.parse(lastModifiedString);
				} catch (ParseException e) {
					lastModified = new Date(System.currentTimeMillis());
				}
			}
			
			// try to read data from the response.
			StringBuffer newContent = new StringBuffer();
			BufferedReader contentReader = null;
			try {
				contentReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = "";
				while((line = contentReader.readLine()) != null) {
					newContent.append(line);
					newContent.append("\n");
				}
			} catch (IOException e) {
				success = false;
			}
			
			try {
				if(contentReader != null) {
					contentReader.close();
				}
			} catch (IOException e) {
				success = false;
			}
			
		} catch (ClientProtocolException e) {
			success = false;
		} catch (IOException e) {
			success = false;
		}
		
		if(success) {
			
			
		}
		
	}
	
	private void setContentCache(String url, long expireTime, String content) {
		
	}
	
	private String getContentCache(String url, String defaultContent) {
		if(url == null || url.length() == 0 || !isAllowedToAccessInternet() || !isNetworkConnected()) {
			return defaultContent;
		}
		
		return "";
	}
	
	private boolean isAllowedToAccessInternet() {
		return this.checkPermission(permission.INTERNET, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED;
	}
	
	private boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
	
	private class HTTPCache {
		public String URL;
		public Date lastModified;
		public Date expire;
		public String content;
	}
}
