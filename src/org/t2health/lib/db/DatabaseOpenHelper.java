package org.t2health.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Base class used to handle database initialization.
 * @author robbiev
 *
 */
public abstract class DatabaseOpenHelper extends OrmLiteSqliteOpenHelper {
	public DatabaseOpenHelper(Context context, String dbName, CursorFactory cursorFactory, int dbVersion) {
		super(context, dbName, cursorFactory, dbVersion);
	}
	
	@Override
	public abstract void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource);
	
	@Override
	public abstract void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion);
}
