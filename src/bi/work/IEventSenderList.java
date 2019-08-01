package bi.work;

import foundation.persist.sql.NamedSQL;


public interface IEventSenderList extends Iterable<IEventSender> {

	void setParametersTo(NamedSQL namedSQL);

	
	
}
