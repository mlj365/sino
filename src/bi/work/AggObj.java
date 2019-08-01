package bi.work;

import foundation.data.Entity;
import foundation.persist.sql.SQLContext;

public class AggObj extends SQLContext implements IEventSender {

	private String name;

	public AggObj() {
	}
	
	@Override
	public String getSqlString(String paramName, Object... args) {
		if ("typecode".equalsIgnoreCase(paramName)) {
			return name;
		}
		else if ("year".equalsIgnoreCase(paramName)) {
			return String.valueOf(ActivePeriod.getInstance().getYear());
		}
		else if ("month".equalsIgnoreCase(paramName)) {
			return String.valueOf(ActivePeriod.getInstance().getMonth());
		}
		
		return null;
	}

	public String getName() {
		return name;
	}
	
	public void load(Entity entity) {
		name = entity.getString("name");
	}

	@Override
	public String toString() {
		return name;
	}

}
