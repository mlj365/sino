package foundation.data.reader;

public interface IEntityReader {

	String getString(Object entity);
	
	String getJSONString(Object entity) throws Exception;
	
}
