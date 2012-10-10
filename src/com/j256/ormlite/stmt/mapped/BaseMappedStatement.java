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
package com.j256.ormlite.stmt.mapped;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;

/**
 * Abstract mapped statement which has common statements used by the subclasses.
 * 
 * @author graywatson
 */
public abstract class BaseMappedStatement<T, ID> {

	protected static Logger logger = LoggerFactory.getLogger(BaseMappedStatement.class);

	protected final TableInfo<T, ID> tableInfo;
	protected final FieldType idField;
	protected final String statement;
	protected final FieldType[] argFieldTypes;

	protected BaseMappedStatement(TableInfo<T, ID> tableInfo, String statement, List<FieldType> argFieldTypeList) {
		this.tableInfo = tableInfo;
		this.idField = tableInfo.getIdField();
		this.statement = statement;
		this.argFieldTypes = argFieldTypeList.toArray(new FieldType[argFieldTypeList.size()]);
	}

	/**
	 * Insert the object into the database
	 */
	protected int insert(DatabaseConnection databaseConnection, T data) throws SQLException {
		try {
			Object[] args = getFieldObjects(data);
			int rowC = databaseConnection.insert(statement, args, argFieldTypes);
			logger.debug("insert data with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
			if (args.length > 0) {
				// need to do the (Object) cast to force args to be a single object
				logger.trace("insert arguments: {}", (Object) args);
			}
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run insert stmt on object " + data + ": " + statement, e);
		}
	}

	/**
	 * Update the object in the database.
	 */
	public int update(DatabaseConnection databaseConnection, T data) throws SQLException {
		try {
			Object[] args = getFieldObjects(data);
			int rowC = databaseConnection.update(statement, args, argFieldTypes);
			logger.debug("update data with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
			if (args.length > 0) {
				// need to do the (Object) cast to force args to be a single object
				logger.trace("update arguments: {}", (Object) args);
			}
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run update stmt on object " + data + ": " + statement, e);
		}
	}

	/**
	 * Delete the object from the database.
	 */
	public int delete(DatabaseConnection databaseConnection, T data) throws SQLException {
		try {
			Object[] args = getFieldObjects(data);
			int rowC = databaseConnection.delete(statement, args, argFieldTypes);
			logger.debug("delete data with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
			if (args.length > 0) {
				// need to do the (Object) cast to force args to be a single object
				logger.trace("delete arguments: {}", (Object) args);
			}
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run delete stmt on object " + data + ": " + statement, e);
		}
	}

	/**
	 * Return the array of field objects pulled from the data object.
	 */
	protected Object[] getFieldObjects(Object data) throws SQLException {
		Object[] objects = new Object[argFieldTypes.length];
		for (int i = 0; i < argFieldTypes.length; i++) {
			FieldType fieldType = argFieldTypes[i];
			objects[i] = fieldType.extractJavaFieldToSqlArgValue(data);
			if (objects[i] == null && fieldType.getDefaultValue() != null) {
				objects[i] = fieldType.getDefaultValue();
			}
		}
		return objects;
	}

	/**
	 * Return a field object converted from an id.
	 */
	protected Object convertIdToFieldObject(ID id) throws SQLException {
		return idField.convertJavaFieldToSqlArgValue(id);
	}

	/**
	 * Return a field-object for the id extracted from the data.
	 */
	protected Object extractIdToFieldObject(T data) throws SQLException {
		return idField.extractJavaFieldToSqlArgValue(data);
	}

	static void appendWhereId(DatabaseType databaseType, FieldType idField, StringBuilder sb,
			List<FieldType> fieldTypeList) {
		sb.append("WHERE ");
		appendFieldColumnName(databaseType, sb, idField, fieldTypeList);
		sb.append("= ?");
	}

	static void appendTableName(DatabaseType databaseType, StringBuilder sb, String prefix, String tableName) {
		if (prefix != null) {
			sb.append(prefix);
		}
		databaseType.appendEscapedEntityName(sb, tableName);
		sb.append(' ');
	}

	static void appendFieldColumnName(DatabaseType databaseType, StringBuilder sb, FieldType fieldType,
			List<FieldType> fieldTypeList) {
		databaseType.appendEscapedEntityName(sb, fieldType.getDbColumnName());
		if (fieldTypeList != null) {
			fieldTypeList.add(fieldType);
		}
		sb.append(' ');
	}

	@Override
	public String toString() {
		return "MappedStatement: " + statement;
	}
}
