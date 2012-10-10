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

import com.j256.ormlite.field.DataType;

/**
 * Results returned by a call to {@link Dao#queryRaw(String, String...)} which returns results as a String[],
 * {@link Dao#queryRaw(String, RawRowMapper, String...)} which returns results mapped by the caller to an Object, and
 * {@link Dao#queryRaw(String, DataType[], String...)} which returns each results as a Object[].
 * 
 * <p>
 * You can access the results one of two ways using this object. You can call the {@link #getResults()} method which
 * will extract all results into a list which is returned. Or you can call the {@link #iterator()} method either
 * directly or with the for... Java statement. The iterator allows you to page through the results and is more
 * appropriate for queries which will return a large number of results.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> If you access the {@link #iterator()} method, you must call {@link CloseableIterator#close()} method
 * when you are done otherwise the underlying SQL statement and connection may be kept open.
 * </p>
 * 
 * @author graywatson
 */
public interface GenericRawResults<T> extends CloseableIterable<T> {

	/**
	 * Return the number of columns in each result row.
	 */
	public int getNumberColumns();

	/**
	 * Return the array of column names for each result row.
	 */
	public String[] getColumnNames();

	/**
	 * Return a list of all of the results. For large queries, this should not be used since the {@link #iterator()}
	 * method will allow your to process the results page-by-page.
	 */
	public List<T> getResults() throws SQLException;

	/**
	 * Close any open database connections associated with the RawResults. This is only applicable if the
	 * {@link Dao#iteratorRaw(String)} or another iterator method was called.
	 */
	public void close() throws SQLException;
}
