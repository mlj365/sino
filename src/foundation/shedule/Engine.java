package foundation.shedule;

import bi.ThemeContext;
import bi.WorkEngine;
import bi.agg.Operator;
import foundation.server.InternalServer;
import foundation.server.Progressor;
import org.apache.log4j.Logger;

public class Engine implements Runnable {

	enum State {Idle, working};
	
	private static Logger logger;
	private static Engine instance;
	private State state;
	private Object lock;
	private Progressor progressor;
	private String command;

	static {
		logger = Logger.getLogger(Engine.class);
	}
	
	private Engine() throws Exception {
		lock = new Object();
		progressor = new Progressor();
		state = State.Idle;
	}
	
	public static synchronized Engine getInstance() throws Exception {
		if (instance == null) {
			instance = new Engine();
		}
		
		return instance;
	}
	
	public void exec(String command) {
		if (State.Idle == state) {
			synchronized (lock) {
				if (InternalServer.Terminate) {
					return;
				}
				
				if (State.Idle == state) {
					this.command = command;
					Thread thread = new Thread(this);
					thread.start();
				}
			}
		}
	}
	
	@Override
	public void run() {
		try {
			if ("agg".equalsIgnoreCase(command)) {
				agg();
			}
			
		}
		catch(Exception e) {
			logger.error(e);
			progressor.appendMesage("系统出错：" + e.getMessage());
		}
		finally {
			command = null;
			state = State.Idle;
		}
	}

	private void agg() {
		try {
		ThemeContext context = new ThemeContext(null, null);
		Operator operator = new Operator(context);


			WorkEngine.getInstance().exec(operator, "createbaseaggregation");

		} catch (Exception e) {
			e.printStackTrace();

		}
	}



	public Progressor getProgressor() {
		return progressor;
	}
	
}
