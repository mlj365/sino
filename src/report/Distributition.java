package report;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import report.bean.FileParams;
import report.bean.FileParams.Sql;

import com.google.gson.Gson;

import foundation.callable.Callable;
import foundation.config.Configer;
import foundation.data.Entity;
import foundation.persist.DataHandler;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.Util;
import foundation.variant.VariantSegment;

public class Distributition extends Callable {
private static String URL_WebMan = "";
	
	static {
		URL_WebMan = Configer.getParam("URL_WebMan");
	}
	
	protected void publishMethod() {
		addMethod("distributeUndetermined");
		addMethod("apply");
		addMethod("confirm");
		addMethod("export");
	}
	
	public void distributeUndetermined() throws Exception {
		String [] ids = request.getParameterValues("company_unmatchedids[]");
		String type = dataPool.getParameter("type").getStringValue();
		Entity entity = new Entity("company_unmatched");
		if (type.equalsIgnoreCase("manager")) {
			for (String id : ids) {
				String subordinate_code = dataPool.getParameter("subordinate_code").getStringValue();
				String subordinate_name = dataPool.getParameter("subordinate_name").getStringValue();
				String state = dataPool.getParameter("state").getStringValue();
				String filters = "id = " + Util.quotedStr(id);
				entity.setString("directorcode", subordinate_code);
				entity.setString("directorname", subordinate_name);
				entity.setString("state", state);
				entity.setString("id", id);
				DataHandler.saveLine(entity, filters);
			}
		} else if (type.equalsIgnoreCase("salesmen")) {
			for (String id : ids) {
				String subordinate_code = dataPool.getParameter("subordinate_code").getStringValue();
				String subordinate_name = dataPool.getParameter("subordinate_name").getStringValue();
				String state = dataPool.getParameter("state").getStringValue();
				String filters = "id = " + Util.quotedStr(id);
				entity.setString("salecode", subordinate_code);
				entity.setString("salename", subordinate_name);
				entity.setString("state", state);
				entity.setString("id", id);
				DataHandler.saveLine(entity, filters);
			}
		} 
	}
	
	public void apply() throws Exception {
		String id = dataPool.getParameter("ids").getStringValue();
		Entity entity = new Entity("company_unmatched");
				String state = dataPool.getParameter("state").getStringValue();
				String filters = "id = " + Util.quotedStr(id);
				entity.setString("state", state);
				entity.setString("id", id);
				DataHandler.saveLine(entity, filters);
	}
	
	public void confirm() throws Exception {
		String id = dataPool.getParameter("ids").getStringValue();
		String customername = dataPool.getParameter("customername").getStringValue();
		String customercode = dataPool.getParameter("customercode").getStringValue();
		String state = dataPool.getParameter("state").getStringValue();
		String issale = dataPool.getParameter("issale").getStringValue();
		String filters = "id = " + Util.quotedStr(id);
		Entity entity = new Entity("company_unmatched");
		entity.setString("customername", customername);
		entity.setString("customercode", customercode);
		entity.setString("state", state);
		entity.setString("issale", issale);
		entity.setString("id", id);
		DataHandler.saveLine(entity, filters);
	}
	
	protected void export() throws Exception {
		//1. create task
		String taskid = dataPool.getParameter("taskid").getStringValue();
		Monitor.createTask(taskid);

		//2. send request to web-man
		String sql = null;
		String filterlist = null;
		ArrayList<Sql> sqls = new ArrayList<Sql>();
		String code = dataPool.getParameter("code").getStringValue();
		String fileExtension = dataPool.getParameter("fileExtension").getStringValue();
		String fileName = dataPool.getParameter("fileName").getStringValue();
		String sqlName = dataPool.getParameter("sqlName").getStringValue();
		String filterList = URLDecoder.decode(dataPool.getParameter("filterList").getStringValue(), "UTF-8");
		if (filterList.startsWith("filter=")) {
			filterlist = filterList.substring(7);
			NamedSQL namedsql = NamedSQL.getInstance(sqlName);
			sql = getSql(namedsql,URLDecoder.decode(filterList, "UTF-8"));
		} else {
			NamedSQL namedsql = NamedSQL.getInstance(sqlName);
			sql = getSql(namedsql,URLDecoder.decode(filterList, "UTF-8")); 
		}
		
		
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
	
	private String getSql(NamedSQL namedSQL, String filterList) throws Exception {
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
			} else {
				value = "1=1";
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
}
