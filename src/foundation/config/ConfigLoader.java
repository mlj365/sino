package foundation.config;

import org.apache.log4j.Logger;


public abstract class ConfigLoader implements IPreloader {

	protected static Logger logger;
	protected String name;
	protected boolean active;
	
	static {
		logger = Logger.getLogger(ConfigLoader.class);		
	}
	
	public ConfigLoader() {
		
	}

	public abstract void load() throws Exception;
	
	public String getName() {
		return name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
