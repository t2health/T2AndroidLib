package com.j256.ormlite.stmt;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.table.TableInfo;

/**
 * Assists in building sql DELETE statements for a particular table in a particular database.
 * 
 * @param T
 *            The class that the code will be operating on.
 * @param ID
 *            The class of the ID column associated with the class. The T class does not require an ID field. The class
 *            needs an ID parameter however so you can use Void or Object to satisfy the compiler.
 * @author graywatson
 */
public class DeleteBuilder<T, ID> extends StatementBuilder<T, ID> {

	public DeleteBuilder(DatabaseType databaseType, TableInfo<T, ID> tableInfo) {
		super(databaseType, tableInfo, StatementType.DELETE);
	}

	/**
	 * Build and return a prepared delete that can be used by {@link Dao#delete(PreparedDelete)} method. If you change
	 * the where or make other calls you will need to re-call this method to re-prepare the statement for execution.
	 */
	public PreparedDelete<T> prepare() throws SQLException {
		return super.prepareStatement();
	}

	@Override
	protected void appendStatementStart(StringBuilder sb, List<FieldType> resultFieldTypeList) {
		sb.append("DELETE FROM ");
		databaseType.appendEscapedEntityName(sb, tableInfo.getTableName());
		sb.append(' ');
	}

	@Override
	protected void appendStatementEnd(StringBuilder sb) {
		// noop
	}
}
