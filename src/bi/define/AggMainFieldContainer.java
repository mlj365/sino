package bi.define;

import bi.AggConstant;
import foundation.config.Preloader;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataHandler;
import foundation.persist.SqlSession;
import foundation.persist.sql.LeftSegment;
import foundation.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


public class AggMainFieldContainer extends Preloader {

	private Logger logger;
	private static AggMainFieldContainer instance;
	private Map<String, String> fieldTableMap;
    private Map<String, LeftSegment> mainTableIdMap;
	public synchronized static AggMainFieldContainer getInstance() {
		if (instance == null) {
			instance = new AggMainFieldContainer();
		}
		return instance;
	}

	private AggMainFieldContainer() {
		logger = LoggerFactory.getLogger(this.getClass());
        fieldTableMap = new HashMap<>();
        mainTableIdMap = new HashMap<>();
		loadData();
	}

	@Override
	public void load() {
		loadData();
	}


	public static void refresh() {
		AggMainFieldContainer instance = getInstance();

        instance.fieldTableMap = new HashMap<>();
        instance.mainTableIdMap = new HashMap<>();

		instance.loadData();
	}
	
	private void loadData() {
		try {
            EntitySet maindatatable = DataHandler.getDataSet(AggConstant.BI_TABLE_MAINDATATABLE);
            Connection connection = SqlSession.createConnection();

            for (Entity entity : maindatatable) {

                String mapfield = entity.getString(AggConstant.BI_Field_MapField);
                String tableName = entity.getString(AggConstant.BI_Field_MainData);
                String maintableId = entity.getId();
                Entity line = DataHandler.getLine(AggConstant.BI_TABLE_TDIMENSIONGROUP, AggConstant.BI_Field_MainTableId, maintableId);
                if (line == null) {
                    continue;
                }
                String dimensionMainCode = line.getString(AggConstant.BI_Field_DimensionMainCode);
                LeftSegment leftSegment = new LeftSegment(tableName, mapfield, AggConstant.agg, dimensionMainCode);
                mainTableIdMap.put(tableName, leftSegment);

                String selectSql = MessageFormat.format(AggConstant.CONNECTION_Field_Template, tableName);
                PreparedStatement ps = connection.prepareStatement(selectSql);
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData meta = rs.getMetaData();
                int columeCount = meta.getColumnCount();
                for (int i = 1; i <= columeCount; i++) {
                    String aggTableField = meta.getColumnName(i);
                    if (AggConstant.filedUnCatchList.contains(aggTableField)) {
                        continue;
                    }
                    fieldTableMap.put(MessageFormat.format(AggConstant.Select_Field_Template, tableName,aggTableField), tableName);
                }
            }

        } catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

    public Map<String, String> getFieldTableMap() {
        return fieldTableMap;
    }

    public static String getTableName(String field) {
        AggMainFieldContainer instance = getInstance();
        if (Util.isNull(instance.getFieldTableMap())) {
            instance.fieldTableMap = new HashMap<>();
            return null;
        }
        return instance.fieldTableMap.get(field);
    }
    public static String getTableMapId(String tableName) {
        AggMainFieldContainer instance = getInstance();
        if (Util.isNull(instance.getFieldTableMap())) {
            instance.mainTableIdMap = new HashMap<>();
            return null;
        }
        LeftSegment leftSegment = instance.mainTableIdMap.get(tableName);
        return leftSegment.getLeftField();
    }
    public static LeftSegment getleftSegment(String tableName) {
        AggMainFieldContainer instance = getInstance();
        if (Util.isNull(instance.getFieldTableMap())) {
            instance.mainTableIdMap = new HashMap<>();
            return null;
        }
        LeftSegment leftSegment = instance.mainTableIdMap.get(tableName);
        return leftSegment;
    }


}
