package bi;

import bi.define.*;
import bi.exception.AggCalculateException;
import foundation.config.Configer;
import foundation.data.Entity;
import foundation.persist.sql.LeftSegment;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.ContentBuilder;
import foundation.util.Util;
import foundation.variant.VariantList;
import foundation.variant.VariantSegment;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GroupSqlBulider {
    private boolean isTable = false;
    private Logger logger;
    private List<String> measurmentList;
    private Map<MainField, LeftSegment> mainSegmentMap;
    private Map<String, Dimension> dimensionLevelMap;

    private static Map<String, String> peroidNameMap = new HashMap<>();
    static {
        peroidNameMap.put(AggConstant.month,AggConstant.peroid);
        adminCode = Configer.getParam("adminCode");
    }

    private boolean addBrand = false;
    private String userId;

    private String aggTableName;
    private String aggCode;
    private String filter;
    private String userType;
    private boolean addDistributor = false;
    private static String adminCode;
    private boolean addRSM;
    private boolean addProductCode;
    private boolean addMainRSM;
    private List<String> BizPeroidDimensionCode;

    public GroupSqlBulider() {
        logger = Logger.getLogger(this.getClass());
    }

    public GroupSqlBulider(boolean isTable) {
        this.isTable = isTable;
    }

    private void clearPreData() {
        this.aggTableName = null;
        this.aggCode = null;
        this.filter = null;
        this.userType = null;
        this.addBrand = false;
        this.addDistributor = false;
        this.addProductCode = false;
        this.addMainRSM = false;
        this.addRSM = true;
        this.userId = null;

        dimensionLevelMap = null;
        mainSegmentMap = null;
        measurmentList = null;
    }

    public String getAggRegroupedSql(String userId, String userType, String topicCode, String fields, String filter) {
        clearPreData();
        this.filter = filter;
        this.userType = userType;
        this.userId = userId;
        String[] fieldsArray = Util.split(fields);
        ArrayList<AggDragableField> dragableFieldList = new ArrayList<>();


        //默认添加peroid orgaineze  pr  Distributor 截取
        //进行左连接或者组装aggcode
        boolean shouldAddPeroid = true;
        boolean shouldAddDistributor = false;
        for (String field : fieldsArray) {
            field = field.trim();

            int index = field.indexOf(Util.Dot);
            String subField;
            if (index != -1) {
                subField = field.substring(index + 1, field.length());
            } else {
                subField = field;
            }

            if (subField.equalsIgnoreCase(AggConstant.RSM)) {
                addRSM = false;
            }
            if (subField.toLowerCase().startsWith(AggConstant.distributor)) {
                shouldAddDistributor = true;
            }


            AggDragableField aggDragableField = new AggDragableField(field, true);
            if (aggDragableField.getType().equals(EDragableFieldType.unknown)) {

                logger.error(MessageFormat.format("fields中存在不能识别的字段:{0}", field));
                return null;
            } else if (aggDragableField.getType().equals(EDragableFieldType.agg) && aggDragableField.getDimension().getGroupId().equalsIgnoreCase(AggConstant.peroid)) {
                shouldAddPeroid = false;
            }
            if (dragableFieldList.contains(aggDragableField)) {
                continue;
            }
            dragableFieldList.add(aggDragableField);
        }
        if (shouldAddPeroid) {
            dragableFieldList.add(new AggDragableField(AggConstant.peroid, true));
        }
        if (shouldAddDistributor) {
            AggDragableField distributorDragableField = new AggDragableField(AggConstant.DistributorCode, true);
            if (!dragableFieldList.contains(distributorDragableField)) {
                dragableFieldList.add(distributorDragableField);
                addDistributor = true;
            }
        }

        if (addRSM) {
            if (userType.equalsIgnoreCase(AggConstant.SuperAdmin)) {
                dragableFieldList.add(new AggDragableField(AggConstant.RSM, true));
            } else {
                dragableFieldList.add(new AggDragableField(userType, true));
            }
        }
        //1.判断是否有brand这个字段
        long brandCount = dragableFieldList.stream().filter(aggDragableField -> aggDragableField.getField().equalsIgnoreCase(AggConstant.Brand)).count();

        if (brandCount == 0) {
            long productCount =dragableFieldList.stream().filter(aggDragableField ->aggDragableField.getType().equals(EDragableFieldType.main))
                    .filter(aggDragableField -> aggDragableField.getTableName().equalsIgnoreCase(AggConstant.Product)).count();
            if (productCount == 0) {
                addBrand = true;
                dragableFieldList.add(new AggDragableField(AggConstant.Brand, true));
            }else {
                addProductCode = true;
                dragableFieldList.add(new AggDragableField(AggConstant.ProductCode, true));
            }
        }else {
            long productCount =dragableFieldList.stream().filter(aggDragableField ->aggDragableField.getType().equals(EDragableFieldType.main))
                    .filter(aggDragableField -> aggDragableField.getTableName().equalsIgnoreCase(AggConstant.Product)).count();
            if (productCount != 0) {
                long productCodeCount = dragableFieldList.stream().filter(aggDragableField -> aggDragableField.getField().equalsIgnoreCase(AggConstant.ProductCode)).count();
                if(productCodeCount == 0) {
                    addProductCode = true;
                    dragableFieldList.add(new AggDragableField(AggConstant.ProductCode, true));
                }
            }
        }

        if (!Util.isNull(filter)) {
            List<String> filterFieldList = filterFieldList();

            for (String filterField : filterFieldList) {
                filterField = filterField.trim();
                if (filterField.equalsIgnoreCase("year") || filterField.equalsIgnoreCase("quarter") || filterField.equalsIgnoreCase("month")) {
                    filterField = MessageFormat.format(AggConstant.Select_Field_Template, "md_peroid", filterField);
                }
                //false代表的是过滤条件
                AggDragableField aggDragableField = new AggDragableField(filterField, false);


                if (aggDragableField.getType().equals(EDragableFieldType.unknown)) {
                    logger.error(MessageFormat.format("{0}--filters中存在不能识别的字段:{1}",topicCode, filterField));
                    return null;
                }
                if (dragableFieldList.contains(aggDragableField)) {
                    continue;
                }
                dragableFieldList.add(aggDragableField);

            }
        }

        String groupedSql = combineSql(topicCode, dragableFieldList);
        return groupedSql;
    }



    private String combineSql(String topicCode, ArrayList<AggDragableField> dragableFieldList) {
        dimensionLevelMap = new HashMap<>();
        mainSegmentMap = new HashMap<>();
        measurmentList = new ArrayList<>();
        boolean regroup = false;//完全与aggcode匹配不需要重组

        for (AggDragableField aggDragableField : dragableFieldList) {
            String rawField = aggDragableField.getField();
            EDragableFieldType type = aggDragableField.getType();
            if (EDragableFieldType.agg.equals(type)) {
                // 包含peroid 相关
                Dimension dimension = aggDragableField.getDimension();
                String groupId = dimension.getGroupId();
                //判断维度内部的属性级别
                Dimension preDimension = dimensionLevelMap.get(groupId);

                if (Util.isNull(preDimension)) {
                    dimensionLevelMap.put(groupId, dimension);
                }
                else {
                    //取最小粒度的属性
                    int prelevel = preDimension.getLevel();
                    int level = dimension.getLevel();
                    int now = prelevel - level;

                    String preField = preDimension.getCode();
                    if (now < 0) {
                        dimensionLevelMap.put(groupId, dimension);
                        String preGroupId = preDimension.getGroupId();

                        add2MainMap(preField, preGroupId, rawField);
                    } else {
                        add2MainMap(rawField, groupId, preField);
                    }
                }

            } else if (EDragableFieldType.measurment.equals(type)) {
                measurmentList.add(rawField);
            } else if (EDragableFieldType.main.equals(type)) {
                regroup = true;
                String mainTableName = aggDragableField.getTableName();
                LeftSegment leftSegment = AggMainFieldContainer.getleftSegment(mainTableName);
                MainField mainField = new MainField(rawField, EMainFieldType.main);
                mainSegmentMap.put(mainField, leftSegment);
            }else if (EDragableFieldType.filter.equals(type)) {
                regroup = true;
                String mainTableName = aggDragableField.getTableName();
                LeftSegment leftSegment = AggMainFieldContainer.getleftSegment(mainTableName);
                MainField mainField = new MainField(rawField, EMainFieldType.filter);
                mainSegmentMap.put(mainField, leftSegment);
            }

            }

            // 添加 bizdate 之上的期间
        checkBizPeroid(dragableFieldList);
        //组合TableName
        this.aggTableName = combineAggTableName(topicCode, dimensionLevelMap);
        //组合AggCode
        this.aggCode = combineAggCode(dimensionLevelMap);
        //判断是否加Territory
        Dimension orgnationDimension = checkOrgnation();
        boolean hasTerritory = checkTerritory();
        //TODO  不会存在多个
        //把编码转换成name
        if (!Util.isNull(orgnationDimension) && !hasTerritory && isTable) {
            String dimensionCode = orgnationDimension.getCode();
            add2MainMap(dimensionCode, AggConstant.organization, dimensionCode);
            addMainRSM = true;
        }
        //给关联表加上别名
        fixLeftTableName();

        String sql;
        if (regroup) {
            sql = regroupData();
        } else {
            sql = getCommonAggSql();
        }


        return sql;
    }

    private void checkBizPeroid(ArrayList<AggDragableField> dragableFieldList) {
        Dimension dimension = dimensionLevelMap.get(AggConstant.peroid);
        if (!dimension.getCode().equalsIgnoreCase(AggConstant.BizDate)) {
            return;
        }
        BizPeroidDimensionCode = dragableFieldList.stream().filter(aggDragableField -> aggDragableField.getType().equals(EDragableFieldType.main))
                .filter(aggDragableField -> aggDragableField.getTableName().equalsIgnoreCase("md_peroid"))
                .filter(aggDragableField -> !aggDragableField.getField().equalsIgnoreCase(AggConstant.BizDate))
                .map(aggDragableField -> aggDragableField.getField())
                .collect(Collectors.toList());
    }

    private void add2MainMap(String rawField, String groupId, String subField) {
        DimensionGroup dimensionGroupById = AggDimensionsContainer.getInstance().getDimensionGroupById(groupId);
        String masterTableName = dimensionGroupById.getMasterTableName();
        LeftSegment leftSegment = AggMainFieldContainer.getleftSegment(masterTableName);
        if (!leftSegment.getNowField().equalsIgnoreCase(subField)) {
            leftSegment.setNowField(subField);
            leftSegment.setLeftField(subField);
        }
        MainField mainField = new MainField(rawField, EMainFieldType.main);
        mainSegmentMap.put(mainField, leftSegment);
    }

    private String getCommonAggSql() {
        return plugVariantValue("commonAggTemplate");
    }

    private String plugVariantValue(String SqlName) {
        String sql = null;
        try {
            NamedSQL aggGroupByTemplate = NamedSQL.getInstance(SqlName);
            VariantList variantList = aggGroupByTemplate.getVariantList();
            for (VariantSegment variantSegment : variantList) {
                String name = variantSegment.getName();
                if (Util.isNull(name)) {
                    continue;
                }
                String value = getVariantValue(name);
                aggGroupByTemplate.setParam(name, value);

            }
            sql = aggGroupByTemplate.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql;
    }
    private String regroupData() {
        return plugVariantValue("aggGroupByTemplate");
    }

    private String getVariantValue(String name) {
        String variantString = null;
        ContentBuilder  builder;
        if (name.toLowerCase().equalsIgnoreCase("aggcode")) {
            variantString = this.aggCode;
        } else if (name.toLowerCase().equalsIgnoreCase("aggtable")) {
            variantString = this.aggTableName;
        }else if (name.toLowerCase().equalsIgnoreCase("selectfield")) {
            builder = getFieldByDimenisonFilter(AggConstant.Real);

            // sum
            String sumField = measurmentList.stream().map(s -> MessageFormat.format(AggConstant.AGG_SameSum_Template, s)).collect(Collectors.joining(Util.comma));
            builder.append(sumField);
            variantString = builder.toString();
        }else if (name.toLowerCase().equalsIgnoreCase("selectinfield") || name.toLowerCase().equalsIgnoreCase("field")) {
            builder = getFieldByDimenisonFilter(AggConstant.Raw);

            // sum
            String sumField = measurmentList.stream().collect(Collectors.joining(Util.comma));
            builder.append(sumField);
            variantString = builder.toString();
        }else if (name.toLowerCase().equalsIgnoreCase("leftjion")) {
            builder = getLeftJoinSegmentSql();

            variantString = builder.toString();

        }else if (name.toLowerCase().equalsIgnoreCase("groupby")) {
            builder = getFieldByDimenisonFilter(AggConstant.GroupBy);
            if (!builder.isEmpty()) {
                variantString = MessageFormat.format("GROUP BY  {0}", builder.toString());
            } else {
                variantString = Util.String_Empty;
            }
        }else if (name.toLowerCase().equalsIgnoreCase("filter")) {
            String[] split = filter.split(Util.And);
            builder = new ContentBuilder(Util.And);
            for (String oneSegment : split) {
                oneSegment = oneSegment.trim();
                Integer index = checkOneFilter(oneSegment);
                if (Util.isNull(index)) {
                    continue;
                }
                String filterName = oneSegment.substring(0, index).trim();
                String subFilterName;
                if (filterName.indexOf(Util.Dot) != -1) {
                    subFilterName = filterName.substring(filterName.indexOf(Util.Dot) + 1, filterName.length());
                }else {
                    subFilterName = filterName;
                }
                MainField mainField = new MainField(subFilterName, EMainFieldType.filter);
                LeftSegment leftSegment = mainSegmentMap.get(mainField);
                if (Util.isNull(leftSegment)) {
                    mainField = new MainField(subFilterName, EMainFieldType.main);
                    leftSegment = mainSegmentMap.get(mainField);
                }

                if (Util.isNull(leftSegment)) {
                    Dimension dimensionByCode = AggDimensionsContainer.getDimensionByCode(subFilterName);
                    if (!Util.isNull(dimensionByCode)) {
                        if (oneSegment.indexOf(Util.Dot) != -1) {
                            String preName = oneSegment.split(Util.Spilt_Dot)[0];
                            oneSegment = oneSegment.replace(preName, AggConstant.agg);
                        }

                    }
                    builder.append(MessageFormat.format("({0})", oneSegment));

                } else {
                    oneSegment = oneSegment.replace(filterName, subFilterName);
                    builder.append(MessageFormat.format("({0})", MessageFormat.format(AggConstant.Select_Field_Template, leftSegment.getLeftTableAcronym(), oneSegment)));
                }
            }
            variantString = builder.toString();
        }
        else if(name.toLowerCase().equalsIgnoreCase("notnull")) {
            if (isTable) {
                variantString = Util.defaultFilter;

            } else {
                builder = getFieldByDimenisonFilter(AggConstant.Raw);
                String notnullString = builder.toString();
                String[] split = notnullString.split(Util.comma);
                String notnullFilter = Arrays.stream(split)
                        .map(s -> Util.bracketStr(Util.stringJoin(s, AggConstant.AGG_IS_NOT_NULL )))
                        .collect(Collectors.joining(Util.And));
                variantString = notnullFilter;
            }
        }else if (name.toLowerCase().equalsIgnoreCase("user")) {
            if (userType.equalsIgnoreCase(AggConstant.SuperAdmin)) {
                variantString = Util.String_Empty;
            } else {
                variantString =  Util.stringJoin(Util.And ,Util.quotedEqualStr(MessageFormat.format(AggConstant.Select_Field_Template, AggConstant.agg, userType), userId));
            }
        }

        return variantString;
    }

    private ContentBuilder getFieldByDimenisonFilter(String type) {
        ContentBuilder builder = new ContentBuilder(Util.comma);
        HashSet<String> finalFieldSet = new HashSet<>();
        //field

        Set<String> groupIdSet = dimensionLevelMap.keySet();
        for (String groupId : groupIdSet) {
            Dimension dimension = dimensionLevelMap.get(groupId);
            String code = dimension.getCode().trim();
            String field = null;
            if (addMainRSM && !addRSM && code.equalsIgnoreCase(AggConstant.RSM)) {
                field = code;
                finalFieldSet.add(field.trim() + "name");
            }
            else if (groupId.equalsIgnoreCase(AggConstant.organization) && checkTerritory()) {
                field = code;
                finalFieldSet.add(field.trim() + "name");
            }
            if (AggConstant.Raw.equalsIgnoreCase(type)) {
                field = MessageFormat.format(AggConstant.Select_Field_Template, AggConstant.agg, code);
            } else if(AggConstant.Real.equalsIgnoreCase(type)){
                if (AggConstant.peroid.equalsIgnoreCase(code)) {
                    continue;
                }
                if (addBrand && AggConstant.Brand.equalsIgnoreCase(code)) {
                    continue;
                }
                if (addProductCode && AggConstant.ProductCode.equalsIgnoreCase(code)) {
                    continue;
                }
                if (addDistributor && AggConstant.DistributorCode.equalsIgnoreCase(code)) {
                    continue;
                }

                if (addRSM && AggConstant.SuperAdmin.equalsIgnoreCase(userType) && code.equalsIgnoreCase(AggConstant.RSM)) {
                    continue;
                }

                field = code;
            }else if (AggConstant.GroupBy.equalsIgnoreCase(type) ) {
                if (AggConstant.peroid.equalsIgnoreCase(code)) {
                    continue;
                }
                if (addBrand && AggConstant.Brand.equalsIgnoreCase(code)) {
                    continue;
                }
                if (addDistributor && AggConstant.DistributorCode.equalsIgnoreCase(code)) {
                    continue;
                }
                if (addProductCode && AggConstant.ProductCode.equalsIgnoreCase(code)) {
                    continue;
                }
                if (addRSM && AggConstant.SuperAdmin.equalsIgnoreCase(userType) && code.equalsIgnoreCase(AggConstant.RSM)) {
                    continue;
                } else {

                    field = MessageFormat.format(AggConstant.Select_Field_Template, AggConstant.A01, code);
                }
            }

            finalFieldSet.add(field.trim());
        }
        //main
        Set<MainField> mainFieldSet = mainSegmentMap.keySet();
        for (MainField mainFieldBean : mainFieldSet) {
            String mainField = mainFieldBean.getField();
            EMainFieldType fieldType = mainFieldBean.getType();

            LeftSegment leftSegment = mainSegmentMap.get(mainFieldBean);
            if (addMainRSM && leftSegment.getLeftTable().equalsIgnoreCase(AggConstant.Territory)) {
                continue;
            }
            String leftTableAcronym = leftSegment.getLeftTableAcronym();
            String field = null;
            if (AggConstant.Raw.equalsIgnoreCase(type)) {
                field = MessageFormat.format(AggConstant.Select_Field_Template, leftTableAcronym, mainField);
                if (leftSegment.getLeftTable().equalsIgnoreCase(AggConstant.Territory)) {
                    finalFieldSet.add(MessageFormat.format(AggConstant.Select_Field_Template, leftTableAcronym, mainField + AggConstant.Name));
                }
            } else if(AggConstant.Real.equalsIgnoreCase(type)){
                if (leftSegment.getLeftTable().equalsIgnoreCase(AggConstant.Territory)) {
                    finalFieldSet.add(mainField + AggConstant.Name);
                }

                if (!Util.isNull(BizPeroidDimensionCode) && BizPeroidDimensionCode.contains(mainField)) {
                    field = MessageFormat.format("DATENAME({0},{1}) as {2}", mainField.toUpperCase(), AggConstant.BizDate, mainField);
                } else {
                    field = mainField;
                }

            }else if (AggConstant.GroupBy.equalsIgnoreCase(type)) {
                field = MessageFormat.format(AggConstant.Select_Field_Template, AggConstant.A01, mainField);
                if (leftSegment.getLeftTable().equalsIgnoreCase(AggConstant.Territory)) {
                    finalFieldSet.add(MessageFormat.format(AggConstant.Select_Field_Template, AggConstant.A01, mainField + AggConstant.Name));
                }
            }
            if (Util.isNull(fieldType) || ((AggConstant.GroupBy.equalsIgnoreCase(type)|| AggConstant.Real.equalsIgnoreCase(type)) && fieldType.equals(EMainFieldType.filter))) {
                continue;
            }

            finalFieldSet.add(field.trim());
        }

        String collect = finalFieldSet.stream().collect(Collectors.joining(Util.comma));
        if (!Util.isNull(collect)) {
            builder.append(collect);
        }

        return builder;
    }

    private boolean checkTerritory() {
        Collection<LeftSegment> values = mainSegmentMap.values();
        long count = values.stream().filter(leftSegment -> leftSegment.getLeftTable().equalsIgnoreCase(AggConstant.Territory)).count();
        if (count == 0) {
            return false;
        }else {
            return true;
        }


    }
    private Dimension checkOrgnation() {
        long count= dimensionLevelMap.keySet().stream().filter(groupId -> groupId.equalsIgnoreCase(AggConstant.organization)).count();

        if (count == 0) {
            return null;
        }
        Dimension dimension = dimensionLevelMap.get(AggConstant.organization);

        return dimension;
    }


    private ContentBuilder getLeftJoinSegmentSql() {
        ContentBuilder builder;
        builder = new ContentBuilder(Util.String_Space);
        Collection<LeftSegment> leftSegmentsCollextion = mainSegmentMap.values();

        HashSet<LeftSegment> leftSegmentSets = new HashSet<>(leftSegmentsCollextion);
        //转set 去重
        for (LeftSegment leftSegment : leftSegmentSets) {
            String mainTableName = leftSegment.getLeftTable();
            String leftTableAcronym = leftSegment.getLeftTableAcronym();
            if (Util.isNull(leftTableAcronym)) {
                leftSegment.setLeftTableAcronym("zzz");
                leftTableAcronym = "zzz";
            }
            String leftSqlSegment = AggConstant.LeftJoinMain_Template.replace(AggConstant.LeftJoin_MainTable, MessageFormat.format(AggConstant.LeftJointableAs, mainTableName, leftTableAcronym));


            leftSqlSegment = leftSqlSegment.replace(AggConstant.LeftJoin_Leftjionfactor, leftSegment.getAcronymString());

            builder.append(leftSqlSegment);

        }

        return builder;
    }

    private void fixLeftTableName() {
        AtomicInteger ascii = new AtomicInteger(97);
        HashSet<LeftSegment> leftSegments = new HashSet<>(mainSegmentMap.values());
        for (LeftSegment leftSegment : leftSegments) {
            String behalfMaintable = String.valueOf((char) ascii.get());
            leftSegment.setLeftTableAcronym(behalfMaintable);
            ascii.incrementAndGet();
        }

    }

    private List<String> filterFieldList() {
        if (Util.isNull(filter))
        return null;

        ArrayList<String> filterFieldList = new ArrayList<>();
        String[] filterSegment = filter.split(Util.And);
        for (String oneSegment : filterSegment) {
            Integer idx = checkOneFilter(oneSegment);
            if (Util.isNull(idx)) {
                continue;
            }
            String fieldName = oneSegment.substring(0, idx).trim();
            filterFieldList.add(fieldName);
        }
        return filterFieldList;
    }

    private Map<String, String> filterFieldMap() {
        if (Util.isNull(filter))
        return null;

        HashMap<String, String> filterFieldMap = new HashMap<>();
        String[] filterSegment = filter.split(Util.And);
        for (String oneSegment : filterSegment) {
            Integer idx = checkOneFilter(oneSegment);
            if (Util.isNull(idx)) {
                continue;
            }
            String fieldName = oneSegment.substring(0, idx).trim();
            String fieldValue = oneSegment.substring(idx, oneSegment.length()).trim();
            filterFieldMap.put(fieldName, fieldValue);
        }
        return filterFieldMap;
    }

    private Integer checkOneFilter (String filter) {
        int index = filter.indexOf(Util.Equal);

        if (index == -1) {
            index = filter.indexOf(Util.Standrad_like);
            if (index == -1) {
                index = filter.indexOf(Util.Standrad_unEqual);
                if (index == -1) {
                    return  null;
                }
            }
        }
        return index;
    }

    private String combineAggCode(Map<String, Dimension> dimensionLevelMap) {
        Collection<Dimension> values = dimensionLevelMap.values();
        List<String> fieldList = values.stream().map(Dimension::getCode).collect(Collectors.toList());
        ContentBuilder builder = new ContentBuilder(AggUtil.initSeparator);
        Collections.sort(fieldList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return  o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });

        for (String oneField : fieldList) {
            builder.append(oneField);
        }

        return builder.toString();
    }

    private String combineAggTableName(String code, Map<String, Dimension> dimensionLevelMap) {
        Set<String> dimensionGroupSet = dimensionLevelMap.keySet();

        ArrayList<String> groupList = new ArrayList<>(dimensionGroupSet);

        ArrayList<String> maxSizeDimensionList  = AggDimensionsContainer.getInstance().getMaxSizeDimensionGroupList();

        String subAggName = AggUtil.calculateTableName(groupList,maxSizeDimensionList);

        String aggTableName = MessageFormat.format("agg_{0}_{1}", code, subAggName);
        return aggTableName;
    }

    public String getAchieveRegroupedSql(String userId, String userType, String aggCode, String type, String sqlName, String dataType, String filter, String orderBy) {
        if (Util.isNull(type)) {
            type = AggConstant.Total;
        }
        this.userType = userType;
        this.userId = userId;
        EAchieveDataType achieveDataType = EAchieveDataType.valueOf(dataType);
        Map<String, String> paramsMap = initBaseParams(type, achieveDataType, filter);
        paramsMap.put("userid", userId);
        paramsMap.put("userType", userType);

        if (aggCode != null) {
            String[] split = aggCode.split(Util.semicolon);

            ArrayList<String> aggcodelist = new ArrayList<>(Arrays.asList(split));

            if (userType.equalsIgnoreCase(AggConstant.SuperAdmin)) {
                aggcodelist.add(AggConstant.RSM);
            } else {
                aggcodelist.add(userType);
            }

            Collections.sort(aggcodelist, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return  o1.toLowerCase().compareTo(o2.toLowerCase());
                }
            });


            String realAggCode = aggcodelist.stream().collect(Collectors.joining(Util.Separator));
            paramsMap.put(AggConstant.BI_Field_Aggcode, realAggCode);

        }

        if (userType.equalsIgnoreCase(AggConstant.SuperAdmin)) {
            paramsMap.put("user", Util.String_Empty);
            paramsMap.put("onlyuser", Util.String_Empty);
            paramsMap.put("targetuser", " and 1=1 ");
            paramsMap.put("onlyuser", " and 1=1 ");

            paramsMap.put("orgchildren", "o.type = 'RSM'");
        } else {
            String user = Util.stringJoin(Util.And, Util.quotedEqualStr(MessageFormat.format(AggConstant.Select_Field_Template, AggConstant.agg, userType), userId));
            String onlyuser = Util.stringJoin(Util.And, Util.quotedEqualStr(userType, userId));
            paramsMap.put("user", user);
            paramsMap.put("onlyuser", onlyuser);
            paramsMap.put("orgchildren", MessageFormat.format("ParentLoginName = {0}", Util.quotedStr(userId)));

            if (!Util.isNull(achieveDataType)) {
                String dataTableType = AggConstant.DataTypeMap.get(achieveDataType);
                String targetuser = Util.stringJoin(Util.And, Util.quotedEqualStr(MessageFormat.format(AggConstant.Select_Field_Template, "m", dataTableType), userId));
                paramsMap.put("targetuser", targetuser);
            }
        }

        try {
            NamedSQL instance = NamedSQL.getInstance(sqlName);
            String sql = instance.getOriginalSql();
            instance.setSql(sql);
            VariantList variantList = instance.getVariantList();
            for (VariantSegment variantSegment : variantList) {
                String name = variantSegment.getName();
                String value = paramsMap.get(name);
                if (Util.isNull(value) && !name.equalsIgnoreCase("user")) {
                    //TODO  未匹配 咋整  报错
                    if (AggConstant.organizationJoin.equalsIgnoreCase(name)) {
                        String tableName = paramsMap.get(AggConstant.Sql_Field_tableName);
                        String datatypecode = paramsMap.get(AggConstant.datatypecode);
                        String organizationJoin = getOrganizationJoin(achieveDataType, tableName, datatypecode);
                        value = organizationJoin;

                    } else if (AggConstant.PageFilter.equalsIgnoreCase(name)) {
                        value = Util.defaultFilter;
                    } else if ("lastinventorydate".equalsIgnoreCase(name)) {
                        NamedSQL.getInstance("getLastDataTimeByPerson");
                        NamedSQL inventoryDateSql = NamedSQL.getInstance(AggConstant.lastDataTimeByPersonSql);
                        inventoryDateSql.setParam(AggConstant.DDITable, "agg_Inventory_o_pe");

                        if (userType.equalsIgnoreCase(AggConstant.SuperAdmin)) {
                            inventoryDateSql.setParam(AggConstant.BI_Field_Aggcode,"BizDate-" + AggConstant.RSM);
                            inventoryDateSql.setParam("user", Util.String_Empty);
                        } else {
                            inventoryDateSql.setParam("user", Util.stringJoin(Util.And, Util.quotedEqualStr(userType, userId)));
                            inventoryDateSql.setParam(AggConstant.BI_Field_Aggcode,"BizDate-" + userType);
                        }

                        Entity entity = SQLRunner.getEntity(inventoryDateSql);
                        if (!Util.isNull(entity)) {
                            String bizdate = entity.getString(AggConstant.BizDate);
                            value = bizdate;
                        }
                        //TODO

                        //value = "2018-08-17";

                    }
                    else if ("subType".equalsIgnoreCase(name)) {
                        String subPosition = AggUtil.getSubPosition(userId);
                        if (Util.isNull(subPosition)) {
                            throw new AggCalculateException(MessageFormat.format("{0}找不到下级", userId));
                        }
                        value = subPosition;
                    }
                    /*else if ("dataTypeSegment".equalsIgnoreCase(name)){
                        if (!Util.isNull(achieveDataType)) {
                            if (achieveDataType.equals(EAchieveDataType.sellin)) {
                                value = "a0.LoginName = m.Supervisor or a0.LoginName = m.RSM";
                            } else if (achieveDataType.equals(EAchieveDataType.sellout)) {
                                value = "a0.LoginName = m.Supervisor or a0.LoginName = m.RSM or a0.LoginName = m.Salesperson";
                            }
                        } else {
                            throw new AggCalculateException(MessageFormat.format("{0}有字段未匹配==={1}", sqlName, name));
                        }
                    }*/
                    else if ("brandfilter".equalsIgnoreCase(name)) {
                        String brand = paramsMap.get("brand");
                        if (!Util.isNull(brand)) {
                            value = MessageFormat.format(" and brand = {0}", brand);
                        } else {
                            value = Util.String_Empty;
                        }

                    }else if ("companyTypefilter".equalsIgnoreCase(name)) {
                        String brand = paramsMap.get("companyTypefilter");
                        if (!Util.isNull(brand)) {
                            value = Util.quotedStr(brand);
                        } else {
                            value = Util.defaultFilter;
                        }

                    }else if ("regionfilter".equalsIgnoreCase(name)) {
                        String brand = paramsMap.get("regionfilter");
                        if (!Util.isNull(brand)) {
                            value = Util.quotedStr(brand);
                        } else {
                            value = Util.defaultFilter;
                        }

                    }else if ("month".equalsIgnoreCase(name)) {
                        VariantSegment quarter = variantList.get("quarter");
                        if(Util.isNull(quarter) || quarter.getValueString() == "4") {
                            value = "(1,2,3,4,5,6,7,8,9,10,11,12)";
                        } else if(quarter.getValueString() == "1"){
                            value = "(1,2,3)";
                        } else if(quarter.getValueString() == "2"){
                            value = "(1,2,3,4,5,6)";
                        } else if(quarter.getValueString() == "3"){
                            value = "(1,2,3,4,5,6,7,8,9)";
                        }
                        //String valueString = quarter.getValueString();
                    } else {
                        throw new AggCalculateException(MessageFormat.format("{0}有字段未匹配==={1}", sqlName, name));
                    }
                }


                instance.setParam(name, value);
            }
            return instance.getSQLString() + orderBy;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }

    private String getOrganizationJoin(EAchieveDataType dataType, String tableName, String datatypecode) {
        ContentBuilder builder = new ContentBuilder(Util.String_Space);
        if (dataType.equals(EAchieveDataType.sellin)) {
            String first = MessageFormat.format(AggConstant.LeftJoin_Template, AggConstant.organization, "a", AggConstant.ParentID, "r", AggConstant.UserID);
            builder.append(first);

            String end = MessageFormat.format(AggConstant.LeftJoin_Template, tableName, "m", datatypecode, "a", AggConstant.LoginName);
            builder.append(end);

        } else if (dataType.equals(EAchieveDataType.sellout)) {
            String first = MessageFormat.format(AggConstant.LeftJoin_Template, AggConstant.organization, "a", AggConstant.ParentID, "r", AggConstant.UserID);
            builder.append(first);

            String second = MessageFormat.format(AggConstant.LeftJoin_Template, AggConstant.organization, "b", AggConstant.ParentID, "a", AggConstant.BI_Field_Id);
            builder.append(second);

            String end = MessageFormat.format(AggConstant.LeftJoin_Template, tableName, "m", datatypecode, "b", AggConstant.LoginName);
            builder.append(end);

        } else if(dataType.equals(EAchieveDataType.Distributor)){
            String first = MessageFormat.format(AggConstant.LeftJoin_Template, AggConstant.organization, "a", AggConstant.BI_Field_Id, "r", AggConstant.UserID);
            String second = MessageFormat.format(AggConstant.LeftJoin_Template, AggConstant.DistributorHierarchy, "b", AggConstant.RSM, "a", AggConstant.LoginName);
            String end = MessageFormat.format(AggConstant.LeftJoin_Template, tableName, "m", datatypecode, "b", datatypecode);
            builder.append(first);
            builder.append(second);
            builder.append(end);
        }
        return builder.toString();
    }

    private Map<String, String> initBaseParams(String type, EAchieveDataType dataType,String filter) {
        Map<String, String> paramsMap = new HashMap<>();
        String achieveSubName = AggConstant.DataTypeMap.get(dataType);
        paramsMap.put(AggConstant.brandType, type);
        paramsMap.put(AggConstant.dataType, achieveSubName);


        String tableName = MessageFormat.format(AggConstant.Achieve_TableName_Template, achieveSubName, type);
        paramsMap.put(AggConstant.Sql_Field_tableName, tableName);

        String datatypecode = achieveSubName;
        if (dataType.equals(EAchieveDataType.Distributor)) {
            datatypecode = "DistributorCode";
        }
        else if (dataType.equals(EAchieveDataType.sellout)) {
            datatypecode = "Salesperson";
        }
        paramsMap.put(AggConstant.datatypecode, datatypecode);

        if (Util.isNull(filter)) {
            return paramsMap;
        }

        String[] filterSegment = filter.split(Util.And);
        int montno = 12;
        ContentBuilder builder = new ContentBuilder(Util.And);
        for (String oneSegment : filterSegment) {
            builder.append(MessageFormat.format("({0})", oneSegment));
            int index = oneSegment.indexOf(Util.Equal);
            if (index == -1) {
                continue;
            }
            String fieldName = oneSegment.substring(0, index);
            String fieldValue = oneSegment.substring(index + 1, oneSegment.length());

            if (Util.isNull(fieldName)) {
                continue;
            }
            fieldName = fieldName.trim();
            if (Util.isNull(fieldValue)) {
                fieldValue = Util.String_Empty;
            }

            if (AggConstant.Peroid_Quarter.equalsIgnoreCase(fieldName) && montno > 3) {
                montno = 3;
            }

            if (AggConstant.Peroid_Month.equalsIgnoreCase(fieldName)) {
                montno = 1;

            }

            paramsMap.put(fieldName.trim(), fieldValue.trim());
        }
        paramsMap.put(AggConstant.monthno, String.valueOf(montno));
        paramsMap.put(AggConstant.filter, builder.toString());

        return paramsMap;
    }


}
