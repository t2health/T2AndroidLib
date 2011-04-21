package org.t2health.lib.activity;

import org.t2health.lib.R;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
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
 * The left button of this activity is by default configured to act as a "back"
 * button. Pressing this will set the result to RESULT_BACK and finish the 
 * activity.
 * 
 * The code in this class is the exact same code used in
 * BasePreferenceNavigationActivity. If you change anything in this class,
 * be sure to copy that over.
 * @author robbiev
 *
 */
public abstract class BaseNavigationActivity extends BaseSecurityActivity {
	private static final String exceptionText = "setContentView should be ran before any modication methods.";
	
	/**
	 * The text (presented as String or Resource ID) to set in the title bar.
	 */
	public static final String EXTRA_TITLE_TEXT = "title";
	
	/**
	 * The visibility of the title text. This is excepted to be an int and
	 * standard view visibility attributes are accepted. 
	 * (eg View.VISIBLE, View.INVISIBLE, View.GONE, etc)
	 */
	public static final String EXTRA_TITLE_VISIBILITY = "titleVisibility";
	
	/**
	 * The text (presented as String or Resource ID) to set as the right 
	 * button's text.
	 */
	public static final String EXTRA_RIGHT_BUTTON_TEXT = "rightButtonText";
	
	/**
	 * The visibility of the right button. This is excepted to be an int and
	 * standard view visibility attributes are accepted. 
	 * (eg View.VISIBLE, View.INVISIBLE, View.GONE, etc)
	 */
	public static final String EXTRA_RIGHT_BUTTON_VISIBILITY = "rightButtonVisibility";
	
	/**
	 * The text (presented as String or Resource ID) to set as the left 
	 * button's text.
	 */
	public static final String EXTRA_LEFT_BUTTON_TEXT = "leftButtonText";
	
	/**
	 * The visibility of the left button. This is excepted to be an int and
	 * standard view visibility attributes are accepted. 
	 * (eg View.VISIBLE, View.INVISIBLE, View.GONE, etc)
	 * The default is "Back"
	 */
	public static final String EXTRA_LEFT_BUTTON_VISIBILITY = "leftButtonVisibility";
	
	/**
	 * The result code that will be send when the left button is pressed and
	 * its method is not overriden.
	 */
	public static final int RESULT_BACK = 34980457;

	private NavigationItemEventListener mNavItemEventListener;
	private boolean mIsInitialized = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
	}
	
	private void initialize() {
		if(mIsInitialized) {
			return;
		}
		mIsInitialized = true;
		
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
		mNavItemEventListener = new NavigationItemEventListener();

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
		this.findViewById(R.id.navigationLeftButton).setOnClickListener(mNavItemEventListener);
		this.findViewById(R.id.navigationRightButton).setOnClickListener(mNavItemEventListener);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		View baseLayout = getLayoutInflater().inflate(R.layout.base_navigation_activity, null);
		((FrameLayout)baseLayout.findViewById(R.id.navigationContent)).addView(
				this.getLayoutInflater().inflate(layoutResID, null),
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
		);
		super.setContentView(baseLayout);
		initialize();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		View baseLayout = getLayoutInflater().inflate(R.layout.base_navigation_activity, null);
		((FrameLayout)baseLayout.findViewById(R.id.navigationContent)).addView(
				view,
				params
		);
		super.setContentView(baseLayout);
		initialize();
	}

	@Override
	public void setContentView(View view) {
		View baseLayout = getLayoutInflater().inflate(R.layout.base_navigation_activity, null);
		((FrameLayout)baseLayout.findViewById(R.id.navigationContent)).addView(
				view,
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
		);
		super.setContentView(baseLayout);
		initialize();
	}

	@Override
	public void setTitle(CharSequence title) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		((TextView)this.findViewById(R.id.navigationTitle)).setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		((TextView)this.findViewById(R.id.navigationTitle)).setText(titleId);
	}

	@Override
	public void setTitleColor(int textColor) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		((TextView)this.findViewById(R.id.navigationTitle)).setTextColor(textColor);
	}
	
	/**
	 * Sets the base background color of the title bar.
	 * @param color	the color to use.
	 */
	public void setNavigationTitleBackgroundColor(int color) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		this.findViewById(R.id.navigationTitleWrapper).setBackgroundColor(color);
	}
	
	/**
	 * Set the visibility of the left button.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default visible)
	 */
	protected void setLeftNavigationButtonVisibility(int v) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		this.findViewById(R.id.navigationLeftButton).setVisibility(v);
	}

	/**
	 * Set the visibility of the right button.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default gone)
	 */
	protected void setRightNavigationButtonVisibility(int v) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		this.findViewById(R.id.navigationRightButton).setVisibility(v);
	}
	
	/**
	 * Set the left button's text with a specific string resource id.
	 * @param resId
	 */
	protected void setLeftNavigationButtonText(int resId) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		((TextView)this.findViewById(R.id.navigationLeftButton)).setText(resId);
	}
	
	private void setLeftNavigationButtonText(CharSequence text) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		((TextView)this.findViewById(R.id.navigationLeftButton)).setText(text);
	}
	
	/**
	 * Specify if the left navigation button should be enabled.
	 * @param b
	 */
	protected void setLeftNavigationButtonEnabled(boolean b) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		this.findViewById(R.id.navigationLeftButton).setEnabled(b);
	}
	
	/**
	 * Set the right button's text with a specific string resource id.
	 * @param resId
	 */
	protected void setRightNavigationButtonText(int resId) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		((TextView)this.findViewById(R.id.navigationRightButton)).setText(resId);
	}
	
	private void setRightNavigationButtonText(CharSequence text) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		((TextView)this.findViewById(R.id.navigationRightButton)).setText(text);
	}
	
	/**
	 * Specify if the right navigation button should be enabled.
	 * @param b
	 */
	protected void setRightNavigationButtonEnabled(boolean b) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
		this.findViewById(R.id.navigationRightButton).setEnabled(b);
	}
	
	/**
	 * Set the visibility of the entire title bar including buttons.
	 * @param v	use view visibility constants. View.VISIBLE, View.INVISIBLE or 
	 * 			View.GONE. (default visible)
	 */
	protected void setNavigationTitleBarVisibility(int v) {
		if(!mIsInitialized) {
			throw new RuntimeException(exceptionText);
		}
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
