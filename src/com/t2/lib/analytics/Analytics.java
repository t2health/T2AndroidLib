package com.t2.lib.analytics;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

/**
 * 
 * @author robbiev
 * 
 * Manages the Analytics for the application.
 */
public class Analytics {
	/**
	 * The list of supported providers.
	 * @author robbiev
	 *
	 */
	public static enum Provider {
		FLURRY
	};
	
	private static Provider currentProvider;
	private static String providerKey;
	private static AnalyticsProvider analytics;
	private static boolean isEnabled = false;
	private static boolean isDebugEnabled = false;
	private static boolean isSessionStarted = false;
	
	/**
	 * Initialize the analytics system. This method should be the first called
	 * in order to handle analytics collection.
	 * @param provider	The analytics provider to use. eg, Flurry, Localytics.
	 * @param apiKey	The API Key the provider provides in order to use their service.
	 * @param enabled	Whether or not analytics is currently enabled.
	 */
	public static void init(Provider provider, String apiKey, boolean enabled) {
		if(provider == null || apiKey == null) {
			return;
		}
		
		if(currentProvider != null && currentProvider != provider && apiKey != providerKey) {
			throw new RuntimeException("Analytics provider was already set. You cannot change it.");
		}
		currentProvider = provider;
		providerKey = apiKey;
		
		if(currentProvider == Provider.FLURRY) {
			analytics = new FlurryProvider();
		}
		
		analytics.init();
		analytics.setApiKey(providerKey);
		isEnabled = enabled;
	}
	
	/**
	 * Retrieves the provider enumeration based on its string representation.
	 * This method is case in-sensitive.
	 * @param providerString	The string form of a provider from the Provider enumeration.
	 * @return	The enumeration element for the string or null if no provider was found.
	 */
	public static Provider providerFromString(String providerString) {
		String newProviderString = providerString.toLowerCase();
		if(newProviderString.equals("flurry")) {
			return Provider.FLURRY;
		}
		return null;
	}
	
	/**
	 * Turn analytics collection on/off.
	 * @param en	Set analytics enabled status. Setting this will inhibit
	 * 				all calls to the underlying provider class.
	 */
	public static void setEnabled(boolean en) {
		isEnabled = en;
	}

	/**
	 * Returns the status of analytics collection.
	 * @return	true if analytics is enabled.
	 */
	public static boolean isEnabled() {
		return isEnabled;
	}
	
	/**
	 * Determines if the analytics system is ready to start collecting data.
	 * @return	true if analytics is ready to pass data to the underlying 
	 * 			provider class.
	 */
	private static boolean isReady() {
		return isEnabled && analytics != null;
	}

	/**
	 * Turns debug mode on or off.
	 * @param b	
	 */
	public static void setDebugEnabled(boolean b) {
		isDebugEnabled = b;
	}
	
	/**
	 * Return if debug mode is on or off.
	 * @return
	 */
	public static boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	/**
	 * Signals to the provider class that the data collection session has begun.
	 * @param context
	 */
	public static void onStartSession(Context context) {
		if(isReady() && !isSessionStarted) {
			analytics.onStartSession(context);
			isSessionStarted = true;
		}
	}

	/**
	 * Signals to the provider class that the data colelction session has ended.
	 * @param context
	 */
	public static void onEndSession(Context context) {
		if(isReady() && isSessionStarted) {
			analytics.onEndSession(context);
			isSessionStarted = false;
		}
	}

	/**
	 * Sends an event to the provider.
	 * @param event
	 * @param key
	 * @param value
	 */
	public static void onEvent(String event, String key, String value) {
		if(isReady()) {
			analytics.onEvent(event, key, value);
		}
	}

	/**
	 * Sends an event to the provider.
	 * @param event
	 * @param parameters
	 */
	public static void onEvent(String event, Bundle parameters) {
		if(isReady()) {
			analytics.onEvent(event, parameters);
		}
	}

	/**
	 * Sends and event to the provider.
	 * @param event
	 */
	public static void onEvent(String event) {
		if(isReady()) {
			analytics.onEvent(event);
		}
	}

	/**
	 * Sends and event to the provider.
	 * @param event
	 * @param parameters
	 */
	public static void onEvent(String event, Map<String,String> parameters) {
		if(isReady()) {
			analytics.onEvent(event, parameters);
		}
	}

	/**
	 * Signals to the provider that the whole screen has changed.
	 */
	public static void onPageView() {
		if(isReady()) {
			analytics.onPageView();
		}
	}
}
