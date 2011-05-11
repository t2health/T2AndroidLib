package org.t2health.lib.util;

import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.SharedPref;
import org.t2health.lib.analytics.Analytics;
import org.t2health.lib.db.DatabaseOpenHelper;
import org.t2health.lib.db.ManifestSqliteOpenHelperFactory;

import android.content.Intent;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.nullwire.trace.ExceptionHandler;

public abstract class BaseService extends OrmLiteBaseService<DatabaseOpenHelper> {
	private boolean isORMConfigured = false;
	
	@Override
	public void onCreate() {
		super.onCreate();

		// register remote callbacks for analytics and stack trace collection.
		// configure remote stack trace collector
        if(ManifestMetaData.RemoteStackTrace.isEnabled(this) && SharedPref.RemoteStackTrace.isEnabled(this)) {
        	ExceptionHandler.register(this, 
        			ManifestMetaData.RemoteStackTrace.getURL(this)
			);
        }
        
		// configure the database.
        isORMConfigured = ManifestMetaData.Database.isConfigured(this) && ManifestSqliteOpenHelperFactory.isClassesConfigured(this);
        if(isORMConfigured) {
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
	public final synchronized DatabaseOpenHelper getHelper() {
		if(isORMConfigured) {
			return super.getHelper();
		}
		
		throw new RuntimeException("The ORM has not been properly configured. Look at the Library's AndroidManifest.xml file for setup instructions.");
	}
}
