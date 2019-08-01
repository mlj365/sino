package report;

public class AggDefination {
	private String tableName;
	private String sqlName;
	private boolean isData;
	private boolean isFirst;
	
	
	public AggDefination(String tableName, String sqlName, boolean isData, boolean isFirst) {
		super();
		this.tableName = tableName;
		this.sqlName = sqlName;
		this.isData = isData;
		this.setFirst(isFirst);
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSqlName() {
		return sqlName;
	}
	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}
	public boolean isData() {
		return isData;
	}
	public void setData(boolean isData) {
		this.isData = isData;
	}
	public boolean isFirst() {
		return isFirst;
	}
	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	
}
