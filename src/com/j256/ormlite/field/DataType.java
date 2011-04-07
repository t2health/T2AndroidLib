package com.j256.ormlite.field;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.DatabaseResults;

/**
 * Data type enumeration to provide Java class to/from database mapping.
 * 
 * <p>
 * <b>NOTE:</b> If you add types here you will need to add to the various DatabaseType implementors' appendColumnArg()
 * method.
 * </p>
 * 
 * <p>
 * Here's a good page about the <a href="http://docs.codehaus.org/display/CASTOR/Type+Mapping" >mapping for a number of
 * database types</a>:
 * </p>
 * 
 * @author graywatson
 */
public enum DataType implements FieldConverter {

	/**
	 * Persists the {@link String} Java class.
	 */
	STRING(SqlType.STRING, new Class<?>[] { String.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return results.getString(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return defaultStr;
		}
	},

	/**
	 * Persists the {@link String} Java class.
	 */
	LONG_STRING(SqlType.LONG_STRING, new Class<?>[0]) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return results.getString(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return defaultStr;
		}
		@Override
		public boolean isAppropriateId() {
			return false;
		}
	},

	/**
	 * Persists the {@link String} Java class.
	 */
	STRING_BYTES(SqlType.BYTE_ARRAY, new Class<?>[0]) {
		private static final String DEFAULT_STRING_BYTES_CHARSET_NAME = "Unicode";
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			byte[] bytes = results.getBytes(columnPos);
			if (bytes == null) {
				return null;
			}
			String charsetName = getCharsetName(fieldType);
			try {
				// NOTE: I can't use new String(bytes, Charset) because it was introduced in 1.6.
				return new String(bytes, charsetName);
			} catch (UnsupportedEncodingException e) {
				throw SqlExceptionUtil.create("Could not convert string with charset name: " + charsetName, e);
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			throw new SQLException("String bytes type cannot have default values");
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
			String string = (String) javaObject;
			String charsetName = getCharsetName(fieldType);
			try {
				// NOTE: I can't use string.getBytes(Charset) because it was introduced in 1.6.
				return string.getBytes(charsetName);
			} catch (UnsupportedEncodingException e) {
				throw SqlExceptionUtil.create("Could not convert string with charset name: " + charsetName, e);
			}
		}
		@Override
		public boolean isAppropriateId() {
			return false;
		}
		@Override
		public boolean isSelectArgRequired() {
			return true;
		}
		private String getCharsetName(FieldType fieldType) {
			if (fieldType == null || fieldType.getFormat() == null) {
				return DEFAULT_STRING_BYTES_CHARSET_NAME;
			} else {
				return fieldType.getFormat();
			}
		}
	},

	/**
	 * Persists the boolean Java primitive.
	 */
	BOOLEAN(SqlType.BOOLEAN, new Class<?>[] { boolean.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Boolean) results.getBoolean(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Boolean.parseBoolean(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public boolean isAppropriateId() {
			return false;
		}
	},

	/**
	 * Persists the {@link Boolean} Java class.
	 */
	BOOLEAN_OBJ(SqlType.BOOLEAN, new Class<?>[] { Boolean.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Boolean) results.getBoolean(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Boolean.parseBoolean(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isAppropriateId() {
			return false;
		}
	},

	/**
	 * Persists the {@link java.util.Date} Java class.
	 * 
	 * <p>
	 * NOTE: This is <i>not</i> the same as the {@link java.sql.Date} class.
	 * </p>
	 */
	DATE(SqlType.DATE, new Class<?>[] { Date.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			Timestamp timeStamp = results.getTimestamp(columnPos);
			if (timeStamp == null) {
				return null;
			} else {
				return new Date(timeStamp.getTime());
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			DateStringFormatConfig dateFormatConfig = convertDateStringConfig(fieldType);
			try {
				return new Timestamp(parseDateString(dateFormatConfig, defaultStr).getTime());
			} catch (ParseException e) {
				throw SqlExceptionUtil.create("Problems parsing default date string '" + defaultStr + "' using '"
						+ dateFormatConfig + '\'', e);
			}
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
			Date date = (Date) javaObject;
			return new Timestamp(date.getTime());
		}
		@Override
		public boolean isSelectArgRequired() {
			return true;
		}
	},

	/**
	 * @deprecated You should use {@link DataType#DATE}
	 */
	@Deprecated
	JAVA_DATE(SqlType.DATE, new Class<?>[] { Date.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			Timestamp timeStamp = results.getTimestamp(columnPos);
			if (timeStamp == null) {
				return null;
			} else {
				return new Date(timeStamp.getTime());
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			DateStringFormatConfig dateFormatConfig = convertDateStringConfig(fieldType);
			try {
				return new Timestamp(parseDateString(dateFormatConfig, defaultStr).getTime());
			} catch (ParseException e) {
				throw SqlExceptionUtil.create("Problems parsing default date string '" + defaultStr + "' using '"
						+ dateFormatConfig + '\'', e);
			}
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
			Date date = (Date) javaObject;
			return new Timestamp(date.getTime());
		}
		@Override
		public boolean isSelectArgRequired() {
			return true;
		}
	},

	/**
	 * Persists the {@link java.util.Date} Java class as long milliseconds since epoch.
	 * 
	 * <p>
	 * NOTE: This is <i>not</i> the same as the {@link java.sql.Date} class.
	 * </p>
	 */
	DATE_LONG(SqlType.LONG, new Class<?>[0]) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return new Date(results.getLong(columnPos));
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			try {
				return Long.parseLong(defaultStr);
			} catch (NumberFormatException e) {
				throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default date-long value: "
						+ defaultStr, e);
			}
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) {
			Date date = (Date) obj;
			return (Long) date.getTime();
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
	},

	/**
	 * @deprecated You should use {@link DataType#DATE_LONG}
	 */
	@Deprecated
	JAVA_DATE_LONG(SqlType.LONG, new Class<?>[0]) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return new Date(results.getLong(columnPos));
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			try {
				return Long.parseLong(defaultStr);
			} catch (NumberFormatException e) {
				throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default date-long value: "
						+ defaultStr, e);
			}
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) {
			Date date = (Date) obj;
			return (Long) date.getTime();
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
	},

	/**
	 * Persists the {@link java.util.Date} Java class as a string of a format.
	 * 
	 * <p>
	 * NOTE: This is <i>not</i> the same as the {@link java.sql.Date} class.
	 * </p>
	 * 
	 * <p>
	 * <b>WARNING:</b> Because of SimpleDateFormat not being reentrant, this has to do some synchronization with every
	 * data in/out unfortunately.
	 * </p>
	 */
	DATE_STRING(SqlType.STRING, new Class<?>[0]) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			String dateStr = results.getString(columnPos);
			if (dateStr == null) {
				return null;
			}
			DateStringFormatConfig formatConfig = convertDateStringConfig(fieldType);
			try {
				return parseDateString(formatConfig, dateStr);
			} catch (ParseException e) {
				throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing date-string '" + dateStr
						+ "' using '" + formatConfig + "'", e);
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			DateStringFormatConfig formatConfig = convertDateStringConfig(fieldType);
			try {
				// we parse to make sure it works and then format it again
				return normalizeDateString(formatConfig, defaultStr);
			} catch (ParseException e) {
				throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default date-string '"
						+ defaultStr + "' using '" + formatConfig + "'", e);
			}
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) {
			Date date = (Date) obj;
			return formatDate(convertDateStringConfig(fieldType), date);
		}
		@Override
		public Object makeConfigObject(FieldType fieldType) {
			String format = fieldType.getFormat();
			if (format == null) {
				return defaultDateFormatConfig;
			} else {
				return new DateStringFormatConfig(format);
			}
		}
	},

	/**
	 * @deprecated You should use {@link DataType#DATE_STRING}
	 */
	@Deprecated
	JAVA_DATE_STRING(SqlType.STRING, new Class<?>[0]) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			String dateStr = results.getString(columnPos);
			if (dateStr == null) {
				return null;
			}
			DateStringFormatConfig formatConfig = convertDateStringConfig(fieldType);
			try {
				return parseDateString(formatConfig, dateStr);
			} catch (ParseException e) {
				throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing date-string '" + dateStr
						+ "' using '" + formatConfig + "'", e);
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			DateStringFormatConfig formatConfig = convertDateStringConfig(fieldType);
			try {
				// we parse to make sure it works and then format it again
				return normalizeDateString(formatConfig, defaultStr);
			} catch (ParseException e) {
				throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default date-string '"
						+ defaultStr + "' using '" + formatConfig + "'", e);
			}
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) {
			Date date = (Date) obj;
			DateStringFormatConfig formatConfig = convertDateStringConfig(fieldType);
			return formatDate(formatConfig, date);
		}
		@Override
		public Object makeConfigObject(FieldType fieldType) {
			String format = fieldType.getFormat();
			if (format == null) {
				return defaultDateFormatConfig;
			} else {
				return new DateStringFormatConfig(format);
			}
		}
	},

	/**
	 * Persists the byte primitive.
	 */
	BYTE(SqlType.BYTE, new Class<?>[] { byte.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Byte) results.getByte(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Byte.parseByte(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
	},

	/**
	 * Persists the byte[] array type.
	 */
	BYTE_ARRAY(SqlType.BYTE_ARRAY, new Class<?>[0]) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (byte[]) results.getBytes(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			throw new SQLException("byte[] type cannot have default values");
		}
		@Override
		public boolean isAppropriateId() {
			return false;
		}
		@Override
		public boolean isSelectArgRequired() {
			return true;
		}
	},

	/**
	 * Persists the {@link Byte} Java class.
	 */
	BYTE_OBJ(SqlType.BYTE, new Class<?>[] { Byte.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Byte) results.getByte(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Byte.parseByte(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
	},

	/**
	 * Persists the short primitive.
	 */
	SHORT(SqlType.SHORT, new Class<?>[] { short.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Short) results.getShort(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Short.parseShort(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
	},

	/**
	 * Persists the {@link Short} Java class.
	 */
	SHORT_OBJ(SqlType.SHORT, new Class<?>[] { Short.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Short) results.getShort(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Short.parseShort(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
	},

	/**
	 * Persists the int primitive.
	 */
	INTEGER(SqlType.INTEGER, new Class<?>[] { int.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Integer) results.getInt(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Integer.parseInt(defaultStr);
		}
		@Override
		public Object convertIdNumber(Number number) {
			return (Integer) number.intValue();
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public boolean isValidGeneratedType() {
			return true;
		}
	},

	/**
	 * Persists the {@link Integer} Java class.
	 */
	INTEGER_OBJ(SqlType.INTEGER, new Class<?>[] { Integer.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Integer) results.getInt(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Integer.parseInt(defaultStr);
		}
		@Override
		public Object convertIdNumber(Number number) {
			return (Integer) number.intValue();
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isValidGeneratedType() {
			return true;
		}
	},

	/**
	 * Persists the long primitive.
	 */
	LONG(SqlType.LONG, new Class<?>[] { long.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Long) results.getLong(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Long.parseLong(defaultStr);
		}
		@Override
		public Object convertIdNumber(Number number) {
			return (Long) number.longValue();
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
		@Override
		public boolean isValidGeneratedType() {
			return true;
		}
	},

	/**
	 * Persists the {@link Long} Java class.
	 */
	LONG_OBJ(SqlType.LONG, new Class<?>[] { Long.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Long) results.getLong(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Long.parseLong(defaultStr);
		}
		@Override
		public Object convertIdNumber(Number number) {
			return (Long) number.longValue();
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isValidGeneratedType() {
			return true;
		}
	},

	/**
	 * Persists the float primitive.
	 */
	FLOAT(SqlType.FLOAT, new Class<?>[] { float.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Float) results.getFloat(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Float.parseFloat(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
	},

	/**
	 * Persists the {@link Float} Java class.
	 */
	FLOAT_OBJ(SqlType.FLOAT, new Class<?>[] { Float.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Float) results.getFloat(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Float.parseFloat(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
	},

	/**
	 * Persists the double primitive.
	 */
	DOUBLE(SqlType.DOUBLE, new Class<?>[] { double.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Double) results.getDouble(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Double.parseDouble(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isPrimitive() {
			return true;
		}
	},

	/**
	 * Persists the {@link Double} Java class.
	 */
	DOUBLE_OBJ(SqlType.DOUBLE, new Class<?>[] { Double.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			return (Double) results.getDouble(columnPos);
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Double.parseDouble(defaultStr);
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
	},

	/**
	 * Persists an unknown Java Object that is serializable.
	 */
	SERIALIZABLE(SqlType.SERIALIZABLE, new Class<?>[0]) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			byte[] bytes = results.getBytes(columnPos);
			// need to do this check because we are a stream type
			if (bytes == null) {
				return null;
			}
			try {
				ObjectInputStream objInStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
				return objInStream.readObject();
			} catch (Exception e) {
				throw SqlExceptionUtil.create(
						"Could not read serialized object from byte array: " + Arrays.toString(bytes) + "(len "
								+ bytes.length + ")", e);
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			throw new SQLException("Default values for serializable types are not supported");
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) throws SQLException {
			try {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				ObjectOutputStream objOutStream = new ObjectOutputStream(outStream);
				objOutStream.writeObject(obj);
				return outStream.toByteArray();
			} catch (Exception e) {
				throw SqlExceptionUtil.create("Could not write serialized object to byte array: " + obj, e);
			}
		}
		@Override
		public boolean isValidForType(Class<?> fieldClass) {
			return Serializable.class.isAssignableFrom(fieldClass);
		}
		@Override
		public boolean isStreamType() {
			// can't do a getObject call beforehand so we have to check for nulls
			return true;
		}
		@Override
		public boolean isComparable() {
			return false;
		}
		@Override
		public boolean isAppropriateId() {
			return false;
		}
		@Override
		public boolean isSelectArgRequired() {
			return true;
		}
	},

	/**
	 * Persists an Enum Java class as its string value. You can also specify the {@link #ENUM_INTEGER} as the type.
	 */
	ENUM_STRING(SqlType.STRING, new Class<?>[] { Enum.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			String val = results.getString(columnPos);
			if (fieldType == null) {
				return val;
			} else if (val == null) {
				return null;
			}
			@SuppressWarnings("unchecked")
			Map<String, Enum<?>> enumStringMap = (Map<String, Enum<?>>) fieldType.getDataTypeConfigObj();
			if (enumStringMap == null) {
				return enumVal(fieldType, val, null, fieldType.getUnknownEnumVal());
			} else {
				return enumVal(fieldType, val, enumStringMap.get(val), fieldType.getUnknownEnumVal());
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return defaultStr;
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) {
			Enum<?> enumVal = (Enum<?>) obj;
			return enumVal.name();
		}
		@Override
		public Object makeConfigObject(FieldType fieldType) throws SQLException {
			Map<String, Enum<?>> enumStringMap = new HashMap<String, Enum<?>>();
			Enum<?>[] constants = (Enum<?>[]) fieldType.getFieldType().getEnumConstants();
			if (constants == null) {
				throw new SQLException("Field " + fieldType + " improperly configured as type " + this);
			}
			for (Enum<?> enumVal : constants) {
				enumStringMap.put(enumVal.name(), enumVal);
			}
			return enumStringMap;
		}
	},

	/**
	 * Persists an Enum Java class as its ordinal interger value. You can also specify the {@link #ENUM_STRING} as the
	 * type.
	 */
	ENUM_INTEGER(SqlType.INTEGER, new Class<?>[] { Enum.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			int val = results.getInt(columnPos);
			if (fieldType == null) {
				return val;
			}
			// do this once
			Integer valInteger = val;
			@SuppressWarnings("unchecked")
			Map<Integer, Enum<?>> enumIntMap = (Map<Integer, Enum<?>>) fieldType.getDataTypeConfigObj();
			if (enumIntMap == null) {
				return enumVal(fieldType, valInteger, null, fieldType.getUnknownEnumVal());
			} else {
				return enumVal(fieldType, valInteger, enumIntMap.get(valInteger), fieldType.getUnknownEnumVal());
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return Integer.parseInt(defaultStr);
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) {
			Enum<?> enumVal = (Enum<?>) obj;
			return (Integer) enumVal.ordinal();
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public Object makeConfigObject(FieldType fieldType) throws SQLException {
			Map<Integer, Enum<?>> enumIntMap = new HashMap<Integer, Enum<?>>();
			Enum<?>[] constants = (Enum<?>[]) fieldType.getFieldType().getEnumConstants();
			if (constants == null) {
				throw new SQLException("Field " + fieldType + " improperly configured as type " + this);
			}
			for (Enum<?> enumVal : constants) {
				enumIntMap.put(enumVal.ordinal(), enumVal);
			}
			return enumIntMap;
		}
	},

	/**
	 * Persists the {@link java.util.UUID} Java class.
	 */
	UUID(SqlType.STRING, new Class<?>[] { UUID.class }) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
			String uuidStr = results.getString(columnPos);
			if (uuidStr == null) {
				return null;
			}
			try {
				return java.util.UUID.fromString(uuidStr);
			} catch (IllegalArgumentException e) {
				throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing UUID-string '" + uuidStr
						+ "'", e);
			}
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
			try {
				return java.util.UUID.fromString(defaultStr);
			} catch (IllegalArgumentException e) {
				throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default UUID-string '"
						+ defaultStr + "'", e);
			}
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) {
			UUID uuid = (UUID) obj;
			return uuid.toString();
		}
		@Override
		boolean isValidGeneratedType() {
			return true;
		}
		@Override
		public boolean isSelfGeneratedId() {
			return true;
		}
		@Override
		public Object generatedId() {
			return java.util.UUID.randomUUID();
		}
	},

	/**
	 * Marker for fields that are unknown.
	 */
	UNKNOWN(SqlType.UNKNOWN, new Class<?>[0]) {
		@Override
		public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) {
			return null;
		}
		@Override
		public Object parseDefaultString(FieldType fieldType, String defaultStr) {
			return null;
		}
		@Override
		public Object javaToSqlArg(FieldType fieldType, Object obj) {
			return null;
		}
		@Override
		public boolean isAppropriateId() {
			return false;
		}
		@Override
		public boolean isComparable() {
			return false;
		}
		@Override
		public boolean isEscapedValue() {
			return false;
		}
		@Override
		public boolean isEscapedDefaultValue() {
			return false;
		}
	},
	// end
	;

	public static final DateStringFormatConfig defaultDateFormatConfig = new DateStringFormatConfig(
			"yyyy-MM-dd HH:mm:ss.SSSSSS");

	private final SqlType sqlType;
	private final Class<?>[] classes;

	private DataType(SqlType sqlType, Class<?>[] classes) {
		this.sqlType = sqlType;
		// only types which have overridden the convertNumber method can be generated
		this.classes = classes;
	}

	/**
	 * Static method that returns the DataType associated with the class argument or {@link #UNKNOWN} if not found.
	 */
	public static DataType lookupClass(Class<?> dataClass) throws SQLException {
		for (DataType dataType : values()) {
			// build a static map from class to associated type
			for (Class<?> dataTypeClass : dataType.classes) {
				if (dataTypeClass == dataClass) {
					return dataType;
				}
			}
		}
		if (dataClass.isEnum()) {
			// special handling of the Enum type
			return ENUM_STRING;
		} else {
			return UNKNOWN;
		}
	}

	public abstract Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos)
			throws SQLException;

	public abstract Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException;

	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		// noop pass-thru is the default
		return javaObject;
	}

	/**
	 * This makes a configuration object for the data-type or returns null if none. The object can be accessed later via
	 * {@link FieldType#getDataTypeConfigObj()}.
	 */
	public Object makeConfigObject(FieldType fieldType) throws SQLException {
		return null;
	}

	public SqlType getSqlType() {
		return sqlType;
	}

	public boolean isStreamType() {
		return false;
	}

	/**
	 * Convert a {@link Number} object to its primitive object suitable for assigning to an ID field.
	 */
	Object convertIdNumber(Number number) {
		// by default the type cannot convert an id number
		return null;
	}

	/**
	 * Return true if this type can be auto-generated by the database. Probably only numbers will return true.
	 */
	boolean isValidGeneratedType() {
		return false;
	}

	/**
	 * Return true if the fieldClass is appropriate for this enum.
	 */
	boolean isValidForType(Class<?> fieldClass) {
		// by default this is a noop
		return true;
	}

	/**
	 * Return whether this field's default value should be escaped in SQL.
	 */
	boolean isEscapedDefaultValue() {
		// default is to not escape the type if it is a number
		return isEscapedValue();
	}

	/**
	 * Return whether this field is a number.
	 */
	boolean isEscapedValue() {
		return true;
	}

	/**
	 * Return whether this field is a primitive type or not. This is used to know if we should throw if the field value
	 * is null.
	 */
	boolean isPrimitive() {
		return false;
	}

	/**
	 * Return true if this data type be compared in SQL statements.
	 */
	boolean isComparable() {
		return true;
	}

	/**
	 * Return true if this data type can be an id column in a class.
	 */
	boolean isAppropriateId() {
		return true;
	}

	/**
	 * Must use SelectArg when querying for values of this type.
	 */
	boolean isSelectArgRequired() {
		return false;
	}

	/**
	 * Return true if this type creates its own generated ids else false to have the database do it.
	 */
	public boolean isSelfGeneratedId() {
		return false;
	}

	/**
	 * Return a generated id if appropriate or null if none.
	 */
	public Object generatedId() {
		return null;
	}

	private static Date parseDateString(DateStringFormatConfig formatConfig, String dateStr) throws ParseException {
		DateFormat dateFormat = formatConfig.getDateFormat();
		return dateFormat.parse(dateStr);
	}

	private static DateStringFormatConfig convertDateStringConfig(FieldType fieldType) {
		if (fieldType == null) {
			return defaultDateFormatConfig;
		}
		DateStringFormatConfig configObj = (DateStringFormatConfig) fieldType.getDataTypeConfigObj();
		if (configObj == null) {
			return defaultDateFormatConfig;
		} else {
			return (DateStringFormatConfig) configObj;
		}
	}

	private static String normalizeDateString(DateStringFormatConfig formatConfig, String dateStr)
			throws ParseException {
		DateFormat dateFormat = formatConfig.getDateFormat();
		Date date = dateFormat.parse(dateStr);
		return dateFormat.format(date);
	}

	private static String formatDate(DateStringFormatConfig formatConfig, Date date) {
		DateFormat dateFormat = formatConfig.getDateFormat();
		return dateFormat.format(date);
	}

	private static Enum<?> enumVal(FieldType fieldType, Object val, Enum<?> enumVal, Enum<?> unknownEnumVal)
			throws SQLException {
		if (enumVal != null) {
			return enumVal;
		} else if (unknownEnumVal == null) {
			throw new SQLException("Cannot get enum value of '" + val + "' for field " + fieldType);
		} else {
			return unknownEnumVal;
		}
	}

	/**
	 * Configuration information for the {@link #DATE_STRING} type.
	 */
	private static class DateStringFormatConfig {
		private final ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();
		final String dateFormatStr;
		public DateStringFormatConfig(String dateFormatStr) {
			this.dateFormatStr = dateFormatStr;
		}
		public DateFormat getDateFormat() {
			DateFormat dateFormat = threadLocal.get();
			if (dateFormat == null) {
				dateFormat = new SimpleDateFormat(dateFormatStr);
				threadLocal.set(dateFormat);
			}
			return dateFormat;
		}
		@Override
		public String toString() {
			return dateFormatStr;
		}
	}
}
