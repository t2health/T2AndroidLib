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

import java.util.List;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.FieldConverter;
import com.j256.ormlite.field.FieldType;

/**
 * Sqlite database type information used to create the tables, etc..
 * 
 * <p>
 * NOTE: We need this here because the Android version subclasses it.
 * 
 * @author graywatson
 */
public abstract class BaseSqliteDatabaseType extends BaseDatabaseType implements DatabaseType {

	private final static FieldConverter booleanConverter = new BooleanNumberFieldConverter();

	@Override
	protected void configureGeneratedId(StringBuilder sb, FieldType fieldType, List<String> statementsBefore,
			List<String> additionalArgs, List<String> queriesAfter) {
		if (fieldType.getDataType() != DataType.INTEGER && fieldType.getDataType() != DataType.INTEGER_OBJ) {
			throw new IllegalArgumentException("Sqlite requires that auto-increment generated-id be integer types");
		}
		sb.append("PRIMARY KEY AUTOINCREMENT ");
		// no additional call to configureId here
	}

	@Override
	public boolean isVarcharFieldWidthSupported() {
		return false;
	}

	@Override
	public boolean isCreateTableReturnsZero() {
		// 'CREATE TABLE' statements seem to return 1 for some reason
		return false;
	}

	@Override
	public FieldConverter getFieldConverter(DataType dataType) {
		// we are only overriding certain types
		switch (dataType) {
			case BOOLEAN :
			case BOOLEAN_OBJ :
				return booleanConverter;
			default :
				return super.getFieldConverter(dataType);
		}
	}
}
