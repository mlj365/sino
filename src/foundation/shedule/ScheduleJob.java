package foundation.shedule;


import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import foundation.util.Util;

public class ScheduleJob implements Job{
	
	protected static Logger logger;
	
	private Object lock = new Object();
	private int tryTimes;
	private int tryCycles;
	private Date beginTime;
	private Date endTime;
	private boolean success;
	
	static {
		logger = Logger.getLogger(ScheduleJob.class);
	}
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		JobDataMap dataMap = jobDetail.getJobDataMap();
		
		IJob job = (IJob)dataMap.get(IJob.Key_Job);
		
		logger.debug("trigger job:" + job.getName());
		
		synchronized (lock) {
			JobStatus status = job.getStatus();

			if (JobStatus.Idle == status) {
				String taskId = Util.newShortGUID();
				
				beforeExecute(job, taskId);
				try {
					executeJob(job, taskId);
				}
				finally {
					afterExecute(job, taskId);
				}
			}
		}
	}

	private void beforeExecute(IJob job, String taskId) {
		success = false;
		beginTime = new Date();
		tryTimes = 0;
		tryCycles = 0;	
				
		job.begin(taskId, beginTime);
	}
	private void afterExecute(IJob job, String taskId) {
		if (success) {
			job.succeed(taskId, tryTimes, tryCycles, endTime);
		}
		else {
			job.fail(taskId, tryTimes, tryCycles, endTime);
		}
	}
	
	private void executeJob(IJob job, String taskId) {
		int cnt = 0;
		String name = job.getName();
		
		for (int j = 0; j < IJob.Max_TryCycles; j++) {
			tryCycles++;
			
			for (int i = 0; i < IJob.Max_TryTimes; i++) {
				if (Schedule.Terminated) {
					break;
				}
				
				tryTimes++;
				
				try {
					cnt = cnt + 1;
					logger.info(name + "尝试第" + cnt + "次运行...");
					
					job.execute();
					
					success = true;
					break;
				}
				catch (Exception e) {
					
					logger.info(e.getMessage());
				}
			}
			
			if (success) {
				break;
			}
			
			try {
				int waitfor = 60 * IJob.Minutes_IntervalCycle;
				
				for (int w = 0; w < waitfor; w++) {
					Thread.sleep(1000);
					
					if (Schedule.Terminated) {
						break;
					}
				}
			} catch (InterruptedException e) {
			}				
		}		
	}
}
