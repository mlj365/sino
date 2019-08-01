package foundation.persist;

import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.data.Page;
import foundation.persist.loader.EntityLoader;
import foundation.persist.loader.EntitySetLoader;
import foundation.persist.loader.ValueLoader;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.Util;

import java.sql.Connection;

public class DataHandler {

	public static EntitySet getDataSet(String tableName) throws Exception {
		return getDataSet(tableName, null);
	}
	
	public static EntitySet getDataSet(String tableName, String filter) throws Exception {
		return getDataSet(tableName, filter, null);
	}
	
	public static EntitySet getDataSet(String tableName, String filter, String orderby) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getDataSet");

		namedSQL.setTableName(tableName);
		namedSQL.setFilter(filter);

		namedSQL.setOrderBy(orderby);
		
		EntitySetLoader loader = new EntitySetLoader(tableName);
		SQLRunner.getData(namedSQL, loader);
		
		return loader.getDataSet();
	}
	
	public static EntitySet getDataSetByPage(String tableName, String filter, Page page, String orderby) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getSetByPage");
		
		namedSQL.setTableName(tableName);
		namedSQL.setFilter(filter);
		namedSQL.setOrderBy(orderby);		
		String sql = namedSQL.getSQL();
		String[] splitWhere = sql.split("where");
		String[] split = splitWhere[0].split("\\*");
		sql = " SELECT * FROM (select ROW_NUMBER() OVER(order by Id ) as rownum , *"+ split[1]+")tb where"+splitWhere[1]+"and rownum >"+page.getBeginRecordNo_1()+" and rownum <="+page.getEndRecordNo();
		namedSQL.setSql(sql);
		EntitySetLoader loader = new EntitySetLoader(tableName);
		SQLRunner.getData(namedSQL, loader);
		
		return loader.getDataSet();
	}
	public static EntitySet getSetByPage4annoucement(String tableName, String filter, Page page) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getSetByPage");
		
		namedSQL.setTableName(tableName);
		namedSQL.setFilter(filter);
		String sql = namedSQL.getSQL();
		String[] splitWhere = sql.split("where");
		String[] split = splitWhere[0].split("\\*");
		sql = " SELECT * FROM (select ROW_NUMBER() OVER(order by CreateTime DESC ) as rownum , *"+ split[1]+")tb where"+splitWhere[1]+"and rownum >"+page.getBeginRecordNo_1()+" and rownum <="+page.getEndRecordNo();
		namedSQL.setSql(sql);
		EntitySetLoader loader = new EntitySetLoader(tableName);
		SQLRunner.getData(namedSQL, loader);
		
		return loader.getDataSet();
	}

	public static Entity getLine(String tableName, String id) throws Exception {
		TableMeta tableMeta = TableMetaCenter.getInstance().get(tableName);
		
		NamedSQL namedSQL = NamedSQL.getInstance("getLineById");
		namedSQL.setTableName(tableName);
		namedSQL.setParam("fieldNameId", tableMeta.getFiledName_Key());
		namedSQL.setParam("id", Util.quotedStr(id));
		
		EntityLoader loader = new EntityLoader(tableName);
		SQLRunner.getData(namedSQL, loader);
		
		return loader.getEntity();
	}


	public static Entity getLine(String tableName, String fieldNameId, String id) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getLineById");
		namedSQL.setTableName(tableName);
		namedSQL.setParam("fieldNameId", fieldNameId);
		namedSQL.setParam("id", Util.quotedStr(id));

		EntityLoader loader = new EntityLoader(tableName);
		SQLRunner.getData(namedSQL, loader);

		return loader.getEntity();
	}
	public static void deleteById(String tableName, String id) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("deleteById");
		namedSQL.setTableName(tableName);
		namedSQL.setParam("id", id);

		SQLRunner.execSQL(namedSQL);
	}

	public static int getCount(String tableName, String filter) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getCount");
		namedSQL.setTableName(tableName);
		namedSQL.setFilter(filter);
		
		ValueLoader loader = new ValueLoader();
		SQLRunner.getData(namedSQL, loader);
		
		return loader.getInt();
	}

	public static EntitySet getSetByPage(String tableName, String filter, Page page) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getSetByPage");
		namedSQL.setTableName(tableName);
		namedSQL.setFilter(filter);
		namedSQL.setParam("beginNo", page.getBeginRecordNo());
		namedSQL.setParam("endNo", page.getEndRecordNo());
		
		EntitySetLoader loader = new EntitySetLoader(tableName);
		SQLRunner.getData(namedSQL, loader);
		
		return loader.getDataSet();
	}
	
	public static EntitySet getSetByPage(TableMeta tableMeta, String filter, Page page) throws Exception {
		String tableName = tableMeta.getName();
		String fieldNames = tableMeta.getDoubleQuotedFieldNames();
		
		NamedSQL namedSQL = NamedSQL.getInstance("getSetByPage");
		namedSQL.setTableName(tableName);
		namedSQL.setFilter(filter);
		namedSQL.setParam("fieldNames", fieldNames);	
		namedSQL.setParam("beginNo", page.getBeginRecordNo());
		namedSQL.setParam("endNo", page.getEndRecordNo());
		
		EntitySetLoader loader = new EntitySetLoader(tableName);
		loader.setTableMeta(tableMeta);
		SQLRunner.getData(namedSQL, loader);
		
		return loader.getDataSet();
	}
	
	public static EntitySet getSetByPage(TableMeta tableMeta, String fields, String filter, Page page) throws Exception {
		String tableName = tableMeta.getName();
		
		NamedSQL namedSQL = NamedSQL.getInstance("getSetByPage");
		namedSQL.setTableName(tableName);
		namedSQL.setFilter(filter);
		namedSQL.setParam("fieldNames", fields);	
		namedSQL.setParam("beginNo", page.getBeginRecordNo());
		namedSQL.setParam("endNo", page.getEndRecordNo());
		
		EntitySetLoader loader = new EntitySetLoader(tableName);
		loader.setTableMeta(tableMeta);
		SQLRunner.getData(namedSQL, loader);
		
		return loader.getDataSet();
	}

	public static void addLine(Entity entity) throws Exception {
		addLine(null, entity);
	}
	
	public static void addLine(Connection conn, Entity entity) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		
		NamedSQL namedSQL = NamedSQL.getInstance("insert");
		namedSQL.setTableName(tableName);
		namedSQL.setFieldNames(tableMeta, entity);
		namedSQL.setValues(entity);
		
		SQLRunner.execSQL(conn, namedSQL);
	}

	public static void saveLine(Entity entity) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		
		NamedSQL namedSQL = NamedSQL.getInstance("getCountOfId");
		namedSQL.setTableName(tableName);
		namedSQL.setParam("id", Util.quotedStr(entity.getString("id")));
		int cnt = SQLRunner.getInteger(namedSQL);
		
		if (cnt == 0) {
			addLine(entity); 
		}
		else {
			updateLine(entity);
		}
	}
	
	public static void saveLine(Entity entity,String filters) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		
		if (filters.equalsIgnoreCase("-1")) {
			return;
		}
		
		NamedSQL namedSQL = NamedSQL.getInstance("getCount");
		namedSQL.setTableName(tableName);
		namedSQL.setParam("filter", filters);
		int cnt = SQLRunner.getInteger(namedSQL);
		
		if (cnt == 0) {
			addLine(entity); 
		}
		else {
			updateLine(entity);
		}
	}
	
	public static void saveLine(Entity entity, String key, String filters) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		
		if (filters.equalsIgnoreCase("-1")) {
			addLine(entity); 
			return;
		}
		
		NamedSQL namedSQL = NamedSQL.getInstance("getCount");
		namedSQL.setTableName(tableName);
		namedSQL.setParam("filter", filters);
		int cnt = SQLRunner.getInteger(namedSQL);
		
		if (cnt == 0) {
			addLine(entity); 
		}
		else {
			updateLine(entity, key, filters);
		}
	}
	
	public static void cascadeSaveLine(Entity entity, String key, String filter) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		
		if (filter.equalsIgnoreCase("-1")) {
			addLine(entity); 
			return;
		}
		
		NamedSQL namedSQL = NamedSQL.getInstance("getCount");
		namedSQL.setTableName(tableName);
		namedSQL.setParam("filter", filter);
		int cnt = SQLRunner.getInteger(namedSQL);
		
		if (cnt == 0) {
			return;
		}
		else {
			updateLine(entity, key, filter);
		}
	}


	public static void updateLine(Entity entity) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		
		NamedSQL namedSQL = NamedSQL.getInstance("updateById");
		namedSQL.setTableName(tableName);
		namedSQL.setFieldNameValues(entity);
		namedSQL.setParam("fieldNameId", "id");
		namedSQL.setParam("id", Util.quotedStr(entity.getString("id")));
		
		SQLRunner.execSQL(namedSQL);		
	}
	
	public static void updateLine(Entity entity,String filters) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		
		NamedSQL namedSQL = NamedSQL.getInstance("updateBychinaname");
		namedSQL.setTableName(tableName);
		namedSQL.setFieldNameValues(entity);
		namedSQL.setParam("fieldNameId", "chinaname");
		namedSQL.setParam("id", Util.quotedStr(filters));
		
		SQLRunner.execSQL(namedSQL);		
	}
	
	public static void updateLine(Entity entity, String key, String filters)throws Exception  {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		NamedSQL namedSQL = NamedSQL.getInstance("updateByCriteria");
		namedSQL.setTableName(tableName);
		namedSQL.setFieldNameValues(entity, key);
		namedSQL.setParam("filter", filters);
		SQLRunner.execSQL(namedSQL);
	}
	
	
	public static void deleteLine(Entity entity) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		
		NamedSQL namedSQL = NamedSQL.getInstance("deleteById");
		namedSQL.setTableName(tableName);
		namedSQL.setParam("id", entity.getString("id"));
		
		SQLRunner.execSQL(namedSQL);
	}

	public static void deleteLine(Entity entity, String key, String filter) throws Exception {
		TableMeta tableMeta = entity.getTableMeta();
		String tableName = tableMeta.getName();	
		NamedSQL namedSQL = null;
		if (key.equalsIgnoreCase("id")) {
			namedSQL = NamedSQL.getInstance("deleteById");
			namedSQL.setTableName(tableName);
			namedSQL.setParam("id", entity.getString("id"));
		}
		else {
			namedSQL = NamedSQL.getInstance("deleteByCriteria");
			namedSQL.setTableName(tableName);
			namedSQL.setParam("filter", filter);
		}
		
		
		SQLRunner.execSQL(namedSQL);
	}

}
