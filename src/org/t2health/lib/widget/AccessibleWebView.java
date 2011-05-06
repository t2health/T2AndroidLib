package org.t2health.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityManager;
import android.webkit.WebView;

public class AccessibleWebView extends WebView {
	private AccessibilityManager aManager;

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
	
	private void init() {
		aManager = (AccessibilityManager)this.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(aManager.isEnabled()) {
			if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return super.onKeyDown(KeyEvent.KEYCODE_P, event);
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				return super.onKeyDown(KeyEvent.KEYCODE_Q, event);
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				return false;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(aManager.isEnabled()) {
			if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return super.onKeyUp(KeyEvent.KEYCODE_P, event);
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				return super.onKeyUp(KeyEvent.KEYCODE_Q, event);
			} else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				return false;
			}
		}
		
		return super.onKeyUp(keyCode, event);
	}
}
