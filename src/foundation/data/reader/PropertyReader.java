package foundation.data.reader;

import java.lang.reflect.Method;
import java.util.Date;

import foundation.data.Entity;
import foundation.data.Variant;
import foundation.data.translator.Translator;

public class PropertyReader {

	protected String name;
	protected Class<?> type;	
	protected Method getMethod;
	protected Method setMethod;
	protected Translator translator;
	protected IEntityReader valueReader;
	
	public PropertyReader(String property, Class<?> propertyType) throws Exception {
		name = property;
		type = propertyType;
		
		if (Translator.containsType(propertyType)) {
			translator = Translator.getInstance(propertyType);
		}
	}

	public void setDataString(Object entity, String value) throws Exception {
		if (setMethod == null) {
			return;
		}
		
		if (translator == null) {
			throw new Exception("can not set string value to " + type);
		}
		
		Object arg = translator.loadToObject(value);
		setMethod.invoke(entity, arg);
	}

	public String getString(Object entity) {
		if (getMethod == null) {
			return null;
		}
		
		try {
			Object value = getMethod.invoke(entity);
			
			if (translator != null) {
				return translator.toString(value);
			}
			else {
				return valueReader.getString(value);
			}
		}
		catch (Exception e) {
			return "error";
		}
	}

	public String getJSONString(Object entity) throws Exception {
		if (getMethod == null) {
			return null;
		}
		
		Object value = getMethod.invoke(entity);
		
		if (translator != null) {
			return translator.toJSONString(value);
		}
		else {
			if (valueReader == null) {
				valueReader = EntityReader.getInstance(value.getClass());
			}
			
			return valueReader.getJSONString(value);
		}
	}

	public String getSQLString(Object entity) throws Exception {
		if (getMethod == null) {
			return null;
		}
		
		if (translator == null) {
			throw new Exception("can not get sql string from " + type);
		}
		
		Object value = getMethod.invoke(entity);
		return translator.toSqlString(value);
	}

	public Variant loadToVariant(String value) throws Exception {
		if (translator == null) {
			throw new Exception("can not load to variant from " + type);
		}
		
		return translator.loadToVariant(value);
	}

	public Integer getInteger(Object entity) throws Exception {
		if (getMethod == null) {
			return null;
		}
		
		if (translator == null) {
			throw new Exception("can not get int from " + type);
		}
		
		Object value = getMethod.invoke(entity);
		return translator.toInteger(value);
	}

	public Double getDouble(Entity entity) throws Exception {
		if (getMethod == null) {
			return null;
		}
		
		if (translator == null) {
			throw new Exception("can not get double from " + type);
		}
		
		Object value = getMethod.invoke(entity);
		return translator.toDouble(value);	
	}

	public Boolean getBoolean(Entity entity) throws Exception {
		if (getMethod == null) {
			return null;
		}
		
		if (translator == null) {
			throw new Exception("can not get boolean from " + type);
		}
		
		Object value = getMethod.invoke(entity);
		return translator.toBoolean(value);	
	}

	public Date getDate(Entity entity) throws Exception {
		if (getMethod == null) {
			return null;
		}
		
		if (translator == null) {
			throw new Exception("can not get date from " + type);
		}
		
		Object value = getMethod.invoke(entity);
		return translator.toDate(value);	
	}
	
	public void setSetMethod(Method setMethod) {
		this.setMethod = setMethod;
	}

	public void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}

	public void setData(Object entity, Object value) throws Exception {
		if (setMethod == null) {
			return;
		}
		
		setMethod.invoke(entity, value);
	}
	
	public Object getObject(Object entity) throws Exception {
		if (getMethod == null) {
			return null;
		}
		
		return getMethod.invoke(entity);
	}

}
