package com.t2.lib.analytics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;

class FlurryProvider implements AnalyticsProvider {

	private String apiKey;
	private Class<?> anlyticsClass;

	@Override
	public void init() {
		try {
			this.anlyticsClass = java.lang.Class.forName("com.flurry.android.FlurryAgent");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setApiKey(String key) {
		this.apiKey = key;
	}

	@Override
	public void setDebugEnabled(boolean b) {
		
	}

	@Override
	public void onStartSession(Context context) {
		if(anlyticsClass == null) {
			return;
		}
		
		try {
			Method m = anlyticsClass.getDeclaredMethod("onStartSession", Context.class, String.class);
			m.invoke(null, context, this.apiKey);
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
		if(anlyticsClass == null) {
			return;
		}
		
		try {
			Method m = anlyticsClass.getDeclaredMethod("onEndSession", Context.class);
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
		if(anlyticsClass == null) {
			return;
		}
		
		try {
			Method m = anlyticsClass.getDeclaredMethod("onEvent", String.class);
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
		if(anlyticsClass == null) {
			return;
		}
		
		try {
			Method m = anlyticsClass.getDeclaredMethod("onEvent", Map.class);
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
		if(anlyticsClass == null) {
			return;
		}
		
		try {
			Method m = anlyticsClass.getDeclaredMethod("onPageView");
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
