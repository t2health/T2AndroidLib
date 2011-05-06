package org.t2health.lib.accessibility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.t2health.lib.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * A WebView that is designed to read its contents. In order to get the screen
 * reading to work properly, you must enable accessibility in system settings,
 * enabled a screen reader such as TalkBack and properly configure the provider
 * for this webview.
 * The later can be done by adding the following to your Manifest file.
 *		<provider android:name="org.t2health.lib.accessibility.TtsContentProvider"
 *			android:authorities="<YOUR APP CLASSPATH HERE>.TtsContentProvider" />
 * @author robbiev
 *
 */
public class AccessibleWebView extends WebView {
	private static final int WHAT_START_DOCUMENT_REACHED = 0;
	private static final int WHAT_END_DOCUMENT_REACHED = 1;
	
	private AccessibilityManager aManager;
	private JSInterface jsInterface;
	private JSInterfaceHandler jsInterfaceHandler;
	
	public AccessibleWebView(Context context) {
		super(context);
		this.init();
	}
	
	public AccessibleWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}
	
	public AccessibleWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}
	
	/**
	 * Init the webview and the necessary fields.
	 */
	private void init() {
		aManager = (AccessibilityManager)this.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
		jsInterface = new JSInterface();
		jsInterfaceHandler = new JSInterfaceHandler();
		
		this.setWebChromeClient(new WebChromeClient());
		this.setBackgroundColor(Color.TRANSPARENT); // make the bg transparent
		
		WebSettings settings = this.getSettings();
		settings.setJavaScriptEnabled(true);
		
		// register a class to handle communication between JS and this class.
		this.addJavascriptInterface(jsInterface, "T2AWV_INTERFACE");
	}
	
	@Override
	public void loadData(String data, String mimeType, String encoding) {
		StringBuffer sb = new StringBuffer();
		
		// add the TTS code to the HTML if everything is configured well.
		if(aManager.isEnabled() && TtsContentProvider.isConfigured()) {
			appendJS(sb);
		}
		
		sb.append(data);
		super.loadData(sb.toString(), mimeType, encoding);
	}

	@Override
	public void loadDataWithBaseURL(String baseUrl, String data,
			String mimeType, String encoding, String historyUrl) {
		StringBuffer sb = new StringBuffer();
		
		// add the TTS code to the HTML if everything is configured well.
		if(aManager.isEnabled() && TtsContentProvider.isConfigured()) {
			appendJS(sb);
		}
		
		sb.append(data);
		super.loadDataWithBaseURL(baseUrl, sb.toString(), mimeType, encoding, historyUrl);
	}
	
	/**
	 * Handles the appending of the code. The JS is read from a file in the raw
	 * resources.
	 * @param sb
	 */
	private void appendJS(StringBuffer sb) {
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("var IDEAL_URI_PREFIX = '"+ TtsContentProvider.getURIPrefix() +"';\n");
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(this.getContext().getResources().openRawResource(R.raw.ideal_tts_aggregated)));
			String line = null;
			while((line = is.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			// ignore
		}
		sb.append("</script>\n");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(aManager.isEnabled()) {
			if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return super.onKeyDown(KeyEvent.KEYCODE_P, changeKeyCode(event, KeyEvent.KEYCODE_P));
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				return super.onKeyDown(KeyEvent.KEYCODE_Q, changeKeyCode(event, KeyEvent.KEYCODE_Q));
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				return false;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(aManager.isEnabled()) {
			if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return super.onKeyUp(KeyEvent.KEYCODE_Q, changeKeyCode(event, KeyEvent.KEYCODE_Q));
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				return super.onKeyUp(KeyEvent.KEYCODE_Q, changeKeyCode(event, KeyEvent.KEYCODE_Q));
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				return false;
			}
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	private static KeyEvent changeKeyCode(KeyEvent base, int newKeyCode) {
		return new KeyEvent(
				base.getDownTime(),
				base.getEventTime(),
				base.getAction(),
				newKeyCode,
				base.getRepeatCount(),
				base.getMetaState(),
				base.getDeviceId(),
				base.getScanCode(),
				base.getFlags()
		);
	}
	
	/**
	 * Handles the communication between the JS and this class.
	 * @author robbiev
	 *
	 */
	private class JSInterface {
		public void startOfDocumentReached() {
			jsInterfaceHandler.sendEmptyMessage(WHAT_START_DOCUMENT_REACHED);
		}
		
		public void endOfDocuemntReached() {
			jsInterfaceHandler.sendEmptyMessage(WHAT_END_DOCUMENT_REACHED);
		}
	}
	
	private class JSInterfaceHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			View tmpView;
			switch(msg.what) {
				// we are at the start of the doc, shift focus to the view above.
				case WHAT_START_DOCUMENT_REACHED:
					if((tmpView = focusSearch(FOCUS_UP)) != null) {
						tmpView.requestFocus();
					}
					break;
					// we are at the start of the doc, shift focus to the view below.	
				case WHAT_END_DOCUMENT_REACHED:
					if((tmpView = focusSearch(FOCUS_DOWN)) != null) {
						tmpView.requestFocus();
					}
					break;
			}
		}
	}
}
