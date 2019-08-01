package foundation.user;

import foundation.callable.Callable;
import foundation.config.Configer;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataCenter;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.Util;
import phonecode.SendVcode;

import javax.servlet.http.HttpSession;
import java.util.*;


public class User extends Callable {

	public static String Error_NotExist = "USER_NotExist";
	public static String Error_EmptyOrgCode = "USER_EmptyOrgCode";
	public static String Error_InvalidUser = "USER_InvalidUser";
	public static String Error_InvalidVCode = "USER_InvalidVCode";
	public static String Error_EmptyVCodeOrPassword = "USER_EmptyVCodeOrPass";
	public static String Error_MultiUser = "USER_MultiUser";
	
	public static boolean isTest = false;
	public static final String Code_UserName = "username";
	public static String Code_User_Dealer = "user_dealer";
	public static String Code_User_Manufacturer = "user_manufacturer";
	public static String SuperVCode;
	private static Random random;
	
	static {
		random = new Random();
		SuperVCode = Configer.getParam("SuperVCode");
		isTest = !Util.stringToBoolean(Configer.getParam("SendSMS"));
	}

	@Override
	protected void doReceive(String[] paths) throws Exception {
		if (paths.length == 2) {
			String operator = paths[1];
			
			if ("login".equalsIgnoreCase(operator)) {
				login();
			}
			else if ("logout".equalsIgnoreCase(operator)) {
				logout();
			}
			else if ("getvcade".equalsIgnoreCase(operator)) {
				getVcode();
			}			
			else if ("changePassword".equalsIgnoreCase(operator)) {
				changePassword();
			}
			else if ("getinfo".equalsIgnoreCase(operator)) {
				getInfo();
			}
			else if ("getMenu".equalsIgnoreCase(operator)) {
				getMenu();
			}
			else if ("getStatistics".equalsIgnoreCase(operator)) {
				getStatistics();
			}
			else if ("dataexists".equalsIgnoreCase(operator)) {
				dataExists();
			}
			else if ("getChildren".equalsIgnoreCase(operator)) {
				//getChildren();
			}
		}
		else {
			writer.ReplyError("bad data message path:" + fullPath);
		}
	}
	

	private void dataExists() throws Exception {
		String filter = dataPool.getParameter("filter").getStringValue();
		String tablename = dataPool.getParameter("tablename").getStringValue();
		
		int cnt = getDataCount(tablename,filter);
		
		resultPool.addValue("exists", cnt > 0);
		resultPool.success();
	}
	
	private int getDataCount(String tablename, String filter) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getCount");
		namedSQL.setTableName(tablename);
		namedSQL.setFilter(filter);
		return SQLRunner.getInteger(namedSQL);
	}
	
	private void getVcode() throws Exception {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String phone = getPhone(username, password);
		if (Util.isEmptyStr(phone)) {
			resultPool.error("电话号码为空");
			return;
		}
		
		String vcode = "";
		for (int i = 0; i < 6; i++) {
			int value = random.nextInt(9);
			
			if (value <= 0) {
				value = 1;
			}
			vcode = vcode + value;
		}
		
		SendVcode sendVcode = new SendVcode();
		boolean issuccess = sendVcode.SendVcode(phone, vcode);
		
		if (issuccess == false ) {
			HttpSession session = request.getSession(true);
			session.setAttribute("vcode", vcode);
			resultPool.success();
		}
	}
	
	private String getPhone(String username, String password) throws Exception {
		String phone = null;
		NamedSQL namedSQL = NamedSQL.getInstance("getMyOwnPhone");
		namedSQL.setParam("username", username);
		namedSQL.setParam("password", password);
		Entity entity = SQLRunner.getEntity(namedSQL);
		if (entity != null) {
			phone = entity.getString("telephone");
		} else {
			phone = null;
		}
		return phone;
		
	}

	private void login() throws Exception {
		HttpSession session = request.getSession(true);
		String telephone = null;
		String orgCode = request.getParameter("org");
		String phone = request.getParameter("phone");
		String username = request.getParameter("username");
		String password = request.getParameter("password");		
		String vcode = request.getParameter("vcode");
		
		//1. 检查用户是否存在
		NamedSQL namedSQL = NamedSQL.getInstance("getClientUserByPhoneOrName");
		if (username != null){
			telephone = getPhone(username, password);
		}
		
		if (phone != null){
			namedSQL.setParam("phone", phone, "empty_phone");
		} else{
			namedSQL.setParam("phone", telephone, "empty_phone");
		}
		namedSQL.setParam("username", username, "empty_username");
		Entity entity = SQLRunner.getEntity(namedSQL);
		
		if (entity == null) {
			resultPool.error(Error_NotExist, "user not exists");
			return;
		}
		
		//2.检查是否需要公司码 
		boolean orgcheck = entity.getBoolean("orgcheck");
		if (orgcheck && Util.isEmptyStr(orgCode)) {
			resultPool.error(Error_EmptyOrgCode, "empty org code");
			return;			
		}
		
		//3.验证码或密码是否正确
		if (!Util.isEmptyStr(vcode)) {
			if (!vcode.equals(SuperVCode)) {
				String sourceVcode = (String)session.getAttribute("vcode");
				if (!vcode.equals(sourceVcode)) {
					resultPool.error(Error_InvalidVCode, "invalid vcode");
					return;
				}
			}
		} else {
			resultPool.error(Error_InvalidVCode, "invalid vcode");
			return;
		}
		
		if (!Util.isEmptyStr(password)) {
			if (!password.equals(entity.getString("password"))) {
				resultPool.error(Error_InvalidUser, "invalid username or password");
				return;
			}
		} else {
			resultPool.error(Error_EmptyVCodeOrPassword, "empty password");
			return;
		}
		
		//4、检查电话号码、公司码是否正确, 获取用户和公司信息
		String orgFilter = Util.isEmptyStr(orgCode) ? "" : " and org.code = '" + orgCode + "'" ;
		String userFilter = Util.isEmptyStr(phone) ? "usr.LoginName = '" + username + "' and usr.Password = '" + password + "'" : "usr.Telephone = '" + phone + "'";
		
		OnlineUser onlineUser = new OnlineUser();
		
		namedSQL = NamedSQL.getInstance("getUser");
		namedSQL.setParam("userfilter", userFilter);
		namedSQL.setParam("orgfilter", orgFilter);
		SQLRunner.getData(namedSQL, onlineUser);
		
		if (!onlineUser.isOnlyOne()) {
			resultPool.error(Error_MultiUser, "multi user, need orgCode");
			return;
		}
		
		if (onlineUser.isEmpty()) {
			resultPool.error(Error_InvalidUser, "invalid username or password");
			return;
		}
			
		session.setAttribute(OnlineUser.class.getSimpleName(), onlineUser);
		resultPool.addValue("user", onlineUser);
		resultPool.success();		
	}

	private void logout() throws Exception {
		HttpSession session = request.getSession(true);
		session.invalidate();
			
		resultPool.success();
	}

	private void changePassword() throws Exception {
		String username = onlineUser.getName();
		String password = request.getParameter("pass");
		
		if (Util.isEmptyStr(username)) {
			resultPool.error("用户名丢失");
		}
		else if (Util.isEmptyStr(password)) {
			resultPool.error("密码丢失");
		}
		else {
			DataCenter.changePassword(username, password);
			resultPool.success();
		}
	}
	
	private void getInfo() throws Exception {
		resultPool.addValue("user", onlineUser);
	}
	
	private void getMenu() throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getUserMenu");
		namedSQL.setParam("rolecode", onlineUser.getRolecode());
		EntitySet entitySet = SQLRunner.getEntitySet(namedSQL);
		
		resultPool.addValue("dataSet", entitySet);
	}
	
	@SuppressWarnings("unchecked")
	private void getStatistics() {
		List<Statistics> stacs = new ArrayList<Statistics>();
		Statistics stac;

		Map<String, Statistics> visitor = (Map<String, Statistics>) request.getServletContext().getAttribute("visitor");
		Set<String> keySet = visitor.keySet();
		for (String ip : keySet) {
			stac = visitor.get(ip);
			stacs.add(stac);
		}
		Collections.sort(stacs);

		resultPool.addValue(stacs);
	}

}
