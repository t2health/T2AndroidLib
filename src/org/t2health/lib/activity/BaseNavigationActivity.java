package org.t2health.lib.activity;

import org.t2health.lib.R;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

/***
 * A base class that is used for basic navigation through the app. It provides
 * a left (typically back) button, a right button and a title with a formatted
 * background.
 * @author robbiev
 *
 */
public abstract class BaseNavigationActivity extends BaseSecurityActivity {
	public static final String EXTRA_TITLE_TEXT = "title";
	public static final String EXTRA_RIGHT_BUTTON_TEXT = "rightButtonText";
	public static final String EXTRA_RIGHT_BUTTON_VISIBILITY = "rightButtonVisibility";
	public static final String EXTRA_LEFT_BUTTON_TEXT = "leftButtonText";
	public static final String EXTRA_LEFT_BUTTON_VISIBILITY = "leftButtonVisibility";
	public static final String EXTRA_TITLE_VISIBILITY = "titleVisibility";
	
	public static final int RESULT_BACK = 34980457;

	private NavigationItemEventListener navItemEventListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.base_navigation_activity);

		// get the activity's title and set it.
		try {
			int resId = this.getPackageManager().getActivityInfo(this.getComponentName(), -1).labelRes;
			if(resId != 0) {
				this.setTitle(resId);
			}
		} catch (NameNotFoundException e) {
			// ignore
		}
		
		// use an event handler that cannot be overridden by parent subclasses.
		navItemEventListener = new NavigationItemEventListener();

		// get intent data.
		Intent intent = this.getIntent();
		
		// get and set component visibility
		this.setLeftNavigationButtonVisibility(
				intent.getIntExtra(
						EXTRA_LEFT_BUTTON_VISIBILITY, 
						//(this.getParent()==null)?View.GONE:View.VISIBLE
						View.VISIBLE
				)
		);
		this.setRightNavigationButtonVisibility(
				intent.getIntExtra(EXTRA_RIGHT_BUTTON_VISIBILITY, View.GONE)
		);
		this.setNavigationTitleBarVisibility(
				intent.getIntExtra(EXTRA_RIGHT_BUTTON_VISIBILITY, View.VISIBLE)
		);
		
		// get and set component text
		String tmpText = null;
		tmpText = getIntentText(intent, EXTRA_TITLE_TEXT);
		if(tmpText != null) {
			this.setTitle(tmpText);
		}
		
		tmpText = getIntentText(intent, EXTRA_LEFT_BUTTON_TEXT);
		if(tmpText != null) {
			this.setLeftNavigationButtonText(tmpText);
		} else {
			this.setLeftNavigationButtonText(R.string.back);
		}
		
		tmpText = getIntentText(intent, EXTRA_RIGHT_BUTTON_TEXT);
		if(tmpText != null) {
			this.setRightNavigationButtonText(tmpText);
		}
		
		// register event handlers
		this.findViewById(R.id.navigationLeftButton).setOnClickListener(navItemEventListener);
		this.findViewById(R.id.navigationRightButton).setOnClickListener(navItemEventListener);
	}
	
	private String getIntentText(Intent intent, String extraKey) {
		int resId = intent.getIntExtra(extraKey, 0);
		String titleText = intent.getStringExtra(extraKey);
		
		if(resId != 0) {
			return getString(resId);
		} else if(titleText != null) {
			return titleText;
		}
		
		return null;
	}

	@Override
	public void setContentView(int layoutResID) {
		((FrameLayout)this.findViewById(R.id.navigationContent)).addView(
				this.getLayoutInflater().inflate(layoutResID, null),
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
		);
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		((FrameLayout)this.findViewById(R.id.navigationContent)).addView(view, params);
	}

	@Override
	public void setContentView(View view) {
		((FrameLayout)this.findViewById(R.id.navigationContent)).addView(
				view,
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
		);
	}

	@Override
	public void setTitle(CharSequence title) {
		((TextView)this.findViewById(R.id.navigationTitle)).setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		((TextView)this.findViewById(R.id.navigationTitle)).setText(titleId);
	}

	@Override
	public void setTitleColor(int textColor) {
		((TextView)this.findViewById(R.id.navigationTitle)).setTextColor(textColor);
	}
	
	/**
	 * Sets the base background color of the title bar.
	 * @param color	the color to use.
	 */
	public void setNavigationTitleBackgroundColor(int color) {
		this.findViewById(R.id.navigationTitleWrapper).setBackgroundColor(color);
	}
	
	/**
	 * Set the visibility of the left button.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default visible)
	 */
	protected void setLeftNavigationButtonVisibility(int v) {
		this.findViewById(R.id.navigationLeftButton).setVisibility(v);
	}

	/**
	 * Set the visibility of the right button.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default gone)
	 */
	protected void setRightNavigationButtonVisibility(int v) {
		this.findViewById(R.id.navigationRightButton).setVisibility(v);
	}
	
	/**
	 * Set the left button's text with a specific string resource id.
	 * @param resId
	 */
	protected void setLeftNavigationButtonText(int resId) {
		((TextView)this.findViewById(R.id.navigationLeftButton)).setText(resId);
	}
	
	private void setLeftNavigationButtonText(CharSequence text) {
		((TextView)this.findViewById(R.id.navigationLeftButton)).setText(text);
	}
	
	/**
	 * Specify if the left navigation button should be enabled.
	 * @param b
	 */
	protected void setLeftNavigationButtonEnabled(boolean b) {
		this.findViewById(R.id.navigationLeftButton).setEnabled(b);
	}
	
	/**
	 * Set the right button's text with a specific string resource id.
	 * @param resId
	 */
	protected void setRightNavigationButtonText(int resId) {
		((TextView)this.findViewById(R.id.navigationRightButton)).setText(resId);
	}
	
	private void setRightNavigationButtonText(CharSequence text) {
		((TextView)this.findViewById(R.id.navigationRightButton)).setText(text);
	}
	
	/**
	 * Specify if the right navigation button should be enabled.
	 * @param b
	 */
	protected void setRightNavigationButtonEnabled(boolean b) {
		this.findViewById(R.id.navigationRightButton).setEnabled(b);
	}
	
	/**
	 * Set the visibility of the entire title bar including buttons.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default visible)
	 */
	protected void setNavigationTitleBarVisibility(int v) {
		this.findViewById(R.id.navigationTitleWrapper).setVisibility(v);
	}

	/**
	 * Fired when the left button is pressed. Unless overridden, 
	 * this will behave like a back button. It will set
	 * the activity result to RESULT_BACK and finish the activity.
	 */
	protected void onLeftNavigationButtonPressed() {
		this.setResult(RESULT_BACK);
		this.finish();
	}

	/**
	 * Fired when the right button is pressed.
	 */
	protected void onRightNavigationButtonPressed() {

	}

	/**
	 * An internal class to handle the interaction of the navigation buttons.
	 * This prevents a subclass of this class from overriding onClick and 
	 * inadvertanly ignoring the behavior of these buttons.
	 * @author robbiev
	 *
	 */
	private class NavigationItemEventListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			switch(arg0.getId()) {
			case R.id.navigationLeftButton:
				onLeftNavigationButtonPressed();
				break;
			case R.id.navigationRightButton:
				onRightNavigationButtonPressed();
				break;
			}
		}
	}
}
