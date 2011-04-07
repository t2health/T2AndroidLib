package com.t2.lib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.t2.lib.R;

/***
 * A base class that is used for basic navigation through the app. It provides
 * a left (typicall back) button, a right button and a title with a formatted
 * background.
 * @author robbiev
 *
 */
public abstract class T2BaseNavigationActivity extends T2BaseActivity {
	public static final String EXTRA_TITLE = "title";
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
		super.setContentView(R.layout.t2_base_navigation_activity);

		// use an event handler that cannot be overridden by parent subclasses.
		navItemEventListener = new NavigationItemEventListener();

		Intent intent = this.getIntent();
		
		// get and set component visibility
		String title = intent.getStringExtra(EXTRA_TITLE);
		int leftButtonVisibility = intent.getIntExtra(EXTRA_LEFT_BUTTON_VISIBILITY, View.VISIBLE);
		int rightButtonVisibility = intent.getIntExtra(EXTRA_RIGHT_BUTTON_VISIBILITY, View.GONE);
		int titleVisibility = intent.getIntExtra(EXTRA_RIGHT_BUTTON_VISIBILITY, View.VISIBLE);
		
		this.setTitle(title);
		this.setLeftButtonVisibility(leftButtonVisibility);
		this.setRightButtonVisibility(rightButtonVisibility);
		this.setTitleBarVisibility(titleVisibility);

		// register event handlers
		this.findViewById(R.id.navigationLeftButton).setOnClickListener(navItemEventListener);
		this.findViewById(R.id.navigationRightButton).setOnClickListener(navItemEventListener);
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
	public void setTitleBackgroundColor(int color) {
		this.findViewById(R.id.navigationTitleWrapper).setBackgroundColor(color);
	}
	
	/**
	 * Set the visibility of the left button.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default visible)
	 */
	protected void setLeftButtonVisibility(int v) {
		this.findViewById(R.id.navigationLeftButton).setVisibility(v);
	}

	/**
	 * Set the visibility of the right button.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default gone)
	 */
	protected void setRightButtonVisibility(int v) {
		this.findViewById(R.id.navigationRightButton).setVisibility(v);
	}
	
	/**
	 * Set the visibility of the entire title bar including buttons.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default visible)
	 */
	protected void setTitleBarVisibility(int v) {
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
