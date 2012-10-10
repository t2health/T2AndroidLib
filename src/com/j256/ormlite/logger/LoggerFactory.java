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

import java.lang.reflect.Constructor;

/**
 * Factory that creates {@link Logger} instances.
 */
public class LoggerFactory {

	private static LogType logType;

	/**
	 * For static calls only.
	 */
	private LoggerFactory() {
	}

	/**
	 * Return a logger associated with a particular class.
	 */
	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	/**
	 * Return a logger associated with a particular class name.
	 */
	public static Logger getLogger(String className) {
		if (logType == null) {
			logType = findLogType();
		}
		return new Logger(logType.createLog(className));
	}

	public static String getSimpleClassName(String className) {
		// get the last part of the class name
		String[] parts = className.split("\\.");
		if (parts.length == 0) {
			return className;
		} else {
			return parts[parts.length - 1];
		}
	}

	/**
	 * Return the most appropriate log type. This should _never_ return null.
	 */
	private static LogType findLogType() {
		for (LogType logType : LogType.values()) {
			if (logType.isAvailable()) {
				return logType;
			}
		}
		// fall back is always LOCAL
		return LogType.LOCAL;
	}

	private enum LogType {
		/**
		 * WARNING: This should be _before_ commons logging since Android provides commons logging but logging messages
		 * are ignored that are sent there. Grrrrr.
		 */
		ANDROID("android.util.Log") {
			@Override
			public Log createLog(String classLabel) {
				try {
					Class<?> clazz = Class.forName("com.j256.ormlite.android.AndroidLog");
					@SuppressWarnings("unchecked")
					Constructor<Log> constructor = (Constructor<Log>) clazz.getConstructor(String.class);
					return constructor.newInstance(classLabel);
				} catch (Exception e) {
					// oh well, fallback to the local log
					return LOCAL.createLog(classLabel);
				}
			}
		},
		COMMONS_LOGGING("org.apache.commons.logging.LogFactory") {
			@Override
			public Log createLog(String classLabel) {
				return new CommonsLoggingLog(classLabel);
			}

		},
		LOG4J("org.apache.log4j.Logger") {
			@Override
			public Log createLog(String classLabel) {
				return new Log4jLog(classLabel);
			}
		},
		LOCAL("com.j256.ormlite.logger.LocalLog") {
			@Override
			public Log createLog(String classLabel) {
				return new LocalLog(classLabel);
			}
			@Override
			public boolean isAvailable() {
				// it's always available
				return true;
			}
		},
		// end
		;

		private String detectClassName;

		private LogType(String detectClassName) {
			this.detectClassName = detectClassName;
		}

		/**
		 * Create and return a Log class for this type.
		 */
		public abstract Log createLog(String classLabel);

		/**
		 * Return true if the log class is available.
		 */
		public boolean isAvailable() {
			try {
				Class.forName(detectClassName);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
}
