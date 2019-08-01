package bi.define;

import bi.AggConstant;
import foundation.config.Preloader;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataHandler;
import foundation.persist.SqlSession;
import foundation.persist.TableMeta;
import foundation.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AggTableContainer  extends Preloader {

	private static AggTableContainer instance;
	private Logger logger;

	private Map<String, AggTable> aggTableMap;
	public synchronized static AggTableContainer getInstance() {
		if (instance == null) {
			instance = new AggTableContainer();
		}
		
		return instance;
	}
	
	public AggTableContainer() {
		logger = LoggerFactory.getLogger(this.getClass());
		aggTableMap = new HashMap<String, AggTable>();
    }

	@Override
	public void load() throws Exception {
	    //loadPeroid();
        AggDimensionsContainer.refresh();
        AggMainFieldContainer.refresh();
		loadData();
	}


	public void refresh() {
        if (aggTableMap == null) {
            aggTableMap = new HashMap<>();
        } else {
            aggTableMap.clear();
        }
        loadData();
    }

    private void loadPeroid() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017,0,1);
        int count = 1;
        TableMeta md_peroid = TableMeta.getInstance("md_peroid");
        do {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int i = (month % 3);
            int quarter;
            if (i > 0) {
                quarter = (month / 3) + 1;
            }else {
                quarter = (month / 3);
            }

            Entity entity = new Entity(md_peroid);
            entity.set(0, Util.newShortGUID());
            entity.set(1, year);
            entity.set(2, quarter);
            entity.set(3, month);
            entity.set(4, count);
            entity.set(5, ((count -1) / 3) + 1);
            entity.insert();
            calendar.add(Calendar.MONTH, 1);
            count++;
        }
        while (count <= 36);
    }

    private void loadData() {
        Connection connection = null;
		try {
			//1
            connection = SqlSession.createConnection();
            DatabaseMetaData dbMetaData = connection.getMetaData();
            ResultSet tableRs = dbMetaData.getTables(null, null, null,new String[] {AggConstant.CONNECTION_TABLE, AggConstant.CONNECTION_VIEW});
            while (tableRs.next()) {
                String tableName = tableRs.getString(AggConstant.CONNECTION_TABLE_NAME);
                if (!loadOneTableFromDb(connection, tableName)) continue;
            }
            //2
			EntitySet dataSet = DataHandler.getDataSet("bi_table", "1 = 1");
			for (Entity entity : dataSet) {
				String aggTableName = entity.getString("name");
				AggTable aggTable = new AggTable(entity);
				aggTableMap.put(aggTableName, aggTable);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
	}

    public boolean loadOneTableFromDb(Connection connection, String tableName) throws Exception {
        if (!(tableName.startsWith(AggConstant.BI_Default_Agg) || tableName.startsWith(AggConstant.BI_Default_TopicView))) {
            return true;
        }
        AggTable aggTable = new AggTable(tableName);

        String selectSql = MessageFormat.format(AggConstant.CONNECTION_Field_Template, tableName);
        PreparedStatement ps = connection.prepareStatement(selectSql);
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData meta = rs.getMetaData();

        int columeCount = meta.getColumnCount();
        for (int i = 1; i <= columeCount; i++) {
            String aggTableField = meta.getColumnName(i);
            int columnType = meta.getColumnType(i);
            if (AggConstant.filedUnCatchList.contains(aggTableField)) {
                continue;
            }
            AggDirection aggTableDirection = AggConstant.AggDefaultSqlTypeMap.get(columnType);
            if (aggTableDirection == null) {
                aggTableDirection = AggDirection.Dimension;
            }

            Entity line;
            String typeCode = null;
            AggDirection aggDirection = null;
            String groupCode = null;
            switch (aggTableDirection) {
                case Dimension:
                    line = DataHandler.getLine(AggConstant.BI_TABLE_Dimension, AggConstant.BI_Field_Code, aggTableField);
                    aggDirection = aggTableDirection;
                    if (line == null) {
                        //default 找不到
                        logger.error(MessageFormat.format("table:{0} ==== fied:{1}== not found",tableName, aggTableField));
                        typeCode = aggTableField;
                        groupCode = aggTableField;
                        break;
                    }
                    aggDirection = aggTableDirection;
                    typeCode = line.getString(AggConstant.BI_Field_Code);
                    String groupId = line.getString(AggConstant.BI_Field_GroupId);
                    Entity groupLine = DataHandler.getLine(AggConstant.BI_TABLE_TDIMENSIONGROUP, groupId);
                    if (groupLine != null) {
                        groupCode = groupLine.getString(AggConstant.BI_Field_Code);
                    }
                    break;
                case Measurment:
                    line = DataHandler.getLine(AggConstant.BI_TABLE_Measurment, AggConstant.BI_Field_Code, aggTableField);
                    if (line == null) {
                        //default 找不到
                        continue;
                    }
                    String type = line.getString(AggConstant.BI_Field_Type);
                    aggDirection = aggTableDirection;
                    if (!Util.isEmptyStr(type)) {
                        aggDirection = AggDirection.valueOf(type);
                    }

                    typeCode = line.getString(AggConstant.BI_Field_Code);
                    groupCode = typeCode;
                    break;
                default:
                    break;
            }
            aggTable.putDirectionMap(aggDirection, typeCode, typeCode, groupCode);
        }
        aggTableMap.put(tableName, aggTable);
        return false;
    }

    public AggTable getAggTable(String name) {
		if (aggTableMap == null) {
			return null;
		}
		return aggTableMap.get(name);
		
	}

	public Map<String, AggTable> getAggTableMap() {
		return aggTableMap;
	}
	
}
