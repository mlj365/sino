package bi.define;

import foundation.data.Entity;

public class Measurment {
	
	private String id;
	private String name;
	private String code;
	private String formula;
	
	public Measurment(Entity entity) {
		this.id = entity.getString("id");
		this.name = entity.getString("name");
		this.code = entity.getString("code");
		this.formula = entity.getString("formula");
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getFormula() {
		return formula;
	}
	
}
