package bi.define;

import bi.AggConstant;
import bi.AggUtil;
import foundation.config.Preloader;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataHandler;
import foundation.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;


public class AggDimensionsContainer extends Preloader {

	private Logger logger;
	private static AggDimensionsContainer instance;
	private Map<String, Map<String,ArrayList<Dimension>>> dimensionByGroupByCodeMap;
	private Map<String, Dimension> dimensionCodeMap;
	private Map<String, Dimension> peroidMap;
    private Map<String, Measurment> measurmentMap;
    private Map<String, DimensionGroup> DimensionGroupMap;

	public synchronized static AggDimensionsContainer getInstance() {
		if (instance == null) {
			instance = new AggDimensionsContainer();
		}
		return instance;
	}

	private AggDimensionsContainer() {
		logger = LoggerFactory.getLogger(this.getClass());
        dimensionByGroupByCodeMap = new HashMap<>();
        dimensionCodeMap = new HashMap<>();
        peroidMap = new HashMap<>();
        measurmentMap = new HashMap<>();
        DimensionGroupMap = new HashMap<>();
		loadData();
	}

	@Override
	public void load() {
		loadData();
	}


	public static void refresh() {
		AggDimensionsContainer instance = getInstance();

        instance.dimensionByGroupByCodeMap = new HashMap<>();
        instance.dimensionCodeMap = new HashMap<>();
        instance.measurmentMap = new HashMap<>();
        instance.peroidMap = new HashMap<>();
        instance.DimensionGroupMap = new HashMap<>();

		instance.loadData();
	}
	
	private void loadData() {
		try {
			EntitySet dataSet = DataHandler.getDataSet(AggConstant.BI_TABLE_TDIMENSIONGROUP, AggConstant.BI_Filter_Active, AggConstant.BI_Field_No);

			for (Entity entity : dataSet) {
                DimensionGroup dimensionGroup = new DimensionGroup(entity);

                String id = dimensionGroup.getId();
                DimensionGroupMap.put(id, dimensionGroup);
                //common
                initDimensionMap(dimensionGroup, id);

            }
            initDimensionGroupSubName();
            //period
            initPeriodMap();
            EntitySet measurmentSet = DataHandler.getDataSet(AggConstant.BI_TABLE_Measurment, AggConstant.BI_Filter_Active);
            for (Entity entity : measurmentSet) {
                Measurment measurment = new Measurment(entity);
                String code = entity.getString(AggConstant.BI_Field_Code);
                measurmentMap.put(code, measurment);
            }

        } catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

    private void initDimensionGroupSubName() {
        Set<String> keySet = DimensionGroupMap.keySet();
        ArrayList<String> maxDimensionGroupNameList = new ArrayList<>(keySet);
        for (String key : keySet) {
            DimensionGroup dimensionGroup = DimensionGroupMap.get(key);
            String noSameSub = AggUtil.getNoSameSub(dimensionGroup.getCode(), maxDimensionGroupNameList);
            dimensionGroup.setTableSubName(noSameSub);
        }
    }

    private void initPeriodMap() throws Exception {
        EntitySet peroidSet = DataHandler.getDataSet(AggConstant.BI_TABLE_Dimension, Util.quotedEqualStr(AggConstant.BI_Field_GroupId, AggConstant.peroid));
        for (Entity entity : peroidSet) {
            Dimension dimension = new Dimension(entity);
            String code = entity.getString(AggConstant.BI_Field_Code);
            peroidMap.put(code, dimension);
        }

    }

    private void initDimensionMap(DimensionGroup dimensionGroup, String id) throws Exception {
        EntitySet dimensionSet = DataHandler.getDataSet(AggConstant.BI_TABLE_Dimension, AggConstant.BI_Filter_Active + Util.And + MessageFormat.format("groupid = {0}", Util.quotedStr(id)));
        Map<String, ArrayList<Dimension>> dimensionGroupMap = getDimensionMapByGroupCode(dimensionGroup.getCode());
        add2Map(dimensionSet, dimensionGroupMap);
        if (dimensionByGroupByCodeMap.containsKey(dimensionGroup.getCode())) {
            return;
        }

        dimensionByGroupByCodeMap.put(dimensionGroup.getCode(), dimensionGroupMap);
    }

    private void add2Map(EntitySet dimensionSet, Map<String, ArrayList<Dimension>> dimensionGroupMap) throws Exception {
        for (Entity dimensionEntity: dimensionSet) {
            Dimension dimension = new Dimension(dimensionEntity);
            addParentTreeDimension(dimension);
            if (dimensionCodeMap.containsKey(dimension.getCode())) {
                continue;
            }
            dimensionCodeMap.put(dimension.getCode(), dimension);
            String rootCode = dimension.getRootCode();

            ArrayList<Dimension> dimensionList = dimensionGroupMap.get(rootCode);
            if (Util.isNull(dimensionList)) {
                dimensionList = new ArrayList<>();
                dimensionGroupMap.put(rootCode, dimensionList);
            }
            dimensionList.add(dimension);

        }
    }

    public Map<String, List<Dimension>> getDimensionByGroupByCodeMap() {
        if (Util.isNull(dimensionByGroupByCodeMap)) {
            dimensionByGroupByCodeMap = new HashMap<>();
        }
        Set<String> keySet = dimensionByGroupByCodeMap.keySet();
        Map<String, List<Dimension>> returnMap = new HashMap<>();
        for (String key : keySet) {
            Map<String, ArrayList<Dimension>> stringListMap = dimensionByGroupByCodeMap.get(key);
            List<Dimension> dimensions = new ArrayList<>();
            Collection<ArrayList<Dimension>> values = stringListMap.values();
            values.stream().map(dimensionList -> dimensions.addAll(dimensionList)).count();
            returnMap.put(key, dimensions);
        }

        return returnMap;
    }

    public Map<String, Map<String, ArrayList<Dimension>>> getRawDimensionGroup() {
        if (Util.isNull(dimensionByGroupByCodeMap)) {
            dimensionByGroupByCodeMap = new HashMap<>();
        }

        return dimensionByGroupByCodeMap;
    }


    public  List<Dimension> getDimensionByGroupCodeRootCode(String groupId,String rootCode) {
        Map<String, Map<String, ArrayList<Dimension>>> rawDimensionGroup = getRawDimensionGroup();
        if (Util.isNull(rawDimensionGroup)) {
            rawDimensionGroup = new HashMap<>();
        }
        Map<String, ArrayList<Dimension>> dimensionMaps = rawDimensionGroup.get(groupId);
        if (Util.isNull(dimensionMaps)) {
            dimensionMaps = new HashMap<>();
            rawDimensionGroup.put(groupId, dimensionMaps);
        }
        ArrayList<Dimension> dimensionList = dimensionMaps.get(rootCode);
        if (Util.isNull(dimensionList)) {
            dimensionList = new ArrayList<>();
            dimensionMaps.put(rootCode,dimensionList);
        }
        return dimensionList;
    }

    public Map<String, ArrayList<Dimension>> getDimensionMapByGroupCode(String groupId) {
        Map<String, Map<String, ArrayList<Dimension>>> rawDimensionGroup = getRawDimensionGroup();
        if (Util.isNull(rawDimensionGroup)) {
            rawDimensionGroup = new HashMap<>();
        }
        Map<String, ArrayList<Dimension>> dimensionMaps = rawDimensionGroup.get(groupId);
        if (Util.isNull(dimensionMaps)) {
            dimensionMaps = new HashMap<>();
            rawDimensionGroup.put(groupId, dimensionMaps);
        }

        return dimensionMaps;
    }

    public Map<String, Dimension> getDimensionCodeMap() {
	    if (Util.isNull(dimensionCodeMap)) {
	        dimensionCodeMap = new HashMap<>();
        }
        return dimensionCodeMap;
    }


    public static List<Dimension> getDimensionByGroupCode(String groupId) {
        AggDimensionsContainer instance = getInstance();
        Map<String, List<Dimension>> dimensionGroupCodeMap = instance.getDimensionByGroupByCodeMap();
        if (Util.isNull(dimensionGroupCodeMap) || dimensionGroupCodeMap.size() == 0) {
            return  null;
        }
        List<Dimension> dimensions = dimensionGroupCodeMap.get(groupId);
        return dimensions;
    }

    public static Dimension getDimensionByCode(String code) {
        AggDimensionsContainer instance = getInstance();
        Map<String, Dimension> dimensionCodeMap = instance.getDimensionCodeMap();
        if (Util.isNull(dimensionCodeMap) || dimensionCodeMap.size() == 0) {
            return  null;
        }
        Dimension dimension = dimensionCodeMap.get(code);
        return dimension;
    }

    public static Measurment getMeasurmentByCode(String code) {
        AggDimensionsContainer instance = getInstance();
        Map<String, Measurment> measurmentMap = instance.getMeasurmentMap();
        if (Util.isNull(measurmentMap) || measurmentMap.size() == 0) {
            return  null;
        }
        Measurment measurment = measurmentMap.get(code);
        return measurment;
    }

    public Map<String, Measurment> getMeasurmentMap() {
        return measurmentMap;
    }

    private void addParentTreeDimension(Dimension dimension) throws Exception {
        String parentCode = dimension.getParentCode();
        String code = dimension.getCode();
        int level = -1;
        String rootCode;
        if (code.equalsIgnoreCase(parentCode)) {
            level = 0;
            rootCode = code;
        } else {
            Map<String, Dimension> dimensionCodeMap = getDimensionCodeMap();
            if (Util.isNull(dimensionCodeMap)) {
                dimensionCodeMap = new HashMap<>();
            }

            Dimension parentDimension =  dimensionCodeMap.get(parentCode);
            if (Util.isNull(parentDimension)) {
                Entity line = DataHandler.getLine(AggConstant.BI_TABLE_Dimension, AggConstant.BI_Field_Code, parentCode);
                parentDimension = new Dimension(line);
                addParentTreeDimension(parentDimension);
                addOneDimension(parentDimension);
            }
            int parentLevel = parentDimension.getLevel();
            rootCode = parentDimension.getRootCode();
            level = parentLevel + 1;
        }
        dimension.setLevel(level);
        dimension.setRootCode(rootCode);
    }

    public void addOneDimension(Dimension dimension) {
        Map<String, Dimension> dimensionCodeMap = getDimensionCodeMap();
        dimensionCodeMap.put(dimension.getCode(), dimension);

        String groupId = dimension.getGroupId();
        String rootCode = dimension.getRootCode();

        List<Dimension> dimensionByGroupCodeRootCodeList = getDimensionByGroupCodeRootCode(groupId, rootCode);
        dimensionByGroupCodeRootCodeList.add(dimension);
    }

    public Map<String, Dimension> getPeroidMap() {
        return peroidMap;
    }

    public Dimension getPeroidDimension(String code) {
        AggDimensionsContainer instance = getInstance();
        Map<String, Dimension> periodMap = instance.getPeroidMap();
        if (Util.isNull(periodMap) || periodMap.size() == 0) {
            return  null;
        }
        Dimension dimension = periodMap.get(code);
        return dimension;
    }

    public Map<String, DimensionGroup> getDimensionGroupMap() {
        return DimensionGroupMap;
    }

    public DimensionGroup getDimensionGroupById(String groupId) {
        AggDimensionsContainer instance = getInstance();
        Map<String, DimensionGroup> dimensionGroupMap = instance.getDimensionGroupMap();
        if (Util.isNull(dimensionGroupMap) || dimensionGroupMap.size() == 0) {
            return  null;
        }
        DimensionGroup dimensionGroup = dimensionGroupMap.get(groupId);
        return dimensionGroup;
    }

    public ArrayList<String> getMaxSizeDimensionGroupList() {
        Set<String> keySet = DimensionGroupMap.keySet();
        if (Util.isNull(keySet)) {
            return null;
        }
        ArrayList<String> dimensionGroupList = new ArrayList<>(keySet);
        return dimensionGroupList;
    }
}
