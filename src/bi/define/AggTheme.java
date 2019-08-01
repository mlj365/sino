package bi.define;

import bi.AggConstant;
import bi.AggUtil;
import bi.exception.AggDBlLoadException;
import foundation.data.Entity;
import foundation.persist.DataHandler;
import foundation.persist.Field;
import foundation.persist.SqlSession;
import foundation.persist.TableMeta;
import foundation.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AggTheme {

	private Logger logger;
	private String sqlTemplateId;
	private String measurementIds;
	private String dimensionCodes;
	private String nameTemplate;
	private String sqlTemplateName;
	private String sqlTemplate;
	private String targetTable;
	private String sourceTable;
	private AggType aggType;

	private List<DimensionSpace> dimensionSpaces;
	private List<Measurment> measurmentList;
	private List<Measurment> aggList;
	
	private List<Entity> periodTypeList;
	
	private List<String> dimensionAggFieldNameList;

	public AggTheme(Entity oneTheme) {
		dimensionSpaces = new ArrayList<DimensionSpace>();
		measurmentList = new ArrayList<Measurment>();
		aggList = new ArrayList<Measurment>();
		periodTypeList = new ArrayList<Entity>();
		dimensionAggFieldNameList = new ArrayList<String>();
		
		logger = LoggerFactory.getLogger(this.getClass());
		load(oneTheme);
	}
	
	private void load(Entity oneTheme) {
		try {
			//1
			this.nameTemplate = oneTheme.getString(AggConstant.BI_Field_Name);
			this.sqlTemplateId = oneTheme.getString(AggConstant.BI_Field_Sqltemplateid);
			this.measurementIds = oneTheme.getString(AggConstant.BI_Field_Measurment);
			this.dimensionCodes = oneTheme.getString(AggConstant.BI_Field_Dimension);
			this.targetTable = oneTheme.getString(AggConstant.BI_Field_Targettable);
			this.sourceTable = oneTheme.getString(AggConstant.BI_Field_Sourcetable);
			this.aggType =  AggType.valueOf(oneTheme.getString(AggConstant.BI_Field_GroupId));
			//2.1
			Entity sqlTemplateEntity = DataHandler.getLine(AggConstant.BI_TABLE_SqlTemplate, this.sqlTemplateId);
			this.sqlTemplateName = sqlTemplateEntity.getString(AggConstant.BI_Field_Name);
			this.sqlTemplate = sqlTemplateEntity.getString(AggConstant.BI_Field_Sql);

            boolean continued = checkTableExists();

            if (!continued) {
                return;
            }

            //2.2
			switch (aggType) {
				case Achieve:
				case Sum:
				case Rank:
					parse2dimensionSpaces(this.dimensionCodes);
			        break;
				case Growth:
					loadDimensionSpace(this.dimensionCodes);
					break;
			    default:
			        break;

			}

			combineMeasurement(measurementIds);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private boolean checkTableExists()  {
	    boolean flag  = true;
			if (!AggUtil.checkTableExists(sourceTable)) {
			throw new AggDBlLoadException(MessageFormat.format("{0} 表不存在", sourceTable));
		}
		if (!AggUtil.checkTableExists(targetTable)) {
            Connection connection = null;
            try {
                List<String> dimensionGroups = Arrays.stream(this.dimensionCodes.split(Util.semicolon)).map(s -> s.substring(0, s.indexOf(Util.Dot))).collect(Collectors.toList());
                List<String> measurments = Arrays.asList(measurementIds.split(Util.semicolon));
                String[] split = targetTable.split(Util.SubSeparator);
                String topicType = "";
                if (split.length >= 2) {
                    topicType = split[1];
                }
                EAggCreateCode createCode = AggUtil.createAggTable(targetTable, aggType.name(), dimensionGroups, measurments, topicType);
                if (EAggCreateCode.uncreated.equals(createCode)) {
                    throw new AggDBlLoadException("{0} 创建失败", targetTable);
                }
                if (EAggCreateCode.parse.equals(createCode)) {
                    flag = false;
                    return flag;
                }
                connection = SqlSession.createConnection();
                flag = AggTableContainer.getInstance().loadOneTableFromDb(connection, targetTable);

            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
            finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    flag = false;
                }
            }

		}
        return flag;
	}

	private void combineMeasurement(String measurementIds) throws Exception {
		String[] measurementIdArray = measurementIds.split(Util.semicolon);
		for (String measurementId : measurementIdArray) {
			Entity entity = DataHandler.getLine(AggConstant.BI_TABLE_Measurment, measurementId);
			String type = entity.getString(AggConstant.BI_Field_Type);
			AggDirection parse = AggDirection.parse(type);
			Measurment measurment = new Measurment(entity);
			if (AggDirection.Agg.equals(parse)) {
				aggList.add(measurment);
			}
			else if (AggDirection.Measurment.equals(parse)) {
				measurmentList.add(measurment);
			}
		}
	}

	private void loadDimensionSpace(String dimensionCodes) {
		String[] dimensionTypes = dimensionCodes.split(Util.semicolon);
		for (String dimensionGroup : dimensionTypes) {
			String dimension = dimensionGroup.split(Util.Spilt_Dot)[0];
			String tableName = sourceTable;
			if(tableName.toLowerCase().indexOf(AggConstant.BI_Default_As) != -1) {
				tableName = tableName.split(AggConstant.BI_Default_As)[0].trim();
			}
			try {
                Entity biTableEntity = DataHandler.getLine(AggConstant.BI_TABLE_TABLE, AggConstant.BI_Field_Name, tableName);
                if (biTableEntity == null) {
                    // 未配置

                    TableMeta tableMeta = TableMeta.getInstance(tableName);
                    if (tableMeta == null) {
                        throw new AggDBlLoadException(MessageFormat.format("无此表{0}", tableName));
                    }
                    List<Field> fieldList = tableMeta.getFields();
                    fieldList.stream()
                            .filter(field -> !AggConstant.filedUnCatchList.contains(field.getName()))
                            .map(field -> dimensionAggFieldNameList.add(field.getName())).collect(Collectors.toList());
                } else {
                    String biTableId = biTableEntity.getString(AggConstant.BI_Field_Id);

                    Entity biFieldEntity = DataHandler.getDataSet(AggConstant.BI_TABLE_FLEID, "groupcode ="+ Util.quotedStr(dimension) +" and tableid =" + Util.quotedStr(biTableId)).next();
                    if (biFieldEntity != null) {
                        String name = biFieldEntity.getString(AggConstant.BI_Field_Name);
                        dimensionAggFieldNameList.add(name);
                    }
                }

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void parse2dimensionSpaces(String dimensionCodes) throws Exception {
		String[] dimensionTypes = dimensionCodes.split(Util.semicolon);
		//1 init 二维数组（集合）
		ArrayList<ArrayList<Dimension>> init2DimensionalList = init2DimensionalList(dimensionTypes);
		// parse
		parse2DimensionalList(this.dimensionSpaces, init2DimensionalList);
		
		loadDimensionSpace(dimensionCodes);
	}

	@SuppressWarnings("unchecked")
	public void parse2DimensionalList(List<DimensionSpace> dimensionSpaces, ArrayList<ArrayList<Dimension>> init2DimensionalList) {
		ArrayList<ArrayList<Dimension>> dikaedList;
		if(init2DimensionalList.size() == 1) {
			dikaedList = new ArrayList<>();
			
			ArrayList<Dimension> DimensionalList = init2DimensionalList.get(0);
			for (Dimension dimension : DimensionalList) {
				ArrayList<Dimension> arrayList = new ArrayList<>();
				arrayList.add(dimension);
				dikaedList.add(arrayList);
			}
            //dikaedList.add(DimensionalList);
		}
		else {
			 dikaedList= Util.Dikaerji0(init2DimensionalList);
		}
		
		for (ArrayList<Dimension> dimensionEntityList : dikaedList) {
			DimensionSpace dimensionSpace = new DimensionSpace();
			dimensionSpace.addDimension(dimensionEntityList);

			dimensionSpaces.add(dimensionSpace);
		}
	}

	/**
	 * @param dimensionTypes
	 * @return ArrayList<ArrayList<String>>
	 * @throws Exception
	 */
	private ArrayList<ArrayList<Dimension>> init2DimensionalList(String[] dimensionTypes) {
		ArrayList<ArrayList<Dimension>> dimensionListLists = new ArrayList<ArrayList<Dimension>>();

		for (String dimensionsType : dimensionTypes) {
            Map<String, ArrayList<Dimension>> dimensionRootMap = new HashMap<>();
			String[] dimensions = dimensionsType.split(Util.comma);
			String fitstDimensionType = dimensions[0];
			
			String fitstDimensionGroup = fitstDimensionType.split(Util.Spilt_Dot)[0];
			String fitstDimension = fitstDimensionType.split(Util.Spilt_Dot)[1];
			
			if(Util.Star.equalsIgnoreCase(fitstDimension)) {
                Map<String, ArrayList<Dimension>> dimensionGroupMap = AggDimensionsContainer.getInstance().getDimensionMapByGroupCode(fitstDimensionGroup);
                dimensionRootMap.putAll(dimensionGroupMap);
			}
			else {
				for (String oneDimensionType : dimensions) {
					String[] oneDimensionSplit = oneDimensionType.split(Util.Spilt_Dot);
					
					String oneDimensionCode = oneDimensionSplit[1];
                    Dimension dimensionByCode = AggDimensionsContainer.getDimensionByCode(oneDimensionCode);
                    String rootCode = dimensionByCode.getRootCode();
                    ArrayList<Dimension> dimensionList = dimensionRootMap.get(rootCode);
                    if (Util.isNull(dimensionList)) {
                        dimensionList = new ArrayList<>();
                        dimensionRootMap.put(rootCode ,dimensionList);
                    }

                    dimensionList.add(dimensionByCode);
				}
			}
			if (!dimensionRootMap.isEmpty()) {
                Collection<ArrayList<Dimension>> values = dimensionRootMap.values();
                values.stream().map(dimensionList -> dimensionListLists.add(dimensionList)).count();

			}
			
		}
		return dimensionListLists;
	}

	
	public String getSqlTemplateId() {
		return sqlTemplateId;
	}

	public String getMeasurementIds() {
		return measurementIds;
	}

	public String getDimensionCodes() {
		return dimensionCodes;
	}

	public String getNameTemplate() {
		return nameTemplate;
	}

	public String getSqlTemplateName() {
		return sqlTemplateName;
	}

	public String getSqlTemplate() {
		return sqlTemplate;
	}

	public List<DimensionSpace> getDimensionSpaces() {
		return dimensionSpaces;
	}

	public String getTargetTable() {
		return targetTable;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public List<Measurment> getMeasurmentList() {
		return measurmentList;
	}

	public List<Measurment> getAggList() {
		return aggList;
	}

	public List<String> getDimensionAggFieldNameList() {
		return dimensionAggFieldNameList;
	}
	
}
