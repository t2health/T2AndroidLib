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
 * Interface so we can front various log code which may or may not be in the classpath.
 * 
 * @author graywatson
 */
public interface Log {

	/**
	 * Returns true if the log mode is in trace or higher.
	 */
	public boolean isLevelEnabled(Level level);

	/**
	 * Log a trace message.
	 */
	public void log(Level level, String message);

	/**
	 * Log a trace message with a throwable.
	 */
	public void log(Level level, String message, Throwable t);

	/**
	 * Level of log messages being sent.
	 */
	public enum Level {
		/** for tracing messages that are very verbose */
		TRACE(1),
		/** messages suitable for debugging purposes */
		DEBUG(2),
		/** information messages */
		INFO(3),
		/** warning messages */
		WARNING(4),
		/** error messages */
		ERROR(5),
		/** severe fatal messages */
		FATAL(6),
		// end
		;

		private int level;

		private Level(int level) {
			this.level = level;
		}

		/**
		 * Return whether or not a level argument is enabled for this level value. So, Level.INFO.isEnabled(Level.WARN)
		 * returns true but Level.INFO.isEnabled(Level.DEBUG) returns false.
		 */
		public boolean isEnabled(Level otherLevel) {
			return level <= otherLevel.level;
		}
	}
}
