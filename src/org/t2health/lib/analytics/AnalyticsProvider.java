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
package org.t2health.lib.analytics;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;

interface AnalyticsProvider {
	/**
	 * Called on first instantiation.
	 */
	public void init();
	
	/**
	 * Set the key the provider requires for collecting data.
	 * @param key
	 */
	public void setApiKey(String key);
	
	/**
	 * 
	 * @param b
	 */
	public void setDebugEnabled(boolean b);
	
	/**
	 * Fires when a new session begins, this typically occurs onStart
	 * in an Activity.
	 * @param context
	 */
	public void onStartSession(Context context);
	
	/**
	 * Fires when a session ends, this typically occurs onStop in an Activity.
	 * @param context
	 */
	public void onEndSession(Context context);
	
	/**
	 * Sends an event to the provider.
	 * @param event
	 * @param key
	 * @param value
	 */
	public void onEvent(String event, String key, String value);
	
	/**
	 * Sends an event to the provider.
	 * @param event
	 * @param parameters
	 */
	public void onEvent(String event, Bundle parameters);
	
	/**
	 * Sends and event to the provider.
	 * @param event
	 */
	public void onEvent(String event);
	
	/**
	 * Sends and event to the provider.
	 * @param event
	 * @param parameters
	 */
	public void onEvent(String event, Map<String,String> parameters);
	
	/**
	 * Signals to the provider that the whole screen has changed.
	 */
	public void onPageView();
}
