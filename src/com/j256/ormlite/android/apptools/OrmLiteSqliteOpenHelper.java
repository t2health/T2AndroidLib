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
package com.j256.ormlite.android.apptools;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;

/**
 * SQLite database open helper which can be extended by your application to help manage when the application needs to
 * create or upgrade its database.
 * 
 * @author kevingalligan
 */
public abstract class OrmLiteSqliteOpenHelper extends SQLiteOpenHelper {

	private AndroidConnectionSource connectionSource = new AndroidConnectionSource(this);

	public OrmLiteSqliteOpenHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);
	}

	/**
	 * What to do when your database needs to be created. Usually this entails creating the tables and loading any
	 * initial data.
	 * 
	 * <p>
	 * <b>NOTE:</b> You should use the connectionSource argument that is passed into this method call or the one
	 * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
	 * </p>
	 * 
	 * @param database
	 *            Database being created.
	 * @param connectionSource
	 *            To use get connections to the database to be created.
	 */
	public abstract void onCreate(SQLiteDatabase database, ConnectionSource connectionSource);

	/**
	 * What to do when your database needs to be updated. This could mean careful migration of old data to new data.
	 * Maybe adding or deleting database columns, etc..
	 * 
	 * <p>
	 * <b>NOTE:</b> You should use the connectionSource argument that is passed into this method call or the one
	 * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
	 * </p>
	 * 
	 * @param database
	 *            Database being upgraded.
	 * @param connectionSource
	 *            To use get connections to the database to be updated.
	 * @param oldVersion
	 *            The version of the current database so we can know what to do to the database.
	 * @param newVersion
	 *            The version that we are upgrading the database to.
	 */
	public abstract void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
			int newVersion);

	/**
	 * Get the connection source associated with the helper.
	 */
	public ConnectionSource getConnectionSource() {
		return connectionSource;
	}

	/**
	 * Satisfies the {@link SQLiteOpenHelper#onCreate(SQLiteDatabase)} interface method.
	 */
	@Override
	public final void onCreate(SQLiteDatabase db) {
		ConnectionSource cs = getConnectionSource();
		/*
		 * The method is called by Android database helper's get-database calls when Android detects that we need to
		 * create or update the database. So we have to use the database argument and save a connection to it on the
		 * AndroidConnectionSource, otherwise it will go recursive if the subclass calls getConnectionSource().
		 */
		DatabaseConnection conn = cs.getSpecialConnection();
		boolean clearSpecial = false;
		if (conn == null) {
			conn = new AndroidDatabaseConnection(db, true);
			try {
				cs.saveSpecialConnection(conn);
				clearSpecial = true;
			} catch (SQLException e) {
				throw new IllegalStateException("Could not save special connection", e);
			}
		}
		try {
			onCreate(db, cs);
		} finally {
			if (clearSpecial) {
				cs.clearSpecialConnection(conn);
			}
		}
	}

	/**
	 * Satisfies the {@link SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)} interface method.
	 */
	@Override
	public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ConnectionSource cs = getConnectionSource();
		/*
		 * The method is called by Android database helper's get-database calls when Android detects that we need to
		 * create or update the database. So we have to use the database argument and save a connection to it on the
		 * AndroidConnectionSource, otherwise it will go recursive if the subclass calls getConnectionSource().
		 */
		DatabaseConnection conn = cs.getSpecialConnection();
		boolean clearSpecial = false;
		if (conn == null) {
			conn = new AndroidDatabaseConnection(db, true);
			try {
				cs.saveSpecialConnection(conn);
				clearSpecial = true;
			} catch (SQLException e) {
				throw new IllegalStateException("Could not save special connection", e);
			}
		}
		try {
			onUpgrade(db, cs, oldVersion, newVersion);
		} finally {
			if (clearSpecial) {
				cs.clearSpecialConnection(conn);
			}
		}
	}

	/**
	 * Close any open connections.
	 */
	@Override
	public void close() {
		super.close();
		try {
			connectionSource.close();
		} finally {
			connectionSource = null;
		}
	}

	/**
	 * Get a DAO for our class. This stores the DAO in a map to try and cache them.
	 */
	public <D extends Dao<T,?>, T> D getDao(Class<T> clazz) throws SQLException {
		@SuppressWarnings("unchecked")
		Dao<Object, Object> dao = (Dao<Object, Object>)DaoManager.createDao(getConnectionSource(), clazz);
		@SuppressWarnings("unchecked")
		D castDao = (D)dao;
		return castDao;
	}
}
