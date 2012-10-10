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
package com.j256.ormlite.android;

import java.sql.SQLException;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.SqliteAndroidDatabaseType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.BaseConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;

/**
 * Android version of the connection source. Uses the standard Android SQLiteOpenHelper. For best results, use our
 * helper,
 * 
 * @see OrmLiteSqliteOpenHelper
 * 
 * @author kevingalligan, graywatson
 */
public class AndroidConnectionSource extends BaseConnectionSource implements ConnectionSource {

	private static final Logger logger = LoggerFactory.getLogger(AndroidConnectionSource.class);

	private SQLiteOpenHelper helper;
	private DatabaseConnection readOnlyConnection = null;
	private DatabaseConnection readWriteConnection = null;
	private DatabaseType databaseType = new SqliteAndroidDatabaseType();

	public AndroidConnectionSource(SQLiteOpenHelper helper) {
		this.helper = helper;
	}

	public DatabaseConnection getReadOnlyConnection() throws SQLException {
		DatabaseConnection conn = getSavedConnection();
		if (conn != null) {
			return conn;
		}
		if (readOnlyConnection == null) {
			readOnlyConnection = new AndroidDatabaseConnection(helper.getReadableDatabase(), false);
		}
		return readOnlyConnection;
	}

	public DatabaseConnection getReadWriteConnection() throws SQLException {
		DatabaseConnection conn = getSavedConnection();
		if (conn != null) {
			return conn;
		}
		if (readWriteConnection == null) {
			readWriteConnection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
		}
		return readWriteConnection;
	}

	public void releaseConnection(DatabaseConnection connection) throws SQLException {
		// noop since connection management is handled by AndroidOS
	}

	public boolean saveSpecialConnection(DatabaseConnection connection) throws SQLException {
		return saveSpecial(connection);
	}

	public void clearSpecialConnection(DatabaseConnection connection) {
		clearSpecial(connection, logger);
	}

	public void close() {
		// the helper is closed so it calls close here, so this CANNOT be a call back to helper.close()
	}

	public DatabaseType getDatabaseType() {
		return databaseType;
	}
}
