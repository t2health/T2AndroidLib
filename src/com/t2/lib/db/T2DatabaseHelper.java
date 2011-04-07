package com.t2.lib.db;

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
public class T2DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private T2DatabaseHelper(Context context, String dbName, CursorFactory cursorFactory, int dbVersion) {
		super(context, dbName, cursorFactory, dbVersion);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
	}
}
