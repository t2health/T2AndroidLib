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
package com.j256.ormlite.dao;

import java.sql.SQLException;

/**
 * Parameterized row mapper that takes output from the {@link RawResults} and returns a T. Is used in the
 * {@link Dao#queryRaw(String, RawRowMapper, String...)} method.
 * 
 * <p>
 * <b> NOTE: </b> If you need to map Objects instead then consider using the
 * {@link Dao#queryRaw(String, com.j256.ormlite.field.DataType[], String...)} method which allows you to iterate over
 * the raw results as Object[].
 * </p>
 * 
 * @param <T>
 *            Type that the mapRow returns.
 * @author graywatson
 */
public interface RawRowMapper<T> {

	/**
	 * Used to convert a raw results row to an object.
	 * 
	 * @return The created object with all of the fields set from the results. Return if there is no object generated
	 *         from these results.
	 * @param columnNames
	 *            Array of names of columns.
	 * @param resultColumns
	 *            Array of result columns.
	 * @throws SQLException
	 *             If there is any critical error with the data and you want to stop the paging.
	 */
	public T mapRow(String[] columnNames, String[] resultColumns) throws SQLException;
}
