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
package com.j256.ormlite.logger;

/**
 * Class which logs to java.util.log. This is not detected by default so is here for now as a reference implementation.
 * 
 * @author graywatson
 */
public class JavaUtilLog implements Log {

	private final java.util.logging.Logger logger;

	public JavaUtilLog(String className) {
		this.logger = java.util.logging.Logger.getLogger(className);
	}

	public boolean isLevelEnabled(com.j256.ormlite.logger.Log.Level level) {
		return logger.isLoggable(levelToJavaLevel(level));
	}

	public void log(com.j256.ormlite.logger.Log.Level level, String msg) {
		logger.log(levelToJavaLevel(level), msg);
	}

	public void log(com.j256.ormlite.logger.Log.Level level, String msg, Throwable throwable) {
		logger.log(levelToJavaLevel(level), msg, throwable);
	}

	private java.util.logging.Level levelToJavaLevel(com.j256.ormlite.logger.Log.Level level) {
		switch (level) {
			case TRACE :
				return java.util.logging.Level.FINER;
			case DEBUG :
				return java.util.logging.Level.FINE;
			case INFO :
				return java.util.logging.Level.INFO;
			case WARNING :
				return java.util.logging.Level.WARNING;
			case ERROR :
				return java.util.logging.Level.SEVERE;
			case FATAL :
				return java.util.logging.Level.SEVERE;
			default :
				return java.util.logging.Level.INFO;
		}
	}
}
