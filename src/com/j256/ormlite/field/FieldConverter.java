package com.j256.ormlite.field;

import java.sql.SQLException;

import com.j256.ormlite.db.BaseDatabaseType;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Convert a Java object into the appropriate argument to a SQL statement and then back from the result set to the Java
 * object. This allows databases to configure per-type conversion. This is used by the
 * {@link BaseDatabaseType#getFieldConverter(DataType)} method to find the converter for a particular database type.
 * Databases can then override the default data conversion mechanisms as necessary.
 * 
 * @author graywatson
 */
public interface FieldConverter {

	/**
	 * Convert a default string object and return the appropriate argument to a SQL insert or update statement.
	 */
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException;

	/**
	 * Convert a Java object and return the appropriate argument to a SQL insert or update statement.
	 */
	public Object javaToSqlArg(FieldType fieldType, Object obj) throws SQLException;

	/**
	 * Return the object extracted from the results associated with column in position columnPos.
	 * 
	 * @throws SQLException
	 *             If there is a problem accessing the results data.
	 * @param fieldType
	 *            Associated FieldType which may be null.
	 */
	public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException;

	/**
	 * Return the SQL type that is stored in the database for this argument.
	 */
	public SqlType getSqlType();

	/**
	 * Return whether or not this is a SQL "stream" object. Cannot get certain stream objects from the SQL results more
	 * than once. If true, the converter has to protect itself against null values.
	 */
	public boolean isStreamType();
}
