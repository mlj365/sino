package report;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

import report.bean.FileParams;
import report.bean.FileParams.Sql;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import foundation.callable.Callable;
import foundation.config.Configer;
import foundation.data.Entity;
import foundation.file.download.ClientAction;
import foundation.file.download.HttpResponseWriter;
import foundation.persist.DataHandler;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.Util;
import foundation.variant.VariantSegment;

public class Console extends Callable {

	private static String URL_WebMan = "";
	
	static {
		URL_WebMan = Configer.getParam("URL_WebMan");
	}
	
	protected void publishMethod() {
		addMethod("getNews");
		addMethod("export");
		addMethod("uplodeFile");
		addMethod("removeUser");
		addMethod("addOrganization");
		addMethod("removeOrganization");
		addMethod("addUser");
		addMethod("deleteLine");
		addMethod("addLine");
		addMethod("asyCheckLoginname");
		addMethod("editUser");
		addMethod("getFileStep");
		addMethod("clearFileStep");
		addMethod("downloadFile");
		addMethod("preview");
		addMethod("aggData");//聚合数据，用于提高报表查询效率
	}

	protected void preview() throws Exception {
		String code = dataPool.getParameter("code").getStringValue();
		String id = dataPool.getParameter("applicationid").getStringValue();
		if (code.indexOf("Extension") >= 0) {
			
		}
		
		HttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet("http://localhost:3456/api/Agreement/GetFile?type=PDF&recordId=" + id + "&templateCode=" + code);
		
		HttpResponse httpResponse = httpclient.execute(httpget);
		StatusLine statusLine = httpResponse.getStatusLine();
		
		if (statusLine.getStatusCode() == HttpStatus.OK.value()) {
			String strResult = EntityUtils.toString(httpResponse.getEntity());  
			JsonObject returnData = new JsonParser().parse(strResult).getAsJsonObject();
			JsonElement jsonElement = returnData.get("FilePath");
			String path = jsonElement.getAsString();
			
			File file = new File(path);
			
			if (!file.exists()) {
				resultPool.error("file not exists: " + file);
				return;
			}
			
			HttpResponseWriter writer = new HttpResponseWriter(response);
			writer.write(file, ClientAction.AsPDF);
		}
	}
	
	protected void asyCheckLoginname() throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getLoginname");
		String tableName = dataPool.getParameter("tableName").getStringValue();
		String field = dataPool.getParameter("field").getStringValue();
		String value = dataPool.getParameter("value").getStringValue();
		namedSQL.setParam("tableName", tableName);
		namedSQL.setParam("filter", field + "='" + value + "'");
		Entity entity = SQLRunner.getEntity(namedSQL);
		if (entity != null) {
			String loginname = entity.getString("loginname");
			resultPool.addValue("loginname", loginname);
		} else
			return;
	}
	
	//1.organization_CURD
	//1.1 organization_CU
	protected void addOrganization() throws Exception {
		Entity organizationEntity = new Entity("organization");
		getEntityFrowRequest(organizationEntity);
		DataHandler.saveLine(organizationEntity);
	}

	//1.2 organization_D
	protected void removeOrganization() throws Exception {
		String id = dataPool.getParameter("value").getStringValue();
		deleteLine("usr", "userid", id);
		deleteLine("organization", "id", id);
		deleteLine("usrorg", "userid", id);
	}

	public static void addLine(String tableName, String fieldNames,
		String fieldValues) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("insert");
		namedSQL.setParam("tableName", tableName);
		namedSQL.setParam("fieldNames", fieldNames);
		namedSQL.setParam("fieldValues", "'" + fieldValues + "'");
		SQLRunner.execSQL(namedSQL);
	}

	public static void deleteLine(String tableName, String field, String value) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("deleteByCriteria");
		namedSQL.setParam("tableName", tableName);
		namedSQL.setParam("filter", field + "='" + value + "'");

		SQLRunner.execSQL(namedSQL);
	}
	
	//user_C
	protected void addUser() throws Exception {
		String rolecode = null;
		String loginname = dataPool.getParameter("loginname").getStringValue();
		String password = dataPool.getParameter("password").getStringValue();
		String chinaname = dataPool.getParameter("chinaname").getStringValue();
		String englishname = dataPool.getParameter("englishname").getStringValue();
		String gender = dataPool.getParameter("gender").getStringValue();
		String telephone = dataPool.getParameter("telephone").getStringValue();
		String email = dataPool.getParameter("email").getStringValue();
		String team = dataPool.getParameter("team").getStringValue();
		String type = dataPool.getParameter("type").getStringValue();
		String area = dataPool.getParameter("area").getStringValue();
		String active = Util.booleanToStr(dataPool.getParameter("active").getBooleanValue());
		String filter = "loginname  = " + "'" + loginname +"'";

		//添加usr
		Entity usrEntity = new Entity("usr");
		usrEntity.set("loginname", loginname);
		usrEntity.set("password", password);
		usrEntity.set("englishname", englishname);
		usrEntity.set("chinaname", chinaname);
		usrEntity.set("gender", gender);
		usrEntity.set("email", email);
		usrEntity.set("telephone", telephone);
		usrEntity.set("team", team);
		usrEntity.set("type", type);
		usrEntity.set("area", area);
		usrEntity.set("active",active);
		if (type.equalsIgnoreCase("superadmin")){
			rolecode = "res1";
		} else if (type.equalsIgnoreCase("distributor")){
			rolecode = "res2";
		} else if (type.equalsIgnoreCase("Salesperson")){
			rolecode = "res3";
		} else if (type.equalsIgnoreCase("Supervisor")){
			rolecode = "res4";
		} else if (type.equalsIgnoreCase("RSM")){
			rolecode = "res5";
		}else if (type.equalsIgnoreCase("admin")){
			rolecode = "res6";
		} else if (type.equalsIgnoreCase("op")){
			rolecode = "res7";
		} else if (type.equalsIgnoreCase("commercial")){
			rolecode = "res6";
		}
		
		usrEntity.set("rolecode",rolecode);
		DataHandler.saveLine(usrEntity,filter);
		
		//添加usrorg
		Entity usrorgEntity = new Entity("usrorg");
		usrorgEntity.set("id", loginname);
		usrorgEntity.set("orgid", "1");
		int count = getUserCount(filter,"usr");
		if (count == 0){
			return;
		}
		int oid = getRecentUid(filter, "usr");
		usrorgEntity.set("userid", oid);
		usrorgEntity.set("id", loginname);
		usrorgEntity.set("orgid", 1);
		usrorgEntity.save();
		
		//添加organization
		if (type.equalsIgnoreCase("distributor") || type.equalsIgnoreCase("superadmin") || type.equalsIgnoreCase("op")){
			return;
		} else {
			Entity organizationEntity = new Entity("organization");
			organizationEntity.set("loginname", loginname);
			organizationEntity.set("englishname", englishname);
			organizationEntity.set("chinaname", chinaname);
			organizationEntity.set("email", email);
			organizationEntity.set("telephone", telephone);
			organizationEntity.set("type", type);
			int cnt = getUserCount(filter,"usr");
			if (cnt == 0){
				return;
			}
			int Uid = getRecentUid(filter, "usr");
			organizationEntity.set("id", Uid);
			organizationEntity.save();
		}
	}
	
	private int getRecentUid(String filter, String tableName) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getRecentUid");
		namedSQL.setParam("tableName", tableName);
		namedSQL.setFilter(filter);
		int uid = SQLRunner.getInteger(namedSQL);
		return uid;
	}

	private int getUserCount(String filter,String tableName) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getCount");
		namedSQL.setParam("tableName", tableName);
		namedSQL.setFilter(filter);
		int cnt = SQLRunner.getInteger(namedSQL);
		return cnt;
	}

	protected void editUser() throws Exception {
		String rolecode = null;
		String loginname = dataPool.getParameter("loginname").getStringValue();
		String userid = dataPool.getParameter("userid").getStringValue();
		String password = dataPool.getParameter("password").getStringValue();
		String chinaname = dataPool.getParameter("chinaname").getStringValue();
		String englishname = dataPool.getParameter("englishname").getStringValue();
		String gender = dataPool.getParameter("gender").getStringValue();
		String telephone = dataPool.getParameter("telephone").getStringValue();
		String email = dataPool.getParameter("email").getStringValue();
		String type = dataPool.getParameter("type").getStringValue();
		String area = dataPool.getParameter("area").getStringValue();
		String team = dataPool.getParameter("team").getStringValue();
		String active = Util.booleanToStr(dataPool.getParameter("active").getBooleanValue());
		
		//更新usr
		Entity usrEntity = new Entity("usr");
		usrEntity.set("loginname", Util.nullStr(loginname));
		usrEntity.set("password", Util.nullStr(password));
		usrEntity.set("englishname", Util.nullStr(englishname));
		usrEntity.set("chinaname", Util.nullStr(chinaname));
		usrEntity.set("gender", Util.nullStr(gender));
		usrEntity.set("email", Util.nullStr(email));
		usrEntity.set("telephone", Util.nullStr(telephone));
		usrEntity.set("type", Util.nullStr(type));
		usrEntity.set("area", Util.nullStr(area));
		usrEntity.set("team", Util.nullStr(team));
		usrEntity.set("active", Util.nullStr(active));
		
		if (type.equalsIgnoreCase("superadmin")){
			rolecode = "res1";
		} else if (type.equalsIgnoreCase("distributor")){
			rolecode = "res2";
		} else if (type.equalsIgnoreCase("Salesperson")){
			rolecode = "res3";
		} else if (type.equalsIgnoreCase("Supervisor")){
			rolecode = "res4";
		} else if (type.equalsIgnoreCase("RSM")){
			rolecode = "res5";
		} else if (type.equalsIgnoreCase("admin")){
			rolecode = "res6";
		} else if (type.equalsIgnoreCase("op")){
			rolecode = "res7";
		} else if (type.equalsIgnoreCase("commercial")){
			rolecode = "res6";
		}
		
		usrEntity.set("rolecode",rolecode);
		String filters = "userid  = " + "'" + userid +"'";
		DataHandler.saveLine(usrEntity,userid, filters);
		
		//organization_U
		Entity organizationEntity = new Entity("organization");
		organizationEntity.set("loginname", Util.nullStr(loginname));
		organizationEntity.set("englishname", Util.nullStr(englishname));
		organizationEntity.set("chinaname", Util.nullStr(chinaname));
		organizationEntity.set("email", Util.nullStr(email));
		organizationEntity.set("telephone", Util.nullStr(telephone));
		organizationEntity.set("type", Util.nullStr(type));
		String filter = "id  = " + "'" + userid +"'";
		DataHandler.cascadeSaveLine(organizationEntity,userid, filter);
	}
	
	// 首页公告
	protected void getNews() throws Exception {
		String id = dataPool.getParameter("id").getStringValue();

		Entity entity = DataHandler.getLine("announcement", id);

		resultPool.addValue("title", entity.getString("title"));
		resultPool.addValue("contents", URLEncoder.encode(entity.getString("contents"), "UTF-8"));
	}

	protected void getFileStep() {
		String taskid = dataPool.getParameter("taskid").getStringValue();
		FileTask filetask = Monitor.getStatus(taskid);
		
		if (filetask != null) {
			resultPool.addValue("filetask", filetask);
		}
	}
	
	protected void clearFileStep() {
		String taskid = dataPool.getParameter("taskid").getStringValue();
		Monitor.endTask(taskid);
	}
	
	protected void downloadFile() throws Exception {
		String filename = dataPool.getParameter("filename").getStringValue();
		
		File file = new File(Configer.getPath_Temp(onlineUser.getName()));
		if (!file.exists()) {
			return;
        }
		 
		file = new File(Configer.getPath_Temp(onlineUser.getName()), filename);
		//FileWriter writer = FileWriter.createInstance(FileType.Unknown);
		 HttpResponseWriter writer = new HttpResponseWriter(response);
		// String houzhui = filename.substring(filename.indexOf(".") + 1);
		 writer.write(file, ClientAction.SaveAs);

	}
	
	protected void export() throws Exception {
		//1. create task
		String taskid = dataPool.getParameter("taskid").getStringValue();
		Monitor.createTask(taskid);

		//2. send request to web-man
		String sql = null;
		ArrayList<Sql> sqls = new ArrayList<Sql>();
		String code = dataPool.getParameter("code").getStringValue();
		String fileExtension = dataPool.getParameter("fileExtension").getStringValue();
		String fileName = dataPool.getParameter("fileName").getStringValue();
		String sqlName = dataPool.getParameter("sqlName").getStringValue();
		String filterList = URLDecoder.decode(dataPool.getParameter("filterList").getStringValue(), "UTF-8");
		String startDate = dataPool.getParameter("startDate").getStringValue();
		String endDate = dataPool.getParameter("endDate").getStringValue();
	
		NamedSQL namedsql = NamedSQL.getInstance(sqlName);
		sql = getSql(namedsql,filterList,startDate,endDate); 
		
		logger.debug(sql);
				
		FileParams fileParams = new FileParams();
		fileParams.setCode(code);
		fileParams.setFileExtension(fileExtension);
		fileParams.setFileName(fileName);
		Sql sq =  fileParams.new Sql();
		sq.setBody(sql);
		sq.setName(sqlName);		
		sqls.add(sq);
		fileParams.setSqls(sqls);
		
		Gson gson = new Gson();
		String json = gson.toJson(fileParams);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(URL_WebMan + "FileCenter/ExportJson");
		httppost.addHeader(HTTP.CONTENT_TYPE, "application/json");
		
//		JsonObject jsobj = new JsonParser().parse(fileParams.getString()).getAsJsonObject();
		StringEntity paramsEntity = new StringEntity(json, Charset.forName("UTF-8"));
		paramsEntity.setContentType("text/json");
		paramsEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
		httppost.setEntity(paramsEntity);
		
		Monitor.setStatus(taskid, "getData");
		CloseableHttpResponse webmanResponse = httpclient.execute(httppost);
		
		//3. do response
		if (webmanResponse != null && webmanResponse.getStatusLine().getStatusCode() == 200) {
			Monitor.setStatus(taskid, "fileCreated");
			
			try {  
                HttpEntity entity = webmanResponse.getEntity();  
                
                if (entity != null) {  
                	byte[] byteArray = EntityUtils.toByteArray(entity);
                	
                	Header header = webmanResponse.getFirstHeader("Content-Disposition");
         			response.setHeader(header.getName(), header.getValue());
         			//ServletOutputStream outputStream = response.getOutputStream();
         			
         			File file = new File(Configer.getPath_Temp(onlineUser.getName()));
         			if (!file.exists()) {
         				file.mkdirs();
         	        }
         			 
         			file = new File(Configer.getPath_Temp(onlineUser.getName()), URLDecoder.decode(fileName, "UTF-8")+".xlsx");
         	        // 创建文件
         	        if (!file.exists()) {
         	            try {
         	            	file.createNewFile();
         	            } catch (Exception e) {
         	                e.printStackTrace();
         	            }
         	        }
         	        
         			FileOutputStream fileOutputStream = new FileOutputStream(file);
         			fileOutputStream.write(byteArray);
         			fileOutputStream.flush();
         			Monitor.set(taskid, "downloading", file.getName());
         			
         			logger.debug("end of call web man, file is :" + file);
         			
         			Monitor.setStatus(taskid, "downloading");
        		}
                
            } 
			catch(Exception e) {
				Monitor.setStatus(taskid, "error");
			} 
			finally { 
            	webmanResponse.close();  
            }  
		}
	}

	private String getSql(NamedSQL namedSQL, String filterList, String startDate, String endDate) throws Exception {
		// 根据导出的条件对SQL进行重置
		String tempsql = namedSQL.getOriginalSql().toLowerCase();
		tempsql = "select " + tempsql.substring(tempsql.indexOf("*"),tempsql.indexOf("@{filter}")) + "@{filterList}" 
					+ tempsql.substring(tempsql.indexOf("@{filter}") + 9);
		namedSQL.setSql(tempsql);
		for (VariantSegment variant : namedSQL) {
			String name = variant.getName();
			String value = locateSQLVariant(name);
			
			if ("beginno".equalsIgnoreCase(name)){
				value = String.valueOf("0");
			}
			else if ("endno".equalsIgnoreCase(name)){
				int count = getTotalCount(namedSQL);
				value = String.valueOf(count);
			}
			else if ("pageFilter".equalsIgnoreCase(name)){
				value = "1=1";
			}
			else if ("filterList".equalsIgnoreCase(name)){
				value = filterList;
			}
			else if ("startDate".equalsIgnoreCase(name)){
				value = startDate;
			}
			else if ("endDate".equalsIgnoreCase(name)){
				value = endDate;
			}
			
			if (value == null) {
				logger.error("execute procedure error, empty param: " + name);
			}
			
			if (name.equalsIgnoreCase("filter") && value == null) {
				value = "1=1";
			}
			
			variant.setValue(value);
		}
		
		String sql = namedSQL.getSQLString();
		return sql;
	}
	
	private int getTotalCount(NamedSQL namedSQL) throws Exception {
		String sql = namedSQL.getCountSQL();
		namedSQL = new NamedSQL("getCount", sql, false);
		
		for (VariantSegment variant : namedSQL) {
			String name = variant.getName();
			String value = locateSQLVariant(name);
			
			if (value == null) {
				logger.error("execute procedure error, empty param: " + name);
			}
			if (name.equalsIgnoreCase("filter") && value == null) {
				value = "1=1";
			}
			variant.setValue(value);
		}
		
		int count = SQLRunner.getInteger(namedSQL);
		return count;
	}

	protected String getProErroMsg(String tableName, String orderby) throws Exception {
		String msg = null;
		NamedSQL namedSQL = NamedSQL.getInstance("getEmsg");
		namedSQL.setTableName(tableName);
		namedSQL.setOrderBy(orderby);
		Entity entity = SQLRunner.getEntity(namedSQL);

		if (entity != null) {
			msg = entity.getString("Msg");
			return msg;
		} else
			return null;
	}

	protected void uplodeFile() throws Exception {
		boolean res = false;
		String msg = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		servletFileUpload.setHeaderEncoding("UTF-8");
		List<FileItem> items;
		items = servletFileUpload.parseRequest(request);

		if (items.isEmpty()) {
			resultPool.error("请选择上传文件");
			return;
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
		String now = format.format(new Date());
		String filesPath = Configer.getParam("uploadFilePath") + "\\" + now;
		String fromPage = dataPool.getParameter("frompage").getStringValue();
		String pro = dataPool.getParameter("pro").getStringValue();

		if (fromPage != null || pro != null) {
			for (FileItem fileItem : items) {
				String fileName = fileItem.getName().substring(
						fileItem.getName().lastIndexOf("\\") + 2);
				;
				byte[] bs = fileItem.get();
				Util.getFile(bs, filesPath, fileName);
			}

			if (!dataPool.getParameter("toDB").isEmpty()) {
				res = upload2DB(filesPath, fromPage, pro);
			}

			if (!res) {
				msg = getProErroMsg("dataOutput", "errortime");
				resultPool.addValue("path", filesPath);
				resultPool.addValue("res", res);
				resultPool.addValue("fromPage", fromPage);
				resultPool.addValue("msg", msg);
			} else {
				resultPool.addValue("path", filesPath);
				resultPool.addValue("res", res);
				resultPool.addValue("fromPage", fromPage);
			}
		} else {
			for (FileItem fileItem : items) {
				String fileName = fileItem.getName().substring(
				fileItem.getName().lastIndexOf("\\") + 2);
				byte[] bs = fileItem.get();
				Util.getFile(bs, filesPath, fileName);
			}

			if (!dataPool.getParameter("toDB").isEmpty()) {
				upload2DB(filesPath);
			}

			resultPool.addValue("path", filesPath);
		}
	}

	protected boolean upload2DB(String filesPath, String fromPage, String pro) {
		boolean res = false;
		try {
			HttpClient httpclient = HttpClients.createDefault();
			String url = "http://localhost:3456/api/FileCenter/Upload?filespath=" + filesPath + "&code=" + fromPage;
			String replace = url.replace("\\", "/");
			HttpGet httpget = new HttpGet(replace);
			HttpResponse httpResponse = httpclient.execute(httpget);
			StatusLine statusLine = httpResponse.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.OK.value()) {
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				JsonObject returnData = new JsonParser().parse(strResult).getAsJsonObject();
				Boolean success = returnData.get("Success").getAsBoolean();

				if (success) {
					String sql = "{" + "call" + " " + pro + "(?,?)" + "}";
					res = SQLRunner.execProcedure(null, sql);
				} else {
					JsonObject Error = (JsonObject) returnData.get("Error");
					String msg = Error.get("Message").getAsString();
					addLine("dataOutput", "Msg", msg);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	protected void upload2DB(String filesPath) {
		try {
			HttpClient httpclient = HttpClients.createDefault();
			String url = "http://localhost:3456/api/FileCenter/Upload?filespath="
					+ filesPath;
			String replace = url.replace("\\", "/");
			HttpGet httpget = new HttpGet(replace);
			HttpResponse httpResponse = httpclient.execute(httpget);
			StatusLine statusLine = httpResponse.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.OK.value()) {
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				JsonObject returnData = new JsonParser().parse(strResult)
						.getAsJsonObject();
				Boolean success = returnData.get("Success").getAsBoolean();
				/*
				 * JsonArray array=returnData.get("Files").getAsJsonArray();
				 * String filepath = ""; JsonArray asJsonArray =
				 * returnData.getAsJsonArray("Files"); for(int
				 * i=0;i<array.size();i++){
				 * System.out.println("---------------"); JsonObject
				 * subObject=array.get(i).getAsJsonObject(); filepath =
				 * subObject.get("FilePath").getAsString(); } File file = new
				 * File(filepath);
				 * 
				 * if (!file.exists()) { resultPool.error("file not exists: " +
				 * file); return; }
				 */

				/*
				 * HttpResponseWriter writer = new HttpResponseWriter(response);
				 * writer.write(file, ClientAction.AsExcel);
				 */
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//根据实际需要，每次聚合时聚合最近三个月的数据
	public void aggData() {
		int year;
		int month;
		int[] date = Util.getCurYearMonth();
		if(null != dataPool){//手工聚合
			// 获取请求参数
			year = dataPool.getParameter("year").getIntValue();
			month = dataPool.getParameter("month").getIntValue();
		}else{//定时任务自动聚合
			year = date[0]; 
			month = date[1];
		}
		int year1;
		int year2;
		int month1;
		int month2;
		if(month == 2){
			month1 = 1;
			month2 = 12;
			year1 = year;
			year2 = year-1;
		}else if(month == 1){
			month1 = 12;
			month2 = 11;
			year1 = year-1;
			year2 = year-1;
		}else{
			month1 = month-1;
			month2 = month-2;
			year1 = year;
			year2 = year;
		}
		execute(year, month);
		execute(year1, month1);
		execute(year2, month2);
	}
	// 聚合数据
	public void execute(int year, int month) {
		
		try{
			long startime=  System.currentTimeMillis();
			logger.debug("---------------聚合开始，聚合数据：" + year + "年" + month + "月--------------");
			List<AggDefination> definationList = getDefinationList();
			NamedSQL namedSQL = null;
			for (AggDefination defination: definationList) {
				//1. 根据日期维度删除表中的数据
				if(defination.isData() && defination.isFirst()){
					if("Agg_InventorySum".equals(defination.getTableName())){
						namedSQL = NamedSQL.getInstance("deleteAggTableByDate");
						namedSQL.setTableName(defination.getTableName());
						namedSQL.setParam("date", Util.newDateStr());
					} else if("Agg_Terminal_Quantity".equals(defination.getTableName())){
						namedSQL = NamedSQL.getInstance("deleteAggTableByYear");
						namedSQL.setTableName(defination.getTableName());
						namedSQL.setParam("year", year);
					} else{
						namedSQL = NamedSQL.getInstance("deleteAggTableByData");
						namedSQL.setTableName(defination.getTableName());
						namedSQL.setParam("year", year);
						namedSQL.setParam("month", month);
					}
					SQLRunner.execSQL(namedSQL);
				} else if((!defination.isData()) && (!defination.isFirst())){
					namedSQL = NamedSQL.getInstance("deleteAggTable");
					namedSQL.setTableName(defination.getTableName());
					SQLRunner.execSQL(namedSQL);
				} 
				
				//2. 聚合数据并插入对应的表中
				namedSQL = NamedSQL.getInstance(defination.getSqlName());
				namedSQL.setParam("year", year);
				namedSQL.setParam("month", month);	
				namedSQL.setParam("quarter", Util.getQuarterByMonth(String.valueOf(month)));
				namedSQL.setParam("date", Util.newDateStr());
				SQLRunner.execSQL(namedSQL);
			}
			long endtime=  System.currentTimeMillis();
			logger.debug("本次聚合用时时长：" + String.valueOf((endtime-startime)/1000) + "s");
		}catch (Exception e) {
			resultPool.error("聚合数据发生异常！");
			e.printStackTrace();
		}
	}		
	
	//获取需要聚合的表名和SQL集合
	private  List<AggDefination> getDefinationList() {
		List<AggDefination> result = new ArrayList<AggDefination>();
		result.add(new AggDefination("Agg_InventorySum", "Agg_InventorySum", true, true));
		result.add(new AggDefination("Agg_VolatilityReport", "Agg_VolatilityReport", true, true));
		result.add(new AggDefination("Agg_Terminal_Quantity", "Agg_Terminal_Quantity", true, true));
		//result.add(new AggDefination("Agg_Terminal_Quantity_2018", "Agg_Terminal_Quantity_2018", true, true));
		
		result.add(new AggDefination("Agg_Distributor_Target", "Agg_Distributor_Roxolid_Target", true, true));
		result.add(new AggDefination("Agg_Distributor_Target", "Agg_Distributor_Target", true, false));
		
		result.add(new AggDefination("Agg_Sales_Target", "Agg_Sales_Roxolid_Target", true, true));
		result.add(new AggDefination("Agg_Sales_Target", "Agg_Sales_Target", true, false));
		
		result.add(new AggDefination("Agg_Distributor_SellIn", "Agg_Distributor_Roxolid_SellIn", true, true));
		result.add(new AggDefination("Agg_Distributor_SellIn", "Agg_Distributor_SellIn", true, false));
		result.add(new AggDefination("Agg_Supervisor_Target", "Agg_Supervisor_Roxolid_Target", true, true));
		result.add(new AggDefination("Agg_Supervisor_Target", "Agg_Supervisor_Target", true, false));
		
		result.add(new AggDefination("Agg_Terminal_SellOut4kpi", "Agg_terminal_Roxolid_SellOut4kpi", true, true));
		result.add(new AggDefination("Agg_Terminal_SellOut4kpi", "Agg_terminal_SellOut4kpi", true, false));
		result.add(new AggDefination("Agg_SellOut_PX", "Agg_SellOut_A_PX", true, true));
		
		//以下的SQL聚合时没有日期参数
		result.add(new AggDefination("PH4Category", "PH4Category", false, false));
		result.add(new AggDefination("InventorySumView", "Agg_straumann_InventorySum", false, false));
		result.add(new AggDefination("Agg_InventorySumRefresh", "Agg_InventorySumRefresh", false, false));
		result.add(new AggDefination("Agg_TotalSales", "Agg_TotalSales", false, false));
		result.add(new AggDefination("Agg_DistributorInvoicingDetails", "Agg_DistributorInvoicingDetails", false, false));
		result.add(new AggDefination("Agg_DistributorTotalSales", "Agg_DistributorTotalSales", false, false));
		result.add(new AggDefination("Agg_InvoicingComparisonTotal", "Agg_InvoicingComparisonTotal", false, false));
		return result;
	}
	
	
	
}
