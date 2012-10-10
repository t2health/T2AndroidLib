/*
 * 
 * T2AndroidLib
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
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
package com.j256.ormlite.stmt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.stmt.query.Clause;
import com.j256.ormlite.stmt.query.SetExpression;
import com.j256.ormlite.stmt.query.SetValue;
import com.j256.ormlite.table.TableInfo;

/**
 * Assists in building sql UPDATE statements for a particular table in a particular database.
 * 
 * @param T
 *            The class that the code will be operating on.
 * @param ID
 *            The class of the ID column associated with the class. The T class does not require an ID field. The class
 *            needs an ID parameter however so you can use Void or Object to satisfy the compiler.
 * @author graywatson
 */
public class UpdateBuilder<T, ID> extends StatementBuilder<T, ID> {

	private List<Clause> updateClauseList = null;

	public UpdateBuilder(DatabaseType databaseType, TableInfo<T, ID> tableInfo) {
		super(databaseType, tableInfo, StatementType.UPDATE);
	}

	/**
	 * Build and return a prepared update that can be used by {@link Dao#update(PreparedUpdate)} method. If you change
	 * the where or make other calls you will need to re-call this method to re-prepare the statement for execution.
	 */
	public PreparedUpdate<T> prepare() throws SQLException {
		return super.prepareStatement();
	}

	/**
	 * Add a column to be set to a value for UPDATE statements. This will generate something like columnName = 'value'
	 * with the value escaped if necessary.
	 */
	public StatementBuilder<T, ID> updateColumnValue(String columnName, Object value) throws SQLException {
		FieldType fieldType = verifyColumnName(columnName);
		if (fieldType.isForeignCollection()) {
			throw new SQLException("Can't update foreign colletion field: " + columnName);
		}
		addUpdateColumnToList(columnName, new SetValue(columnName, fieldType, value));
		return this;
	}

	/**
	 * Add a column to be set to a value for UPDATE statements. This will generate something like 'columnName =
	 * expression' where the expression is built by the caller.
	 * 
	 * <p>
	 * The expression should have any strings escaped using the {@link #escapeValue(String)} or
	 * {@link #escapeValue(StringBuilder, String)} methods and should have any column names escaped using the
	 * {@link #escapeColumnName(String)} or {@link #escapeColumnName(StringBuilder, String)} methods.
	 * </p>
	 */
	public StatementBuilder<T, ID> updateColumnExpression(String columnName, String expression) throws SQLException {
		addUpdateColumnToList(columnName, new SetExpression(columnName, expression));
		return this;
	}

	/**
	 * When you are building the expression for {@link #updateColumnExpression(String, String)}, you may need to escape
	 * column names since they may be reserved words to the database. This will help you by adding escape characters
	 * around the word.
	 */
	public void escapeColumnName(StringBuilder sb, String columnName) {
		databaseType.appendEscapedEntityName(sb, columnName);
	}

	/**
	 * Same as {@link #escapeColumnName(StringBuilder, String)} but it will return the escaped string. The StringBuilder
	 * method is more efficient since this method creates a {@link StatementBuilder} internally.
	 */
	public String escapeColumnName(String columnName) {
		StringBuilder sb = new StringBuilder();
		databaseType.appendEscapedEntityName(sb, columnName);
		return sb.toString();
	}

	/**
	 * When you are building the expression for {@link #updateColumnExpression(String, String)}, you may need to escape
	 * values since they may be reserved words to the database. Numbers should not be escaped. This will help you by
	 * adding escape characters around the word.
	 */
	public void escapeValue(StringBuilder sb, String value) {
		databaseType.appendEscapedWord(sb, value);
	}

	/**
	 * Same as {@link #escapeValue(StringBuilder, String)} but it will return the escaped string. Numbers should not be
	 * escaped. The StringBuilder method is more efficient since this method creates a {@link StatementBuilder}
	 * internally.
	 */
	public String escapeValue(String value) {
		StringBuilder sb = new StringBuilder();
		databaseType.appendEscapedWord(sb, value);
		return sb.toString();
	}

	@Override
	protected void appendStatementStart(StringBuilder sb, List<FieldType> resultFieldTypeList) throws SQLException {
		if (updateClauseList == null || updateClauseList.size() == 0) {
			throw new IllegalArgumentException("UPDATE statements must have at least one SET column");
		}
		sb.append("UPDATE ");
		databaseType.appendEscapedEntityName(sb, tableInfo.getTableName());
		sb.append(" SET ");
		boolean first = true;
		for (Clause clause : updateClauseList) {
			if (first) {
				first = false;
			} else {
				sb.append(',');
			}
			clause.appendSql(databaseType, sb, null);
		}
	}

	@Override
	protected void appendStatementEnd(StringBuilder sb) {
		// noop
	}

	private void addUpdateColumnToList(String columnName, Clause clause) {
		FieldType fieldType = verifyColumnName(columnName);
		if (fieldType.isForeignCollection()) {
			throw new IllegalArgumentException("Can't update foreign colletion field: " + columnName);
		}
		if (updateClauseList == null) {
			updateClauseList = new ArrayList<Clause>();
		}
		updateClauseList.add(clause);
	}
}
