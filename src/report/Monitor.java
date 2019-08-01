package report;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Monitor {

	private static Monitor instance;
	private static Object lock;
	private Map<String, FileTask> taskMap;

	static {
		lock = new Object();
	}
	
	private Monitor() {
		taskMap = new ConcurrentHashMap<String, FileTask>();
	}
	
	private static Monitor getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new Monitor();
				}
			}
		}
		
		return instance;
	}
	
	public static FileTask getStatus(String id) {
		instance = getInstance();
		FileTask task = instance.taskMap.get(id);
		return task;
	}



	public static FileTask createTask(String id) {
		instance = getInstance();		
		FileTask task = new FileTask(id);
		instance.taskMap.put(id, task);
		return task;
	}

	public static FileTask endTask(String taskid) {
		return instance.taskMap.remove(taskid);
	}

	public static void setStatus(String taskid, String status) {
		instance = getInstance();
		FileTask task = instance.taskMap.get(taskid);
		task.setStatus(status);
	}
	
	public static void set(String taskid, String status, String filename) {
		instance = getInstance();
		FileTask task = instance.taskMap.get(taskid);
		task.setStatus(status);
		task.setFileName(filename);
	}
	
}
