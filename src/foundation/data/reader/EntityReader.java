package foundation.data.reader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public abstract class EntityReader implements IEntityReader {

	protected static Logger logger;
	protected static Set<String> excludeMethods;
	protected Class<?> clazz;

	static {
		logger = Logger.getLogger(ObjectReader.class);
		createExcludeMethodSet();
	}

	protected EntityReader(Class<?> clazz) {
		this.clazz = clazz;
	}

	public static IEntityReader getInstance(Class<?> clazz) throws Exception {

		if (Collection.class.isAssignableFrom(clazz)) {
			return new CollectionReader(clazz);

		} else if (Map.class.isAssignableFrom(clazz)) {
			return new MapReader(clazz);

		} else {
			return new ObjectReader(clazz);
		}
	}

	protected abstract void initValueReader(Class<?> dataType) throws Exception;

	public abstract String getString(Object entity);

	public abstract String getJSONString(Object entity) throws Exception;

	public Class<?> getEntityClass() {
		return clazz;
	}

	private static void createExcludeMethodSet() {
		excludeMethods = new HashSet<String>();
		excludeMethods.add("getclass");
		excludeMethods.add("getnames");
		excludeMethods.add("getsize");
		excludeMethods.add("containsproperty");
		excludeMethods.add("loaddata");
		excludeMethods.add("setdata");
		excludeMethods.add("setdatastring");
		excludeMethods.add("getdata");
		excludeMethods.add("getstring");
		excludeMethods.add("getjsonstring");
		excludeMethods.add("getsqlstring");
		excludeMethods.add("getvalue");
		excludeMethods.add("getdynamicdata");
		excludeMethods.add("iterator");
		excludeMethods.add("tostring");
		excludeMethods.add("finalize");
		excludeMethods.add("wait");
		excludeMethods.add("hashcode");
		excludeMethods.add("clone");
		excludeMethods.add("registernatives");
		excludeMethods.add("equals");
		excludeMethods.add("notify");
		excludeMethods.add("notifyall");
		excludeMethods.add("getinstance");
	}

}
