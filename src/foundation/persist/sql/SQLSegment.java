package foundation.persist.sql;

import foundation.variant.Segment;
import org.apache.log4j.Logger;

public abstract class SQLSegment extends Segment{

	protected static Logger logger;
	
	static {
		logger = Logger.getLogger(SQLSegment.class);
	}
	
	abstract public String getValueString();

	abstract public Segment newInstance();
	
}
