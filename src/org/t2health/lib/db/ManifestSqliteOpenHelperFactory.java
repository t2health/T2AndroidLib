package org.t2health.lib.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.t2health.lib.ManifestMetaData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

/**
 * Initializes the database based on configuration from the manifest file.
 * @author robbiev
 *
 */
public class ManifestSqliteOpenHelperFactory implements OpenHelperManager.SqliteOpenHelperFactory {
	private static ManifestSqliteOpenHelperFactory sFactoryInstance;
	
	public static ManifestSqliteOpenHelperFactory getInstance() {
		if(sFactoryInstance == null) {
			sFactoryInstance = new ManifestSqliteOpenHelperFactory();
		}
		return sFactoryInstance;
	}
	
	private ManifestSqliteOpenHelperFactory() {
	}
	
	@Override
	public OrmLiteSqliteOpenHelper getHelper(Context context) {
		OrmLiteSqliteOpenHelper helper;
		String helperClassPath = ManifestMetaData.Database.getOpenHelper(context);
		String databaseName = ManifestMetaData.Database.getName(context);
		int databaseVersion = ManifestMetaData.Database.getVersion(context);
		
		Class<? extends DatabaseOpenHelper> dbHelperClass;
		try {
			dbHelperClass = (Class<? extends DatabaseOpenHelper>) Class.forName(helperClassPath);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(helperClassPath+" does not exist. "+e.getMessage());
		} catch (ClassCastException e) {
			throw new RuntimeException(helperClassPath+" is not an instance of DatabaseOpenHelper");
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
			
			helper = (OrmLiteSqliteOpenHelper)obj;
			return helper;
			
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
	
	private static boolean isConfiguredCache = false;
	private static boolean isConfiguredRun = false;
	/**
	 * Checks that all the classes to support database operations are
	 * present and configured.
	 * @param c
	 * @return
	 */
	public static boolean isClassesConfigured(Context c) {
		if(isConfiguredRun) {
			return isConfiguredCache;
		}
		
		boolean returnValue = false;
		String helperClassPath = ManifestMetaData.Database.getOpenHelper(c);
		Class<? extends DatabaseOpenHelper> dbHelperClass;
		try {
			dbHelperClass = (Class<? extends DatabaseOpenHelper>) Class.forName(helperClassPath);
			returnValue = true;
		} catch (Exception e) {
			returnValue = false;
		}
		
		isConfiguredRun = true;
		isConfiguredCache = returnValue;
		return isConfiguredCache;
	}
}
