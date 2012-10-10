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
package com.j256.ormlite.android.apptools;

import java.lang.reflect.Constructor;

import android.content.Context;

/**
 * The default helper factory. This uses the "open_helper_classname" string identifier in your context as the class-name
 * of your helper class.
 * 
 * @author kevingalligan
 */
public class ClassNameProvidedOpenHelperFactory implements OpenHelperManager.SqliteOpenHelperFactory {

	public OrmLiteSqliteOpenHelper getHelper(Context c) {
		int id = c.getResources().getIdentifier("open_helper_classname", "string", c.getPackageName());
		if (id == 0) {
			throw new IllegalStateException("string resrouce open_helper_classname required");
		}

		String className = c.getResources().getString(id);
		try {
			Class<?> helperClass = Class.forName(className);
			Constructor<?> constructor = helperClass.getConstructor(Context.class);
			return (OrmLiteSqliteOpenHelper) constructor.newInstance(c);
		} catch (Exception e) {
			throw new IllegalStateException("Count not create helper instance for class " + className, e);
		}
	}
}
