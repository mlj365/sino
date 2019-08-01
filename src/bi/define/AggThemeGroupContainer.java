package bi.define;

import bi.AggConstant;
import foundation.config.Preloader;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class AggThemeGroupContainer extends Preloader {
	
	private Logger logger;
	private static AggThemeGroupContainer instance;
	private List<AggThemeGroup> aggThemeGroupList;
	
	public synchronized static AggThemeGroupContainer getInstance() {
		if (instance == null) {
			instance = new AggThemeGroupContainer();
		}
		return instance;
	}

	private AggThemeGroupContainer() {
		logger = LoggerFactory.getLogger(this.getClass());
		aggThemeGroupList = new ArrayList<AggThemeGroup>();
		
		loadData();
	}

	@Override
	public void load() throws Exception {
		loadData();
	}


	public static void refresh() {
		AggThemeGroupContainer instance = getInstance();
		
		instance.aggThemeGroupList = new ArrayList<AggThemeGroup>();
		instance.loadData();
	}
	
	private void loadData() {
		try {
			EntitySet dataSet = DataHandler.getDataSet(AggConstant.BI_TABLE_THEMEGROUP, AggConstant.BI_Filter_Active, AggConstant.BI_Field_No);

			for (Entity entity : dataSet) {
				AggThemeGroup aggThemeGroup = new AggThemeGroup(entity);
				aggThemeGroupList.add(aggThemeGroup);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public List<AggThemeGroup> getAggThemeGroupList() {
		return aggThemeGroupList;
	}
	
	
}
