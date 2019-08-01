package bi.define;

import bi.AggConstant;
import foundation.data.Entity;
import foundation.persist.DataHandler;
import foundation.util.ContentBuilder;
import foundation.util.Util;

public class DimensionGroup {

	private static final String masterTableSplit = ";";
	private static final String masterTableTemplate = "select * , '' as period_lastest from ";
	private static final String masterTableNamesuffix = "master";
	private static final String historyTableTemplate = "select *  from ";

	private String id;
	private String code;
	private String name;

	private String masterTableContent;
	private String masterTableName;
	private String tableSubName;

	public DimensionGroup(Entity entity) {
		load(entity);
	}

	public  void load(Entity entity) {
		load(entity, false);
	}

	public void load(Entity entity, boolean assembly) {
		id = entity.getString("id");
		code = entity.getString("code");

		name = entity.getString("name");
        String maintableid = entity.getString("maintableid");
        try {
			initMasterTableName(maintableid);
			if (assembly) {
				masterTableContent = assemblyMasterTable(maintableid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initMasterTableName(String maintableid) throws Exception {
        Entity line = DataHandler.getLine(AggConstant.BI_TABLE_MAINDATATABLE, maintableid);
        String masterTableName = line.getString(AggConstant.BI_Field_MainData);
        this.masterTableName = masterTableName;
    }

	private String assemblyMasterTable(String mainTableId) throws Exception {
		if (Util.isEmptyStr(mainTableId)) {
			throw new Exception("DimensionGroup mainTableId is null");
		}
		ContentBuilder builder  = new ContentBuilder(" union ");
		
		Entity line = DataHandler.getLine(AggConstant.BI_TABLE_MAINDATATABLE, mainTableId);
		String masterTableName = line.getString("maindata");
		builder.append(masterTableTemplate + masterTableName);
		
		String historyTableName = line.getString("historymaindata");
		if (!Util.isEmptyStr(historyTableName)) {
			String[] split = historyTableName.split(masterTableSplit);
			for (int i = 0; i < split.length; i++) {
				String tableName = split[i];
				String oneTableString = historyTableTemplate + tableName;
				builder.append(oneTableString);
			}
		}
		
		String master = "(" + builder.toString() + ") as " + masterTableName;
		return master;
	}
	
	public String getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getMasterTableName() {
		return masterTableName;
	}

	public String getMasterTableContent() {
		return masterTableContent;
	}

	public void setTableSubName(String tableSubName) {
		this.tableSubName = tableSubName;
	}

	public String getTableSubName() {
		return tableSubName;
	}
}
