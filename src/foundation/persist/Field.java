package foundation.persist;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import foundation.config.Configer;
import foundation.data.DataType;
import foundation.util.Util;

public class Field {

	private static Map<Integer, String> sqlTypeMap;
	private static Set<Integer> sizableType;
	private String name;
	private int sqlType;
	private DataType type;
	private int length;
	private boolean key;
	private int nullable;

	static {
		sqlTypeMap = new HashMap<Integer, String>();
		sizableType = new HashSet<Integer>();
		initSQLTypeMap();
	}

	public Field(String name) {
		nullable = 1;
		this.name = name;
	}

	public int getSQLType() {
		return sqlType;
	}

	public boolean isKey() {
		return key;
	}

	public void setType(DataType value) {
		type = value;
		sqlType = value.toSQLTypes();
	}

	public String getName() {
		return name;
	}

	public Object getSQLTypeCode() {
		String result = sqlTypeMap.get(sqlType);

		if (sizableType.contains(sqlType)) {
			result = result + "(" + length + ")";
		}

		return result;
	}

	public Object getSQLNullCode() {
		if (nullable == 0) {
			return "NOT NULL";
		} else {
			return "NULL";
		}
	}

	public DataType getDataType() {
		return type;
	}

	public void setType(int value) {
		sqlType = value;
		type = DataType.valueOfTypes(value);
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setNullable(int nullable) {
		this.nullable = nullable;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("fieldName=").append(name).append(",");
		result.append("dataType=").append(type).append(",");
		result.append("length=").append(length);

		return result.toString();
	}

	public void toString(StringBuilder result) {
		result.append("fieldName=").append(name).append(",");
		result.append("dataType=").append(type).append(",");
		result.append("length=").append(length);
	}

	private static void initSQLTypeMap() {
		sqlTypeMap.put(Types.ARRAY, "ARRAY");
		sqlTypeMap.put(Types.BIGINT, "BIGINT");
		sqlTypeMap.put(Types.BINARY, "BINARY");
		sqlTypeMap.put(Types.BIT, "BIT");
		sqlTypeMap.put(Types.BLOB, "BLOB");
		sqlTypeMap.put(Types.BOOLEAN, "BOOLEAN");
		sqlTypeMap.put(Types.CHAR, "CHAR");
		sqlTypeMap.put(Types.CLOB, "CLOB");
		sqlTypeMap.put(Types.DATALINK, "DATALINK");
		sqlTypeMap.put(Types.DATE, "DATE");
		sqlTypeMap.put(Types.DECIMAL, "DECIMAL");
		sqlTypeMap.put(Types.DISTINCT, "DISTINCT");
		sqlTypeMap.put(Types.DOUBLE, "DOUBLE");
		sqlTypeMap.put(Types.FLOAT, "FLOAT");
		sqlTypeMap.put(Types.INTEGER, "INTEGER");
		sqlTypeMap.put(Types.LONGNVARCHAR, "LONGNVARCHAR");
		sqlTypeMap.put(Types.LONGVARBINARY, "LONGVARBINARY");
		sqlTypeMap.put(Types.LONGVARCHAR, "LONGVARCHAR");
		sqlTypeMap.put(Types.NCHAR, "NCHAR");
		sqlTypeMap.put(Types.NCLOB, "NCLOB");
		sqlTypeMap.put(Types.NULL, "NULL");
		sqlTypeMap.put(Types.NUMERIC, "NUMERIC");
		sqlTypeMap.put(Types.NVARCHAR, "NVARCHAR");
		sqlTypeMap.put(Types.OTHER, "OTHER");
		sqlTypeMap.put(Types.REAL, "REAL");
		sqlTypeMap.put(Types.REF, "REF");
		sqlTypeMap.put(Types.ROWID, "ROWID");
		sqlTypeMap.put(Types.SMALLINT, "SMALLINT");
		sqlTypeMap.put(Types.SQLXML, "SQLXML");
		sqlTypeMap.put(Types.STRUCT, "STRUCT");
		sqlTypeMap.put(Types.TIME, "TIME");
		sqlTypeMap.put(Types.TIMESTAMP, "TIMESTAMP");
		sqlTypeMap.put(Types.TINYINT, "TINYINT");
		sqlTypeMap.put(Types.VARBINARY, "VARBINARY");
		sqlTypeMap.put(Types.VARCHAR, "VARCHAR");

		sizableType.add(Types.ARRAY);
		sizableType.add(Types.CHAR);
		sizableType.add(Types.LONGNVARCHAR);
		sizableType.add(Types.LONGVARBINARY);
		sizableType.add(Types.LONGVARCHAR);
		sizableType.add(Types.NCHAR);
		sizableType.add(Types.NVARCHAR);
		sizableType.add(Types.SQLXML);
		sizableType.add(Types.VARBINARY);
		sizableType.add(Types.VARCHAR);
	}

	public String objectToString(Object obj) throws Exception {
		if (obj == null) {
			return null;
		}

		if (DataType.String == type) {
			return (String) obj;
		} else if (DataType.Integer == type) {
			return String.valueOf(obj);
		} else if (DataType.Double == type) {
			return String.valueOf(obj);
		} else if (DataType.Boolean == type) {
			return String.valueOf(obj);
		} else if (DataType.Date == type) {
			Date date = null;

			if (obj instanceof oracle.sql.TIMESTAMP) {
				oracle.sql.TIMESTAMP timestamp = (oracle.sql.TIMESTAMP) obj;
				date = timestamp.dateValue();
			} else if (obj instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) obj;
				date = new Date(timestamp.getTime());
			} else if (obj instanceof Date) {
				date = (Date) obj;
			}

			if (date != null) {
				return Util.DataTimeToString(date);
			}
		}

		return String.valueOf(obj);
	}

	public boolean objectToBoolean(Object obj) {
		if (obj == null) {
			return false;
		}

		if (DataType.String == type) {
			return Util.stringToBoolean(String.valueOf(obj));
		} else if (DataType.Boolean == type) {
			return (Boolean) obj;
		}

		return false;
	}

	public Integer objectToInteger(Object obj) {
		if (obj == null) {
			return null;
		}

		if (DataType.String == type) {
			return Integer.valueOf((String) obj);
		}
		else if (DataType.Integer == type) {
			try {
				return (Integer) obj;
			}
			catch (Exception e) {
				return Integer.valueOf(String.valueOf(obj));
			}
		} 
		else if (DataType.Double == type) {
			return ((Double) obj).intValue();
		} 
		else if (DataType.Decimal == type) {
			return ((BigDecimal) obj).intValue();
		} 
		else {
			return null;
		}
	}
	
	public BigDecimal objectToBigDecimal(Object obj) {
		if (obj == null) {
			return null;
		}

		if (DataType.String == type) {
			return BigDecimal.valueOf(Double.valueOf((String) obj));
		}
		else if (DataType.Integer == type) {
			return BigDecimal.valueOf((Integer) obj);
		} 
		else if (DataType.Double == type) {
			return BigDecimal.valueOf((Double) obj);
		} 
		else if (DataType.Decimal == type) {
			return (BigDecimal) obj;
		} 
		else {
			return null;
		}
	}

	public Date objectToDate(Object obj) throws ParseException {
		if (obj == null) {
			return null;
		}

		if (obj instanceof Timestamp) {
			Timestamp value = (Timestamp) obj;
			return new Date(value.getTime());
		}

		if (obj instanceof Date) {
			return (Date) obj;
		}

		if (obj instanceof String) {
			String value = (String) obj;
			return Util.StringToDate(value);
		}

		return null;
	}

	public String objectToSchemaString(Object obj) throws Exception {
		if (obj == null) {
			return null;
		}

		if (DataType.String == type) {
			return "\"" + obj.toString() + "\"";
		} else if (DataType.Integer == type) {
			return obj.toString();
		} else if (DataType.Double == type) {
			return obj.toString();
		} else if (DataType.Boolean == type) {
			Boolean bool = (Boolean) obj;
			return String.valueOf(bool);
		} else if (DataType.Date == type) {
			Date date = null;

			if (obj instanceof oracle.sql.TIMESTAMP) {
				oracle.sql.TIMESTAMP timestamp = (oracle.sql.TIMESTAMP) obj;
				date = timestamp.dateValue();
			} else if (obj instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) obj;
				date = new Date(timestamp.getTime());
			} else if (obj instanceof Date) {
				date = (Date) obj;
			}

			if (date != null) {
				return "\"" + Util.DataTimeToString(date) + "\"";
			}

			return "\"" + obj.toString() + "\"";
		}

		return String.valueOf(obj);
	}

	public String objectToJSONSString(Object obj) throws SQLException {
		if (obj == null) {
			return "null";
		}

		if (DataType.String == type) {
			return "\"" + obj.toString().replace("\\", "/") + "\"";
		} else if (DataType.Integer == type) {
			return obj.toString();
		} else if (DataType.Double == type) {
			return obj.toString();
		} else if (DataType.Decimal == type) {
			return obj.toString();
		} else if (DataType.Boolean == type) {
			Boolean bool = (Boolean) obj;
			return String.valueOf(bool);
		} else if (DataType.Date == type) {
			Date date = null;

			if (obj instanceof oracle.sql.TIMESTAMP) {
				oracle.sql.TIMESTAMP timestamp = (oracle.sql.TIMESTAMP) obj;
				date = timestamp.dateValue();
			} else if (obj instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) obj;
				date = new Date(timestamp.getTime());
			} else if (obj instanceof Date) {
				date = (Date) obj;
			}

			if (date != null) {
				return "\"" + Util.DataTimeToString(date) + "\"";
			}
		}

		return "\"" + obj.toString() + "\"";
	}

	public String objectToSQLString(Object obj) throws Exception {
		if (obj == null) {
			return "null";
		}

		if (DataType.String == type) {
			return "'" + obj.toString() + "'";
		} else if (DataType.Integer == type) {
			return obj.toString();
		} else if (DataType.Double == type) {
			return obj.toString();
		} else if (DataType.Boolean == type) {
			if (Configer.getDataBaseType().compareTo(DataBaseType.SQLServer) == 0) {
				if (obj.equals("true")) {
					return String.valueOf(1);
				}
				else {
					return String.valueOf(0);
				}
			}
			
			Boolean bool = (Boolean) obj;
			return String.valueOf(bool);
		} else if (DataType.Date == type) {
			Date date = null;

			if (obj instanceof oracle.sql.TIMESTAMP) {
				oracle.sql.TIMESTAMP timestamp = (oracle.sql.TIMESTAMP) obj;
				date = timestamp.dateValue();
			} else if (obj instanceof Timestamp) {
				Timestamp timestamp = (Timestamp) obj;
				date = new Date(timestamp.getTime());
			} else if (obj instanceof Date) {
				date = (Date) obj;
			} else if (obj instanceof String) {
				date = Util.StringToDate((String) obj);
			}

			if (date != null) {
				return Util.newDBDateString(date);
			}
		}

		return "'" + obj.toString() + "'";
	}

	public Object stringToObject(String value) throws ParseException {
		if (Util.isEmptyStr(value)) {
			return null;
		}

		if (DataType.String == type) {
			return value;
		} else if (DataType.Integer == type) {
			return Integer.valueOf(value);
		} else if (DataType.Double == type) {
			return Double.valueOf(value);
		} else if (DataType.Date == type) {
			return Util.StringToDate(value);
		}

		return value;
	}

	public void setValueToStatement(Object obj, PreparedStatement stmt, int paramIdx) throws SQLException {
		stmt.setObject(paramIdx, obj, sqlType);
	}

}
