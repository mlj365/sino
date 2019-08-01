package foundation.shedule;

import java.util.Date;

import org.quartz.JobKey;
import org.quartz.TriggerKey;

public interface IJob {

	static String Key_Job = "Job";
	static String Key_Config = "Config";
	
	static int Max_TryTimes = 3;
	static int Max_TryCycles = 2;
	static int Minutes_IntervalCycle = 10;
	
	String getId();
	
	String getName();
	
	void begin(String taskId, Date beginTime);
	
	void succeed(String taskId, int tryTimes, int tryCycles, Date endTime);
	
	void fail(String taskId, int tryTimes, int tryCycles, Date endTime);

	void execute() throws Exception;

	String getRunTime();

	JobKey getJobKey();
	
	TriggerKey getTriggerKey();

	JobStatus getStatus();
	
}
