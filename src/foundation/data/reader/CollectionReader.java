package foundation.data.reader;

import java.util.Collection;

import foundation.data.translator.Translator;
import foundation.util.ContentBuilder;

public class CollectionReader extends EntityReader {

	protected IEntityReader valueReader;
	protected Translator translator;
	
	public CollectionReader(Class<?> clazz) throws Exception {
		super(clazz);
	}

	@Override
	protected void initValueReader(Class<?> dataType) throws Exception {
		if (translator == null && valueReader == null) {
			if (Translator.containsType(dataType)) {
				translator = Translator.getInstance(dataType);
			}
			else {
				valueReader = EntityReader.getInstance(dataType);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getString(Object entity) {
		Collection<Object> collection = (Collection<Object>) entity;
		
		if (collection.isEmpty()) {
			return "[]";
		}
		
		ContentBuilder result = new ContentBuilder();
		result.append("[");
		
		try {
			initValueReader(collection.iterator().next().getClass());
		} catch (Exception e) {
			return e.getMessage();
		}
		
		for (Object value: collection) {
			try {
				if (translator != null) {
					result.append(translator.toString(value), ", ");
				}
				else {
					result.append(valueReader.getString(value), ", ");
				}
			}
			catch (Exception e) {
				result.append("error", ",");
			}
		}
		
		result.append("]");
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getJSONString(Object entity) throws Exception {
		Collection<Object> collection = (Collection<Object>) entity;
		
		if (collection.isEmpty()) {
			return "[]";
		}
		
		ContentBuilder result = new ContentBuilder();
		result.append("[");
		
		try {
			Class<?> clazz = collection.iterator().next().getClass();
			initValueReader(clazz);
		} 
		catch (Exception e) {
			return e.getMessage();
		}
		
		for (Object value: collection) {
			try {
				if (translator != null) {
					result.append(translator.toJSONString(value), ", ");
				}
				else {
					result.append(valueReader.getJSONString(value), ", ");
				}
			}
			catch (Exception e) {
				result.append("error", ",");
			}
		}
		
		result.append("]");
		return result.toString();
	}

}
