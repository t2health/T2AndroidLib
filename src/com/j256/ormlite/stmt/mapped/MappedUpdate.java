package com.j256.ormlite.stmt.mapped;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.table.TableInfo;

/**
 * Mapped statement for updating an object.
 * 
 * @author graywatson
 */
public class MappedUpdate<T, ID> extends BaseMappedStatement<T, ID> {

	private MappedUpdate(TableInfo<T, ID> tableInfo, String statement, List<FieldType> argFieldTypeList) {
		super(tableInfo, statement, argFieldTypeList);
	}

	public static <T, ID> MappedUpdate<T, ID> build(DatabaseType databaseType, TableInfo<T, ID> tableInfo)
			throws SQLException {
		FieldType idField = tableInfo.getIdField();
		if (idField == null) {
			throw new SQLException("Cannot update " + tableInfo.getDataClass() + " because it doesn't have an id field");
		}
		if (tableInfo.getFieldTypes().length == 1) {
			throw new SQLException("Cannot update " + tableInfo.getDataClass()
					+ " with only the id field.  You should use updateId().");
		}
		StringBuilder sb = new StringBuilder();
		List<FieldType> argFieldTypeList = new ArrayList<FieldType>();
		appendTableName(databaseType, sb, "UPDATE ", tableInfo.getTableName());
		boolean first = true;
		for (FieldType fieldType : tableInfo.getFieldTypes()) {
			// we never update the idField or foreign collections
			if (fieldType == idField || fieldType.isForeignCollection()) {
				continue;
			}
			if (first) {
				sb.append("SET ");
				first = false;
			} else {
				sb.append(", ");
			}
			appendFieldColumnName(databaseType, sb, fieldType, argFieldTypeList);
			sb.append("= ?");
		}
		sb.append(' ');
		appendWhereId(databaseType, idField, sb, argFieldTypeList);
		return new MappedUpdate<T, ID>(tableInfo, sb.toString(), argFieldTypeList);
	}
}
