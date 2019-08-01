package bi.work;

import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataHandler;
import foundation.persist.SystemCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarrierContainer {

	private static CarrierContainer instance;
	private Map<String, List<Carrier>> carriersMap;
	private List<Carrier> emptyList; 
	
	private CarrierContainer() {
		carriersMap = new HashMap<String, List<Carrier>>();
		emptyList = new ArrayList<Carrier>();
	}
	
	public synchronized static CarrierContainer getInstance() {
		if(instance == null){
			instance = new CarrierContainer();
		}
		return instance;
	}
	
	public void refresh() throws Exception {
		clear();
		load();
	}

	public void load() throws Exception {
		EntitySet dataSet = DataHandler.getDataSet("carrier", "", "order by  no");
		
		for (Entity entity : dataSet) {
			Carrier carrier = new Carrier();
			carrier.load(entity);
			
			String objectname = entity.getString("objectname");
			
			if (objectname == null) {
				continue;
			}
			
			objectname = objectname.toLowerCase();
			List<Carrier> carrieLlist = carriersMap.get(objectname);
			
			if (carrieLlist == null) {
				carrieLlist = new ArrayList<Carrier>();
				carriersMap.put(objectname, carrieLlist);
			}
			
			carrieLlist.add(carrier);
		}
	}

	public void clear() {
		carriersMap.clear();
	}

	public List<Carrier> getList(String name) {
		if (name == null) {
			return emptyList;
		}
		
		name = name.toLowerCase();
		
		List<Carrier> validCarrier = new ArrayList<Carrier>();
		List<Carrier> allCarrier = carriersMap.get(name);
		
		if(allCarrier == null){
			return validCarrier;
		}
		
		for (Carrier carrier : allCarrier) {
			//保证初始化时执行init、all、null    work是执行working、all、null
			if (!SystemCondition.isCompatible(carrier.getCondition())) {
				continue;
			}
			
			validCarrier.add(carrier);
			
		}
		return validCarrier;
	}

	public List<Carrier> getList(String dataName, String flowName) {
		String name = dataName + "(" + flowName + ")";
		return getList(name);
	}
}
