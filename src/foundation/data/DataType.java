package foundation.data;

import java.sql.Types;


public enum DataType {  

	Integer, Double, Float, Long, Decimal, Date, String, Boolean, List, ArrayList, LinkedList, Enum, Unknown;

	public static DataType valueOfTypes(int value) {
		if (Types.BIGINT == value) {
			return Integer;
		}
		else if (Types.SMALLINT == value) {
			return Integer;
		}
		else if (Types.TINYINT == value) {
			return Integer;
		}
		else if (Types.INTEGER == value) {
			return Integer;
		}
		else if (Types.DECIMAL == value) {
			return Decimal;
		}
		else if (Types.NUMERIC == value) {
			return Decimal;
		}	
		else if (Types.DOUBLE == value) {
			return Double;
		}
		else if (Types.FLOAT == value) {
			return Double;
		}
		else if (Types.DATE == value) {
			return Date;
		}	
		else if (Types.TIME == value) {
			return Date;
		}	
		else if (Types.TIMESTAMP == value) {
			return Date;
		}	
		else if (Types.BOOLEAN == value) {
			return Boolean;
		}		
		else if (Types.BIT == value) {
			return Boolean;
		}
		else {
			return String;
		}
	}

	public static DataType valueOfString(String value) {
		if (value == null) {
			return String;
		}
		
		value = value.toLowerCase();
		
		if ("string".equals(value)) {
			return String;
		}
		else if ("char".equals(value)) {
			return String;
		}else if ("enum".equals(value)) {
			return Enum;
		}
		else if ("varchar".equals(value)) {
			return String;
		}
		else if ("vchar".equals(value)) {
			return String;
		}
		else if ("text".equals(value)) {
			return String;
		}
		else if ("int".equals(value)) {
			return Integer;
		}else if ("integer".equals(value)) {
			return Integer;
		}
		else if ("tinyint".equals(value)) {
			return Integer;
		}
		else if ("smallint".equals(value)) {
			return Integer;
		}
		else if ("bigint".equals(value)) {
			return Integer;
		}
		else if ("long".equals(value)) {
			return Long;
		}
		else if ("number".equals(value)) {
			return Double;
		}
		else if ("decimal".equals(value)) {
			return Decimal;
		}
		else if ("float".equals(value)) {
			return Float;
		}
		else if ("double".equals(value)) {
			return Double;
		}
		else if ("date".equals(value)) {
			return Date;
		}
		else if ("time".equals(value)) {
			return Date;
		}
		else if ("datetime".equals(value)) {
			return Date;
		}
		else if ("boolean".equals(value)) {
			return Boolean;
		}else if ("Boolean".equals(value)) {
			return Boolean;
		}
		else if ("List".equals(value)) {
			return List;
		}
		else if ("list".equals(value)) {
			return List;
		}
		else if ("arraylist".equals(value)) {
			return ArrayList;
		}
		else if ("arrayList".equals(value)) {
			return ArrayList;
		}
		else if ("linkedlist".equals(value)) {
			return LinkedList;
		}
		else if ("linkedList".equals(value)) {
			return LinkedList;
		}
		else {
			return Unknown;
		}
	}
	
	public String toJavaScriptType() {
		int no = ordinal();

		if (no == 0) {
			return "string";
		}
		else if (no == 1) {
			return "integer";
		}
		else if (no == 2) {
			return "double";
		}
		else if (no == 3) {
			return "date";
		}
		else if (no == 4) {
			return "boolean";
		}

		return "string";
	}

	public static DataType valueOfSAPTypes(String type) {
		if (type == null) {
			return String;
		}
		
		type = type.trim().toLowerCase();
		
		if ("c".equalsIgnoreCase(type)) {
			return String;
		}
		else if ("n".equalsIgnoreCase(type)) {
			return String;
		}
		else if ("t".equalsIgnoreCase(type)) {
			return String;
		}	
		else if ("d".equalsIgnoreCase(type)) {
			return String;
		}
		else if ("i".equalsIgnoreCase(type)) {
			return Integer;
		}
		else if ("f".equalsIgnoreCase(type)) {
			return Double;
		}
		else if ("p".equalsIgnoreCase(type)) {
			return Double;
		}
		else if ("x".equalsIgnoreCase(type)) {
			return String;
		}
		
		return String;
	}
	
	public int toSQLTypes() {
		if (Integer == this) {
			return Types.INTEGER;
		}
		else if (Double == this) {
			return Types.NUMERIC;
		}	
		else if (Date == this) {
			return Types.DATE;
		}	
		else if (Boolean == this) {
			return Types.BIT;
		}		
		else {
			return Types.VARCHAR;
		}
	}
	public Class<?> getJavaClass() {
		if (Integer.equals(this)) {
			return java.lang.Integer.class;
		}
		else if (Double.equals(this)) {
			return java.lang.Double.class;
		}
		else if (Float.equals(this)) {
			return java.lang.Float.class;
		}
		else if (Decimal.equals(this)) {

			return java.math.BigDecimal.class;
		}
		else if (Date.equals(this)) {
			return java.util.Date.class;
		}
		else if (List.equals(this)) {
			return java.util.List.class;
		}
		else if (Boolean.equals(this)) {
			return java.lang.Boolean.class;
		}
		else {
			return java.lang.String.class;
		}
	}
}
