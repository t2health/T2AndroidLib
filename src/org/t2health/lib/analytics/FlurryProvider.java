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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

class FlurryProvider implements AnalyticsProvider {

	private String mApiKey;
	private Class<?> mAnalyticsClass;

	@Override
	public void init() {
		try {
			this.mAnalyticsClass = java.lang.Class.forName("com.flurry.android.FlurryAgent");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setApiKey(String key) {
		this.mApiKey = key;
	}

	@Override
	public void setDebugEnabled(boolean b) {
		
	}

	@Override
	public void onStartSession(Context context) {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onStartSession", Context.class, String.class);
			m.invoke(null, context, this.mApiKey);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		/*FlurryAgent.onStartSession(
				context, 
				apiKey
		);*/
	}

	@Override
	public void onEndSession(Context context) {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onEndSession", Context.class);
			m.invoke(null, context);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		//FlurryAgent.onEndSession(context);
	}

	@Override
	public void onEvent(String event, String key, String value) {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put(key, value);
		onEvent(event, params);
	}

	@Override
	public void onEvent(String event, Bundle parameters) {
		HashMap<String,String> params = new HashMap<String,String>();
		for(String key: parameters.keySet()) {
			Object val = parameters.get(key);
			params.put(key, val+"");
		}

		onEvent(event, params);
	}

	@Override
	public void onEvent(String event) {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onEvent", String.class);
			m.invoke(null, event);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//FlurryAgent.onEvent(event);
	}

	@Override
	public void onEvent(String event, Map<String, String> parameters) {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onEvent", Map.class);
			m.invoke(null, parameters);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//FlurryAgent.onEvent(event, parameters);
	}

	@Override
	public void onPageView() {
		if(mAnalyticsClass == null) {
			return;
		}
		
		try {
			Method m = mAnalyticsClass.getDeclaredMethod("onPageView");
			m.invoke(null);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//FlurryAgent.onPageView();
	}
}
