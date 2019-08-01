package bi.work;

import foundation.config.ConfigLoader;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.SystemCondition;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;

public class ActivePeriodLoader extends ConfigLoader {

	private boolean active;
	private String name;
	
	@Override
	public void load() throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getDataSet");
		namedSQL.setTableName("workperiod");
				
		EntitySet entitySet = SQLRunner.getEntitySet(namedSQL);
		
		if (entitySet.isEmpty()) {
			throw new Exception("empty active period");
		}
		
		Entity entity = entitySet.next();
		
		ActivePeriod.getInstance().year = entity.getInteger("openyear");
		ActivePeriod.getInstance().month = entity.getInteger("openmonth");
		ActivePeriod.getInstance().calculateYear = entity.getInteger("calyear");
		ActivePeriod.getInstance().calculateMonth = entity.getInteger("calmonth");	
		
		String condition = entity.getInteger("calyear") == null ? "init" : "working";
		SystemCondition.setValue(condition);
	}
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public String getName() {
		return name;
	}
}
