package bi.define;

import foundation.data.Entity;

public class AggField {
	
	private String id;
	private String name;
	private String direction;
	private String groupCode;
	private String typeCode;

	public AggField(Entity entity) {
		this.id = entity.getString("id");
		this.name = entity.getString("name");
		this.groupCode = entity.getString("groupcode");
		this.typeCode = entity.getString("typecode");
		this.direction = entity.getString("direction");
		
	}

	public AggField(String name, String typeCode, String direction) {
		this.name = name;
		this.typeCode = typeCode;
		this.direction = direction;
	}

	public AggField(String name, String direction, String groupCode, String typeCode) {
		this.name = name;
		this.direction = direction;
		this.groupCode = groupCode;
		this.typeCode = typeCode;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public String getDirection() {
		return direction;
	}
	
}
