package org.t2health.lib.activity;


import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.R;
import org.t2health.lib.SharedPref;
import org.t2health.lib.accessibility.Accessibility;
import org.t2health.lib.analytics.Analytics;
import org.t2health.lib.db.DatabaseOpenHelper;
import org.t2health.lib.db.ManifestSqliteOpenHelperFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.nullwire.trace.ExceptionHandler;

/**
 * The base class to use when creating an activity. This class will read
 * meta-data from the manifest.xml file in order to configure ORM, Analytics,
 * Remote Stack Trace and other code.
 * The code in this activity is the exact same as the code used in
 * BasePreferenceActivity and BaseService, be sure any changes you make to this
 * source code is copied to those as well.
 * @author robbiev
 */
public abstract class BaseActivity extends OrmLiteBaseActivity<DatabaseOpenHelper> {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// register remote callbacks for analytics and stack trace collection.
		// configure remote stack trace collector
        if(ManifestMetaData.RemoteStackTrace.isEnabled(this) && SharedPref.RemoteStackTrace.isEnabled(this)) {
        	ExceptionHandler.register(this, 
        			ManifestMetaData.RemoteStackTrace.getURL(this)
			);
        }
        
		// configure the database.
        if(ManifestMetaData.Database.isConfigured(this) && ManifestSqliteOpenHelperFactory.isClassesConfigured(this)) {
			OpenHelperManager.setOpenHelperFactory(
					ManifestSqliteOpenHelperFactory.getInstance()
			);
		}
        
		// configure and make analytics event call.
		if(ManifestMetaData.Analytics.isEnabled(this)) {
			Analytics.init(
					Analytics.providerFromString(ManifestMetaData.Analytics.getProvider(this)),
					ManifestMetaData.Analytics.getProviderKey(this), 
					SharedPref.Analytics.isEnabled(this)
			);
			Analytics.setDebugEnabled(ManifestMetaData.isDebugEnabled(this));
			Analytics.onPageView();
			String event = getAnalyticsActivityEvent();
			if(event != null) {
				Analytics.onEvent(event);
			}
		}
	}
	
	/**
	 * When the activity loads, this string will be sent to Analytics.onEvent
	 * @return (default: this.getClass().getSimpleName())
	 */
	protected String getAnalyticsActivityEvent() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * Retrieve a string value from an intent. This will handle both resource id
	 * and string values.
	 * @param intent
	 * @param extraKey
	 * @return
	 */
	protected String getIntentText(Intent intent, String extraKey) {
		String text = intent.getStringExtra(extraKey);
		
		if(text != null && text.matches("[0-9]+")) {
			int resId = Integer.parseInt(text);
			String resourceText = getString(resId);
			if(resourceText != null) {
				text = resourceText;
			}
		}
		
		return text;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Analytics.onStartSession(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Analytics.onEndSession(this);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		// Enabled the accessibility layer
		if(ManifestMetaData.Accessibility.isEnabled(this) && Accessibility.isSystemEnabled(this)) {
			View baseView = getLayoutInflater().inflate(R.layout.accessibility_layout, null);
			((FrameLayout)baseView.findViewById(R.id.accessibilityActivityContent)).addView(
					this.getLayoutInflater().inflate(layoutResID, null),
					LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT
			);
			super.setContentView(baseView);
			
		// Dont use accessibility
		} else {
			super.setContentView(layoutResID);
		}
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		// Enabled the accessibility layer
		if(ManifestMetaData.Accessibility.isEnabled(this) && Accessibility.isSystemEnabled(this)) {
			View baseView = getLayoutInflater().inflate(R.layout.accessibility_layout, null);
			((FrameLayout)baseView.findViewById(R.id.accessibilityActivityContent)).addView(
					view,
					params
			);
			super.setContentView(baseView);
			
		// Dont use accessibility
		} else {
			super.setContentView(view, params);
		}
	}

	@Override
	public void setContentView(View view) {
		// Enabled the accessibility layer
		if(ManifestMetaData.Accessibility.isEnabled(this) && Accessibility.isSystemEnabled(this)) {
			View baseView = getLayoutInflater().inflate(R.layout.accessibility_layout, null);
			((FrameLayout)baseView.findViewById(R.id.accessibilityActivityContent)).addView(
					view,
					LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT
			);
			super.setContentView(baseView);
		
		// Dont use accessibility
		} else {
			super.setContentView(view);
		}
	}
}
