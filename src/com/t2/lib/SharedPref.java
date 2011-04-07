package com.t2.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPref {
	private static SharedPreferences sharedPref;
	
	private static void init(Context c) {
		if(sharedPref == null) {
			sharedPref = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
		}
	}
	
	public static boolean getAnalyticsEnabled(Context c) {
		init(c);
		return sharedPref.getBoolean("analytics_enabled", false) &&
			ManifestMetaData.Analytics.analyticsEnabled(c);
	}
	
	public static void setAnalyticsEnabled(Context c, boolean b) {
		init(c);
		sharedPref.edit().putBoolean("analytics_enabled", b).commit();
	}
	
	public static boolean getRemoteStackTraceEnabled(Context c) {
		init(c);
		return sharedPref.getBoolean("remote_stack_trace_enabled", true) &&
			ManifestMetaData.RemoteStackTrace.remoteStackTraceEnabled(c);
	}
	
	public static void setRemoteStackTraceEnabled(Context c, boolean b) {
		init(c);
		sharedPref.edit().putBoolean("remote_stack_trace_enabled", b).commit();
	}
}
