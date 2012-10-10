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
package com.j256.ormlite.android;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Android implementation of the compiled statement.
 * 
 * @author kevingalligan, graywatson
 */
public class AndroidCompiledStatement implements CompiledStatement {

	private final String sql;
	private final SQLiteDatabase db;
	private final StatementType type;

	private Cursor cursor;
	private final List<Object> args = new ArrayList<Object>();
	private Integer max;

	public AndroidCompiledStatement(String sql, SQLiteDatabase db, StatementType type) {
		this.sql = sql;
		this.db = db;
		this.type = type;
	}

	public int getColumnCount() throws SQLException {
		return getCursor().getColumnCount();
	}

	public String getColumnName(int column) throws SQLException {
		return getCursor().getColumnName(column);
	}

	public DatabaseResults runQuery() throws SQLException {
		// this could come from DELETE or UPDATE, just not a SELECT
		if (type != StatementType.SELECT) {
			throw new IllegalArgumentException("Cannot call query on a " + type + " statement");
		}
		return new AndroidDatabaseResults(getCursor());
	}

	public int runUpdate() throws SQLException {
		if (type == StatementType.SELECT) {
			throw new IllegalArgumentException("Cannot call update on a " + type + " statement");
		}
		String finalSql = null;
		try {
			if (max == null) {
				finalSql = sql;
			} else {
				finalSql = sql + " " + max;
			}
			db.execSQL(finalSql, args.toArray(new Object[args.size()]));
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("Problems executing Android statement: " + finalSql, e);
		}
		return 1;
	}

	public int runExecute() throws SQLException {
		if (type != StatementType.EXECUTE) {
			throw new IllegalArgumentException("Cannot call execute on a " + type + " statement");
		}
		try {
			db.execSQL(sql, new Object[0]);
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("Problems executing Android statement: " + sql, e);
		}
		return 0;
	}

	public DatabaseResults getGeneratedKeys() throws SQLException {
		throw new UnsupportedOperationException("Unsupported operation to getGeneratedKeys");
	}

	public void close() throws SQLException {
		if (cursor != null) {
			try {
				cursor.close();
			} catch (android.database.SQLException e) {
				throw SqlExceptionUtil.create("Problems closing Android cursor", e);
			}
		}
	}

	public void setNull(int parameterIndex, SqlType sqlType) throws SQLException {
		isInPrep();
		args.add(parameterIndex, null);
	}

	public void setObject(int parameterIndex, Object obj, SqlType sqlType) throws SQLException {
		isInPrep();
		args.add(parameterIndex, obj.toString());
	}

	public void setMaxRows(int max) throws SQLException {
		isInPrep();
		this.max = max;
	}

	/***
	 * This is mostly an internal class but is exposed for those people who need access to the Cursor itself.
	 * 
	 * <p>
	 * NOTE: This is not thread safe. Not sure if we need it, but keep that in mind.
	 * </p>
	 */
	public Cursor getCursor() throws SQLException {
		if (cursor == null) {
			String finalSql = null;
			try {
				if (max == null) {
					finalSql = sql;
				} else {
					finalSql = sql + " " + max;
				}
				cursor = db.rawQuery(finalSql, args.toArray(new String[args.size()]));
				cursor.moveToFirst();
			} catch (android.database.SQLException e) {
				throw SqlExceptionUtil.create("Problems executing Android query: " + finalSql, e);
			}
		}

		return cursor;
	}

	private void isInPrep() throws SQLException {
		if (cursor != null) {
			throw new SQLException("Query already run. Cannot add argument values.");
		}
	}
}
