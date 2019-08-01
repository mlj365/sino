package foundation.persist.sql;


public class SQLString extends SQLSegment {

	private String value;
	
	public SQLString(String sql) {
		value = sql;
	}
	
	@Override
	public String getValueString() {
		return value;
	}

	@Override
	public SQLSegment newInstance() {
		return this;
	}
	
	public String toString() {
		return value;
	}

}
