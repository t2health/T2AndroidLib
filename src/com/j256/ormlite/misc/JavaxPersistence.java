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
package com.j256.ormlite.misc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;

/**
 * Class for isolating the detection of the javax.persistence annotations. This used to be a hard dependency but it has
 * become optinal/test since we use reflection here.
 * 
 * @author graywatson
 */
public class JavaxPersistence {

	/**
	 * Create a field config from the javax.persistence annotations associated with the field argument. Returns null if
	 * none.
	 */
	public static DatabaseFieldConfig createFieldConfig(DatabaseType databaseType, Field field) throws SQLException {
		Annotation columnAnnotation = null;
		Annotation idAnnotation = null;
		Annotation generatedValueAnnotation = null;
		Annotation oneToOneAnnotation = null;
		Annotation manyToOneAnnotation = null;

		for (Annotation annotation : field.getAnnotations()) {
			Class<?> annotationClass = annotation.annotationType();
			if (annotationClass.getName().equals("javax.persistence.Column")) {
				columnAnnotation = annotation;
			} else if (annotationClass.getName().equals("javax.persistence.Id")) {
				idAnnotation = annotation;
			} else if (annotationClass.getName().equals("javax.persistence.GeneratedValue")) {
				generatedValueAnnotation = annotation;
			} else if (annotationClass.getName().equals("javax.persistence.OneToOne")) {
				oneToOneAnnotation = annotation;
			} else if (annotationClass.getName().equals("javax.persistence.ManyToOne")) {
				manyToOneAnnotation = annotation;
			}
		}

		if (columnAnnotation == null && idAnnotation == null && oneToOneAnnotation == null
				&& manyToOneAnnotation == null) {
			return null;
		}

		DatabaseFieldConfig config = new DatabaseFieldConfig();
		String fieldName = field.getName();
		if (databaseType.isEntityNamesMustBeUpCase()) {
			fieldName = fieldName.toUpperCase();
		}
		config.setFieldName(fieldName);

		if (columnAnnotation != null) {
			try {
				Method method = columnAnnotation.getClass().getMethod("name");
				String name = (String) method.invoke(columnAnnotation);
				if (name != null && name.length() > 0) {
					config.setColumnName(name);
				}
				method = columnAnnotation.getClass().getMethod("length");
				config.setWidth((Integer) method.invoke(columnAnnotation));
				method = columnAnnotation.getClass().getMethod("nullable");
				config.setCanBeNull((Boolean) method.invoke(columnAnnotation));
				method = columnAnnotation.getClass().getMethod("unique");
				config.setUnique((Boolean) method.invoke(columnAnnotation));
			} catch (Exception e) {
				throw SqlExceptionUtil.create("Problem accessing fields from the Column annotation for field " + field,
						e);
			}
		}
		if (idAnnotation != null) {
			if (generatedValueAnnotation == null) {
				config.setId(true);
			} else {
				// generatedValue only works if it is also an id according to {@link GeneratedValue)
				config.setGeneratedId(true);
			}
		}
		// foreign values are always ones we can't map as primitives (or Strings)
		config.setForeign(oneToOneAnnotation != null || manyToOneAnnotation != null);
		config.setDataType(DataType.lookupClass(field.getType()));
		config.setUseGetSet(DatabaseFieldConfig.findGetMethod(field, false) != null
				&& DatabaseFieldConfig.findSetMethod(field, false) != null);
		return config;
	}

	/**
	 * Return the javax.persistence.Entity annotation name for the class argument or null if none or if there was no
	 * entity name.
	 */
	public static String getEntityName(Class<?> clazz) {
		Annotation entityAnnotation = null;
		for (Annotation annotation : clazz.getAnnotations()) {
			Class<?> annotationClass = annotation.annotationType();
			if (annotationClass.getName().equals("javax.persistence.Entity")) {
				entityAnnotation = annotation;
			}
		}

		if (entityAnnotation == null) {
			return null;
		}
		try {
			Method method = entityAnnotation.getClass().getMethod("name");
			String name = (String) method.invoke(entityAnnotation);
			if (name != null && name.length() > 0) {
				return name;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new IllegalStateException("Could not get entity name from class " + clazz, e);
		}
	}
}
