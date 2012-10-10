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
import java.util.Collection;

import com.j256.ormlite.field.ForeignCollectionField;

/**
 * Collection that is set on a field that as been marked with the {@link ForeignCollectionField} annotation when an
 * object is refreshed or queried (i.e. not created).
 * 
 * <p>
 * <blockquote>
 * 
 * <pre>
 * &#064;ForeignCollectionField(eager = false)
 * private ForeignCollection&lt;Order&gt; orders;
 * </pre>
 * 
 * </blockquote>
 * 
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> If the collection has been marked as being "lazy" then just about all methods in this class result in a
 * pass through the database using the {@link #iterator()}. Even {@link #size()} and other seemingly simple calls can
 * cause a lot of database I/O. Most likely just the {@link #iterator()}, {@link #toArray()}, and
 * {@link #toArray(Object[])} methods should be used if you are using a lazy collection.
 * </p>
 * 
 * @author graywatson
 */
public interface ForeignCollection<T> extends Collection<T> {

	/**
	 * Like {@link Collection#iterator()} but returns a closeable iterator instead. This may throw
	 * {@link RuntimeException} if there is any SQL exceptions unfortunately.
	 */
	public CloseableIterator<T> iterator();

	/**
	 * Like {@link Collection#iterator()} but returns a closeable iterator instead and can throw a SQLException.
	 */
	public CloseableIterator<T> iteratorThrow() throws SQLException;
}
