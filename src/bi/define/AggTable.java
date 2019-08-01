package bi.define;

import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataHandler;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AggTable {

	private String id;
	private String name;
	private Map<AggDirection, HashMap<String, AggField>> aggFieldMaps;
	private Logger logger;

    private AggTable() {
        logger = LoggerFactory.getLogger(this.getClass());
        aggFieldMaps = new HashMap<AggDirection, HashMap<String, AggField>>();

    }

	public AggTable(Entity entity) {
		this();
		this.id = entity.getString("id");
		this.name = entity.getString("name");
		
		loadFields(id);
	}

    public AggTable(String name) {
        this();

        this.name = name;
    }
	private void loadFields(String id) {
		try {
			NamedSQL instance = NamedSQL.getInstance("getDataGroupby");
			instance.setParam("fields", "direction");
			instance.setParam("tablename", "[dbo].[bi_field]");
			instance.setParam("groupby", "direction");
			EntitySet entitySet = SQLRunner.getEntitySet(instance);
			for (Entity directionEntity : entitySet) {
				String direction = directionEntity.getString("direction");
				AggDirection aggDirection = AggDirection.parse(direction);
				
				HashMap<String,AggField> oneDirectionMap = new HashMap<String, AggField>();
				EntitySet dataSet = DataHandler.getDataSet("bi_field", "tableid = " + Util.quotedStr(id) + " and direction = " + Util.quotedStr(direction));
				
				for (Entity entity : dataSet) {
					String fieldName = entity.getString("typecode");
					AggField aggField = new AggField(entity);
					oneDirectionMap.put(fieldName.toLowerCase(), aggField);
				}
				aggFieldMaps.put(aggDirection, oneDirectionMap);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public AggField getAggField(String name) {
		if (aggFieldMaps == null) {
			return null;
		}
		AggField field = null;
		Collection<HashMap<String,AggField>> values = aggFieldMaps.values();
		
		for (HashMap<String, AggField> hashMap : values) {
			field = hashMap.get(name);
		}
		
		return field;
	}
	
	public String getAggFieldName(AggDirection direction, String name) {
		if (aggFieldMaps == null) {
			return null;
		}
		String fieldName = null;
		HashMap<String,AggField> hashMap = aggFieldMaps.get(direction);
		AggField aggField = hashMap.get(name);
	
		if (aggField != null) {
			fieldName = aggField.getName();
		}
		
		return fieldName;
	}
	public AggField getAggField(AggDirection direction, String name) {
		if (aggFieldMaps == null) {
			return null;
		}
		HashMap<String,AggField> hashMap = aggFieldMaps.get(direction);
		AggField aggField = hashMap.get(name);

		return aggField;
	}
	
	public HashMap<String, AggField> getDirectionMap(AggDirection direction){
		if (aggFieldMaps == null) {
			return null;
		}
		return aggFieldMaps.get(direction);
	}

	public AggTable putDirectionMap(AggDirection direction, String typeCode, AggField field) {
		if (aggFieldMaps == null) {
			aggFieldMaps = new HashMap<>();
		}
        HashMap<String, AggField> aggFieldHashMap = aggFieldMaps.get(direction);
        if (aggFieldHashMap == null) {
            aggFieldHashMap = new HashMap<>();
			aggFieldMaps.put(direction, aggFieldHashMap);
        }
        aggFieldHashMap.put(typeCode, field);
        return this;
	}

    public AggTable putDirectionMap(AggDirection direction, String typeCode, String fieldName) {
        if (aggFieldMaps == null) {
            aggFieldMaps = new HashMap<>();
        }
        HashMap<String, AggField> aggFieldHashMap = aggFieldMaps.get(direction);
        if (aggFieldHashMap == null) {
            aggFieldHashMap = new HashMap<>();
			aggFieldMaps.put(direction, aggFieldHashMap);
        }
        AggField aggField = new AggField(fieldName, typeCode, direction.name());
        aggFieldHashMap.put(typeCode, aggField);

        return this;
    }

	public AggTable putDirectionMap(AggDirection direction, String typeCode, String fieldName, String groupName) {

    	if (aggFieldMaps == null) {
			aggFieldMaps = new HashMap<>();
		}
		HashMap<String, AggField> aggFieldHashMap = aggFieldMaps.get(direction);
		if (aggFieldHashMap == null) {
			aggFieldHashMap = new HashMap<>();
			aggFieldMaps.put(direction, aggFieldHashMap);
		}
		AggField aggField = new AggField(fieldName, typeCode, groupName, direction.name());
		aggFieldHashMap.put(typeCode, aggField);

		return this;
	}

	public Map<AggDirection, HashMap<String, AggField>> getAggFieldMaps() {
		return aggFieldMaps;
	}
}
