package foundation.data.reader;

import java.util.Map;
import java.util.Set;

import foundation.data.translator.Translator;
import foundation.util.ContentBuilder;

public class MapReader extends EntityReader {

	protected Translator translator;
	protected IEntityReader valueReader;
	
	public MapReader(Class<?> clazz) throws Exception {
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
		try {
			Map<Object, Object> map = (Map<Object, Object>) entity;		
			
			if (map.isEmpty()) {
				return "[]";
			}
			
			ContentBuilder result = new ContentBuilder();
			result.append("[");
			
			Set<Object> keys = map.keySet();

			initValueReader(keys.iterator().next().getClass());
		
			for (Object key: keys) {
				Object obj = map.get(key);
				
				try {
					if (translator != null) {
						result.append(key, ",").append(":").append(translator.toString(obj));
					}
					else {
						result.append(key, ",").append(":").append(valueReader.getString(obj));
					}					
				}
				catch (Exception e) {
					result.append(key, ",").append(":error");
				}
			}
			
			result.append("]");
			return result.toString();
		} 
		catch (Exception e) {
			return e.getMessage();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getJSONString(Object entity) throws Exception {
		Map<Object, Object> map = (Map<Object, Object>) entity;
		
		if (map.isEmpty()) {
			return "{}";
		}
		
		ContentBuilder result = new ContentBuilder();
		result.append("{");
		
		Set<Object> keys = map.keySet();
		
		try {
			Class<?> clazz = keys.iterator().next().getClass();
			initValueReader(clazz);
		} catch (Exception e) {
			return e.getMessage();
		}
		
		for (Object key: keys) {
			Object obj = map.get(key);
			
			try {
				if (translator != null) {
					result.append("\"", ",").append(key).append("\":").append(translator.toJSONString(obj));
				}
				else {
					result.append("\"", ",").append(key).append("\":").append(valueReader.getJSONString(obj));
				}				
			}
			catch (Exception e) {
				result.append("\"", ",").append(key).append("\":\"error\"");
			}
		}
		
		result.append("}");
		return result.toString();
	}

}
