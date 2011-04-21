package org.t2health.lib.accessibility;

import org.t2health.lib.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

class ActivityTouchLayer extends View {
	private boolean mIsInitilized = false;
	private boolean mIsEnabled = false;
	private View mBaseView = null;
	
	public ActivityTouchLayer(Context context) {
		super(context);
		initialize();
	}
	
	public ActivityTouchLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public ActivityTouchLayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	
	private void initialize() {
		if(mIsInitilized) {
			return;
		}
		mIsInitilized = true;
		
		// Attempt to find the base view for the activity.
		ViewParent baseParent = this.getParent();
		ViewParent tmpParent = null;
		while(true) {
			tmpParent = baseParent.getParent();
			if(tmpParent == null) {
				break;
			} else {
				baseParent = tmpParent;
			}
		}
		
		// Find the content area for the base view.
		try {
			mBaseView = ((View)baseParent).findViewById(R.id.accessibilityActivityContent);
			this.mIsEnabled = true;
		} catch (ClassCastException e) {
			throw new RuntimeException("Could not case the activity's base parent to a view.");
		} catch (NullPointerException e) {
			throw new RuntimeException("Could not find element with id of accessibilityActivityContent defined in accessibility_layout.");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Accessibility could not be enabled, don't try to do any work.
		if(!this.mIsEnabled) {
			return super.onTouchEvent(event);
		}
		
		return super.onTouchEvent(event);
	}
}
