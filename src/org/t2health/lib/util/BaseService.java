package org.t2health.lib.util;

import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.SharedPref;
import org.t2health.lib.ManifestMetaData.Database;
import org.t2health.lib.SharedPref.RemoteStackTrace;
import org.t2health.lib.analytics.Analytics;
import org.t2health.lib.db.DatabaseOpenHelper;
import org.t2health.lib.db.ManifestSqliteOpenHelperFactory;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseService;
import com.nullwire.trace.ExceptionHandler;

public abstract class BaseService extends OrmLiteBaseService<DatabaseOpenHelper> {
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
}
