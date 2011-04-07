package com.t2.lib.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.t2.lib.ManifestMetaData;

/**
 * Initializes the database based on configuration from the manifest file.
 * @author robbiev
 *
 */
public class T2SqliteOpenHelperFactory implements OpenHelperManager.SqliteOpenHelperFactory {
	private Class<T2DatabaseHelper> dbHelperClass;

	public T2SqliteOpenHelperFactory(Class<T2DatabaseHelper> dbHelperClass) {
		this.dbHelperClass = dbHelperClass;
	}
	
	@Override
	public OrmLiteSqliteOpenHelper getHelper(Context context) {
		String databaseName = ManifestMetaData.getString(context, ManifestMetaData.DATABSE_NAME);
		int databaseVersion = ManifestMetaData.getInt(context, ManifestMetaData.DATABASE_VERSION);
		
		if(!isDatabaseConfigured(context) || this.dbHelperClass == null) {
			return null;
		}
		
		try {
			Constructor<?> con = dbHelperClass.getConstructor(
					Context.class,
					String.class,
					CursorFactory.class,
					int.class
			);
			Object obj = con.newInstance(
					context,
					databaseName,
					null,
					databaseVersion
			);
			
			return (OrmLiteSqliteOpenHelper)obj;
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static boolean isDatabaseEnabledRun = false;
	private static boolean isDatabaseEnabled = false;
	/**
	 * Based on the database information from the manifest file, will return 
	 * if the database is configured enough to run.
	 * @param c
	 * @return
	 */
	public static boolean isDatabaseConfigured(Context c) {
		if(isDatabaseEnabledRun) {
			return isDatabaseEnabled;
		}
		
		String databaseName = ManifestMetaData.getString(c, ManifestMetaData.DATABSE_NAME);
		isDatabaseEnabled = databaseName != null && databaseName.trim().length() > 0;
		isDatabaseEnabledRun = true;
		
		return isDatabaseEnabled;
	}
}
