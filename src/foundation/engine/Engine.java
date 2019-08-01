package foundation.engine;

import foundation.callable.Context;
import foundation.server.Progressor;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class Engine implements IEngine {

	protected static Logger logger;
	protected State state;
	protected Object lock;
	protected Map<String, Command> commandMap;
	protected Progressor progressor;

	static {
		logger = Logger.getLogger(Engine.class);
	}

	protected Engine() throws Exception {
		lock = new Object();
		commandMap = new HashMap<String, Command>();
		progressor = new Progressor();
		state = State.Idle;
		
		initCommandMap();
	}

	protected abstract void initCommandMap() throws SecurityException, NoSuchMethodException;


	  public void exec(Context context, String commandText) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	    Command command = (Command)this.commandMap.get(commandText);

	    if (command == null) {
	      throw new NoSuchMethodException("no such method: " + commandText);
	    }

	    command.exec(new Object[] { context });
	  }

	@Override
	public State getState() {
		return state;
	}

	@Override
	public void setState(State state) {
		this.state = state;
	}

	@Override
	public Object getLock() {
		return lock;
	}

	public Progressor getProgressor() {
		return progressor;
	}
}
