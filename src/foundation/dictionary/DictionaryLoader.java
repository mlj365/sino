package foundation.dictionary;

import foundation.config.IPreloader;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;


public class DictionaryLoader implements IPreloader {

	private String name;
	private boolean active;
	private static DictionaryLoader instance;
	private static DictionaryContainer container;
	
	private DictionaryLoader() {
		container = DictionaryContainer.getInstance();
	}
	
	public static synchronized DictionaryLoader getInstance() {
		if (instance == null) {
			instance = new DictionaryLoader();
		}
		
		return instance;
	}
	
	@Override
	public void load() throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("getDictionary");
		EntitySet entitySet = SQLRunner.getEntitySet(namedSQL);
		
		for (Entity entity: entitySet) {
			String group = entity.getString("groupcode");
			String key = entity.getString("code");
			String value = entity.getString("value");
			
			container.add(group, key, value);
		}
	}
	
	public void refresh(Dictionary defination) throws Exception {
		defination.clear();
		
		String name = defination.getName();
		
		NamedSQL namedSQL = NamedSQL.getInstance("getOneDictionary");
		namedSQL.setParam("groupcode", name);
		EntitySet entitySet = SQLRunner.getEntitySet(namedSQL);
		
		for (Entity entity: entitySet) {
			String key = entity.getString("code");
			String value = entity.getString("value");
			
			defination.append(key, value);
		}
		
		defination.setDirty(false);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
