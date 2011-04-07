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
