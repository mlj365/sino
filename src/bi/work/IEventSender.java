package bi.work;

import foundation.persist.sql.NamedSQL;

public interface IEventSender {

	String getName();

	void setParametersTo(NamedSQL namedSQL, Object... args) throws Exception;

}
