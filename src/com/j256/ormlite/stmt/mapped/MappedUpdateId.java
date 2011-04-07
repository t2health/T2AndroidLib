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
