package bi.agg;

import bi.ThemeContext;
import foundation.callable.Context;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Operator extends Context {

	private static Set<String> defaultSettings;
	
	private boolean test;
	private boolean validate;
	private boolean calculateSum;
	private boolean calculateRate;
	private boolean calculateRanking;
	private Set<String> settings;
	private Date workingTime;
	private ThemeContext context;
	
	static {
		defaultSettings = new HashSet<String>();
	}
	
	public Operator(ThemeContext context) {
		super(context.getRequest());
		this.context = context;
		workingTime = new Date();
		settings = new HashSet<String>();
		
		validate = true;
		calculateSum = true;
		calculateRate = true;
		calculateRanking = true;
		
		loadContext(context);
	}
	
	private void loadContext(ThemeContext context) {
		settings.add("agg_total");
	}

	public ThemeContext getContext() {
		return context;
	}

	public String[] getTypeCodes() {
		return context.getTypeCodes();
	}
	
	public int getYear() {
		return context.getYear();
	}
	
	public int getMonth() {
		return context.getMonth();
	}
	
	public boolean isTest() {
		return test;
	}
	
	public boolean isValidate() {
		return validate;
	}
	
	public boolean isCalculateSum() {
		return calculateSum;
	}
	
	public boolean isCalculateRate() {
		return calculateRate;
	}
	
	public boolean isCalculateRanking() {
		return calculateRanking;
	}
	
	public Date getWorkingTime() {
		return workingTime;
	}
	

	public static void appendDefaultSetting(String name) {
		if (name == null) {
			return;
		}
		
		name = name.toLowerCase();
		defaultSettings.add(name);
	}
	
}
