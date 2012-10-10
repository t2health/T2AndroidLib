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
package com.j256.ormlite.field;

import java.sql.SQLException;

import com.j256.ormlite.db.BaseDatabaseType;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Convert a Java object into the appropriate argument to a SQL statement and then back from the result set to the Java
 * object. This allows databases to configure per-type conversion. This is used by the
 * {@link BaseDatabaseType#getFieldConverter(DataType)} method to find the converter for a particular database type.
 * Databases can then override the default data conversion mechanisms as necessary.
 * 
 * @author graywatson
 */
public interface FieldConverter {

	/**
	 * Convert a default string object and return the appropriate argument to a SQL insert or update statement.
	 */
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException;

	/**
	 * Convert a Java object and return the appropriate argument to a SQL insert or update statement.
	 */
	public Object javaToSqlArg(FieldType fieldType, Object obj) throws SQLException;

	/**
	 * Return the object extracted from the results associated with column in position columnPos.
	 * 
	 * @throws SQLException
	 *             If there is a problem accessing the results data.
	 * @param fieldType
	 *            Associated FieldType which may be null.
	 */
	public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException;

	/**
	 * Return the SQL type that is stored in the database for this argument.
	 */
	public SqlType getSqlType();

	/**
	 * Return whether or not this is a SQL "stream" object. Cannot get certain stream objects from the SQL results more
	 * than once. If true, the converter has to protect itself against null values.
	 */
	public boolean isStreamType();
}
