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
import java.util.List;

/**
 * You should be using {@link GenericRawResults} with &lt;String[]&gt; argument.
 * 
 * @deprecated
 */
@Deprecated
public interface RawResults extends CloseableIterable<String[]> {

	/**
	 * Return the number of columns in each result row.
	 */
	public int getNumberColumns();

	/**
	 * Return the array of column names for each result row.
	 */
	public String[] getColumnNames();

	/**
	 * Return a list of results mapped by the mapper argument.
	 */
	public List<String[]> getResults() throws SQLException;

	/**
	 * Return a list of results mapped by the mapper argument.
	 */
	public <T> List<T> getMappedResults(RawRowMapper<T> mapper) throws SQLException;

	/**
	 * Get an iterator which will return a T which is mapped from the String[] array raw results my the mapper argument.
	 */
	public <T> CloseableIterator<T> iterator(RawRowMapper<T> mapper) throws SQLException;

	/**
	 * Close any open database connections associated with the RawResults. This is only applicable if the
	 * {@link Dao#iteratorRaw(String)} or another iterator method was called.
	 */
	public void close() throws SQLException;
}
