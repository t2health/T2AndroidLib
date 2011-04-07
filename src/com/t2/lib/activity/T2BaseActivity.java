package com.t2.lib.activity;


import android.content.Context;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.support.ConnectionSource;
import com.nullwire.trace.ExceptionHandler;
import com.t2.lib.ManifestMetaData;
import com.t2.lib.SharedPref;
import com.t2.lib.analytics.Analytics;
import com.t2.lib.db.T2DatabaseHelper;
import com.t2.lib.db.T2SqliteOpenHelperFactory;

/**
 * The base class to use when creating an activity. This class will read
 * meta-data from the manifest.xml file in order to configure ORM, Analytics,
 * Remote Stack Trace and other code.
 * @author robbiev
 */
public abstract class T2BaseActivity extends OrmLiteBaseActivity<T2DatabaseHelper> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean inDebugMode = ManifestMetaData.getBoolean(this, ManifestMetaData.DEBUG_MODE);
		
		// register remote callbacks for analytics and stack trace collection.
		if(!inDebugMode) {
			// configure remote stack trace collector
			String stackTraceURL = ManifestMetaData.getString(this, ManifestMetaData.REMOTE_STACK_TRACK_URL);
	        if(SharedPref.getRemoteStackTraceEnabled(this) && stackTraceURL != null && stackTraceURL.length() > 0) {
	        	ExceptionHandler.register(this, stackTraceURL);
	        }
        }

		// configure the database.
		if(isDatabaseEnabled()) {
			OpenHelperManager.setOpenHelperFactory(
					new T2SqliteOpenHelperFactory(this.getDatabaseHelperClass())
			);
		}
		
		// configure and make analytics event call.
		Analytics.init(
				Analytics.providerFromString(ManifestMetaData.getString(this, ManifestMetaData.ANALYTICS_PROVIDER)),
				ManifestMetaData.getString(this, ManifestMetaData.ANALYTICS_KEY), 
				SharedPref.getAnalyticsEnabled(this) && !inDebugMode
		);
		Analytics.setDebugEnabled(ManifestMetaData.getBoolean(this, ManifestMetaData.DEBUG_MODE));
		Analytics.onPageView();
		String event = getAnalyticsActivityEvent();
		if(event != null) {
			Analytics.onEvent(event);
		}
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
	public synchronized T2DatabaseHelper getHelper() {
		if(!isDatabaseEnabled()) {
			return null;
		}
		return super.getHelper();
	}

	@Override
	public ConnectionSource getConnectionSource() {
		if(!isDatabaseEnabled()) {
			return null;
		}
		return super.getConnectionSource();
	}

	@Override
	protected T2DatabaseHelper getHelperInternal(Context context) {
		if(!isDatabaseEnabled()) {
			return null;
		}
		return super.getHelperInternal(context);
	}

	@Override
	protected void releaseHelper(T2DatabaseHelper helper) {
		if(!isDatabaseEnabled()) {
			return;
		}
		super.releaseHelper(helper);
	}

	/**
	 * Helper method to determine if the database is ready to be used by this
	 * activity.
	 * @return	true if the database is enabled.
	 */
	protected boolean isDatabaseEnabled() {
		return T2SqliteOpenHelperFactory.isDatabaseConfigured(this) && getDatabaseHelperClass() != null;
	}
	
	/**
	 * Provides the activity with a chance to override the operation of the 
	 * database helper. @see T2DatabaseHelper
	 * @return	The class of the object to use as the database helper.
	 */
	protected Class<T2DatabaseHelper> getDatabaseHelperClass() {
		return T2DatabaseHelper.class;
	}
	
	protected String getAnalyticsActivityEvent() {
		return this.getClass().getSimpleName();
	}
}
