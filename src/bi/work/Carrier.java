package bi.work;

import foundation.data.Entity;
import foundation.persist.sql.ISQLString;

import java.util.HashMap;
import java.util.Map;


public class Carrier implements ISQLString {
	private String objectName;
	private String operator;
	private String condition;
	private Map<String, String> params;
	
	public Carrier() {
		params = new HashMap<String, String>();
	}
	
	public String getCondition() {
		return condition;
	}
	
	public String getObjectName() {
		return objectName;
	}

	public String getOperator() {
		return operator;
	}

	@Override
	public String toString() {
		return operator + " " + condition;
	}

	public void load(Entity entity) {
		objectName = entity.getString("objectname");
		operator = entity.getString("operator");
		condition = entity.getString("condition");
		
		int idx_begin = operator.indexOf("(");
		int idx_end = operator.indexOf(")", idx_begin);
		
		if (idx_begin >= 0 && idx_end >= 0) {
			String paramString = operator.substring(idx_begin + 1, idx_end);
			paramString = paramString.replace(",", ";").replace(" ", "");
			
			String[] expressions = paramString.split(";");
			for (String expression: expressions) {
				String[] segments = expression.split("=");
				
				if (segments.length == 2) {
					params.put(segments[0].toLowerCase(), segments[1]);
				}
			}
			
			operator = operator.substring(0, idx_begin);
		}
		
	}

	@Override
	public String getSqlString(String name) {
		if (name == null) {
			return null;
		}
		
		return params.get(name.toLowerCase());
	}
}
