package org.t2health.lib.accessibility;

import java.util.ArrayList;

import org.t2health.lib.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import android.widget.Toast;

public class Accessibility {
	private static final String SPACE = " ";
	private static String TOAST_PREFIX = null;
	
	public static void show(Dialog d) {
		d.show();
		
		// Adjust the window settings so the soft nav interface can be shown.
		WindowManager.LayoutParams p = new WindowManager.LayoutParams();
		p.format = PixelFormat.TRANSLUCENT;
		d.getWindow().setAttributes(p);
	}
	
	public static void show(Toast t) {
		t.show();
		
		Context context = t.getView().getContext();
		AccessibilityManager aManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		
		// if accessibility is not enabled, don't event bother with the rest.
		if(!aManager.isEnabled()) {
			return;
		}
		
		// get all the textviews from the toast.
		ArrayList<TextView> textViews = new ArrayList<TextView>();
		ArrayList<View> children = new ArrayList<View>();
		children.add(t.getView());
		int childrenIndex = 0;
		View currentChild;
		while(true) {
			if(childrenIndex >= children.size()) {
				break;
			}
			
			currentChild = children.get(childrenIndex);
			if(currentChild instanceof ViewGroup) {
				ViewGroup parent = (ViewGroup)currentChild;
				for(int i = 0; i < parent.getChildCount(); ++i) {
					children.add(parent.getChildAt(i));
				}
				
			} else if(currentChild instanceof TextView) {
				textViews.add((TextView)currentChild);
			}
			
			++childrenIndex;
		}
		
		
		// build the text for the text views
		StringBuffer toastText = new StringBuffer();
		for(int i = 0; i < textViews.size(); ++i) {
			toastText.append(textViews.get(i).getText());
			toastText.append(SPACE);
		}
		
		//only send the event if there is text to send.
		String toastTextStr = toastText.toString().trim();
		if(toastTextStr.length() > 0) {
			if(TOAST_PREFIX == null) {
				TOAST_PREFIX = context.getString(R.string.accessibility_toast_prefix);
			}
			
			// build the accessibility event.
			AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_FOCUSED);
			event.setPackageName(Toast.class.getPackage().toString());
			event.setClassName(Toast.class.getSimpleName());
			event.setContentDescription(TOAST_PREFIX+toastTextStr);
			event.setEventTime(System.currentTimeMillis());
			
			// send the event.
			aManager.sendAccessibilityEvent(event);
		}
	}
}
