/*
 * 
 * T2AndroidLib
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2AndroidLib001
 * Government Agency Original Software Title: T2AndroidLib
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.lib.util;

import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Some tasks to perform to a webview.
 * @author robbiev
 *
 */
public class WebViewUtil {
	/**
	 * Sets the text style of a webview to match the current style of textviews 
	 * in your app.
	 * @param c
	 * @param wv
	 * @param contentString
	 * @param textColor
	 */
	public static void formatWebViewText(Activity c, WebView wv, String contentString, int textColor) {
		if(contentString == null) {
			contentString = "<span></span>";
		}

		TextView tv = new TextView(c);
		DisplayMetrics metrics = new DisplayMetrics();
		c.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int dpi = metrics.densityDpi;
		float textSizePixels = tv.getTextSize();
		int webViewFontSizePoints = (int)(textSizePixels * 72 / dpi * 2.75);
		int color = textColor;

		StringBuffer contentBuffer = new StringBuffer();
		contentBuffer.append("\n<style type=\"text/css\">\n");
		contentBuffer.append("\tbody,a {\n");
			contentBuffer.append("\t\tcolor:rgb(");
			contentBuffer.append(Color.red(color));
			contentBuffer.append(",");
			contentBuffer.append(Color.green(color));
			contentBuffer.append(",");
			contentBuffer.append(Color.blue(color));
			contentBuffer.append(");\n");
		contentBuffer.append("\t}\n");
		contentBuffer.append("</style>");
		contentBuffer.append(contentString);

		wv.setWebChromeClient(new WebChromeClient());
		wv.setBackgroundColor(Color.TRANSPARENT); // make the bg transparent
		
		WebSettings settings = wv.getSettings();
		settings.setDefaultFontSize(webViewFontSizePoints);
		settings.setDefaultFixedFontSize(webViewFontSizePoints);
		settings.setJavaScriptEnabled(true);
		
		wv.loadDataWithBaseURL("", contentBuffer.toString(), "text/html", "utf-8", null);
	}
}
