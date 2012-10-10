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
package com.j256.ormlite.db;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.FieldConverter;

/**
 * Sqlite database type information for the Android OS that makes native calls to the Android OS database APIs.
 * 
 * @author graywatson
 */
public class SqliteAndroidDatabaseType extends BaseSqliteDatabaseType implements DatabaseType {

	public SqliteAndroidDatabaseType() {
	}

	@Override
	public void loadDriver() {
		// noop
	}

	public boolean isDatabaseUrlThisType(String url, String dbTypePart) {
		// not used by the android code
		return true;
	}

	@Override
	protected String getDriverClassName() {
		// no driver to load in android-land
		return null;
	}

	@Override
	public String getDatabaseName() {
		return "Android SQLite";
	}

	@Override
	protected void appendDateType(StringBuilder sb, int fieldWidth) {
		// default is to store the date as a string
		appendDateStringType(sb, fieldWidth);
	}

	@Override
	protected void appendBooleanType(StringBuilder sb) {
		// we have to convert booleans to numbers
		appendShortType(sb);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FieldConverter getFieldConverter(DataType dataType) {
		// we are only overriding certain types
		switch (dataType) {
			case JAVA_DATE :
			case DATE :
				return DataType.DATE_STRING;
			default :
				return super.getFieldConverter(dataType);
		}
	}

	@Override
	public boolean isNestedSavePointsSupported() {
		return false;
	}

	@Override
	public boolean isBatchUseTransaction() {
		return true;
	}
}
