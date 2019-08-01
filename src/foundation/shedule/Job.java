package foundation.shedule;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.TriggerKey;


public abstract class Job implements IJob {

	protected static Logger logger;
	private Object lock = new Object();
	private JobStatus status;
	protected String group;
	protected String id;
	protected String name;
	protected String runtime;
	protected JobKey jobKey;
	protected TriggerKey triggerKey;
	
	static {
		logger = Logger.getLogger(Job.class);
	}
	
	public Job() {
		
	}
	
	public Job(String group, String id) {
		this.group = group;
		this.id = id;
		
		init();
	}
	
	private void init() {
		jobKey = new JobKey(id, group);
		triggerKey = new TriggerKey(id, group);
		status = JobStatus.Idle;		
	}

	public void execute() throws Exception {
		if (JobStatus.Idle == status || JobStatus.Error == status) {
			synchronized (lock) {
				if (JobStatus.Idle == status || JobStatus.Error == status) {
					doExecute();				
				}
				else {
					logger.info(name + "自动退出本次运行，前一次运行状态：" + status);
				}
			}
		}
		else {
			logger.info(name + "自动退出本次运行，前一次运行状态：" + status);
		}		
	}

	protected abstract void doExecute() throws Exception;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRunTime() {
		return runtime;
	}

	public JobKey getJobKey() {
		return jobKey;
	}

	public TriggerKey getTriggerKey() {
		return triggerKey;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	public void setId(String id) {
		this.id = id;
		
		if (group != null) {
			init();
		}
	}

	public void setGroup(String group) {
		this.group = group;
		
		if (id != null) {
			init();
		}		
	}
}
