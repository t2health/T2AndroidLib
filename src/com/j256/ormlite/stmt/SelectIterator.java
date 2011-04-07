package com.j256.ormlite.stmt;

import java.sql.SQLException;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Internal iterator so we can page through the class. This is used by the {@link Dao#iterator} methods.
 * 
 * @param T
 *            The class that the code will be operating on.
 * @param ID
 *            The class of the ID column associated with the class. The T class does not require an ID field. The class
 *            needs an ID parameter however so you can use Void or Object to satisfy the compiler.
 * @author graywatson
 */
public class SelectIterator<T, ID> implements CloseableIterator<T> {

	private final static Logger logger = LoggerFactory.getLogger(SelectIterator.class);

	private final Class<?> dataClass;
	private final Dao<T, ID> classDao;
	private final ConnectionSource connectionSource;
	private final DatabaseConnection connection;
	private final CompiledStatement compiledStmt;
	private final DatabaseResults results;
	private final GenericRowMapper<T> rowMapper;
	private final String statement;
	private boolean closed = false;
	private T last = null;
	private int rowC = 0;

	/**
	 * If the statement parameter is null then this won't log information
	 */
	public SelectIterator(Class<?> dataClass, Dao<T, ID> classDao, GenericRowMapper<T> rowMapper,
			ConnectionSource connectionSource, DatabaseConnection connection, CompiledStatement compiledStmt,
			String statement) throws SQLException {
		this.dataClass = dataClass;
		this.classDao = classDao;
		this.rowMapper = rowMapper;
		this.connectionSource = connectionSource;
		this.connection = connection;
		this.compiledStmt = compiledStmt;
		this.results = compiledStmt.runQuery();
		this.statement = statement;
		if (statement != null) {
			logger.debug("starting iterator @{} for '{}'", hashCode(), statement);
		}
	}

	/**
	 * Returns whether or not there are any remaining objects in the table. Must be called before next().
	 * 
	 * @throws SQLException
	 *             If there was a problem getting more results via SQL.
	 */
	public boolean hasNextThrow() throws SQLException {
		if (closed) {
			return false;
		} else if (results.next()) {
			return true;
		} else {
			close();
			return false;
		}
	}

	/**
	 * Returns whether or not there are any remaining objects in the table. Must be called before next().
	 * 
	 * @throws IllegalStateException
	 *             If there was a problem getting more results via SQL.
	 */
	public boolean hasNext() {
		try {
			return hasNextThrow();
		} catch (SQLException e) {
			last = null;
			try {
				close();
			} catch (SQLException e1) {
				// ignore it
			}
			// unfortunately, can't propagate back the SQLException
			throw new IllegalStateException("Errors getting more results of " + dataClass, e);
		}
	}

	/**
	 * Returns the next() object in the table.
	 * 
	 * @throws SQLException
	 *             If there was a problem extracting the object from SQL.
	 */
	public T nextThrow() throws SQLException {
		if (closed) {
			return null;
		}
		last = rowMapper.mapRow(results);
		rowC++;
		return last;
	}

	/**
	 * Returns the next() object in the table.
	 * 
	 * @throws IllegalStateException
	 *             If there was a problem extracting the object from SQL.
	 */
	public T next() {
		try {
			return nextThrow();
		} catch (SQLException e) {
			last = null;
			try {
				close();
			} catch (SQLException e1) {
				// ignore it
			}
			// unfortunately, can't propagate back the SQLException
			throw new IllegalStateException("Errors getting more results of " + dataClass, e);
		}
	}

	/**
	 * Removes the last object returned by next() by calling delete on the dao associated with the object.
	 * 
	 * @throws IllegalStateException
	 *             If there was no previous next() call.
	 * @throws SQLException
	 *             If the delete failed.
	 */
	public void removeThrow() throws SQLException {
		if (last == null) {
			throw new IllegalStateException("No last " + dataClass
					+ " object to remove. Must be called after a call to next.");
		}
		if (classDao == null) {
			// we may never be able to get here since it should only be null for queryForAll methods
			throw new IllegalStateException("Cannot remove " + dataClass + " object because classDao not initialized");
		}
		try {
			classDao.delete(last);
		} finally {
			// if we've try to delete it, clear the last marker
			last = null;
		}
	}

	/**
	 * Removes the last object returned by next() by calling delete on the dao associated with the object.
	 * 
	 * @throws IllegalStateException
	 *             If there was no previous next() call or if delete() throws a SQLException (set as the cause).
	 */
	public void remove() {
		try {
			removeThrow();
		} catch (SQLException e) {
			try {
				close();
			} catch (SQLException e1) {
				// ignore it
			}
			// unfortunately, can't propagate back the SQLException
			throw new IllegalStateException("Errors trying to delete " + dataClass + " object " + last, e);
		}
	}

	/**
	 * Close the underlying statement.
	 */
	public void close() throws SQLException {
		if (!closed) {
			compiledStmt.close();
			closed = true;
			last = null;
			if (statement != null) {
				logger.debug("closed iterator @{} after {} rows", hashCode(), rowC);
			}
			connectionSource.releaseConnection(connection);
		}
	}

	/**
	 * Return the internal raw results object that was created by this iterator. This should not be used unless you know
	 * what you are doing.
	 */
	public DatabaseResults getRawResults() {
		return results;
	}
}
