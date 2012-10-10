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
package com.j256.ormlite.misc;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

/**
 * Base class that your data elements can extend which allow them to refresh, update, etc. themselves. ORMLite will
 * automagically set the appropriate {@link Dao} on the class if it is received by a query but if you are trying to
 * create the class, you will need to either create it through the DAO or set the dao on it directly with
 * {@link #setDao(Dao)}.
 * 
 * <p>
 * <b>NOTE:</b> The default pattern is to use the {@link Dao} classes to operate on your data classes. This will allow
 * your data classes to have their own hierarchy and isolates the database code in the Daos. However, you are free to
 * use this base class if you prefer this pattern.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> The internal Dao field has been marked with transient so that it won't be serialized (thanks jc). If you
 * do de-serialize on these classes, you will need to refresh it with the Dao to get it to work again.
 * </p>
 * 
 * @author graywatson
 */
public abstract class BaseDaoEnabled<T, ID> {

	protected transient Dao<T, ID> dao;

	/**
	 * A call through to the {@link Dao#create(Object)}.
	 */
	public int create() throws SQLException {
		checkForDao();
		@SuppressWarnings("unchecked")
		T t = (T) this;
		return dao.create(t);
	}

	/**
	 * A call through to the {@link Dao#refresh(Object)}.
	 */
	public int refresh() throws SQLException {
		checkForDao();
		@SuppressWarnings("unchecked")
		T t = (T) this;
		return dao.refresh(t);
	}

	/**
	 * A call through to the {@link Dao#update(Object)}.
	 */
	public int update() throws SQLException {
		checkForDao();
		@SuppressWarnings("unchecked")
		T t = (T) this;
		return dao.update(t);
	}

	/**
	 * A call through to the {@link Dao#updateId(Object, Object)}.
	 */
	public int updateId(ID newId) throws SQLException {
		checkForDao();
		@SuppressWarnings("unchecked")
		T t = (T) this;
		return dao.updateId(t, newId);
	}

	/**
	 * A call through to the {@link Dao#delete(Object)}.
	 */
	public int delete() throws SQLException {
		checkForDao();
		@SuppressWarnings("unchecked")
		T t = (T) this;
		return dao.update(t);
	}

	/**
	 * A call through to the {@link Dao#objectToString(Object)}.
	 */
	public String objectToString() {
		try {
			checkForDao();
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
		@SuppressWarnings("unchecked")
		T t = (T) this;
		return dao.objectToString(t);
	}

	/**
	 * A call through to the {@link Dao#extractId(Object)}.
	 */
	public ID extractId() throws SQLException {
		checkForDao();
		@SuppressWarnings("unchecked")
		T t = (T) this;
		return dao.extractId(t);
	}

	/**
	 * A call through to the {@link Dao#objectsEqual(Object, Object)}.
	 */
	public boolean objectsEqual(T other) throws SQLException {
		checkForDao();
		@SuppressWarnings("unchecked")
		T t = (T) this;
		return dao.objectsEqual(t, other);
	}

	/**
	 * Set the {@link Dao} on the object. For the {@link #create()} call to work, this must be done beforehand by the
	 * caller. If the object has been received from a query call to the Dao then this should have been set
	 * automagically.
	 */
	public void setDao(Dao<T, ID> dao) {
		this.dao = dao;
	}

	private void checkForDao() throws SQLException {
		if (dao == null) {
			throw new SQLException("Dao has not been set on " + getClass() + " object: " + this);
		}
	}
}
