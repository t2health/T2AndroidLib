package com.t2.lib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * Gather meta-data from the application tag of the app's manifest file.
 * @see http://developer.android.com/guide/topics/manifest/meta-data-element.html
 * @author robbiev
 *
 */
/**
 * @author robbiev
 *
 */
public class ManifestMetaData {
	public static final String ANALYTICS_PROVIDER = "analyticsProvider";
	public static final String ANALYTICS_KEY = "analyticsKey";
	public static final String ANALYTICS_ENABLED = "analyticsEnabled";
	
	public static final String REMOTE_STACK_TRACK_URL = "stackTraceURL";
	public static final String REMOTE_STACK_TRACE_ENABLED = "remoteStackTraceEnabled";
	
	public static final String DATABSE_NAME = "databaseName";
	public static final String DATABASE_VERSION = "databaseVersion";
	
	public static final String DEBUG_MODE = "debugMode";
	
	
	private static Bundle applicationMetaData;
	
	/**
	 * Loads the manifest meta-data into a cached bundle.
	 * @param c
	 */
	private static void initAppBundle(Context c) {
		// meta-data already read, use the cache version.
		if(applicationMetaData != null) {
			return;
		}
		
		// Load the application meta-data (if it is there)
		try {
			ApplicationInfo ai = c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
			applicationMetaData = ai.metaData;
			
		} catch (NameNotFoundException e) {
			// ignore
		}
		
		// Could not load meta-data, make empty bundle
		if(applicationMetaData == null) {
			applicationMetaData = new Bundle();
		}
	}
	
	/**
	 * Get a string from the manifest.
	 * @param c	The context
	 * @param name	The name of the variable.
	 * @return	The string from the tag, null otherwise.
	 */
	public static String getString(Context c, String name) {
		initAppBundle(c);
		return applicationMetaData.getString(name);
	}
	
	/**
	 * Get an integer from the manifest.
	 * @param c
	 * @param name
	 * @return	The integer or 0 if it cannot be converted to an integer.
	 */
	public static int getInt(Context c, String name) {
		initAppBundle(c);
		return applicationMetaData.getInt(name);
	}
	
	/**
	 * Get a boolean from the manifest.
	 * @param c
	 * @param name
	 * @return	true if the value is "true" otherwise false.
	 */
	public static boolean getBoolean(Context c, String name) {
		initAppBundle(c);
		return applicationMetaData.getBoolean(name);
	}
	
	/**
	 * Get a float from the manifest
	 * @param c
	 * @param name
	 * @return	the float or 0 otherwise.
	 */
	public static float getFloat(Context c, String name) {
		initAppBundle(c);
		return applicationMetaData.getFloat(name);
	}
	
	public static class Analytics {
		public static boolean analyticsEnabled(Context c) {
			String key = getString(c, ANALYTICS_KEY);
			String provider = getString(c, ANALYTICS_PROVIDER);
			
			return provider != null && provider.trim().length() > 0 &&
					key != null && key.trim().length() > 0 &&
					getBoolean(c, ANALYTICS_ENABLED);
		}
	}
	
	public static class RemoteStackTrace {
		public static boolean remoteStackTraceEnabled(Context c) {
			String url = getString(c, REMOTE_STACK_TRACK_URL);
			
			return url != null && url.trim().length() > 0 &&
					getBoolean(c, REMOTE_STACK_TRACE_ENABLED);
		}
	}
}
