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
import com.j256.ormlite.stmt.QueryBuilder.InternalQueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;

/**
 * Mapped statement for querying for an object by its ID.
 * 
 * @author graywatson
 */
public class MappedQueryForId<T, ID> extends BaseMappedQuery<T, ID> {

	private final String label;

	protected MappedQueryForId(TableInfo<T, ID> tableInfo, String statement, List<FieldType> argFieldTypeList,
			List<FieldType> resultFieldTypeList, String label) {
		super(tableInfo, statement, argFieldTypeList, resultFieldTypeList);
		this.label = label;
	}

	/**
	 * Query for an object in the database which matches the id argument.
	 */
	public T execute(DatabaseConnection databaseConnection, ID id) throws SQLException {
		Object[] args = new Object[] { convertIdToFieldObject(id) };
		// @SuppressWarnings("unchecked")
		Object result = databaseConnection.queryForOne(statement, args, new FieldType[] { idField }, this);
		if (result == null) {
			logger.debug("{} using '{}' and {} args, got no results", label, statement, args.length);
		} else if (result == DatabaseConnection.MORE_THAN_ONE) {
			logger.error("{} using '{}' and {} args, got >1 results", label, statement, args.length);
			logArgs(args);
			throw new SQLException(label + " got more than 1 result: " + statement);
		} else {
			logger.debug("{} using '{}' and {} args, got 1 result", label, statement, args.length);
		}
		logArgs(args);
		@SuppressWarnings("unchecked")
		T castResult = (T) result;
		return castResult;
	}

	public static <T, ID> MappedQueryForId<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo)
			throws SQLException {
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		List<FieldType> resultFieldTypeList = new ArrayList<FieldType>();
		String statement = buildStatement(databaseType, tableInfo, argFieldTypeList, resultFieldTypeList);
		return new MappedQueryForId<T, ID>(tableInfo, statement, argFieldTypeList, resultFieldTypeList, "query-for-id");
	}

	protected static <T, ID> String buildStatement(DatabaseType databaseType, TableInfo<T, ID> tableInfo,
			List<FieldType> argFieldTypeList, List<FieldType> resultFieldTypeList) throws SQLException {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			throw new SQLException("Cannot query-for-id with " + tableInfo.getDataClass()
					+ " because it doesn't have an id field");
		}
		InternalQueryBuilder<T, ID> qb = new InternalQueryBuilder<T, ID>(databaseType, tableInfo);
		// this selectArg is ignored here because we pass in the id as a fixed argument
		SelectArg idSelectArg = new SelectArg();
		qb.where().eq(idField.getDbColumnName(), idSelectArg);
		List<SelectArg> selectArgList = new ArrayList<SelectArg>();
		return qb.buildStatementString(argFieldTypeList, resultFieldTypeList, selectArgList);
	}

	private void logArgs(Object[] args) {
		if (args.length > 0) {
			// need to do the (Object) cast to force args to be a single object
			logger.trace("{} arguments: {}", label, (Object) args);
		}
	}
}
