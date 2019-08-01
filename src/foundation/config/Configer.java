package foundation.config;

import foundation.persist.DataBaseType;
import foundation.server.Sysparam;
import foundation.util.Util;
import foundation.variant.Expression;
import foundation.variant.VariantContext;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configer extends VariantContext {

	private static Configer instance;
	private static Object lock = new Object();
	
	private Map<String, String> params;
	private List<Sysparam> clientSysparams;
	private String DataBase_Schema = null;
	private String Path_Application;
	private String Path_WebInfo;
	private String Path_Config;
	private String Path_SQL;
	private DataBaseType dataBaseType;

	private Configer() {
		
	}
	
	private static Configer getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new Configer();
				}
			}
		}
		
		return instance;
	}
	
	public static void init(ServletContext servletContext) {
		getInstance();
		
		instance.params = new HashMap<String, String>();
		instance.clientSysparams = new ArrayList<Sysparam>();

		instance.Path_Application = servletContext.getRealPath("").replace("\\", "/");
		instance.Path_WebInfo = servletContext.getRealPath("/WEB-INF").replace("\\", "/");
		instance.Path_Config = instance.Path_WebInfo + "/config/";
		instance.Path_SQL = instance.Path_WebInfo + "/sql/";
	}

	public static void afterLoadParams() {
		String typeString = getParam("dataBaseType");
		instance.dataBaseType = DataBaseType.valueOfString(typeString);
	}

	public static void addParam(String name, String value, boolean client) throws Exception {
		Expression expression = new Expression(value);
		
		if (!expression.isVariantEmpty()) {
			instance.setParametersTo(expression);
			value = expression.getString();
		}
		
		instance.params.put(name.toLowerCase(), value);
		
		if (client) {
			instance.clientSysparams.add(new Sysparam(name, value));
		}
	}
	
	@Override
	public String getStringValue(String variantName) {
		if (variantName == null) {
			return null;
		}
		
		if ("path_application".equalsIgnoreCase(variantName)) {
			return Path_Application;
		}
		else if ("path_config".equalsIgnoreCase(variantName)) {
			return Path_Config;
		}
		else if ("path_sql".equalsIgnoreCase(variantName)) {
			return Path_SQL;
		}
		else if ("path_webinfo".equalsIgnoreCase(variantName)) {
			return Path_WebInfo;
		}
		else {
			return params.get(variantName.toLowerCase());
		}
	}

	public static String getParam(String name) {
		if (name == null) {
			return null;
		}

		return instance.params.get(name.toLowerCase());
	}

	public static String getPath_WebInfo() {
		return instance.Path_WebInfo;
	}

	public static String getPath_Config() {
		return instance.Path_Config;
	}

	public static String getPath_TimerConfig() {
		return instance.Path_Config + "timer.properties";
	}

	public static String getWebserviceURI() {
		return "http://www.jydatas.com/";
	}

	public static String getPath_Application() {
		return instance.Path_Application;
	}
	
	public static String getPath_Application(String subpath) {
		if (subpath == null) {
			return instance.Path_Application;
		}
		
		subpath = subpath.replace("\\", "/");
		
		if ('/' != subpath.charAt(0)) {
			subpath = "/" + subpath;
		}
		
		if ('/' != subpath.charAt(subpath.length() - 1)) {
			subpath = subpath + "/";
		}
		
		return instance.Path_Application + subpath;
	}

	public static String getPath_LoggerConfig() {
		return instance.Path_Config + "log4j.properties";
	}

	public static String getPath_ActivePeriodConfig() {
		return instance.Path_Config + "activeperiod.properties";
	}

	public static String getPath_SQLConfig() {
		return instance.Path_SQL;
	}

	public static String getPath_SQLDTD() {
		return instance.Path_Config + "sql.dtd";
	}

	public static String getPath_MainConfig() {
		return instance.Path_Config + "config.xml";
	}

	public static String getPath_Datasource() {
		return instance.Path_Config + "datasource.xml";
	}

	public static String getPath_Upload(String username) {
		return instance.Path_Application + "/upload/" + username;
	}

	public static String getPath_Temp() {
		return instance.Path_Application + "/temp";
	}
	
	public static String getPath_Temp(String username) {
		return instance.Path_Application + "/temp/" + username;
	}	

	public static String getPath_WXConfig() {
		return instance.Path_Config + "weixin.properties";
	}

	public static List<Sysparam> getClientSysparams() {
		return instance.clientSysparams;
	}

	// ***************Page******************//
	public static String getPage_TimeOut() {
		String appName = getParam("appName");
		String timeOutPage = getParam("timeOutPage");

		return "/" + appName + "/" + timeOutPage;
	}

	public static DataBaseType getDataBaseType() {
		return instance.dataBaseType;
	}

	public static String getPath_ImageLib(String typeCode) {
		return instance.Path_Application + "/imagelib/" + typeCode + "/" + Util.newDateTimeStr("yyyyMM") + "/";
	}

	public static boolean isMultiplyDatasoure() {
		String lowerCase = "multipleDataSource".toLowerCase();
		String multi = instance.params.get(lowerCase);
		return Util.stringToBoolean(multi);
	}

	public static String getDataBase_Schema() {
		return instance.DataBase_Schema;
	}

	public static void setDataBase_Schema(String dataBase_Schema) {
		instance.DataBase_Schema = dataBase_Schema;
	}

}
