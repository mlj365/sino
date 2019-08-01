package bi.define;

import bi.AggConstant;
import bi.AggUtil;
import bi.exception.AggDBlLoadException;
import foundation.config.Configer;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataHandler;
import foundation.persist.SqlSession;
import foundation.persist.TableMeta;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AggThemeGroup {

    private static final String HISTORY_SPLIT = ";";

    private List<AggTheme> aggThemesMapList;
    private String code;
    private String name;
    private Logger logger;


    public AggThemeGroup(Entity themeGroup) {
        aggThemesMapList = new ArrayList<>();
        logger = LoggerFactory.getLogger(this.getClass());
        load(themeGroup);
    }

    private void load(Entity themeGroup) {
        String id = themeGroup.getString(AggConstant.BI_Field_Id);
        if (Util.isEmptyStr(id)) {
            throw new AggDBlLoadException("themeGroup id is null");
        }
        this.code = themeGroup.getString(AggConstant.BI_Field_Code);
        this.name = themeGroup.getString(AggConstant.BI_Field_Name);
        try {

            String groupId = Util.quotedEqualStr(AggConstant.BI_Field_ThemeGroupId, id);
            EntitySet themeGroupMapEntitySet = DataHandler.getDataSet(AggConstant.BI_TABLE_THEMEGROUPMAP, groupId);

            EntitySet dataSet = DataHandler.getDataSet(AggConstant.BI_TABLE_THEMEG, AggConstant.BI_Filter_Active + " and groupid = " + Util.quotedStr(id));

            if (dataSet.size() == 0) {
                if (themeGroupMapEntitySet.size() == 0) {
                    return;
                }
                initThemeByGroup(themeGroupMapEntitySet, dataSet);
                AggTableContainer.getInstance().load();
            }

            for (Entity entity : dataSet) {
                AggTheme aggTheme = new AggTheme(entity);
                aggThemesMapList.add(aggTheme);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initThemeByGroup(EntitySet themeGroupMapSet, EntitySet dataSet) throws Exception {
        for (Entity themeGroupMapEntity : themeGroupMapSet) {
            addOneTable(themeGroupMapEntity,dataSet);
        }
    }

    private void addOneTable(Entity themeGroupMapEntity, EntitySet dataSet) throws Exception {
        TableMeta tableMeta = dataSet.getTableMeta();
        String measurmentGroups = themeGroupMapEntity.getString(AggConstant.BI_Field_Measurmentgroups);
        String dimensionGroupStr = themeGroupMapEntity.getString(AggConstant.BI_Field_DimensionGroups);
        String topicTableName = themeGroupMapEntity.getString(AggConstant.BI_Field_RealTable);
        Map<AggDirection, ArrayList<String>> realFieldTypeList = null;
        if (Util.isNull(measurmentGroups)) {
            realFieldTypeList = getRealFieldTypeList(topicTableName);
            ArrayList<String> strings = realFieldTypeList.get(AggDirection.Measurment);
            measurmentGroups = strings.stream().collect(Collectors.joining(Util.semicolon));
        }

        if (Util.isNull(dimensionGroupStr)) {
            if (Util.isNull(realFieldTypeList)) {
                realFieldTypeList = getRealFieldTypeList(topicTableName);
            }

            ArrayList<String> strings = realFieldTypeList.get(AggDirection.Dimension);
            dimensionGroupStr = strings.stream().collect(Collectors.joining(Util.semicolon));
        }

        List<String> measurmentList = Util.StringToList(measurmentGroups, Util.semicolon);
        String measurmentStrs = measurmentList.stream().map(measurment -> Util.quotedStr(measurment)).collect(Collectors.joining(Util.comma));
        NamedSQL getmeasurmentSql = NamedSQL.getInstance(AggConstant.Sql_getMeasurmentByCode);
        getmeasurmentSql.setParam(AggConstant.BI_Field_Code, measurmentStrs);
        EntitySet entitySet = SQLRunner.getEntitySet(getmeasurmentSql);
        String measurments = entitySet.getEntityList().stream().map(entity -> entity.getString(AggConstant.BI_Field_Code)).collect(Collectors.joining(Util.semicolon));


        ArrayList<String> dimensionGroupList = Util.StringToList(dimensionGroupStr, Util.semicolon);
        String[] dimensionGroupArray = new String[dimensionGroupList.size()];
        dimensionGroupList.toArray(dimensionGroupArray);
        ArrayList<ArrayList<String>> dimensionGroups = new ArrayList<>();
        Util.combine(dimensionGroupArray, dimensionGroups);

        String topicType = topicTableName.substring(AggConstant.BI_Default_TopicView.length());

        boolean successCreateParition = addPartition(dimensionGroups, topicType);
        //boolean successCreateParition = addPartitionByPeroid(topicType);
        if (!successCreateParition) {
            throw new AggDBlLoadException("创建分区失败");
        }

        ArrayList<String> maxSizeDimensionList = dimensionGroups.get(0);

        for (ArrayList<String> oneDimensionGroups : dimensionGroups) {
            Entity entity = new Entity(tableMeta);
            entity.set(0, Util.newShortGUID());
            entity.set(1, AggConstant.BI_Theme_DefaultName);
            entity.set(2, AggConstant.BI_SqlTemplate_DefaultAchieveSqlId);

            String groupId = themeGroupMapEntity.getString(AggConstant.BI_Field_ThemeGroupId);
            entity.set(3, groupId);

            String oneDimensionStr = oneDimensionGroups.stream().map(dimension -> MessageFormat.format("{0}.*", dimension)).collect(Collectors.joining(Util.semicolon));
            entity.set(4, oneDimensionStr);
            entity.set(5, measurments);

            entity.set(6, topicTableName);

            String subAggName = AggUtil.calculateTableName(oneDimensionGroups, maxSizeDimensionList);

            String aggTableName = MessageFormat.format("agg_{0}_{1}", topicType, subAggName);
            if (!AggUtil.checkTableExists(aggTableName)) {
                EAggCreateCode createCode = AggUtil.createAggTable(aggTableName, groupId, oneDimensionGroups, measurmentList, topicType);
                if (EAggCreateCode.uncreated.equals(createCode)) {
                    throw new AggDBlLoadException("{0} 创建失败", aggTableName);
                }
                if (EAggCreateCode.parse.equals(createCode)) {
                     continue;
                }
            }

            entity.set(7, aggTableName);
            entity.set(8, Util.TRUE);
            entity.insert();

            dataSet.append(entity);
        }
    }

    private boolean addPartitionByPeroid(String topicType) {
        Connection connection = null;
        boolean success = true;
        try {
            connection = SqlSession.createConnection();
            String dbName = connection.getCatalog();
            EntitySet md_peroid = DataHandler.getDataSet("md_peroid");
            ArrayList<String> peroidList = new ArrayList<>();
            ArrayList<String> fileGroupNameList = new ArrayList<>();

            String defaultgroupName = Util.stringJoin("aggGroup", Util.SubSeparator, topicType, Util.SubSeparator, "0");
            String createFileGroupSql = MessageFormat.format(AggConstant.Create_FILEGROUP_Template, dbName, defaultgroupName);
            SQLRunner.execSQL(connection, createFileGroupSql);

            //2 为文件组分别创建文件
            String fileName = Util.stringJoin("aggfile", Util.SubSeparator, topicType, Util.SubSeparator, "0");
            String dbFilePath = Configer.getParam("DbFilePath");
            String filePath = Util.stringJoin(dbFilePath, fileName, Util.Dot, AggConstant.MDF);
            String createFileSql = MessageFormat.format(AggConstant.Create_FILE_Template, dbName, Util.quotedStr(fileName), Util.quotedStr(filePath), defaultgroupName);
            SQLRunner.execSQL(connection, createFileSql);

            fileGroupNameList.add(defaultgroupName);

            for (Entity entity : md_peroid) {
                String id = entity.getId();
                String monthno = entity.getString("monthno");
                //1 文件组
                String groupName = Util.stringJoin("aggGroup", Util.SubSeparator, topicType, Util.SubSeparator, monthno);
                createFileGroupSql = MessageFormat.format(AggConstant.Create_FILEGROUP_Template, dbName, groupName);
                SQLRunner.execSQL(connection, createFileGroupSql);

                //2 为文件组分别创建文件
                fileName = Util.stringJoin("aggfile", Util.SubSeparator, topicType, Util.SubSeparator, monthno);
                dbFilePath = Configer.getParam("DbFilePath");
                filePath = Util.stringJoin(dbFilePath, fileName, Util.Dot, AggConstant.MDF);
                createFileSql = MessageFormat.format(AggConstant.Create_FILE_Template, dbName, Util.quotedStr(fileName), Util.quotedStr(filePath), groupName);
                SQLRunner.execSQL(connection, createFileSql);

                peroidList.add(id);
                fileGroupNameList.add(groupName);
            }

            //3 创建分区函数
            String values = peroidList.stream().map(aggCode -> Util.quotedStr(aggCode)).collect(Collectors.joining(Util.comma));
            String partitionFuncSql  = MessageFormat.format(AggConstant.Create_PartitionFunc_Template, topicType, values);
            SQLRunner.execSQL(connection, partitionFuncSql);

            //4创建分区方案
            String fileGroupValue = fileGroupNameList.stream().collect(Collectors.joining(Util.comma));
            String partitionSchemeSql  = MessageFormat.format(AggConstant.Create_PartitionScheme_Template, topicType, fileGroupValue);
            SQLRunner.execSQL(connection, partitionSchemeSql);

        }catch (Exception e) {
            success = false;
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return success;
    }

    private boolean addPartition(ArrayList<ArrayList<String>> dimensionGroups, String topicType) {
        Connection connection = null;
        boolean success = true;
        try {
            connection = SqlSession.createConnection();
            String dbName = connection.getCatalog();
            ArrayList<String> fileGroupNameList = new ArrayList<>();
            ArrayList<String> aggCodeList = new ArrayList<>();
            //0.1 文件组
            String groupName = Util.stringJoin("aggGroup", Util.SubSeparator, topicType, Util.SubSeparator, AggConstant.agg);
            String createFileGroupSql = MessageFormat.format(AggConstant.Create_FILEGROUP_Template, dbName, groupName);
            SQLRunner.execSQL(connection, createFileGroupSql);

            //0.2 为文件组添加默认文件
            String fileName =Util.stringJoin("aggfile", Util.SubSeparator, topicType, Util.SubSeparator, AggConstant.agg);
            String dbFilePath = Configer.getParam("DbFilePath");
            String filePath = Util.stringJoin(dbFilePath, fileName, Util.Dot, AggConstant.MDF);
            String createFileSql = MessageFormat.format(AggConstant.Create_FILE_Template, dbName, Util.quotedStr(fileName), Util.quotedStr(filePath), groupName);
            SQLRunner.execSQL(connection, createFileSql);

            fileGroupNameList.add(groupName);

            HashSet<String> maxDimensionSet = new HashSet<>();
            for (ArrayList<String> dimensionGroup : dimensionGroups) {
                for (String dimensionGroupCode : dimensionGroup) {
                    Map<String, ArrayList<Dimension>> dimensionMapByGroupCode = AggDimensionsContainer.getInstance().getDimensionMapByGroupCode(dimensionGroupCode);

                    Collection<ArrayList<Dimension>> values = dimensionMapByGroupCode.values();
                    for (ArrayList<Dimension> value : values) {
                        maxDimensionSet.addAll(value.stream().map(dimension -> dimension.getCode()).collect(Collectors.toList()));
                    }
                }
            }

            ArrayList<String> maxSizeDimensionList = new ArrayList<>(maxDimensionSet);

            for (ArrayList<String> dimensionGroup : dimensionGroups) {

                if (!(dimensionGroup.contains(AggConstant.peroid))) {
                    //剔除不含期间维度的表
                    continue;
                }

                if (!(dimensionGroup.contains(AggConstant.organization) || dimensionGroup.contains(AggConstant.distributor))) {
                    continue;
                }

                ArrayList<ArrayList<String>> dikaerList = getDikaerDimensionList(dimensionGroup);


                for (ArrayList<String> dimensions : dikaerList) {

                    Collections.sort(dimensions, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            int compare = o1.toLowerCase().compareTo(o2.toLowerCase());
                            if (compare < 0) {
                                return  -1;
                            }else if (compare == 0) {
                                return 0;
                            }
                            else {
                                return 1;
                            }
                        }
                    });

                    String aggCode = dimensions.stream().collect(Collectors.joining(Util.Separator));
                    logger.info(aggCode);
                    //1 文件组
                    groupName = getCombinePrefixName(Util.stringJoin("aggGroup", Util.SubSeparator, topicType, Util.SubSeparator), dimensions, maxSizeDimensionList);
                    createFileGroupSql = MessageFormat.format(AggConstant.Create_FILEGROUP_Template, dbName, groupName);
                    SQLRunner.execSQL(connection, createFileGroupSql);

                    //2 为文件组分别创建文件
                    fileName = getCombinePrefixName(Util.stringJoin("aggfile", Util.SubSeparator, topicType, Util.SubSeparator), dimensions, maxSizeDimensionList);
                    dbFilePath = Configer.getParam("DbFilePath");
                    filePath = Util.stringJoin(dbFilePath, fileName, Util.Dot, AggConstant.MDF);

                    createFileSql = MessageFormat.format(AggConstant.Create_FILE_Template, dbName, Util.quotedStr(fileName), Util.quotedStr(filePath), groupName);
                    SQLRunner.execSQL(connection, createFileSql);

                    fileGroupNameList.add(groupName);
                    aggCodeList.add(aggCode);
                }
            }
            //3 创建分区函数
            String values = aggCodeList.stream().map(aggCode -> Util.quotedStr(aggCode)).collect(Collectors.joining(Util.comma));
            String partitionFuncSql  = MessageFormat.format(AggConstant.Create_PartitionFunc_Template, topicType,values);
            SQLRunner.execSQL(connection, partitionFuncSql);

            //4创建分区方案
            String fileGroupValue = fileGroupNameList.stream().collect(Collectors.joining(Util.comma));
            String partitionSchemeSql  = MessageFormat.format(AggConstant.Create_PartitionScheme_Template, topicType, fileGroupValue);
            SQLRunner.execSQL(connection, partitionSchemeSql);


        } catch (Exception e) {
            success = false;
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return success;
    }

    private ArrayList<ArrayList<String>> getDikaerDimensionList(ArrayList<String> dimensionGroup) {
        ArrayList<ArrayList<String>> dimensionListLists = new ArrayList<ArrayList<String>>();
        for (String oneDimensionGroup : dimensionGroup) {
            Map<String, ArrayList<Dimension>> dimensionRootMap = new HashMap<>();
            Map<String, ArrayList<Dimension>> dimensionGroupMap = AggDimensionsContainer.getInstance().getDimensionMapByGroupCode(oneDimensionGroup);
            dimensionRootMap.putAll(dimensionGroupMap);

            if (!dimensionRootMap.isEmpty()) {
                Collection<ArrayList<Dimension>> values = dimensionRootMap.values();
                for (ArrayList<Dimension> value : values) {
                    List<String> collect = value.stream().map(dimension -> dimension.getCode()).collect(Collectors.toList());
                    dimensionListLists.add(new ArrayList<>(collect));
                }
            }
        }

        ArrayList<ArrayList<String>> dikaerList;
        if (dimensionListLists.size() == 1) {
            dikaerList = new ArrayList<>();
            ArrayList<String> DimensionalList = dimensionListLists.get(0);
            for (String dimensionCode : DimensionalList) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(dimensionCode);
                dikaerList.add(arrayList);
            }
        } else {
            dikaerList = Util.Dikaerji0(dimensionListLists);
        }
        return dikaerList;
    }

    private String getCombinePrefixName(String fileGroupPrefix, ArrayList<String> oneDimensionGroups, ArrayList<String> maxSizeDimensionList) {

        String subAggName = AggUtil.calculateTableName(oneDimensionGroups, maxSizeDimensionList);

        return Util.stringJoin(fileGroupPrefix, subAggName);
    }

    private Map<AggDirection, ArrayList<String>> getRealFieldTypeList(String topicTableName) throws Exception {
        Map<AggDirection, ArrayList<String>> typeMap = new HashMap<>();
        Connection connection = SqlSession.createConnection();
        String selectSql = MessageFormat.format(AggConstant.CONNECTION_Field_Template, topicTableName);
        PreparedStatement ps = connection.prepareStatement(selectSql);
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData meta = rs.getMetaData();
        int columeCount = meta.getColumnCount();
        for (int i = 1; i <= columeCount; i++) {
            String aggTableField = meta.getColumnName(i);
            if (AggConstant.filedUnCatchList.contains(aggTableField)) {
                continue;
            }
            Entity line = DataHandler.getLine(AggConstant.BI_TABLE_Measurment, AggConstant.BI_Field_Code, aggTableField);
            if (!Util.isNull(line)) {
                String type = line.getString(AggConstant.BI_Field_Type);
                AggDirection parse = AggDirection.parse(type);
                ArrayList<String> strings = typeMap.get(parse);
                if (Util.isNull(strings)) {
                    strings = new ArrayList<>();


                }
                if (!strings.contains(aggTableField)) {
                    strings.add(aggTableField);
                }
                typeMap.put(parse, strings);
            } else {
                line = DataHandler.getLine(AggConstant.BI_TABLE_Dimension, AggConstant.BI_Field_Code, aggTableField);
                if (!Util.isNull(line)) {
                    ArrayList<String> strings = typeMap.get(AggDirection.Dimension);
                    if (Util.isNull(strings)) {
                        strings = new ArrayList<>();

                    }
                    String groupid = line.getString(AggConstant.BI_Field_GroupId);
                    if (!strings.contains(groupid)) {
                        strings.add(groupid);
                    }

                    typeMap.put(AggDirection.Dimension, strings);
                }
            }
        }
        return typeMap;
    }






    public List<AggTheme> getAggThemesMapList() {
        return aggThemesMapList;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
