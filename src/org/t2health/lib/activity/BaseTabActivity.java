/*
 * 
 * T2AndroidLib
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2AndroidLib001
 * Government Agency Original Software Title: T2AndroidLib
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package org.t2health.lib.activity;


import org.t2health.lib.ManifestMetaData;
import org.t2health.lib.SharedPref;
import org.t2health.lib.analytics.Analytics;
import org.t2health.lib.db.DatabaseOpenHelper;
import org.t2health.lib.db.ManifestSqliteOpenHelperFactory;

import android.content.Intent;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteBaseTabActivity;
import com.nullwire.trace.ExceptionHandler;

/**
 * The base class to use when creating a tab activity. This class will read
 * meta-data from the manifest.xml file in order to configure ORM, Analytics,
 * Remote Stack Trace and other code.
 * @author robbiev
 */
public abstract class BaseTabActivity extends OrmLiteBaseTabActivity<DatabaseOpenHelper> {
	private boolean isORMConfigured = false;
	
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
