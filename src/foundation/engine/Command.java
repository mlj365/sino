package foundation.engine;

import foundation.server.InternalServer;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Command implements Runnable{

	private static Logger logger;
	private IEngine engine;
	private Method method;
	private boolean isNewThread;
	private Object[] args;
	
	static {
		logger = Logger.getLogger(Command.class);
	}
	
	public Command(IEngine engine, Method method, boolean isNewThread) {
		this.engine = engine;
		this.method = method;
		this.isNewThread = isNewThread;
	}

	public void exec(Object ...args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		State state = engine.getState();
		Object lock = engine.getLock();
		
		if (!isNewThread) {
			method.setAccessible(true);
			method.invoke(engine);
			return;
		}
		
		if (State.Idle == state) {
			synchronized (lock) {
				if (InternalServer.Terminate) {
					return;
				}
				
				state = engine.getState();
				
				if (State.Idle == state) {
					this.args = args; 
					Thread thread = new Thread(this);
					thread.start();
				}
			}
		}		
	}

	@Override
	public void run() {
		try {
			method.setAccessible(true);
			method.invoke(engine, args);
		}
		catch (InvocationTargetException ie) {
			Throwable targetEx = ie.getTargetException();

			if (targetEx != null) {
				logger.error(targetEx.getClass().getName() + ": " + targetEx.getMessage());
			}
			else {
				logger.error(ie.getClass().getName() + ": " + ie.getMessage());
			}
		}
		catch (Exception e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage());
		}
		finally {
			args = null;
			engine.setState(State.Idle);
		}
	}
}
