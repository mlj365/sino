package foundation.persist;

import foundation.callable.Callable;
import foundation.callable.NewObjectWriter;
import foundation.config.Configer;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.data.Page;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.Result;
import foundation.persist.sql.ReturnType;
import foundation.persist.sql.SQLRunner;
import foundation.server.Sysparam;
import foundation.util.Util;
import foundation.variant.VariantSegment;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataObject extends Callable {

	private static NewObjectWriter newObjectWriter = new NewObjectWriter();
	private String data;
	private String operator;

	protected void doReceive(String[] paths) throws Exception {
		try {
 			if (paths.length >= 3) {
				data = paths[1];
				operator = paths[2];

				if ("procedure".equalsIgnoreCase(data)) {
					execProcedure();
					resultPool.success();
				}
				else {
					if ("getDataSet".equalsIgnoreCase(operator)) {
						try {
							String filter = getFilter(data);
							String orderby = getOrderBy();
							EntitySet entitySet = DataHandler.getDataSet(data, filter, orderby);

							resultPool.addValue(entitySet);
						}
						catch (Exception e) {
							e.printStackTrace();
							throw e;
						}
					}
					else if ("getSetByPage".equalsIgnoreCase(operator)) {
						String filter = getFilter(data);
						int totalCount = DataHandler.getCount(data, filter);
						String orderby = getOrderBy();
						Page page = getPage(totalCount);
						EntitySet entitySet = DataHandler.getDataSetByPage(data, filter, page, orderby);
						
						for (Entity entity : entitySet) {
							String atachment = entity.getString("Contents");
							
							if(!Util.isEmptyStr(atachment)){
								String encode = URLEncoder.encode(atachment);
								entity.set("Contents", encode);
								
							}
						}
						resultPool.addValue("page", page);
						resultPool.addValue(entitySet);
					}
					else if ("getSetByPage4annoucement".equalsIgnoreCase(operator)) {
						String filter = getFilter(data);
						int totalCount = DataHandler.getCount(data, filter);
						Page page = getPage(totalCount);
						EntitySet entitySet = DataHandler.getSetByPage4annoucement(data, filter, page);
						
						for (Entity entity : entitySet) {
							String atachment = entity.getString("Contents");
							
							if(!Util.isEmptyStr(atachment)){
								String encode = URLEncoder.encode(atachment);
								entity.set("Contents", encode);
								
							}
						}
						resultPool.addValue("page", page);
						resultPool.addValue(entitySet);
					}
					else if ("getComboboxDataSet".equalsIgnoreCase(operator)) {
						String filter = getFilter(data);
						String orderby = getOrderBy();
						EntitySet entitySet = DataHandler.getDataSet(data, filter, orderby);
						resultPool.addValue(entitySet);
					}
					else if ("getLine".equalsIgnoreCase(operator)) {
						String id = getId();
						Entity entity = DataHandler.getLine(data, id);
						resultPool.addValue(entity);
					}
					else if ("deleteById".equalsIgnoreCase(operator)) {
						String id = getId();
						DataHandler.deleteById(data, id);
						resultPool.success();
					}
					else if ("deleteByPrimaryKeys".equalsIgnoreCase(operator)) {
						deleteByPrimaryKeys();
					}
					else if ("newObject".equalsIgnoreCase(operator)) {
						resultPool.addValue("line", newObjectWriter);
					}
					else if ("addLine".equalsIgnoreCase(operator)) {
						Entity entity = new Entity(data);
						getEntity(entity);

						DataHandler.addLine(entity);
						resultPool.success();
					}
					else if ("addLineNoId".equalsIgnoreCase(operator)) {
						Entity entity = new Entity(data);
						getEntity(entity, false);

						DataHandler.addLine(entity);
						resultPool.success();
					}
					else if ("saveLine".equalsIgnoreCase(operator)) {
						Entity entity = new Entity(data);
						getEntity(entity);
						
						DataHandler.saveLine(entity);
						resultPool.success();
					}
					else if ("dataExists".equalsIgnoreCase(operator)) {
						int cnt = getDataCount(data);
						
						resultPool.addValue("exists", cnt > 0);
						resultPool.success();
					}
					else if ("saveLineByKeys".equalsIgnoreCase(operator)) {
						Entity entity = new Entity(data);
						getEntity(entity, false);
						Map<String, String[]> parameterMap = request.getParameterMap();
						
						String key = request.getParameter("key");
						String parameter = request.getParameter("clientcode");
						String filters = combinationFilters(key);
						try {
							DataHandler.saveLine(entity,key, filters);
						} catch (Exception e) {
							resultPool.error("error","存在重复键值！");
						}
						
						resultPool.success();
					}
					else if ("getCount".equalsIgnoreCase(operator)) {
						String filter = getFilter(data);
						int totalCount = DataHandler.getCount(data, filter);
						resultPool.addValue(totalCount);
					}
					else if ("getSysparams".equalsIgnoreCase(operator)) {
						List<Sysparam> list = Configer.getClientSysparams();
						resultPool.addValue(list);
					}
				}
			}
			else {
				writer.ReplyError("bad data message path:" + fullPath);
			}			
		}
		catch (Exception e) {
			logger.error("execute dataobject error: " + fullPath);
			throw e;
		}
	}
	
	private int getDataCount(String dataname) throws Exception {
		String filter = dataPool.getParameter("filter").getStringValue();
		
		NamedSQL namedSQL = NamedSQL.getInstance("getCount");
		namedSQL.setTableName(dataname);
		namedSQL.setFilter(filter);
		
		return SQLRunner.getInteger(namedSQL);
	}
	
	private void deleteByPrimaryKeys() throws Exception {
		String key = request.getParameter("key");
		
		String filter = combinationFilters4remove(key);
		
		EntitySet dataSet = DataHandler.getDataSet(data, filter);
		Entity first = dataSet.next();
		DataHandler.deleteLine(first, key, filter);
		
	}

	private String combinationFilters(String key) {
		String[] keys = Util.split(key);
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < keys.length; i++) {
			String keyString = keys[i];
			String valueString = Util.isEmptyStr(request.getParameter(keyString)) ? request.getParameter(keyString) : request.getParameter(keyString.toLowerCase());
			
			if (valueString == null) {
				valueString = request.getParameter("value");
			}
			
			if (valueString == null) {
				return "-1";
			}
			
			if (i == keys.length-1) {
				if (keyString.equals("ID") || keyString.equals("Id")) {
					builder.append(keyString + "=" + valueString);
				}
				else {
					builder.append(keyString + "=" + Util.quotedStr(valueString));
				}
				continue;
			}
			
			if (keyString.equals("ID") || keyString.equals("Id")) {
				builder.append(keyString + "=" + valueString);
			}
			else {
				builder.append(keyString + "=" + Util.quotedStr(valueString) + " and ");
			}
			
		}

		return builder.toString();
	}

	private String combinationFilters4remove(String key) {
		String[] keys = Util.split(key);
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < keys.length; i++) {
			String keyString = keys[i];
			String valueString = Util.isEmptyStr(request.getParameter(keyString)) ? request.getParameter(keyString) : request.getParameter(key.toLowerCase());
			
			if (valueString == null) {
				valueString = request.getParameter("value").toString();
				String[] valueList = valueString.split(",");
				valueString = valueList[i];
			}
			
			if (valueString == null) {
				return "-1";
			}
			
			if (i == keys.length-1) {
				if (keyString.equals("ID") || keyString.equals("Id")) {
					builder.append(keyString + "=" + valueString);
				}
				else {
					builder.append(keyString + "=" + Util.quotedStr(valueString));
				}
				continue;
			}
			
			if (keyString.equals("ID") || keyString.equals("Id")) {
				builder.append(keyString + "=" + valueString);
			}
			else {
				builder.append(keyString + "=" + Util.quotedStr(valueString) + " and ");
			}
			
		}

		return builder.toString();
	}
	
	private void execProcedure() throws Exception {
		ReturnType returnType = ReturnType.EntitySet;
		if (paths.length > 3) {
			returnType = ReturnType.valueOfString(paths[3]);
		}

		NamedSQL namedSQL = NamedSQL.getInstance(operator);
		namedSQL.setReturnType(returnType);
		
		boolean reset = false;
		Set<String> pageVariants = Page.getVarinatNameSet();
		
		//set parameter for first time
		for (VariantSegment variant : namedSQL) {
			String name = variant.getName();
			
			if (pageVariants.contains(name.toLowerCase())) { 
				reset = true;
				continue;
			}
			
			String value = locateSQLVariant(name);
			
			if (value == null) {
				logger.error("execute procedure error, empty param: " + name);
				return;
			}
			
			variant.setValue(value);
		}
		
		//set parameter for second time 
		Page page = null;
		if (reset) {
			int count = getTotalCount(namedSQL); 
			page = getPage(count);
			
			for (VariantSegment variant : namedSQL) {
				String name = variant.getName();
				
				if (!pageVariants.contains(name.toLowerCase())) {
					continue;
				}
				
				String value = page.getStringValue(name);
				
				if (value == null) {
					logger.error("execute procedure error, empty param: " + name);
					return;
				}
				
				variant.setValue(value);
			}
		}

		Result result = namedSQL.exec();
		resultPool.addValue("rows",result.getObject());
		
		if (page != null) {
			resultPool.addValue("page", page);
			resultPool.addValue("total", page.getRecordCount());
		}

		if (ReturnType.None == returnType) {
			resultPool.success();
		}
	}

	private int getTotalCount(NamedSQL namedSQL) throws Exception {
		String sql = namedSQL.getCountSQL();
/*		if (sql.contains("@{filter}")) {
			int filterIndex = sql.indexOf("@{filter}");
			sql = sql.substring(0,filterIndex)+" 1=1 "+sql.substring(filterIndex+"@{filter}".length());
		}*/
			
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
