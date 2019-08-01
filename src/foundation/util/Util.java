package foundation.util;

import bi.AggConstant;
import foundation.config.Configer;
import foundation.data.DataType;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataBaseType;
import foundation.persist.DataHandler;
import foundation.persist.SqlSession;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.user.OnlineUser;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {
	
	public static final String TRUE = "T"; 
	public static final String FALSE = "F"; 
	public static final String String_Return = "\r\n";
	public static final String String_Escape_newSpace = "\t";
	public static final String String_Escape_newLine = "\n";
	public static final String String_Empty = "";
	public static final String Sql_Empty = "''";
	public static final String Default_Patter = "(?<=@\\{)(.+?)(?=\\})";
	public static final String Integer_Patter = "^-?[1-9]\\d*$";
	public static final String Double_Patter = "^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$";
	public static final String Separator = "-";
	public static final String SubSeparator = "_";
	public static final String comma = " , ";
	public static final String with = "&";
	public static final String dollar = "$";
	public static final String Star = "*";
	public static final String RMB = "￥";
	public static final String ait = "@";
	public static final String semicolon = ";";
	public static final String defaultFilter = " 1 = 1 ";
	public static final String Dot = ".";
	public static final String Spilt_Dot = "[.]";
	public static final String Spilt_Line = "\\|";
	public static final String Spilt_Star = "\\*";
	public static final String Spilt_Slash = "\\\\";
	public static final String Spilt_Brackets = "\\[\\]";
	public static final String Equal = "=";
	public static final String And = " and ";
	public static final String Or = " or ";
	public static final String Standrad_And = "and";
	public static final String Standrad_Or = "or";
	public static final String Percentage = "%";
	public static String like = " like ";
	public static String Standrad_like = "like";
	public static String unEqual = " <> ";
	public static String Standrad_unEqual = "<>";
	public static final String String_Space = " ";
	public static final String windows_slash = "/";
	public static final String java_slash = "\\";
	public static final String sql_slash = "\\\\";

	public static int field;
    private static  ArrayList<String> tmpArr = new ArrayList<String>();


	public static String newShortGUID() {
		UUID uuid = UUID.randomUUID();
		String strGUID;
		String shortGUID;

		strGUID = uuid.toString();
		shortGUID = strGUID.substring(0, 8) + strGUID.substring(9, 13) + strGUID.substring(14, 18)
						+ strGUID.substring(19, 23) + strGUID.substring(24, 36);

		return shortGUID;
	}

	public static String filePathJion(String preFilePath, String filePath) {
		if (preFilePath == null || filePath == null) {
			return  null;
		}
		preFilePath = preFilePath.replace(windows_slash,java_slash);
		filePath = filePath.replace(windows_slash,java_slash);
		return  preFilePath + java_slash + filePath;
	}

	public static String stringJoin(String... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s: strings) {
			stringBuilder.append(s);
		}
		return  stringBuilder.toString();
	}

	public static String quotedStr(String str) {
		if (str != null)
			return "'" + str + "'";
		else
			return "''";
	}
	
	public static String doubleQuotedStr(String str) {
		if (str != null)
			return "\"" + str + "\"";
		else
			return "\"\"";
	}
	
	public static String quotedLikeStr(String str) {
		if (str != null)
			return "'%" + str + "%'";
		else
			return "''";
	}
	public static String quotedLeftLikeStr(String str) {
		if (str != null)
			return "'%" + str + "'";
		else
			return "''";
	}
	public static String quotedRightLikeStr(String str) {
		if (str != null)
			return "'" + str + "'";
		else
			return "''";
	}
	
	public static String quotedEqualStr(String key, String value) {
		if (!Util.isEmptyStr(key) && !Util.isEmptyStr(value))
			return key + String_Space + Equal + String_Space + Util.quotedStr(value);
		else
			return null;
	}
	
	public static String bracketStr(String str) {
		if (str != null)
			return "(" + str + ")";
		else
			return "()";
	}

	public static String getDBUUID() {
		DataBaseType dbType = Configer.getDataBaseType();
		
		if (DataBaseType.Oracle == dbType) {
			return newOracleUUID();
		}
		else if (DataBaseType.SQLServer == dbType) {
			return newSqlServerUUID();
		}
		else if (DataBaseType.MySQL == dbType) {
			return newMySqlUUID();
		}
		else {
			return newShortGUID();
		}
	}
	
	private static String newMySqlUUID() {
		return "replace(uuid(),'-','')";
	}

	private static String newSqlServerUUID() {
		return "replace(NEWID(),'-', '')";
	}

	private static String newOracleUUID() {

		return "lower(sys_guid())";
	}

	public static String newDBDateString() throws Exception {
		Date date = new Date();
		return newDBDateString(date);
	}
	
	public static String newDBDateString(Date date) throws Exception {
		DataBaseType dbType = Configer.getDataBaseType();
		
		if (DataBaseType.Oracle == dbType) {
			return newOracleDateString(date);
		}
		else if (DataBaseType.SQLServer == dbType) {
			return newSqlServerDateString(date);
		}
		else if (DataBaseType.MySQL == dbType) {
			return newMySqlDateString(date);
		}
		else {
			return DataTimeToString(date);
		}
	}
	
	public static String toOracleDataStr(String dataStr) {
		return "to_date('" + dataStr + "','YYYY-MM-DD HH24:MI:SS')";
	}

	public static String toMySQLDateStr(Date value) {
		return DataTimeToString(value, "yyyy-MM-dd HH:mm:ss");
	}

	public static String DataTimeToString(Date value) {
		return DataTimeToString(value, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static String DataTimeToString(Date value, String format) {
		if (value == null) {
			return null;
		}
		
		String result = "";
		DateFormat dateFormat = new SimpleDateFormat(format);
		result = dateFormat.format(value);
		
		return result;		
	}
	
	public static String newDateStr(){
		return newDateTimeStr("yyyy-MM-dd");
	}
	
	public static String newDateTimeStr(){
		return newDateTimeStr("yyyy-MM-dd kk:mm:ss");		
	}
	
	public static String newDateTimeStr(String fomater){
		return getDateTimeStr(new Date(), fomater);
	}
	
	public static String getDateTimeStr(Date date, String fomater){
		String result = "";
		DateFormat dateFormat = new SimpleDateFormat(fomater);
		result = dateFormat.format(date);
		
		return result;
	}
	
	public static String booleanToStr(boolean value) {
		if (value)
			return "T";
		else
			return "F";
	}
	
	
	
	public static boolean isEmptyStr(Object str) { 
		boolean result = false;
		
		if ((str == null) || ("".equals(str)) || "null".equals(str))
			result = true;
		
		return result;
	}
	
	public static boolean isNull(Object object) {
		if ((object == null))
			return true;
		if (object instanceof String) {
			return isNull((String)object);
		}
		
		return false;
	}
	public static boolean isNull(String value) {
		if ((value == null))
			return true;

		if ("".equals(value)) {
			return true;
		}
		if ("1=1".equals(value.replace(Util.String_Space, Util.String_Empty))) {
			return true;
		}

		if (value.length() == 4) {
			value = value.toLowerCase();
			return "null".equals(value);
		}

		return false;
	}
	
	public static String UTF8decode(String str) {
		if (!isUTF8Encoding(str))
			return str;
		byte[] bytes = str.getBytes();
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		Charset csets = Charset.forName("UTF-8");
		CharBuffer c = csets.decode(bb);
		return c.toString();
	}

	private static boolean isUTF8Encoding(String str) {
		byte[] bytes = str.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			int byteLen = Byte.toString(bytes[i]).length();
			if (byteLen == 4)
				return true;
			else
				continue;
		}
		return false;
	}

	public static Boolean stringToBoolean(String value, Boolean defaultValue) {
		if (value != null) {
			value = value.toLowerCase();
			
			if (value.equalsIgnoreCase("t")) {
				return true;
			}
			else if (value.equals("y")) {
				return true;
			}
			else if (value.equals("true")) {
				return true;
			}	
			else if (value.equals("yes")) {
				return true;
			}	
			else {
				return false;			
			}
		}
		else 
			return defaultValue;
	}
	
	public static  Boolean stringToBoolean(String value) {
		return stringToBoolean(value, false);
	}

	public static int stringToInt(String value, int defaultValue) {
		if (value != null) {
			try {
				Double doubleValue = Double.valueOf(value);
				return doubleValue.intValue();
			}
			catch (Exception e) {
				return defaultValue;
			}
		}
		else 
			return defaultValue;
	}
	
	public static double StringToDouble(String dataValue) {
		BigDecimal parseBigDecimal = parseBigDecimal(dataValue);
		return parseBigDecimal.doubleValue();
		
	}
	
	public static float StringToFloat(String dataValue) {
		BigDecimal parseBigDecimal = parseBigDecimal(dataValue);
		return parseBigDecimal.floatValue();
	}
	
	@SuppressWarnings("unchecked")
	public static <T, Y> T StringToOther(String dataValue, Class<? extends  Object> clazz, Class<Y> listClazz) {
		if (clazz.getSimpleName().equalsIgnoreCase("string")) {
			return (T) dataValue;
		}
		else if (clazz.getSimpleName().equalsIgnoreCase("int")) {
			return (T)(Object) stringToInt(dataValue, -1);
		}else if (clazz.getSimpleName().equalsIgnoreCase("integer")) {
			return (T)(Object) stringToInt(dataValue, -1);
		}
		else if (clazz.getSimpleName().equalsIgnoreCase("double")) {
			return (T)(Object) StringToDouble(dataValue);
		}else if (clazz.getSimpleName().equalsIgnoreCase("long")) {
			return (T)(Object) StringtoLong(dataValue);
		}
		else if (clazz.getSimpleName().equalsIgnoreCase("float")) {
			return (T)(Object) StringToFloat(dataValue);
		}
		else if (clazz.getSimpleName().equalsIgnoreCase("date")) {
			try {
				return (T)StringToDate(dataValue);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else if (clazz.getSimpleName().equalsIgnoreCase("decimal")) {
			return (T)parseBigDecimal(dataValue);
		}else if (clazz.getSimpleName().equalsIgnoreCase("boolean")) {
			return (T) stringToBoolean(dataValue);
		} else if (clazz.getSimpleName().equalsIgnoreCase("list")) {
            return (T) StringToList(dataValue, listClazz);
        } else {

            return (T) dataValue;
        }
        return  (T) dataValue;
	}

	private static Object StringtoLong(String dataValue) {
		return  new Long(dataValue);
	}

	public static <T> T StringToOther(String dataValue, Class<T> clazz)  {
		return StringToOther(dataValue, clazz, String.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> T StringToOther(Object dataValue, DataType type, DataType listType) {
		
		if (type.equals(DataType.String)) {
			return (T) dataValue;
		}
		else if (type.equals(DataType.Integer)) {
			return (T)(Object) stringToInt(String.valueOf(dataValue), -1);
		}
		else if (type.equals(DataType.Double)) {
			return (T)(Object) StringToDouble(String.valueOf(dataValue));
		}
		else if (type.equals(DataType.Float)) {
			return (T)(Object) StringToFloat(String.valueOf(dataValue));
		}
		else if (type.equals(DataType.Date)) {
			try {
				return (T)(Object) StringToDate(String.valueOf(dataValue));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else if (type.equals(DataType.Decimal)) {
			return (T)(Object) parseBigDecimal(dataValue);
		}
		else if (type.equals(DataType.List)) {
			Class<?> listClaz = listType.getJavaClass();
			return (T) StringToList(String.valueOf(dataValue), listClaz);
		}

		return null;
	}
	
	public static <T> T StringToOther(String dataValue, DataType type) throws ParseException {
		return StringToOther(dataValue, type, DataType.String);
	}
	
	public static <T> List<T> StringToList(String dataValue, Class<T> claz) {
		return StringToList(dataValue, semicolon, claz);
	}
	
	public static List<String> StringToList(String dataValue) {
		return StringToList(dataValue, semicolon, String.class);
	}
	
	public static <T> ArrayList<T> StringToList(String dataValue, String split, Class<T> claz){
		// 只支持一层list  
		if (Util.isEmptyStr(dataValue)) {
			return new ArrayList<>();
		}
		ArrayList<T> resultList = new ArrayList<>();
		String[] splitedData = dataValue.split(split);
		for (String oneValue : splitedData){
            if (Util.isEmptyStr(oneValue)) {
		        continue;
            }
			T stringToOther = StringToOther(oneValue, claz);
			resultList.add(stringToOther);
		}
		return resultList;
	}
	
	public static ArrayList<String> StringToList(String dataValue, String split)  {
		return StringToList(dataValue, split, String.class);
	}
	
	public static Date StringToDate(String str) throws ParseException {
		Date result = null;

		String fomater = null;
		
		str = str.replace('T', ' ');
		
		if (str.indexOf("/") == 4) {
			fomater = "yyyy/MM/dd";
		}
		else if (str.indexOf("/") == 2 || str.indexOf("/") == 1) {
			fomater = "MM/dd/yyyy";			
		}
		else if (str.indexOf("-") == 2 || str.indexOf("-") == 1) {
			fomater = "MM-dd-yyyy";		
		}
		else if (str.indexOf("-") == 4 && str.indexOf(":")<0) {
			fomater = "yyyy-MM-dd";				
		}
		else if (str.indexOf("-") == 4  && str.indexOf(":")>0) {
			if(str.split(":").length == 3){
			   fomater = "yyyy-MM-dd HH:mm:ss";
			}else{
			   str = str + ":00";
			   fomater = "yyyy-MM-dd HH:mm:00";
			}
						
		}		
		else if (str.indexOf(".") == 2 || str.indexOf(".") == 1) {
			fomater = "MM.dd.yyyy";	
		}
		else if (str.indexOf(".") == 4) {
			fomater = "yyyy.MM.dd";		
		}
		else if (str.indexOf("-") < 0 && str.indexOf("/") < 0) {
			fomater = "yyyyMMdd";
		}

		DateFormat dateFormat = new SimpleDateFormat(fomater);
		result = dateFormat.parse(str);
		
		return result;
	}

	public static Date doubleToDate(Double value) throws ParseException {
		Date result = null;

		if (value != null) {
			if (value > 195000 && value <= 210001) {
				value = value * 100 + 01;
			}
			
			if (value >= 19500101 && value <= 21000101) {
				String value_Str = String.valueOf(value.intValue());
				result = Util.StringToDate(value_Str);
			}
			else if (value > (1950 - 1900) * 365 && value < (2100 - 1900) * 365) {
				int dateValue = value.intValue();
				double secValue = value - dateValue;
				Date dayDate = intToDate(dateValue);
				long sec = Math.round(secValue * 24 * 3600 * 1000);
				
				result = new Date();
				result.setTime(dayDate.getTime() + sec);
				return result;
			}
		}

		return result;
	}

	public static Date intToDate(int value){
		Calendar result = Calendar.getInstance();
		result.set(Calendar.YEAR, 1900);
		result.set(Calendar.MONTH, 0);
		result.set(Calendar.DAY_OF_MONTH, 1);
		result.set(Calendar.HOUR_OF_DAY, 0);
		result.set(Calendar.MINUTE, 0);
		result.set(Calendar.SECOND, 0);
		
		result.add(Calendar.DATE, value-2);
		return result.getTime();
	}

	public static int getArrayContentSize(Object[] datas) {
		int result = 0;
		
		for (int i = 0; i < datas.length; i++) {
			if (datas[i] != null) {
				result ++;
			}
		}
		
		return result;
	}

	public static String deleteSuffix(String name) {
		String result = null;
		
		if (!isEmptyStr(name)) {
			int pos = name.lastIndexOf(".");
			result = name.substring(0, pos);
		}
		
		return result;
	}

	public static String[] mergeArray(String[] array1, String[] array2) {
		if (array1 == null) {
			return array2;
		}
		
		if (array2 == null) {
			return array2;
		}
	
		List<String> set = new ArrayList<String>(array1.length + array2.length);
		
		for (int i = 0; i < array1.length; i++) {
			set.add(array1[i]);
		}
		
		for (int i = 0; i < array2.length; i++) {
			if (!set.contains(array2[i])) {
				set.add(array2[i]);
			}
		}
		
		String[] result = new String[0];
		return set.toArray(result);
	}
	
	public static String newOracleDateString(Date date) {
		String nowStr = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nowStr = dateFormat.format(date);

		return "to_date('" + nowStr + "','YYYY-MM-DD HH24:MI:SS')";		
	}
	
	public static String newMySqlDateString(Date date) {
		String nowStr = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nowStr = dateFormat.format(date);

		return "('" + nowStr + "')";		
	}	
	
	public static String newSqlServerDateString(Date date) {
		String nowStr = "";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nowStr = dateFormat.format(date);

		return "('" + nowStr + "')";		
	}

	public static boolean isSameString(String value1, String value2) {
		if (value1 == null) {
			if (value2 == null) {
				return true;				
			}
			else {
				return false;				
			}
		}
		else {
			if (value2 == null) {
				return false;				
			}
			else {
				return value1.equals(value2);
			}
		}
	}
	
	public static boolean isSameStringIgnoreCase(String value1, String value2) {
		if (value1 == null) {
			if (value2 == null) {
				return true;				
			}
			else {
				return false;				
			}
		}
		else {
			if (value2 == null) {
				return false;				
			}
			else {
				return value1.equalsIgnoreCase(value2);
			}
		}
	}

	public static String toLowerCase(String name, String defaultValue) {
		if (name == null) {
			return defaultValue;
		}
		else {
			return name.toLowerCase();
		}
	}
	
	public static String getExceptionStack(Exception e) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outStream);
		e.printStackTrace(printStream);	
			
		return outStream.toString();
	}
	


	public static String setDefaultStringValue(String value) throws Exception {
		if (isEmptyStr(value)) {
			return "";
		}
		return value;
	}

    public static boolean checkTableExists(String tableName) {
        Connection connection = SqlSession.createConnection();
        ResultSet rs = null;
        try {
            rs = connection.getMetaData().getTables(null, null, tableName, null);
            if (rs.next()) {
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

	public static void changeData(String basePosition) throws Exception {
		changeData(basePosition, "id", "admin");
	}


	public static void changeData(String basePosition, String baseId) throws Exception {
		changeData(basePosition, baseId, "admin");
	}

	public static void changeData(String basePosition, String baseId, String adminStr) throws Exception {
		if (Util.isNull(baseId)) {
			baseId = "id";
		}
		if (Util.isNull(baseId)) {
			baseId = "admin";
		}


		try {

			//1 根据岗位 重建 territory 表
			NamedSQL getOrganizationType = NamedSQL.getInstance("getOrganizationType");
			EntitySet typeSet = SQLRunner.getEntitySet(getOrganizationType);
			List<String> typeList = typeSet.getFieldList("type");
			if (!typeList.contains(basePosition)) {
				throw new Exception("未包含最基础岗位");
			}
			boolean exists = checkTableExists("territory");
			if (exists) {
				NamedSQL dropTable = NamedSQL.getInstance("dropTable");
				dropTable.setParam("table", "territory");
				SQLRunner.execSQL(dropTable);
			}

			if (!typeList.contains(adminStr)) {
				typeList.add(adminStr);
			}
			ContentBuilder builder = new ContentBuilder(Util.comma);
			for (String type : typeList) {
				builder.append(MessageFormat.format("[{0}] nvarchar({1}) NULL", type, 32));
				builder.append(MessageFormat.format("[{0}] nvarchar({1}) NULL", Util.stringJoin(type, "name"), 32));
			}
			String fields = builder.toString();

			NamedSQL createSql = NamedSQL.getInstance("createCommonTableTemplate");
			createSql.setParam(AggConstant.Sql_Field_tableName, "territory");
			createSql.setParam(AggConstant.Sql_Field_fields, fields);
			SQLRunner.execSQL(createSql);

			//测试中有 同岗位上下级情况  线上应无此中数据
			for (String position : typeList) {
				createTerritoryEntity(position, baseId, adminStr);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void createTerritoryEntity(String basePosition, String baseId, String adminStr) throws Exception {
		if (basePosition.equalsIgnoreCase(adminStr)) {
			return;
		}
		//2 刷新数据
		NamedSQL getemployeeidSql = NamedSQL.getInstance("getBaseUserByOrganization");
		getemployeeidSql.setParam("baseId", baseId);
		getemployeeidSql.setParam("baseType", basePosition);
		EntitySet employeeSet = SQLRunner.getEntitySet(getemployeeidSql);
		List<String> idList = new ArrayList<String>();
		List<String> parentIdList = new ArrayList<String>();
		//1所有一级岗位
		for (Entity entity : employeeSet) {
            String id = entity.getString(baseId);
            idList.add(id);
            String parentid = entity.getString("Parent" + baseId);
            parentIdList.add(parentid);
        }

		for (String id : parentIdList) {
            if (idList.contains(id)) {
                idList.remove(id);
            }
        }

		//2格式新架构
		for (String id : idList) {
            Entity entity = new Entity("territory");
            NamedSQL getAdminByOrganization = NamedSQL.getInstance("getUserByOrganization");
            getAdminByOrganization.setParam("loginName", id);
            Entity userEntity = SQLRunner.getEntity(getAdminByOrganization);
            String englishName = userEntity.getString("EnglishName");
            entity.set(basePosition, id);
            entity.set(basePosition + "name", englishName);
            String childId = id;
            field = 1;

            while (field <= entity.getFieldCount()) {
                String childid = addparentid(entity, childId, "getparentidfromorganization", baseId);

                if (childid == null) {
                    //中间断层 或者RSM
                    String adminCode = Configer.getParam("adminCode");
                    //strumann 特殊
                    getAdminByOrganization.setParam("loginName", adminCode);
                    Entity adminEntity = SQLRunner.getEntity(getAdminByOrganization);
                    String adminId = adminEntity.getString(baseId);
                    String adminname = adminEntity.getString("EnglishName");
                    entity.set(adminStr, adminId);
                    entity.set(adminStr+"name", adminname);
                    break;
                }
                childId = childid;
                field++;

            }
            DataHandler.addLine(entity);
        }
	}

	protected static String addparentid(Entity entity, String id, String sqlname, String baseId) throws Exception {
		String  parents = "Parent" + baseId;

		if(Util.isEmptyStr(id)) {
			return null;
		}
		NamedSQL getparentidfromemployee = NamedSQL.getInstance(sqlname);
		getparentidfromemployee.setParam("baseId", baseId);
		getparentidfromemployee.setParam("id", id);
		getparentidfromemployee.setParam("parentId", parents);

		Entity parentidEntity = SQLRunner.getEntity(getparentidfromemployee);
		
		if ((parentidEntity == null || Util.isEmptyStr(parentidEntity.getString(parents))) && sqlname.equalsIgnoreCase("getparentidfromorganization")) {
			field++;
			
			//String childid = addparentid(entity, id, "getgrandparentidfromemployee");
			//return childid;

		}
		if (parentidEntity != null) {
			String parentId = parentidEntity.getString(parents);



			NamedSQL getUserByOrganization = NamedSQL.getInstance("getUserByOrganization");
			getUserByOrganization.setParam("loginName", parentId);
            Entity parentEntity = SQLRunner.getEntity(getUserByOrganization);

            if (Util.isNull(parentEntity)) {
                return null;
            }
            String englishName = parentEntity.getString("EnglishName");
            String type = parentEntity.getString("type");

			entity.set(type, parentId);
			entity.set(type+"name", englishName);
			return parentId;
		} else {
			return null;
		}

	}
	
	public static String getFileExt(String filename) {
        int pos = filename.lastIndexOf(".");
        String ext = filename.substring(pos);  		
		return ext;
	}
	
	public static List<String> Regular(String regular, String text) {
		
		return null;
		
	}
	
	public static Boolean isNum(String textStr) {
		boolean isInt = Pattern.compile(Integer_Patter).matcher(textStr).find();
		boolean isDouble = Pattern.compile(Double_Patter).matcher(textStr).find();
		return (isInt || isDouble);
	}
	
	public static List<String> matcher(String testStr) {
		return matcher(Default_Patter, testStr);
	}
	
	public static List<String> matcher(String patter, String testStr) {
//		String test = "@{databaseIp}:{databasePort}{instanceName};database";
//		String initCompile = "(?<=@\\{)(.+?)(?=\\})";
		
		List<String> ls=new ArrayList<String>();
		
		Pattern pattern = Pattern.compile(patter);
		Matcher matcher = pattern.matcher(testStr);

		while(matcher.find()){

			ls.add(matcher.group());

		}
		return ls;
	}
	
    public static  ArrayList Dikaerji0(List al0) {
        ArrayList a0 = (ArrayList) al0.get(0);// l1
        ArrayList result = new ArrayList<>();// 组合的结果
        for (int i = 1; i < al0.size(); i++) {
            ArrayList a1 = (ArrayList) al0.get(i);  
            ArrayList temp = new ArrayList();  
            // 每次先计算两个集合的笛卡尔积，然后用其结果再与下一个计算  
            for (int j = 0; j < a0.size(); j++) {  
                for (int k = 0; k < a1.size(); k++) {  
                    ArrayList cut = new ArrayList();  
  
                    if (a0.get(j) instanceof ArrayList) {  
                        cut.addAll((ArrayList) a0.get(j));  
                    } else {  
                        cut.add(a0.get(j));  
                    }  
                    if (a1.get(k) instanceof ArrayList) {  
                        cut.addAll((ArrayList) a1.get(k));  
                    } else {  
                        cut.add(a1.get(k));  
                    }  
                    temp.add(cut);  
                }  
            }  
            a0 = temp;  
            if (i == al0.size() - 1) {  
                result = temp;  
            }  
        }
        /*for (int i = 0; i < al0.size(); i++) {
            ArrayList<String> list = (ArrayList) al0.get(i);
            ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
            combine(0, list.size() -1, list.toArray(new String[list.size()]), arrayList);
            result.addAll(arrayList);
        }*/
        return result;
    }

    public static ArrayList<ArrayList<String>> combine(String []arr, ArrayList<ArrayList<String>> result) {
        int index = 0;
	    combine(index, arr, result);
        return result;
    }

    public static ArrayList<ArrayList<String>> combine(int index, String []arr, ArrayList<ArrayList<String>> result) {
        int count = 0;
	    while (count < arr.length) {
            int k = arr.length - count;
            if (k <= 0) {
                return result;
            }
            combine(index, k, arr, result);
            count++;
        }

        return result;
    }
	/**
	 * 组合
	 * 按一定的顺序取出元素，就是组合,元素个数[C arr.len 3]
	 * @param index 元素位置
	 * @param k 选取的元素个数
	 * @param arr 数组
	 */
	public static void combine(int index, int k, String []arr, ArrayList<ArrayList<String>> result) {
		 if(k == 1){
	            for (int i = index; i < arr.length; i++) {
	                tmpArr.add(arr[i]);
                    ArrayList<String> strings = new ArrayList<>();
                    strings.addAll(tmpArr);
                    result.add(strings);
	                tmpArr.remove(arr[i]);
	            }
	        }else if(k > 1){
	            for (int i = index; i <= arr.length - k; i++) {
	                tmpArr.add(arr[i]); //tmpArr都是临时性存储一下
	                combine(i + 1,k - 1, arr, result); //索引右移，内部循环，自然排除已经选择的元素
	                tmpArr.remove(arr[i]); //tmpArr因为是临时存储的，上一个组合找出后就该释放空间，存储下一个元素继续拼接组合了
	            }
	        }else{
			 return ;
	        }
    }
	
	public static String List2String(List<? extends Object> list, String linked) {
		if (null == list) {
			return null;
		}
		if (Util.isEmptyStr(linked)) {
			linked = ",";
		}
		ContentBuilder builder = new ContentBuilder(linked);
		builder.append(list.toArray());
		
		return builder.toString();
	}
	
	public static String Join2ReplacedTemplate (String param) {
		if (Util.isEmptyStr(param)) {
			return null;
		}
		return "@{" + param + "}";
	}

	public static BigDecimal parseBigDecimal(Object value) {
		BigDecimal ret = null;
		if (value != null) {
			if (value instanceof BigDecimal) {
				ret = (BigDecimal) value;
			} else if (value instanceof String) {
				ret = new BigDecimal((String) value);
			}else if (value instanceof BigInteger) {
				ret = new BigDecimal((BigInteger) value);
			} else if (value instanceof Number) {
				ret = new BigDecimal(((Number) value).doubleValue());
			} else {
				throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass()+" into a BigDecimal.");
			}
		
		}
		return ret;
	}

	public static Object getDefaultValue(DataType type) {
	    Object object = null;

        if (DataType.String.equals(type)) {
            object = "";
        } else if (DataType.Integer.equals(type)) {
            object = 0;
        }else if (DataType.Double.equals(type)) {
            object = 0D;
        }else if (DataType.Float.equals(type)) {
            object = 0F;
        }else if (DataType.Long.equals(type)) {
            object = 0L;
        }else if (DataType.Boolean.equals(type)) {
            object = false;
        }else if (DataType.ArrayList.equals(type)) {
            object = new ArrayList<>();
        }else if (DataType.LinkedList.equals(type)) {
            object = new LinkedList<>();
        }
        //TODO 未完成
	    return  object;
    }

	public static String pathNormalize(String path) {
		if (Util.isEmptyStr(path)) {
			return path;
		}
		return  path.replace(windows_slash, java_slash);
	}

	public static String path2Window(String path) {
		if (Util.isEmptyStr(path)) {
			return path;
		}
		return  path.replace(java_slash, sql_slash);
	}

	public static int getQuarter(String dateString) throws ParseException {
        Date date = StringToDate(dateString);
        return getQuarter(date);
    }

    public static int getQuarter(Date date)  {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        int quarter = (month / 3) + 1;
        return quarter;
    }

	public static Date getSpecialDayOffToday(int dayCount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + dayCount);
		return calendar.getTime();
	}

	public static String getPassWord(int length) {
		int[] array = new int[length];
		char[] chars = new char[length];
		StringBuilder str = new StringBuilder();
		int temp = 0;
		for (int i = 0; i < length; i++) {
			while (true) {
				temp = (int) (Math.random() * 1000);
				if (temp >= 48 && temp <= 57)
					break;
				if (temp >= 65 && temp <= 90)
					break;
				if (temp >= 97 && temp <= 122)
					break;
			}

			array[i] = temp;
			chars[i] = (char) array[i];
			str.append(chars[i]);
		}

		return str.toString();
	}

	public static void main(String[] args) {
		ArrayList<ArrayList<String>> strings = new ArrayList<>();
        ArrayList<String> objects1 = new ArrayList<>();
        objects1.addAll(Arrays.asList(new String[]{"bizdate", "period"}));
        ArrayList<String> objects2 = new ArrayList<>();
        objects2.addAll(Arrays.asList(new String[]{"brand", "ProductCode", "ProductCategory","ProductType"}));
        ArrayList<String> objects3 = new ArrayList<>();
        objects3.addAll(Arrays.asList(new String[]{"RSM", "Salesperson", "Supervisor"}));
        strings.add(objects1);
        strings.add(objects2);
        strings.add(objects3);

		ArrayList arrayList = Util.Dikaerji0(strings);
        for (Object o : arrayList) {

        }
	}

	public static String escapeQuoted(String filter) {
		if (filter == null) {
			return filter;
		}

		int length = filter.length();

		if (length <= 1) {
			return filter;
		}

		char first = filter.charAt(0);
		char last = filter.charAt(length - 1);

		if (('\'' == first || '"' == first) && (first == last)) {
			filter = filter.substring(1, length - 1);
			filter = filter.trim();
			return filter;
		}

		return filter;
	}

	public static int[] getCurYearMonth() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		return new int[] { year, month };
	}

	public static String getTimeStamp(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}



	public static int getMonth(String value) throws ParseException {
		Date date = StringToDate(value);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	public static int getYear(String value) throws ParseException {
		Date date = StringToDate(value);
        Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	public static void getFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if(!dir.exists()){//判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath+"\\"+fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public static String getMyChildren (OnlineUser user){
		StringBuilder builder = new StringBuilder();
		try {
			String myChildrenSalesperson = getMyChildrenSalesperson(user);
			builder.append("(");
			builder.append(myChildrenSalesperson);
			builder.append(" or ");

			String myChildrenDistributor = getMyChildrenDistributor(user);
			builder.append(myChildrenDistributor);
			builder.append(")");
		} catch (Exception e) {
			e.printStackTrace();
		}


		return builder.toString();
	}

	public static String getMyChildrenSalesperson (OnlineUser user) {
		StringBuilder builder = new StringBuilder("clientcode in (");
		//获取登录用户的角色类型
		String type = user.getType();
		try {
			if ("RSM".equals(type)) {
				NamedSQL terminalByRSM = NamedSQL.getInstance("getTerminalByRSM");
				terminalByRSM.setParam("username", user.getName());
				EntitySet entitySet = SQLRunner.getEntitySet(terminalByRSM);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String clientcode = entity.getString("clientcode");
					//getTermianlByCSF(builder,loginname);
					builder.append(quotedStr(clientcode) + ",");
				}

			} else if (type.equalsIgnoreCase("Supervisor")) {
				NamedSQL terminalBySupervisor = NamedSQL.getInstance("getTerminalBySupervisor");
				terminalBySupervisor.setParam("username", user.getName());
				EntitySet entitySet = SQLRunner.getEntitySet(terminalBySupervisor);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String clientcode = entity.getString("clientcode");
					builder.append(quotedStr(clientcode) + ",");
					//builder.append(quotedStr(loginname) + ",");
				}

			} else if (type.equalsIgnoreCase("Salesperson")) {
				NamedSQL terminalByCSF = NamedSQL.getInstance("getTerminalByCSF");
				terminalByCSF.setParam("username", user.getName());
				EntitySet entitySet = SQLRunner.getEntitySet(terminalByCSF);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String clientcode = entity.getString("clientcode");
					builder.append(quotedStr(clientcode) + ",");
					//builder.append(quotedStr(loginname) + ",");
				}
			} else if (type.equalsIgnoreCase("admin") || type.equalsIgnoreCase("superadmin") || type.equalsIgnoreCase("op")) {
				return "1=1";

			} else {
				return "1 <> 1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.substring(0,builder.length()-1) + ")";
	}

	public static void getTermianlByCSF (StringBuilder builder,String loginname) throws Exception {
		//StringBuilder builder = new StringBuilder("ClientCode in (");
		//获取登录用户的角色类型
		NamedSQL terminalByCSF = NamedSQL.getInstance("getTerminalByCSF");
		terminalByCSF.setParam("username", loginname);
		EntitySet entitySet = SQLRunner.getEntitySet(terminalByCSF);
		if (entitySet.isEmpty()){
			return;
		}

		for (Entity entity : entitySet) {
			String clientcode = entity.getString("ClientCode");
			builder.append(quotedStr(clientcode) + ",");
		}
		System.out.println(builder.toString());
	}

	public static String getMyChildrenSalespersonMLJ (OnlineUser user) {
		StringBuilder builder = new StringBuilder("saleCode in (");
		String id = user.getId();
		try {
			NamedSQL roleFromManagerSql = NamedSQL.getInstance("getRoleFromManager");
			roleFromManagerSql.setParam("id", id);
			Entity OnlineUserManger = SQLRunner.getEntity(roleFromManagerSql);
			String role = OnlineUserManger.getString("type");

			if (Util.isEmptyStr(role)) {
				return null;
			}

			if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("superadmin")  || role.equalsIgnoreCase("op")) {
				return "1 = 1";

			} else if (role.equalsIgnoreCase("RSM")) {
				NamedSQL parentFromManagerSql = NamedSQL.getInstance("getParentFromManager");
				parentFromManagerSql.setParam("id", id);
				parentFromManagerSql.setParam("role", "Supervisor");
				EntitySet entitySet = SQLRunner.getEntitySet(parentFromManagerSql);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String loginname = entity.getString("loginname");

					if (Util.isEmptyStr(loginname)) {
						continue;
					}

					supervisor2SalesPerson(builder, loginname);
				}

			} else if (role.equalsIgnoreCase("Supervisor")){
				EntitySet dataSet = DataHandler.getDataSet("[dbo].[organization]", "userid = '" + id + "'");
				Entity next = dataSet.next();
				String loginname = next.getString("loginname");

				if (Util.isEmptyStr(loginname)) {
					return "1 <> 1";
				}
				supervisor2SalesPerson(builder, loginname);

			} else if (role.equalsIgnoreCase("Salesperson")) {
				EntitySet dataSet = DataHandler.getDataSet("[dbo].[organization]", "userid = '" + id + "'");
				Entity next = dataSet.next();
				String loginname = next.getString("loginname");

				builder.append(quotedStr(loginname) + ",");

			} else{
				return "1 <> 1";
			}
			if (builder.length() == 0){
				return builder.append("(1 <> 1)").toString();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		String substring = builder.substring(builder.length() -1);
		if (",".equalsIgnoreCase(substring)) {
			builder.deleteCharAt(builder.length()-1);
		}

		builder.append(")");
		return builder.toString();
	}

	public static String getMyChildrenDistributorMLJ(OnlineUser user) {
		StringBuilder builder = new StringBuilder("DistributorCode in (");
		String id = user.getId();
		try {
			NamedSQL roleFromManagerSql = NamedSQL.getInstance("getRoleFromManager");
			roleFromManagerSql.setParam("id", id);
			Entity OnlineUserManger = SQLRunner.getEntity(roleFromManagerSql);
			String role = OnlineUserManger.getString("type");

			if (Util.isEmptyStr(role)) {
				return null;
			}

			if (role.equalsIgnoreCase("RSM")) {
				NamedSQL parentFromManagerSql = NamedSQL.getInstance("getParentFromManager");
				parentFromManagerSql.setParam("id", id);
				parentFromManagerSql.setParam("role", "Supervisor");
				EntitySet entitySet = SQLRunner.getEntitySet(parentFromManagerSql);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String loginname = entity.getString("loginname");
					if (Util.isEmptyStr(loginname)) {
						continue;
					}
					supervisor2DistributorCode(builder, loginname);
				}
			}

			else if (role.equalsIgnoreCase("Supervisor")) {
				EntitySet dataSet = DataHandler.getDataSet("[dbo].[organization]", "userid = '" + id + "'");
				Entity next = dataSet.next();
				String loginname = next.getString("loginname");

				if (Util.isEmptyStr(loginname)) {
					return "1 <> 1";
				}
				supervisor2DistributorCode(builder, loginname);
			}

			else if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("superadmin")  || role.equalsIgnoreCase("op")) {
				return "1=1";
			}

			else if (role.equalsIgnoreCase("distributor")) {
				EntitySet dataSet = DataHandler.getDataSet("usr", "UserID = '" + id + "'");
				Entity next = dataSet.next();
				String loginname = next.getString("loginname");

				builder.append(quotedStr(loginname) + ",");

			} else {
				return "1 <> 1";
			}


			if (builder.length() == 0){
				return builder.append("(1 <> 1)").toString();
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		String substring = builder.substring(builder.length() -1);
		if (",".equalsIgnoreCase(substring)) {
			builder.deleteCharAt(builder.length()-1);
		}
		builder.append(")");
		return builder.toString();
	}

	public static String getMyChildrenDistributor(OnlineUser user) {
		StringBuilder builder = new StringBuilder("DistributorCode in (");
		//获取登录用户的角色类型
		String type = user.getType();
		try {
			if ("RSM".equals(type)) {
				NamedSQL distributorByRSM = NamedSQL.getInstance("getDistributorByRSM");
				distributorByRSM.setParam("username", user.getName());
				EntitySet entitySet = SQLRunner.getEntitySet(distributorByRSM);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String distributoreCode = entity.getString("DistributorCode");
					builder.append(quotedStr(distributoreCode) + ",");
				}
			} else if (type.equalsIgnoreCase("Supervisor")) {
				NamedSQL distributorByRSM = NamedSQL.getInstance("getDistributorBySupervisor");
				distributorByRSM.setParam("username", user.getName());
				EntitySet entitySet = SQLRunner.getEntitySet(distributorByRSM);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String distributoreCode = entity.getString("DistributorCode");
					builder.append(quotedStr(distributoreCode) + ",");
				}
			} else if (type.equalsIgnoreCase("distributor")) {
				builder.append(quotedStr(user.getName()) + ",");
			} else if (type.equalsIgnoreCase("admin") || type.equalsIgnoreCase("superadmin")  || type.equalsIgnoreCase("op")) {
				return "1=1";
			} else {
				return "1 <> 1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.substring(0,builder.length()-1) + ")";
	}

	public static void supervisor2DistributorCode(StringBuilder builder, String id) throws Exception  {
		NamedSQL distributorCodeFromHierartorySql = NamedSQL.getInstance("getDistributorCodeFromHierartory");
		distributorCodeFromHierartorySql.setParam("supervisorid", id);
		EntitySet entitySet = SQLRunner.getEntitySet(distributorCodeFromHierartorySql);
		if (entitySet.isEmpty()) {
			builder.setLength(0);
			return;
		}
		for (Entity entity : entitySet) {
			String distributoreCode = entity.getString("DistributorCode");

			if (Util.isEmptyStr(distributoreCode)) {
				continue;
			}

			builder.append(quotedStr(distributoreCode) + ",");
		}
	}
	//根据查询的月份返回季度
	public static String getQuarterByMonth(String month){
		if("1".equals(month) || "2".equals(month) || "3".equals(month)){
			return "1";
		}else if("4".equals(month) || "5".equals(month) || "6".equals(month)){
			return "2";
		}else if("7".equals(month) || "8".equals(month) || "9".equals(month)){
			return "3";
		}else if("10".equals(month) || "11".equals(month) || "12".equals(month)){
			return "4";
		}else{
			//不等于1~12时，返回1
			return "1";
		}
	}


	private static void supervisor2SalesPerson(StringBuilder builder,String id) throws Exception {
		NamedSQL SaleCodeFromManager = NamedSQL.getInstance("getSaleCodeFromManager");
		SaleCodeFromManager.setParam("id", id);
		SaleCodeFromManager.setParam("role", "Salesperson");
		EntitySet entitySet = SQLRunner.getEntitySet(SaleCodeFromManager);
		if (entitySet.isEmpty()) {
			builder.setLength(0);
			return;
		}
		for (Entity entity : entitySet) {
			String longinName = entity.getString("loginname");

			if (isEmptyStr(longinName)) {
				continue;
			}
			builder.append(quotedStr(longinName) + ",");
		}
	}

	public static String getMyRSM(OnlineUser user) {
		StringBuilder builder = new StringBuilder("RSM in (");
		String username = user.getName();
		String RSMfilter = "loginname = " + quotedStr(username);
		//获取登录用户的角色类型
		String type = user.getType();
		try {
			if ("RSM".equals(type)) {
				NamedSQL RSMByLogninName = NamedSQL.getInstance("getRSMByLogninName");
				RSMByLogninName.setParam("RSMfilter",RSMfilter);
				EntitySet entitySet = SQLRunner.getEntitySet(RSMByLogninName);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String loginname = entity.getString("LoginName");
					builder.append(quotedStr(loginname) + ",");
				}
			} else if (type.equalsIgnoreCase("admin") || type.equalsIgnoreCase("superadmin")  || type.equalsIgnoreCase("op")) {
				return "1=1";
			} else {
				return "1 = 1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.substring(0,builder.length()-1) + ")";
	}

	public static String getMySupervisor(OnlineUser user) {
		StringBuilder builder = new StringBuilder("Supervisor in (");
		String username = user.getName();
		String RSMfilter = quotedStr(username);
		//获取登录用户的角色类型
		String type = user.getType();
		try {
			if ("RSM".equals(type)) {
				NamedSQL SupervisorByRSM = NamedSQL.getInstance("getSupervisorByRSM");
				SupervisorByRSM.setParam("username",RSMfilter);
				EntitySet entitySet = SQLRunner.getEntitySet(SupervisorByRSM);
				if (entitySet.isEmpty()){
					return "1<>1";
				}
				for (Entity entity : entitySet) {
					String loginname = entity.getString("LoginName");
					builder.append(quotedStr(loginname) + ",");
				}
			} else if (type.equalsIgnoreCase("supervisor")) {
				builder.append(quotedStr(user.getName()) + ",");

			} else if (type.equalsIgnoreCase("Salesperson")) {
				return "1=1";

			} else if (type.equalsIgnoreCase("admin") || type.equalsIgnoreCase("superadmin")  || type.equalsIgnoreCase("op")) {
				return "1=1";

			} else {
				return "1 <> 1";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return builder.substring(0,builder.length()-1) + ")";
	}

	public static String getMySaleperson(OnlineUser user) {
		StringBuilder builder = new StringBuilder("salecode in (");
		//获取登录用户的角色类型
		String type = user.getType();
		try {
			if ("RSM".equals(type)) {
				return "1=1";

			} else if (type.equalsIgnoreCase("supervisor")) {
				return "1=1";

			} else if (type.equalsIgnoreCase("Salesperson")) {
				builder.append(quotedStr(user.getName()) + ",");

			} else if (type.equalsIgnoreCase("admin") || type.equalsIgnoreCase("superadmin")  || type.equalsIgnoreCase("op")) {
				return "1=1";

			} else {
				return "1 <> 1";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return builder.substring(0,builder.length()-1) + ")";
	}
	public static String nullStr(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}
	public static String[] split(String str) {
		if (str == null) {
			return new String[0];
		}

		return str.replace(",", ";").replace("，", ";").replace("；", ";").split(";");
	}





}
