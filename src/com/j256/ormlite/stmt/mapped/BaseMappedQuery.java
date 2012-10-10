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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.BaseForeignCollection;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.TableInfo;

/**
 * Abstract mapped statement for queries which handle the creating of a new object and the row mapping functionality.
 * 
 * @author graywatson
 */
public abstract class BaseMappedQuery<T, ID> extends BaseMappedStatement<T, ID> implements GenericRowMapper<T> {

	protected final FieldType[] resultsFieldTypes;
	// cache of column names to results position
	private Map<String, Integer> columnPositions = null;

	protected BaseMappedQuery(TableInfo<T, ID> tableInfo, String statement, List<FieldType> argFieldTypeList,
			List<FieldType> resultFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList);
		this.resultsFieldTypes = resultFieldTypeList.toArray(new FieldType[resultFieldTypeList.size()]);
	}

	public T mapRow(DatabaseResults results) throws SQLException {
		Map<String, Integer> colPosMap;
		if (columnPositions == null) {
			colPosMap = new HashMap<String, Integer>();
		} else {
			colPosMap = columnPositions;
		}
		// create our instance
		T instance = tableInfo.createObject();
		// populate its fields
		Object id = null;
		boolean foreignCollections = false;
		for (FieldType fieldType : resultsFieldTypes) {
			if (fieldType.isForeignCollection()) {
				foreignCollections = true;
			} else {
				Object val = fieldType.resultToJava(results, colPosMap);
				fieldType.assignField(instance, val);
				if (fieldType == idField) {
					id = val;
				}
			}
		}
		if (foreignCollections) {
			// go back and initialize any foreign collections
			for (FieldType fieldType : resultsFieldTypes) {
				if (fieldType.isForeignCollection()) {
					BaseForeignCollection<?, ?> collection = fieldType.buildForeignCollection(id);
					fieldType.assignField(instance, collection);
				}
			}
		}
		if (columnPositions == null) {
			columnPositions = colPosMap;
		}
		return instance;
	}

	public FieldType[] getResultsFieldTypes() {
		return resultsFieldTypes;
	}
}
