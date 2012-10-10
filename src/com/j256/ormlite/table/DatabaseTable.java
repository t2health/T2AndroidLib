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
package com.j256.ormlite.table;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.j256.ormlite.dao.DaoManager;

/**
 * Annotation that marks a class to be stored in the database. It is only required if you want to mark the class or
 * change its default tableName. You specify this annotation above the classes that you want to persist to the database.
 * For example:
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * &#64;DatabaseTable(tableName = "accounts")
 * public class Account {
 *   ...
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> Classes that are persisted using this package <i>must</i> have a no-argument constructor with at least
 * package visibility so objects can be created when you do a query, etc..
 * </p>
 * 
 * @author graywatson
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface DatabaseTable {

	/**
	 * The name of the column in the database. If not set then the name is taken from the class name lowercased.
	 */
	String tableName() default "";

	/**
	 * The DAO class that corresponds to this class. This is used by the {@link DaoManager} when it constructs a DAO
	 * internally.  It is important to use this on devices with minimal memory such as mobile devices.
	 */
	Class<?> daoClass() default Void.class;
}
