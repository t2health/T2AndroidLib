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
package com.j256.ormlite.support;

import java.sql.SQLException;

import com.j256.ormlite.field.SqlType;

/**
 * An internal reduction of the SQL PreparedStatment so we can implement its functionality outside of JDBC.
 * 
 * @author graywatson
 */
public interface CompiledStatement {

	/**
	 * Returns the number of columns in this statement.
	 */
	public int getColumnCount() throws SQLException;

	/**
	 * Get the designated column's name.
	 */
	public String getColumnName(int columnIndex) throws SQLException;

	/**
	 * Run the prepared update statement returning the number of rows affected.
	 */
	public int runUpdate() throws SQLException;

	/**
	 * Run the prepared query statement returning the results.
	 */
	public DatabaseResults runQuery() throws SQLException;

	/**
	 * Run the prepared execute statement returning the number of rows affected.
	 */
	public int runExecute() throws SQLException;

	/**
	 * Get the generated key results.
	 */
	public DatabaseResults getGeneratedKeys() throws SQLException;

	/**
	 * Close the statement.
	 */
	public void close() throws SQLException;

	/**
	 * Set the parameter specified by the index and type to be null.
	 * 
	 * @param parameterIndex
	 *            Index of the parameter with 0 being the first parameter, etc..
	 * @param sqlType
	 *            SQL type of the parameter.
	 */
	public void setNull(int parameterIndex, SqlType sqlType) throws SQLException;

	/**
	 * Set the parameter specified by the index and type to be an object.
	 * 
	 * @param parameterIndex
	 *            Index of the parameter with 0 being the first parameter, etc..
	 * @param obj
	 *            Object that we are setting.
	 * @param sqlType
	 *            SQL type of the parameter.
	 */
	public void setObject(int parameterIndex, Object obj, SqlType sqlType) throws SQLException;

	/**
	 * Set the number of rows to return in the results.
	 */
	public void setMaxRows(int max) throws SQLException;
}
