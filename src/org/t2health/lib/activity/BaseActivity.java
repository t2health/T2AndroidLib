package org.t2health.lib.activity;


import org.t2health.lib.IntentFactory;
import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.SharedPref;
import org.t2health.lib.analytics.Analytics;
import org.t2health.lib.db.DatabaseOpenHelper;
import org.t2health.lib.db.ManifestSqliteOpenHelperFactory;

import android.content.Intent;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.nullwire.trace.ExceptionHandler;

/**
 * The base class to use when creating an activity. This class will read
 * meta-data from the manifest.xml file in order to configure ORM, Analytics,
 * Remote Stack Trace and other code.
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
