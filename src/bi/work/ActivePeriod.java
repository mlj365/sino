package bi.work;

import foundation.util.Util;

public class ActivePeriod {

	private static ActivePeriod instance;
	protected int year;
	protected int month;
	protected Integer calculateYear;
	protected Integer calculateMonth;	
	protected Integer monthNo;
	
	private ActivePeriod() {
		
	}
	
	public synchronized static ActivePeriod getInstance() {
		if (instance == null) {
			instance = new ActivePeriod();
		}
		
		return instance;
	}
	
	public int getValue(String periodType) {
		if (Util.isEmptyStr(periodType)) {
			return month;
		}
		
		if ("month".equalsIgnoreCase(periodType)) {
			return month;
		}
		if ("monthno".equalsIgnoreCase(periodType)) {
			return monthNo;
		}
		else if ("dmonth".equalsIgnoreCase(periodType)) {
			return getDMonth();
		}
		else if ("year".equalsIgnoreCase(periodType)) {
			return 0;
		}
		
		return 0;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}
	
	public int getDMonth() {
		return Double.valueOf(Math.ceil(month / 2.0)).intValue();
	}

	public Integer getCalculateYear() {
		return calculateYear;
	}

	public Integer getCalculateMonth() {
		return calculateMonth;
	}

	public Integer getMonthNo() {
		return monthNo;
	}
	
}
