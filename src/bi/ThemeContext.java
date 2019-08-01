package bi;

import bi.theme.Hierarchy;
import bi.work.ActivePeriod;
import foundation.config.Configer;
import foundation.data.Page;
import foundation.engine.Parameters;
import foundation.persist.sql.SQLContext;
import foundation.user.OnlineUser;
import foundation.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

public class ThemeContext extends SQLContext {

	private static Hierarchy hierarchy;
	private static int defaultPageSize;
	private HttpServletRequest request;
	private Parameters parameters;
	private OnlineUser onlineUser;
	private Page page;
	private Calendar calendar;
	private Connection conn;
	static {
		hierarchy = Hierarchy.getInstance();
		String defaultThemePageSize = Configer.getParam("DefaultThemePageSize");
		
		if (Util.isEmptyStr(defaultThemePageSize)) {
			defaultThemePageSize = "1";
		}
		
		defaultPageSize = Integer.valueOf(defaultThemePageSize);
	}
	
	public ThemeContext(HttpServletRequest request, OnlineUser onlineUser) throws Exception {
		this.onlineUser = onlineUser;
		this.request = request;
		parameters = new Parameters();
		parameters.putAll(request);
	}
	
	public ThemeContext(Connection connection) {
		conn = connection;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getSqlString(String name, Object... args) {
		if (name == null) {
			return null;
		}
		
		name = name.toLowerCase();
		
		if ("roleid".equals(name)) {
			return getRoleId();
		}
		else if ("rolename".equals(name)) {
			return getRoleName();
		}
		else if ("position".equals(name)) {
			return getPosition();
		}
		else if ("periodtype".equals(name)) {
			return getPeriodType();
		}
		else if ("periodflag".equals(name)) {
			return getPeriodFlag();
		}		
		else if ("periodvalue".equals(name) || "periodno".equals(name)) {
			return getPeriodValue();
		}
		else if ("year".equals(name)) {
			return String.valueOf(getYear());
		}
		else if ("month".equals(name)) {
			return String.valueOf(getMonth());
		}
		else if ("dmonth".equals(name)) {
			return String.valueOf(getDMonth());
		}		
		else if ("ordertype".equals(name)) {
			return getOrderType();
		}
		else if ("pagesize".equals(name)) {
			return getPageSize();
		}
		else if ("beginrecordno_1".equals(name)) {
			return getBeginRecordNo_1();
		}
		else if ("beginrecordno".equals(name)) {
			return getBeginRecordNo();
		}
		else if ("filter".equals(name)) {
			return getFilter(name);
		}
		else {
			return parameters.get(name);
		}
	}

	public String getRoleId() {
		String result = parameters.get("roleid");
		String userid = onlineUser.getEmp_id();
		
		if (Util.isEmptyStr(result)) {
			result = userid;
			return result;
		}
		
		if (result.equals(userid)) {
			return result;
		}
		
		if (hierarchy.isCompatible(userid, result)) {
			return result;
		}
		
		return null;
	}

	public String getRoleName() {
		return onlineUser.getName();
	}

	public String getPosition() {
		return onlineUser.getEmp_position();
	}

	public String getPeriodType() {
		String result = parameters.get("periodtype");
		
		if (Util.isEmptyStr(result)) {
			result = "month";
		}
		
		return result;
	}

	private String getPeriodFlag() {
		String periodflag = parameters.get("periodflag");
		
		if (!Util.isEmptyStr(periodflag)) {
			return periodflag;
		}
		
		String periodtype = getPeriodType();
		
		if ("year".equalsIgnoreCase(periodtype)) {
			return "y";
		}
		else if ("dmonth".equalsIgnoreCase(periodtype)) {
			return "s";
		}		
		else if ("month".equalsIgnoreCase(periodtype)) {
			return "m";
		}
		
		return null;
	}
	
	public String getPeriodValue() {
		String result = parameters.get("periodvalue");
		
		if (Util.isEmptyStr(result)) {
			String periodType = parameters.get("periodType");
			
			int periodValue = ActivePeriod.getInstance().getValue(periodType);
			result = String.valueOf(periodValue);
		}
		
		return result;
	}
	
	public int getYear() {
		String result = parameters.get("year");
		
		if (Util.isEmptyStr(result)) {
			result = String.valueOf(ActivePeriod.getInstance().getYear());
		}
		
		return Integer.valueOf(result);
	}
	
	public int getMonth() {
		String result = parameters.get("month");
		
		if (Util.isEmptyStr(result)) {
			result = String.valueOf(ActivePeriod.getInstance().getMonth());
		}
		
		return Integer.valueOf(result);
	}
	
	private int getDMonth() {
		int month = Integer.valueOf(getMonth());
		int dmonthval;
		if (month%2 != 0 ) {
			dmonthval = month + 1;
		}
		else {
			dmonthval = month;
		}
		int dmonth = (dmonthval / 2);
		
		return dmonth;
	}
	
	public String getOrderType() {
		String result = parameters.get("ordertype");
		
		if (Util.isEmptyStr(result)) {
			result = "asc";
		}
		
		return " " + result;
	}
	
	private String getPageSize() {
		if (page == null) {
			getPage();
		}
		
		return String.valueOf(page.getPageSize());
	}
	
	private String getBeginRecordNo_1() {
		if (page == null) {
			getPage();
		}
		
		return String.valueOf(page.getBeginRecordNo_1());
	}

	private String getBeginRecordNo() {
		if (page == null) {
			getPage();
		}
		
		return String.valueOf(page.getBeginRecordNo());
	}

	protected void getCalendar() {
		calendar = Calendar.getInstance();
		calendar.setTime(new Date());			
	}
	
	private void getPage() {
		String pageNo = parameters.get("pageno");
		String pageSize = parameters.get("pagesize");
		
		if (Util.isEmptyStr(pageNo) || Util.isEmptyStr(pageSize)) {
			page = new Page(defaultPageSize);
			return;
		}
		
		page = new Page(defaultPageSize);
		page.setPageSize(Integer.valueOf(pageSize));
		page.setPageNo(Integer.valueOf(pageNo));		
	}
	
	private String getFilter(String name) {
		String result = parameters.get("filter");
		
		if (Util.isEmptyStr(result)) {
			result = " 1 = 1 ";
		}
		
		return result;
	}
	
	public String[] getTypeCodes() {
		String typecodes = parameters.get("typecode");
		
		if (Util.isEmptyStr(typecodes)) {
			typecodes = "A,B";
		}
		
		typecodes = typecodes.replace(",", ";").replace("，", ";").replace("；", ";");
		
		String[] array = typecodes.split(";");
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].toUpperCase();
		}
		
		return array;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}


}
