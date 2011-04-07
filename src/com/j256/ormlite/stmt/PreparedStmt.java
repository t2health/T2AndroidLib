package com.j256.ormlite.stmt;

import java.sql.SQLException;

import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseConnection;

/**
 * Parent interface for the {@link PreparedQuery}, {@link PreparedUpdate}, and {@link PreparedDelete} interfaces.
 */
public interface PreparedStmt<T> extends GenericRowMapper<T> {

	/**
	 * Create and return the associated compiled statement.
	 */
	public CompiledStatement compile(DatabaseConnection databaseConnection) throws SQLException;

	/**
	 * Return the associated SQL statement string for logging purposes.
	 */
	public String getStatement() throws SQLException;
}
