package foundation.engine;


public interface IEngine {

	State getState();

	void setState(State idle);
	
	Object getLock();
	
}
