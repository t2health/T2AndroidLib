package com.j256.ormlite.support;

import java.sql.SQLException;
import java.sql.Savepoint;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;

/**
 * A reduction of the SQL Connection so we can implement its functionality outside of JDBC.
 * 
 * @author graywatson
 */
public interface DatabaseConnection {

	/** returned by {@link #queryForOne} if more than one result was found by the query */
	public final static Object MORE_THAN_ONE = new Object();

	/**
	 * Return if auto-commit is supported.
	 */
	public boolean isAutoCommitSupported() throws SQLException;

	/**
	 * Return if auto-commit is currently enabled.
	 */
	public boolean getAutoCommit() throws SQLException;

	/**
	 * Set the auto-commit to be on (true) or off (false).
	 */
	public void setAutoCommit(boolean autoCommit) throws SQLException;

	/**
	 * Start a save point with a certain name. It can be a noop if savepoints are not supported.
	 * 
	 * @param name
	 *            to use for the Savepoint although it can be ignored.
	 * 
	 * @return A SavePoint object with which we can release or commit in the future or null if none.
	 */
	public Savepoint setSavePoint(String name) throws SQLException;

	/**
	 * Commit all changes since the savepoint was created. If savePoint is null then commit all outstanding changes.
	 * 
	 * @param savePoint
	 *            That was returned by setSavePoint or null if none.
	 */
	public void commit(Savepoint savePoint) throws SQLException;

	/**
	 * Roll back all changes since the savepoint was created. If savePoint is null then roll back all outstanding
	 * changes.
	 * 
	 * @param savePoint
	 *            That was returned by setSavePoint previously or null if none.
	 */
	public void rollback(Savepoint savePoint) throws SQLException;

	/**
	 * Compile and prepare the SQL statement for execution.
	 */
	public CompiledStatement compileStatement(String statement, StatementType type, FieldType[] argfieldTypes,
			FieldType[] resultfieldTypes) throws SQLException;

	/**
	 * Perform a SQL insert with the associated SQL statement, arguments, and types.
	 * 
	 * @param statement
	 *            SQL statement to use for inserting.
	 * @param args
	 *            Object arguments for the SQL '?'s.
	 * @param argfieldTypes
	 *            Field types of the arguments.
	 * @return The number of rows affected by the update. With some database types, this value may be invalid.
	 */
	public int insert(String statement, Object[] args, FieldType[] argfieldTypes) throws SQLException;

	/**
	 * Perform a SQL update while returning generated keys with the associated SQL statement, arguments, and types.
	 * 
	 * @param statement
	 *            SQL statement to use for inserting.
	 * @param args
	 *            Object arguments for the SQL '?'s.
	 * @param argfieldTypes
	 *            Field types of the arguments.
	 * @param keyHolder
	 *            The holder that gets set with the generated key value.
	 * @return The number of rows affected by the update. With some database types, this value may be invalid.
	 */
	public int insert(String statement, Object[] args, FieldType[] argfieldTypes, GeneratedKeyHolder keyHolder)
			throws SQLException;

	/**
	 * Perform a SQL update with the associated SQL statement, arguments, and types.
	 * 
	 * @param statement
	 *            SQL statement to use for updating.
	 * @param args
	 *            Object arguments for the SQL '?'s.
	 * @param argfieldTypes
	 *            Field types of the arguments.
	 * @return The number of rows affected by the update. With some database types, this value may be invalid.
	 */
	public int update(String statement, Object[] args, FieldType[] argfieldTypes) throws SQLException;

	/**
	 * Perform a SQL delete with the associated SQL statement, arguments, and types.
	 * 
	 * @param statement
	 *            SQL statement to use for deleting.
	 * @param args
	 *            Object arguments for the SQL '?'s.
	 * @param argfieldTypes
	 *            Field types of the arguments.
	 * @return The number of rows affected by the update. With some database types, this value may be invalid.
	 */
	public int delete(String statement, Object[] args, FieldType[] argfieldTypes) throws SQLException;

	/**
	 * Perform a SQL query with the associated SQL statement, arguments, and types and returns a single result.
	 * 
	 * @param statement
	 *            SQL statement to use for deleting.
	 * @param args
	 *            Object arguments for the SQL '?'s.
	 * @param argfieldTypes
	 *            Field types of the arguments.
	 * @param rowMapper
	 *            The mapper to use to convert the row into the returned object.
	 * @return The first data item returned by the query which can be cast to <T>, null if none, the object
	 *         {@link #MORE_THAN_ONE} if more than one result was found.
	 */
	public <T> Object queryForOne(String statement, Object[] args, FieldType[] argfieldTypes,
			GenericRowMapper<T> rowMapper) throws SQLException;

	/**
	 * Perform a query whose result should be a single long-integer value.
	 * 
	 * @param statement
	 *            SQL statement to use for the query.
	 */
	public long queryForLong(String statement) throws SQLException;

	/**
	 * Close the connection to the database.
	 */
	public void close() throws SQLException;

	/**
	 * Return if the connection has been closed either through a call to {@link #close()} or because of a fatal error.
	 */
	public boolean isClosed() throws SQLException;

	/**
	 * Return true if the table exists in the database.
	 */
	public boolean isTableExists(String tableName) throws SQLException;
}
