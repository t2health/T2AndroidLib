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
package com.j256.ormlite.table;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.JavaxPersistence;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Database table configuration information either supplied by Spring or direct Java wiring or from a
 * {@link DatabaseTable} annotation.
 * 
 * @author graywatson
 */
public class DatabaseTableConfig<T> {

	private Class<T> dataClass;
	private String tableName;
	private List<DatabaseFieldConfig> fieldConfigs;
	private FieldType[] fieldTypes;
	private Constructor<T> constructor;

	public DatabaseTableConfig() {
		// for spring
	}

	/**
	 * Setup a table config associated with the dataClass and field configurations. The table-name will be extracted
	 * from the dataClass.
	 */
	public DatabaseTableConfig(Class<T> dataClass, List<DatabaseFieldConfig> fieldConfigs) {
		this(dataClass, extractTableName(dataClass), fieldConfigs);
	}

	/**
	 * Setup a table config associated with the dataClass, table-name, and field configurations.
	 */
	public DatabaseTableConfig(Class<T> dataClass, String tableName, List<DatabaseFieldConfig> fieldConfigs) {
		this.dataClass = dataClass;
		this.tableName = tableName;
		this.fieldConfigs = fieldConfigs;
	}

	private DatabaseTableConfig(Class<T> dataClass, String tableName, FieldType[] fieldTypes) {
		this.dataClass = dataClass;
		this.tableName = tableName;
		this.fieldTypes = fieldTypes;
	}

	/**
	 * Initialize the class if this is being called with Spring.
	 */
	public void initialize() {
		if (dataClass == null) {
			throw new IllegalStateException("dataClass was never set on " + getClass().getSimpleName());
		}
		if (tableName == null) {
			tableName = extractTableName(dataClass);
		}
	}

	public Class<T> getDataClass() {
		return dataClass;
	}

	// @Required
	public void setDataClass(Class<T> dataClass) {
		this.dataClass = dataClass;
	}

	public String getTableName() {
		return tableName;
	}

	/**
	 * Set the table name which is turned into lowercase. If not specified then the name is gotten from the class name.
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName.toLowerCase();
	}

	public void setFieldConfigs(List<DatabaseFieldConfig> fieldConfigs) {
		this.fieldConfigs = fieldConfigs;
	}

	/**
	 * Extract the field types from the fieldConfigs if they have not already been configured.
	 */
	public void extractFieldTypes(ConnectionSource connectionSource) throws SQLException {
		if (fieldTypes == null) {
			if (fieldConfigs == null) {
				fieldTypes = extractFieldTypes(connectionSource, dataClass, tableName, 0);
			} else {
				fieldTypes = convertFieldConfigs(connectionSource, tableName, fieldConfigs);
			}
		}
	}

	/**
	 * Return the field types associated with this configuration.
	 */
	public FieldType[] getFieldTypes(DatabaseType databaseType) throws SQLException {
		if (fieldTypes == null) {
			throw new SQLException("Field types have not been extracted in table config");
		}
		return fieldTypes;
	}

	public List<DatabaseFieldConfig> getFieldConfigs() {
		return fieldConfigs;
	}

	/**
	 * Return the constructor for this class. If not constructor has been set on the class then it will be found on the
	 * class through reflection.
	 */
	public Constructor<T> getConstructor() {
		if (constructor == null) {
			constructor = findNoArgConstructor(dataClass);
		}
		return constructor;
	}

	// @NotRequired
	public void setConstructor(Constructor<T> constructor) {
		this.constructor = constructor;
	}

	/**
	 * Extract the DatabaseTableConfig for a particular class by looking for class and field annotations. This is used
	 * by internal classes to configure a class.
	 */
	public static <T> DatabaseTableConfig<T> fromClass(ConnectionSource connectionSource, Class<T> clazz)
			throws SQLException {
		return fromClass(connectionSource, clazz, 0);
	}

	/**
	 * This is used by internal methods to configure the database in case we recurse. It should not be called by users
	 * directly. Instead use {@link #fromClass(ConnectionSource, Class)}.
	 * 
	 * @param recurseLevel
	 *            is used to make sure we done get in an infinite recursive loop if a foreign object refers to itself.
	 */
	public static <T> DatabaseTableConfig<T> fromClass(ConnectionSource connectionSource, Class<T> clazz,
			int recurseLevel) throws SQLException {
		String tableName = extractTableName(clazz);
		if (connectionSource.getDatabaseType().isEntityNamesMustBeUpCase()) {
			tableName = tableName.toUpperCase();
		}
		return new DatabaseTableConfig<T>(clazz, tableName, extractFieldTypes(connectionSource, clazz, tableName,
				recurseLevel));
	}

	/**
	 * @param recurseLevel
	 *            is used to make sure we done get in an infinite recursive loop if a foreign object refers to itself.
	 */
	private static <T> FieldType[] extractFieldTypes(ConnectionSource connectionSource, Class<T> clazz,
			String tableName, int recurseLevel) throws SQLException {
		List<FieldType> fieldTypes = new ArrayList<FieldType>();
		for (Class<?> classWalk = clazz; classWalk != null; classWalk = classWalk.getSuperclass()) {
			for (Field field : classWalk.getDeclaredFields()) {
				FieldType fieldType =
						FieldType.createFieldType(connectionSource, tableName, field, clazz, recurseLevel);
				if (fieldType != null) {
					fieldTypes.add(fieldType);
				}
			}
		}
		if (fieldTypes.size() == 0) {
			throw new IllegalArgumentException("No fields have a " + DatabaseField.class.getSimpleName()
					+ " annotation in " + clazz);
		}
		return fieldTypes.toArray(new FieldType[fieldTypes.size()]);
	}

	private static <T> String extractTableName(Class<T> clazz) {
		DatabaseTable databaseTable = clazz.getAnnotation(DatabaseTable.class);
		String name = null;
		if (databaseTable != null && databaseTable.tableName() != null && databaseTable.tableName().length() > 0) {
			name = databaseTable.tableName();
		} else {
			/*
			 * NOTE: to remove javax.persistence usage, comment the following line out
			 */
			name = JavaxPersistence.getEntityName(clazz);
			if (name == null) {
				// if the name isn't specified, it is the class name lowercased
				name = clazz.getSimpleName().toLowerCase();
			}
		}
		return name;
	}

	private FieldType[] convertFieldConfigs(ConnectionSource connectionSource, String tableName,
			List<DatabaseFieldConfig> fieldConfigs) throws SQLException {
		List<FieldType> fieldTypes = new ArrayList<FieldType>();
		for (DatabaseFieldConfig fieldConfig : fieldConfigs) {
			Field field;
			try {
				field = dataClass.getDeclaredField(fieldConfig.getFieldName());
			} catch (Exception e) {
				throw SqlExceptionUtil.create("Could not configure field with name '" + fieldConfig.getFieldName()
						+ "' for " + dataClass, e);
			}
			FieldType fieldType = new FieldType(connectionSource, tableName, field, fieldConfig, dataClass, 0);
			fieldTypes.add(fieldType);
		}
		if (fieldTypes.size() == 0) {
			throw new SQLException("No fields were configured for class " + dataClass);
		}
		return fieldTypes.toArray(new FieldType[fieldTypes.size()]);
	}

	private Constructor<T> findNoArgConstructor(Class<T> dataClass) {
		Constructor<T>[] constructors;
		try {
			@SuppressWarnings("unchecked")
			Constructor<T>[] consts = (Constructor<T>[]) dataClass.getDeclaredConstructors();
			// i do this [grossness] to be able to move the Suppress inside the method
			constructors = consts;
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't lookup declared constructors for " + dataClass, e);
		}
		for (Constructor<T> con : constructors) {
			if (con.getParameterTypes().length == 0) {
				return con;
			}
		}
		throw new IllegalArgumentException("Can't find a no-arg constructor for " + dataClass);
	}
}
