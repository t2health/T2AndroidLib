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
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;

/**
 * Mapped statement for updating an object's ID field.
 * 
 * @author graywatson
 */
public class MappedUpdateId<T, ID> extends BaseMappedStatement<T, ID> {

	private MappedUpdateId(TableInfo<T, ID> tableInfo, String statement, List<FieldType> argFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList);
	}

	/**
	 * Update the id field of the object in the database.
	 */
	public int execute(DatabaseConnection databaseConnection, T data, ID newId) throws SQLException {
		try {
			// the arguments are the new-id and old-id
			Object[] args = new Object[] { convertIdToFieldObject(newId), extractIdToFieldObject(data) };
			int rowC = databaseConnection.update(statement, args, argFieldTypes);
			if (rowC == 1) {
				// adjust the object to assign the new id
				idField.assignField(data, newId);
			}
			logger.debug("updating-id with statement '{}' and {} args, changed {} rows", statement, args.length, rowC);
			if (args.length > 0) {
				// need to do the cast otherwise we only print the first object in args
				logger.trace("updating-id arguments: {}", (Object) args);
			}
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run update-id stmt on object " + data + ": " + statement, e);
		}
	}

	public static <T, ID> MappedUpdateId<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo)
			throws SQLException {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			throw new SQLException("Cannot update-id in " + tableInfo.getDataClass()
					+ " because it doesn't have an id field");
		}
		StringBuilder sb = new StringBuilder();
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		appendTableName(databaseType, sb, "UPDATE ", tableInfo.getTableName());
		sb.append("SET ");
		appendFieldColumnName(databaseType, sb, idField, argFieldTypeList);
		sb.append("= ? ");
		appendWhereId(databaseType, idField, sb, argFieldTypeList);
		return new MappedUpdateId<T, ID>(tableInfo, sb.toString(), argFieldTypeList);
	}
}
