package org.t2health.lib.preference;

import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.SharedPref;
import org.t2health.lib.analytics.Analytics;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.nullwire.trace.ExceptionHandler;

public abstract class BasePreferenceActivity extends PreferenceActivity {
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
	protected final String getIntentText(Intent intent, String extraKey) {
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
}
