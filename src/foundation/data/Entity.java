package foundation.data;

import foundation.persist.DataHandler;
import foundation.persist.TableMeta;
import foundation.persist.TableMetaCenter;
import foundation.rule.IRuledValue;
import foundation.rule.RuleType;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class Entity implements IRuledValue {

	private static TableMetaCenter metaCenter = TableMetaCenter.getInstance();
	private TableMeta tableMeta;
	private Object[] dataArray;

	public Entity(TableMeta tableMeta) {
		this.tableMeta = tableMeta;
		dataArray = new Object[tableMeta.getFieldCount()];
	}

	public Entity(String tableName) throws Exception {
		TableMeta tableMeta = metaCenter.get(tableName);

		if (tableMeta == null) {
			throw new Exception("table not exists: " + tableName);
		}

		this.tableMeta = tableMeta;
		dataArray = new Object[tableMeta.getFieldCount()];
	}

	public void set(int i, Object object) {
		dataArray[i] = object;
	}

	public void set(String name, Object object) {
		tableMeta.setObject(dataArray, name, object);
	}

	public void setString(String name, String value) throws ParseException {
		tableMeta.setString(dataArray, name, value);
	}

	public Object getValue(int idx) {
		return dataArray[idx];
	}

	public String getString(String string) {
		try {
			return tableMeta.getString(dataArray, string, null);
		}
		catch (Exception e) {
			return "error";
		}
	}
	public String getId() {
		try {
			return getString("id");
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getString(String string, String defaultValue) {
		try {
			return tableMeta.getString(dataArray, string, defaultValue);
		}
		catch (Exception e) {
			return "error";
		}
	}

	public String getString(int idx, String defaultValue) {
		try {
			return tableMeta.getString(dataArray, idx, defaultValue);
		}
		catch (Exception e) {
			return "error";
		}
	}

	public boolean getBoolean(String string) {
		return tableMeta.getBoolean(dataArray, string);
	}

	public Date getDate(String string) throws ParseException {
		return tableMeta.getDate(dataArray, string);
	}

	public Integer getInteger(String string) {
		return tableMeta.getInteger(dataArray, string);
	}
	
	public BigDecimal getBigDecimal(String string) {
		return tableMeta.getBigDecimal(dataArray, string);
	}

	public String getSchemaString(int i) throws Exception {
		return tableMeta.getSchemaString(dataArray, i);
	}

	public String getJSONString(int i) throws SQLException {
		return tableMeta.getJSONSString(dataArray, i);
	}

	public String getSQLString(int i) throws Exception {
		return tableMeta.getSQLString(dataArray, i);
	}

	public Object getVirtualSchemaString(int i) throws Exception {
		return tableMeta.getSchemaString(dataArray, i);
	}

	public int getFieldCount() {
		return tableMeta.getFieldCount();
	}

	public String[] getLowerNames() {
		return tableMeta.getLowerNames();
	}

	public TableMeta getTableMeta() {
		return tableMeta;
	}

	public boolean isEmptyField(int i) {
		return dataArray[i] == null;
	}

	public void setValueToStatement(int dataIdx, PreparedStatement stmt, int paramIdx) throws SQLException {
		tableMeta.setValueToStatement(dataArray, dataIdx, stmt, paramIdx);
	}

	@Override
	public String getRuledValue() {
		return getString("sql");
	}

	@Override
	public RuleType getRuledType() {
		return RuleType.sql;
	}

	public void insert() throws Exception {
		DataHandler.addLine(this);
	}

	public void update() throws Exception {
		DataHandler.updateLine(this);
	}

	public void delete() throws Exception {
		DataHandler.deleteLine(this);
	}

	public void save() throws Exception {
		DataHandler.saveLine(this);
	}
	
	public void update(Entity entity,String filters) throws Exception {
		DataHandler.updateLine(entity, filters);
	}
}
