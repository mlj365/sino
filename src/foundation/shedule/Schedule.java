package foundation.shedule;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import foundation.config.Configer;



public class Schedule {


	private static Logger logger;
	private static Scheduler scheduler;
	public static boolean Terminated;
	
	static {
		logger = Logger.getLogger(Schedule.class);
		Terminated = false;
	}
	
	
	public static void startup() {
	    try {
			SchedulerFactory factory = new StdSchedulerFactory(Configer.getPath_TimerConfig());
			scheduler = factory.getScheduler();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	public static void shutdown() {
		if (scheduler == null) {
			return;
		}
		
		try {
			Terminated = true;
			scheduler.shutdown();
		}
		catch (Exception e) {
			logger.error(e);
		}
	}
	
	public static void appendJob(IJob job) throws Exception {
		if (scheduler == null) {
			startup();
		}
			
		String runTime = job.getRunTime();
		
		if (runTime == null) {
			return;
		}
		
		JobKey jobKey = job.getJobKey();
	    JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(jobKey).build();
	    
	    JobDataMap dataMap = jobDetail.getJobDataMap();
	    dataMap.put(IJob.Key_Job, job);
	    
	    TriggerKey triggerKey = job.getTriggerKey();
	    TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
	    triggerBuilder.withIdentity(triggerKey);
	    triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(runTime));
	    CronTrigger trigger = (CronTrigger)triggerBuilder.build();

	    if (!scheduler.checkExists(jobKey)) {
	    	logger.debug("添加job到schedule：" + job.getName() + "(" + job.getRunTime() + ")");
		    scheduler.scheduleJob(jobDetail, trigger);
	    }
	}

	public static void deleteJob(IJob job) throws SchedulerException {
		JobKey jobKey = job.getJobKey();
		
		if (scheduler.checkExists(jobKey)) {
			scheduler.deleteJob(jobKey);
		}
	}
	
	public static void start() throws SchedulerException {
		scheduler.start();
	}
	
	public static void pause() throws SchedulerException {
		scheduler.pauseAll();
	}
	
	public void clear() throws SchedulerException {
		scheduler.clear();
	}
	
	public void isStarted() throws SchedulerException {
		scheduler.isStarted();
	}
	
	public void isShutdown() throws SchedulerException {
		scheduler.isShutdown();
	}
	
}
