/*
 * 
 * T2AndroidLib
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
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

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * A reduction of the SQL ResultSet so we can implement it outside of JDBC.
 * 
 * <p>
 * <b>NOTE:</b> In all cases, the columnIndex parameters are 0 based -- <i>not</i> 1 based like JDBC.
 * </p>
 * 
 * @author graywatson
 */
public interface DatabaseResults {

	/**
	 * Returns the number of columns in these results.
	 */
	public int getColumnCount() throws SQLException;

	/**
	 * Moves to the next result.
	 * 
	 * @return true if there are more results to be processed.
	 */
	public boolean next() throws SQLException;

	/**
	 * Return the column index associated with the column name.
	 * 
	 * @throws SQLException
	 *             if the column was not found in the results.
	 */
	public int findColumn(String columnName) throws SQLException;

	/**
	 * Returns the string from the results at the column index.
	 */
	public String getString(int columnIndex) throws SQLException;

	/**
	 * Returns the boolean value from the results at the column index.
	 */
	public boolean getBoolean(int columnIndex) throws SQLException;

	/**
	 * Returns the byte value from the results at the column index.
	 */
	public byte getByte(int columnIndex) throws SQLException;

	/**
	 * Returns the byte array value from the results at the column index.
	 */
	public byte[] getBytes(int columnIndex) throws SQLException;

	/**
	 * Returns the short value from the results at the column index.
	 */
	public short getShort(int columnIndex) throws SQLException;

	/**
	 * Returns the integer value from the results at the column index.
	 */
	public int getInt(int columnIndex) throws SQLException;

	/**
	 * Returns the long value from the results at the column index.
	 */
	public long getLong(int columnIndex) throws SQLException;

	/**
	 * Returns the float value from the results at the column index.
	 */
	public float getFloat(int columnIndex) throws SQLException;

	/**
	 * Returns the double value from the results at the column index.
	 */
	public double getDouble(int columnIndex) throws SQLException;

	/**
	 * Returns the SQL timestamp value from the results at the column index.
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException;

	/**
	 * Returns an input stream for a blob value from the results at the column index.
	 */
	public InputStream getBlobStream(int columnIndex) throws SQLException;

	/**
	 * Returns true if the last object returned with the column index is null.
	 */
	public boolean wasNull(int columnIndex) throws SQLException;
}
