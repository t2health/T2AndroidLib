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
package com.j256.ormlite.stmt.query;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

/**
 * Internal class handling the SQL 'in' query part. Used by {@link Where#in}.
 * 
 * @author graywatson
 */
public class In extends BaseComparison {

	private Iterable<?> objects;

	public In(String columnName, FieldType fieldType, Iterable<?> objects) throws SQLException {
		super(columnName, fieldType, null);
		this.objects = objects;
	}

	public In(String columnName, FieldType fieldType, Object[] objects) throws SQLException {
		super(columnName, fieldType, null);
		// grrrr, Object[] should be Iterable
		this.objects = Arrays.asList(objects);
	}

	@Override
	public StringBuilder appendOperation(StringBuilder sb) {
		sb.append("IN ");
		return sb;
	}

	@Override
	public StringBuilder appendValue(DatabaseType databaseType, StringBuilder sb, List<SelectArg> columnArgList)
			throws SQLException {
		sb.append('(');
		boolean first = true;
		for (Object value : objects) {
			if (value == null) {
				throw new IllegalArgumentException("one of the IN values for '" + columnName + "' is null");
			}
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}
			// for each of our arguments, add it to the output
			super.appendArgOrValue(databaseType, fieldType, sb, columnArgList, value);
		}
		sb.append(") ");
		return sb;
	}
}
