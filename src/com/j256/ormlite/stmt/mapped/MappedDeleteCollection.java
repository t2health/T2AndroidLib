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
import java.util.Collection;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;

/**
 * A mapped statement for deleting objects that correspond to a collection of IDs.
 * 
 * @author graywatson
 */
public class MappedDeleteCollection<T, ID> extends BaseMappedStatement<T, ID> {

	private MappedDeleteCollection(TableInfo<T, ID> tableInfo, String statement, List<FieldType> argFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList);
	}

	/**
	 * Delete all of the objects in the collection. This builds a {@link MappedDeleteCollection} on the fly because the
	 * datas could be variable sized.
	 */
	public static <T, ID> int deleteObjects(DatabaseType databaseType, TableInfo<T, ID> tableInfo,
			DatabaseConnection databaseConnection, Collection<T> datas) throws SQLException {
		MappedDeleteCollection<T, ID> deleteCollection =
				MappedDeleteCollection.build(databaseType, tableInfo, datas.size());
		Object[] fieldObjects = new Object[datas.size()];
		int objC = 0;
		for (T data : datas) {
			fieldObjects[objC] = tableInfo.getIdField().extractJavaFieldToSqlArgValue(data);
			objC++;
		}
		return updateRows(databaseConnection, deleteCollection, fieldObjects);
	}

	/**
	 * Delete all of the objects in the collection. This builds a {@link MappedDeleteCollection} on the fly because the
	 * ids could be variable sized.
	 */
	public static <T, ID> int deleteIds(DatabaseType databaseType, TableInfo<T, ID> tableInfo,
			DatabaseConnection databaseConnection, Collection<ID> ids) throws SQLException {
		MappedDeleteCollection<T, ID> deleteCollection =
				MappedDeleteCollection.build(databaseType, tableInfo, ids.size());
		Object[] fieldObjects = new Object[ids.size()];
		int objC = 0;
		for (ID id : ids) {
			fieldObjects[objC] = tableInfo.getIdField().convertJavaFieldToSqlArgValue(id);
			objC++;
		}
		return updateRows(databaseConnection, deleteCollection, fieldObjects);
	}

	/**
	 * This is private because the execute is the only method that should be called here.
	 */
	private static <T, ID> MappedDeleteCollection<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo,
			int dataSize) throws SQLException {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			throw new SQLException("Cannot delete " + tableInfo.getDataClass()
					+ " because it doesn't have an id field defined");
		}
		StringBuilder sb = new StringBuilder();
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		appendTableName(databaseType, sb, "DELETE FROM ", tableInfo.getTableName());
		appendWhereIds(databaseType, idField, sb, dataSize, argFieldTypeList);
		return new MappedDeleteCollection<T, ID>(tableInfo, sb.toString(), argFieldTypeList);
	}

	private static <T, ID> int updateRows(DatabaseConnection databaseConnection,
			MappedDeleteCollection<T, ID> deleteCollection, Object[] args) throws SQLException {
		try {
			int rowC = databaseConnection.delete(deleteCollection.statement, args, deleteCollection.argFieldTypes);
			logger.debug("delete-collection with statement '{}' and {} args, changed {} rows",
					deleteCollection.statement, args.length, rowC);
			if (args.length > 0) {
				// need to do the (Object) cast to force args to be a single object
				logger.trace("delete-collection arguments: {}", (Object) args);
			}
			return rowC;
		} catch (SQLException e) {
			throw SqlExceptionUtil.create("Unable to run delete collection stmt: " + deleteCollection.statement, e);
		}
	}

	private static void appendWhereIds(DatabaseType databaseType, FieldType idField, StringBuilder sb, int numDatas,
			List<FieldType> fieldTypeList) {
		sb.append("WHERE ");
		databaseType.appendEscapedEntityName(sb, idField.getDbColumnName());
		sb.append(" IN (");
		boolean first = true;
		for (int i = 0; i < numDatas; i++) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}
			sb.append('?');
			if (fieldTypeList != null) {
				fieldTypeList.add(idField);
			}
		}
		sb.append(") ");
	}
}
