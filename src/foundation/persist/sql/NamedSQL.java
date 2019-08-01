package foundation.persist.sql;

import foundation.data.Entity;
import foundation.persist.Field;
import foundation.persist.SystemCondition;
import foundation.persist.TableMeta;
import foundation.util.ContentBuilder;
import foundation.util.Util;
import foundation.variant.Expression;
import foundation.variant.IExpression;
import foundation.variant.VariantList;
import foundation.variant.VariantSegment;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


public class NamedSQL implements Iterable<VariantSegment>, IExpression {

	public static final String Param_Schema = "schema";
	public static final String Param_TableName = "tablename";
	public static final String Param_FieldNames = "fieldNames";
	public static final String Param_FieldNameValues = "fieldNameValues";
	public static final String Param_Values = "fieldValues";	
	public static final String Param_PlaceHolders = "placeHolders";
	public static final String Param_Filter = "filter";
	public static final String Param_FieldNamePlaceHolders = "fieldNamePlaceHolders";
	public static final String Param_KeyFieldName = "keyFieldName";
	public static final String Param_OrderBy = "orderby";
	
	private static NamedSQLContainer namedSQLContainer;
	
	protected String name;
	protected String sql;
	protected ReturnType returnType;
	protected Expression expression;
	protected String countSQL;
	
	static {
		namedSQLContainer = NamedSQLContainer.getInstance();
	}

	private NamedSQL(String name) {
		this.name = name;
		returnType = ReturnType.None;
	}
	
	public NamedSQL(String name, String sql) throws Exception {
		this.name = name;
		this.sql = sql;
		
		parseSQL(sql, true);
	}
	
	public NamedSQL(String name, String sql, boolean parseRowNum) throws Exception {
		this.name = name;
		this.sql = sql;
		
		parseSQL(sql, parseRowNum);
	}
	
	protected void parseSQL(String sql, boolean parseRowNum) throws Exception {
		String lower = sql.toLowerCase();
		
		if (parseRowNum) {
			if (lower.indexOf("row_number()") > 0) {
				int pos_order = lower.lastIndexOf("order by");
				
				String temp = null;
				
				String prior = lower.substring(pos_order - 5, pos_order);
				
				if (!"over(".equals(prior)) {
					//解决按照时间不能排序的问题
					int pos_desc = lower.indexOf(" desc", pos_order);
					temp = sql.substring(0, pos_order);
					String orderBy = sql.substring(pos_order);
					if (pos_desc != -1) {
						temp +=  sql.substring(pos_desc + " desc".length());
						orderBy = sql.substring(pos_order, pos_desc + " desc".length());
					}
						
					countSQL = "select count(1) from (" + temp + ") tempTbl";
					sql = "select * from (" + temp + ")table_t where @{pageFilter}" + orderBy;

				}
				else {
					countSQL = "select count(1) from (" + sql + ") tempTbl";
					sql = "select * from (" + sql + ")table_t where @{pageFilter}";
				}
			}
		}
		
		expression = new SQLCreator(sql);
	}

	public String getSQL() throws Exception {
		String result = expression.getString();
		return result;
	}
	
	public void setSql(String sqlString) throws Exception{
		expression = new SQLCreator(sqlString);
	}
	
	public String getCountSQL() throws Exception {
		if (countSQL != null) {
			return countSQL;
		}
		
		return "select count(1) from (" + getSQL() + ") tempTbl";
	}
	
	public static NamedSQL[] getInstance(String[] names) throws Exception {
		if (names == null) { return new NamedSQL[0]; };
		
		NamedSQL[] result = new NamedSQL[names.length];
		
		for (int i = 0; i < names.length; i++) {
			result[i] = getInstance(names[i]);
		}
		
		return result;
	}
	
	public static NamedSQL getInstance(String name) throws Exception {
		String condtion = SystemCondition.getValue();
		
		NamedSQL result = namedSQLContainer.get(name, condtion);
		
		if (result == null) {
			result = namedSQLContainer.get(name, null);
		}
		
		if (result == null) {
			throw new Exception("can not find named sql: " + name);
		}
		
		result = result.newInstance();
		return result;
	}
	
	public NamedSQL newInstance() throws Exception {
		NamedSQL instance = new NamedSQL(this.name);
		instance.sql = this.sql;
		instance.countSQL = this.countSQL;
		instance.expression = this.expression.newInstance();
		
		return instance;
	}

	public String getOriginalSql(){
		return sql;
	}

	public Result exec() throws Exception {
		Result result = SQLRunner.getResult(this);
		return result;
	}
	
	public void setSchema(String schema) {
		setParam(Param_Schema, schema);
	}

	public void setTableName(String tableName) {
		setParam(Param_TableName, tableName);
	}

	public void setFieldNames(String names) {
		setParam(Param_FieldNames, names);
	}
	
	public void setFieldNames(TableMeta tableMeta) {
		StringBuilder result = new StringBuilder();
		boolean empty = true;
		
		for (Field field: tableMeta) {
			if (!empty) {
				result.append(", ");
			}
			
			result.append(field.getName());
			
			empty = false;
		}
		
		setParam(Param_FieldNames, result.toString());
	}
	
	public void setFieldNames(TableMeta tableMeta, Entity entity) {
		StringBuilder result = new StringBuilder();
		boolean empty = true;
		Field field;
		
		for (int i = 0; i < tableMeta.getFieldCount(); i++) {
			if (entity.isEmptyField(i)) {
				continue;
			}
				
			field = tableMeta.get(i);
			
			if (!empty) {
				result.append(", ");
			}
			
			result.append(field.getName());
			empty = false;
		}
		
		
		setParam(Param_FieldNames, result.toString());
	}
	
	public void setFieldNames(Collection<Field> fields) {
		ContentBuilder result = new ContentBuilder();
		
		for (Field field: fields) {
			result.append(field.getName(), ", ");
		}
		
		setParam(Param_FieldNames, result.toString());
	}
	
	public void setValues(Entity entity) throws Exception {
		ContentBuilder result = new ContentBuilder();
		int cnt = entity.getFieldCount();
		
		for (int i = 0; i < cnt; i++) {
			if (entity.isEmptyField(i)) {
				continue;
			}
			
			result.append(entity.getSQLString(i), ", ");
		}
		
		setParam(Param_Values, result.toString());
	}
	
	public void setQuotedFieldNames(TableMeta tableMeta) {
		ContentBuilder result = new ContentBuilder();
		
		for (Field field: tableMeta) {
			result.append(Util.doubleQuotedStr(field.getName()), ", ");
		}
		
		setParam(Param_FieldNames, result.toString());
	}

	public void setPlaceHolders(Collection<Field> fields) {
		ContentBuilder result = new ContentBuilder();
		int cnt = fields.size();
		
		for (int i = 0; i < cnt; i++) {
			result.append("?", ", ");
		}
		
		setParam(Param_PlaceHolders, result.toString());
	}
	
	public void setPlaceHolders(String placeHolders) {
		setParam(Param_PlaceHolders, placeHolders);
	}

	public void setFieldNamePlaceHolders(TableMeta tableMeta) {
		ContentBuilder result = new ContentBuilder();
		
		for (Field field: tableMeta) {
			result.append(field.getName() + " = ? ", ", ");
		}
		
		setParam(Param_FieldNamePlaceHolders, result.toString());
	}

	public void setKeyFieldName(TableMeta tableMeta) throws Exception {
		String keyName = tableMeta.getFiledName_Key();
		setParam(Param_KeyFieldName, keyName);		
	}

	public void setKeyFieldName(String fieldName) throws Exception {
		setParam(Param_KeyFieldName, fieldName);			
	}
	
	public void setFilter(String filter) {
		if (Util.isEmptyStr(filter)) {
			filter = "1=1";
		}
		
		setParam(Param_Filter, filter);	
	}

	public void setOrderBy(String orderby) {
		setParam(Param_OrderBy, orderby);	
	}

	public void setFieldNameValues(Entity entity) throws Exception {
		ContentBuilder result = new ContentBuilder();
		
		TableMeta tableMeta = entity.getTableMeta();
		int cnt = tableMeta.getFieldCount();
		
		for (int i = 0; i < cnt; i++) {
			if (entity.isEmptyField(i)) {
				continue;
			}
			
			Field field = tableMeta.get(i);
			if (field.getName().equalsIgnoreCase("id") ) {
				continue;
			}
			result.append(field.getName() + "=" + entity.getSQLString(i), ", ");
		}
		
		setParam(Param_FieldNameValues, result.toString());
	}

	public void setFieldNameValues(Entity entity, String key) throws Exception {
		ContentBuilder result = new ContentBuilder();
		
		TableMeta tableMeta = entity.getTableMeta();
		int cnt = tableMeta.getFieldCount();
		
		for (int i = 0; i < cnt; i++) {
			if (entity.isEmptyField(i)) {
				continue;
			}
			
			Field field = tableMeta.get(i);
			if (field.getName().equalsIgnoreCase("id") || field.getName().equalsIgnoreCase(key)) {
				continue;
			}
			result.append(field.getName() + "=" + entity.getSQLString(i), ", ");
		}
		
		setParam(Param_FieldNameValues, result.toString());
	}
	
	public void setParam(String name, String value) {
		if (value == null) {
			return;
		}
		
		VariantSegment variant = expression.getVariant(name);
		
		if (variant != null) {
			variant.setValue(value);
		}
	}
	
	public void setParam(String name, String value, String defaultValue) {
		if (value == null) {
			value = defaultValue;
		}
		
		VariantSegment sqllVariant = expression.getVariant(name);
		
		if (sqllVariant != null) {
			sqllVariant.setValue(value);
		}
	}
	
	public void setParam(String name, int value) {
		String stringValue = String.valueOf(value);
		setParam(name, stringValue);
	}
	
	public void setParam(String name, BigDecimal value) {
		String stringValue = value.toString();
		setParam(name, stringValue);		
	}
	
	public void setParam(String name, Date date) {
		String stringValue = Util.toMySQLDateStr(date);
		setParam(name, stringValue);
	}
	
	public void setParam(String name, boolean value) {
		String stringValue = Util.booleanToStr(value);
		setParam(name, stringValue);
	}

	public String getName() {
		return name;
	}

	public ReturnType getReturnType() {
		return returnType;
	}
	

	public void setReturnType(ReturnType returnType) {
		this.returnType = returnType;
	}
	
	public String getSQLString() throws Exception {
		return expression.tryGetString(); 
	}
	
	@Override
	public String toString() {
		return expression.getString();
	}

	@Override
	public Iterator<VariantSegment> iterator() {
		return expression.iterator();
	}

	public void clearVariantValues() {
		expression.clearVariantValues();
	}

	public VariantList getVariantList() {
		return expression.getVariantList();
	}


	
	public Expression getExpression() {
		return expression;
	}
	
	public static void main(String[] args) {
		String sql = "where 1 =1   AND DistributorLevel ='一级商' order by [BizDate] desc";
		sql = sql.toLowerCase();
		int pos_order = sql.indexOf("order by");
		int pos_desc = sql.indexOf(" desc", pos_order);
		String temp = sql.substring(0, pos_order) + sql.substring(pos_desc + " desc".length());
		System.out.println(temp);
	}

	public VariantList getVariantMap() {
		VariantList variantList = expression.getVariantList();
		return variantList;
	}
}
