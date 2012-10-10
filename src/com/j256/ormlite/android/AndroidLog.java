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
package com.j256.ormlite.android;

import android.util.Log;

import com.j256.ormlite.logger.LoggerFactory;

/**
 * Implementation of our logger which delegates to the internal Android logger.
 * 
 * @author graywatson
 */
public class AndroidLog implements com.j256.ormlite.logger.Log {

	private final static int MAX_TAG_LENGTH = 23;
	private String className;

	public AndroidLog(String className) {
		// get the last part of the class name
		this.className = LoggerFactory.getSimpleClassName(className);
		// make sure that our tag length is not too long
		int length = this.className.length();
		if (length > MAX_TAG_LENGTH) {
			this.className = this.className.substring(length - MAX_TAG_LENGTH, length);
		}
	}

	public boolean isLevelEnabled(Level level) {
		return Log.isLoggable(className, levelToJavaLevel(level));
	}

	public void log(Level level, String msg) {
		switch (level) {
			case TRACE :
				Log.v(className, msg);
				break;
			case DEBUG :
				Log.d(className, msg);
				break;
			case INFO :
				Log.i(className, msg);
				break;
			case WARNING :
				Log.w(className, msg);
				break;
			case ERROR :
				Log.e(className, msg);
				break;
			case FATAL :
				Log.e(className, msg);
				break;
			default :
				Log.i(className, msg);
				break;
		}
	}

	public void log(Level level, String msg, Throwable t) {
		switch (level) {
			case TRACE :
				Log.v(className, msg, t);
				break;
			case DEBUG :
				Log.d(className, msg, t);
				break;
			case INFO :
				Log.i(className, msg, t);
				break;
			case WARNING :
				Log.w(className, msg, t);
				break;
			case ERROR :
				Log.e(className, msg, t);
				break;
			case FATAL :
				Log.e(className, msg, t);
				break;
			default :
				Log.i(className, msg, t);
				break;
		}
	}

	private int levelToJavaLevel(com.j256.ormlite.logger.Log.Level level) {
		switch (level) {
			case TRACE :
				return Log.VERBOSE;
			case DEBUG :
				return Log.DEBUG;
			case INFO :
				return Log.INFO;
			case WARNING :
				return Log.WARN;
			case ERROR :
				return Log.ERROR;
			case FATAL :
				return Log.ERROR;
			default :
				return Log.INFO;
		}
	}
}
