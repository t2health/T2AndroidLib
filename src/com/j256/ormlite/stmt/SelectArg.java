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
package com.j256.ormlite.stmt;

import java.sql.SQLException;

import com.j256.ormlite.field.FieldType;

/**
 * An argument to a select SQL statement. After the query is constructed, the caller can set the value on this argument
 * and run the query. Then the argument can be set again and the query re-executed. This is equivalent in SQL to a ?
 * type argument.
 * 
 * <p>
 * NOTE: If the argument has not been set by the time the query is executed, an exception will be thrown.
 * </p>
 * 
 * <p>
 * NOTE: For protections sake, the object cannot be reused with different column names.
 * </p>
 * 
 * <blockquote>
 * 
 * <pre>
 * // build a query using the Account DAO
 * QueryBuilder&lt;Account, String&gt; qb = accountDao.queryBuilder();
 * 
 * // create an argument which will be set later
 * SelectArg passwordSelectArg = new SelectArg();
 * qb.where().eq(Account.PASSWORD_FIELD_NAME, passwordSelectArg);
 * // prepare the query
 * PreparedQuery&lt;Account&gt; preparedQuery = qb.prepareQuery();
 * // ...
 * 
 * // some time later we set the value and run the query
 * passwordSelectArg.setValue(&quot;_secret&quot;);
 * List&lt;Account&gt; results = accountDao.query(preparedQuery);
 * // we can then re-set the value and re-run the query
 * passwordSelectArg.setValue(&quot;qwerty&quot;);
 * List&lt;Account&gt; results = accountDao.query(preparedQuery);
 * </pre>
 * 
 * </blockquote>
 * 
 * @author graywatson
 */
public class SelectArg {

	private boolean hasBeenSet = false;
	private String columnName = null;
	private FieldType fieldType = null;
	private Object value = null;

	/**
	 * Return the column-name associated with this argument. The name is set by the package internally.
	 */
	public String getColumnName() {
		if (columnName == null) {
			throw new IllegalArgumentException("Column name has not been set");
		} else {
			return columnName;
		}
	}

	/**
	 * Used internally by the package to set the column-name associated with this argument.
	 */
	public void setMetaInfo(String columnName, FieldType fieldType) {
		if (this.columnName == null) {
			// not set yet
		} else if (this.columnName.equals(columnName)) {
			// set to the same value as before
		} else {
			throw new IllegalArgumentException("Column name cannot be set twice from " + this.columnName + " to "
					+ columnName);
		}
		if (this.fieldType == null) {
			// not set yet
		} else if (this.fieldType == fieldType) {
			// set to the same value as before
		} else {
			throw new IllegalArgumentException("FieldType name cannot be set twice from " + this.fieldType + " to "
					+ fieldType);
		}
		this.columnName = columnName;
		this.fieldType = fieldType;
	}

	/**
	 * Return the value associated with this argument. The value should be set by the user before it is consumed.
	 */
	public Object getSqlArgValue() throws SQLException {
		if (!hasBeenSet) {
			throw new SQLException("Column value has not been set for " + columnName);
		}
		if (fieldType == null) {
			return value;
		} else if (fieldType.isForeign() && fieldType.getFieldType() == value.getClass()) {
			FieldType idFieldType = fieldType.getForeignIdField();
			return idFieldType.extractJavaFieldValue(value);
		} else {
			return fieldType.convertJavaFieldToSqlArgValue(value);
		}
	}

	/**
	 * Set the value associated with this argument. The value should be set by the user after the query has been built
	 * but before it has been executed.
	 */
	public void setValue(Object value) {
		this.hasBeenSet = true;
		this.value = value;
	}

	public FieldType getFieldType() {
		return fieldType;
	}

	@Override
	public String toString() {
		if (!hasBeenSet) {
			return "[unset]";
		}
		if (value == null) {
			return "[null]";
		}
		Object val;
		try {
			val = getSqlArgValue();
			if (val == null) {
				return "[null]";
			} else {
				return val.toString();
			}
		} catch (SQLException e) {
			return "[could not get value: " + e.getMessage() + "]";
		}
	}
}
