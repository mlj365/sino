package foundation.data.reader;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import foundation.data.Entity;
import foundation.data.PropertyIterator;
import foundation.data.Variant;
import foundation.util.ContentBuilder;

public class ObjectReader extends EntityReader implements Iterable<PropertyReader> {

	private int size;
	private String[] names;
	private String[] lowerNames;
	private Map<String, Integer> nameMap;
	private PropertyReader[] propertyReaderArray;
	
	public ObjectReader(Class<?> clazz) throws Exception {
		super(clazz);
		initValueReader(clazz);
	}
	
	@Override
	protected void initValueReader(Class<?> dataType) throws Exception {
		ObjectPropertyRecorder propertyRecorder = new ObjectPropertyRecorder();
		
		Method[] methods;
		String methodName;
		String property;
		char first;
		
		while (dataType != null) {
			//自己的所有方法(含私有)
			methods = dataType.getDeclaredMethods();
		
			for (Method method: methods) {
				if (method.isBridge()) {
					continue;
				}
				
				methodName = method.getName();
				Class<?>[] params = method.getParameterTypes();
				
				if (excludeMethods.contains(methodName.toLowerCase())) {
					continue;
				}
				
				if (methodName.startsWith("get")) {
					if (params.length > 0) {
						continue;
					}
					
					Class<?> returnTypeClass = method.getReturnType();
					if (Enum.class.isAssignableFrom(returnTypeClass)) {
						continue;
					}
					
					property = methodName.substring(3); 
					first = property.charAt(0);
					property = Character.toLowerCase(first) + property.substring(1);
					
					propertyRecorder.addGetMethod(property, method);
				}
				else if (methodName.startsWith("is")) {
					if (params.length > 0) {
						continue;
					}
					
					property = methodName.substring(2); 
					first = property.charAt(0);
					property = Character.toLowerCase(first) + property.substring(1);
					
					propertyRecorder.addGetMethod(property, method);
				}
				else if (methodName.startsWith("set")) {
					if (params.length > 1) {
						continue;
					}
					
					property = methodName.substring(3); 
					first = property.charAt(0);
					property = Character.toLowerCase(first) + property.substring(1);
					
					propertyRecorder.addSetMethod(property, method);
				}
			}
			
			dataType = dataType.getSuperclass();
		}
	
		names = propertyRecorder.getNameArray();
		lowerNames = propertyRecorder.getLowerNameArray();
		nameMap = propertyRecorder.getNameMap();
		propertyReaderArray = propertyRecorder.getPropertyReaderArray();
		size = propertyReaderArray.length;		
	}

	@Override
	public String getString(Object entity) {
		ContentBuilder result = new ContentBuilder();
		result.append("[");
		
		for (int i = 0; i < size; i++) {
			String value;
			try {
				value = propertyReaderArray[i].getJSONString(entity);
			} catch (Exception e) {
				value = "error";
			}
			result.append(lowerNames[i], ",").append(":").append(value);
		}
		
		result.append("]");
		return result.toString();
	}

	@Override
	public String getJSONString(Object entity) throws Exception {
		ContentBuilder result = new ContentBuilder();
		result.append("{");
		
		result.setEmpty(true);
		
		for (int i = 0; i < size; i++) {
			String value = propertyReaderArray[i].getJSONString(entity);
			result.append("\"", ",").append(lowerNames[i]).append("\":").append(value);
		}
		
		result.append("}");
		return result.toString();
	}

	public String[] getNames() {
		return names;
	}
	
	public String[] getLowerNames() {
		return lowerNames;
	}
	
	public boolean containsProperty(String name) {
		if (name == null) {
			return false;
		}
		
		return nameMap.containsKey(name.toLowerCase());
	}
	
	public int size() {
		return size;
	}
	
	public Integer getIndexByName(String name) {
		if (name == null) {
			return null;
		}
		
		return nameMap.get(name.toLowerCase());
	}

	public PropertyReader getPropertyReader(String name) {
		if (name == null) {
			return null;
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			return null;			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		return propertyReader;
	}
	
	public PropertyReader getPropertyReader(int idx) {
		return propertyReaderArray[idx];
	}
	
	public void setData(String name, Object value, Object entity) throws Exception {
		if (name == null) {
			return;
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			throw new Exception("set data error: " + name + " not exist");			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		propertyReader.setData(entity, value);
	}
	
	public void setData(int idx, Object value, Object entity) throws Exception {
		if (idx >= 0 && idx < size) {
			PropertyReader propertyReader = propertyReaderArray[idx];
			propertyReader.setData(entity, value);
		}
	}
	
	public void setDataString(String name, String value, Object entity) throws Exception {
		if (name == null) {
			return;
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			throw new Exception("set data string error: " + name + " not exist");			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		propertyReader.setDataString(entity, value);
	}
	
	public void setDataString(int idx, String value, Object entity) throws Exception {
		if (idx >= 0 && idx < size) {
			PropertyReader propertyReader = propertyReaderArray[idx];
			propertyReader.setDataString(entity, value);
		}
	}
	
	public Object getData(String name, Object entity) throws Exception {
		if (name == null) {
			return null;
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			return null;	
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		return propertyReader.getObject(entity);
	}
	
	public Object getData(int propertyIndex, Object entity) throws Exception {
		if (propertyIndex >= 0 && propertyIndex < size) {
			PropertyReader propertyReader = propertyReaderArray[propertyIndex];
			return propertyReader.getObject(entity);
		}
		else {
			throw new Exception("get data error: index " + propertyIndex + " out of 0~" + size);
		}
	}
	
	public String getString(String name, Object entity) {
		if (name == null) {
			return "error";
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			return "error";			
		}
		
		try {
			PropertyReader propertyReader = propertyReaderArray[idx];
			return propertyReader.getString(entity);
		}
		catch (Exception e) {
			return "error";
		}
	}
	
	public String getString(int index, Object entity) {
		if (index >= 0 && index < size) {
			try {
				PropertyReader propertyReader = propertyReaderArray[index];
				return propertyReader.getString(entity);
			}
			catch (Exception e) {
				return "error";
			}
		}
		else {
			return "error";
		}
	}
	
	public String getJSONString(String name, Object entity) throws Exception {
		if (name == null) {
			return "error";
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			return "error";			
		}
		
		try {
			PropertyReader propertyReader = propertyReaderArray[idx];
			return propertyReader.getJSONString(entity);
		}
		catch (Exception e) {
			return "error";
		}
	}

	public String getJSONString(int index, Object entity) throws Exception {
		if (index >= 0 && index < size) {
			try {
				PropertyReader propertyReader = propertyReaderArray[index];
				return propertyReader.getJSONString(entity);
			}
			catch (Exception e) {
				return "\"error\"";
			}
		}
		else {
			return "\"error\"";
		}
	}
	
	public String getSQLString(String name, Object entity) throws Exception {
		if (name == null) {
			throw new Exception("get sql string error: empty name");
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			throw new Exception("get sql string error: " + name + " not exist");			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		return propertyReader.getSQLString(entity);
	}
	
	public String getSQLString(int propertyIndex, Object entity) throws Exception {
		if (propertyIndex >= 0 && propertyIndex < size) {
			PropertyReader propertyReader = propertyReaderArray[propertyIndex];
			return propertyReader.getSQLString(entity);
		}
		else {
			throw new Exception("get json string error: index " + propertyIndex + " out of 0~" + size);
		}
	}
	
	public Integer getInteger(String name, Entity entity) throws Exception {
		if (name == null) {
			throw new Exception("get integer error: empty name");
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			throw new Exception("get integer error: " + name + " not exist");			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		return propertyReader.getInteger(entity);
	}

	public Double getDouble(String name, Entity entity) throws Exception {
		if (name == null) {
			throw new Exception("get double error: empty name");
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			throw new Exception("get integer error: " + name + " not exist");			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		return propertyReader.getDouble(entity);
	}

	public Boolean getBoolean(String name, Entity entity) throws Exception {
		if (name == null) {
			throw new Exception("get boolean error: empty name");
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			throw new Exception("get integer error: " + name + " not exist");			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		return propertyReader.getBoolean(entity);
	}

	public Date getDate(String name, Entity entity) throws Exception {
		if (name == null) {
			throw new Exception("get date error: empty name");
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			throw new Exception("get integer error: " + name + " not exist");			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		return propertyReader.getDate(entity);
	}
	
	public Variant loadToVariant(String name, String value) throws Exception {
		if (name == null) {
			throw new Exception("load to variant error: empty name");
		}
		
		Integer idx = nameMap.get(name.toLowerCase());
		
		if (idx == null) {
			throw new Exception("load to variant error: " + name + " not exist");			
		}
		
		PropertyReader propertyReader = propertyReaderArray[idx];
		return propertyReader.loadToVariant(value);
	}
	
	public String getPropertyName(int idx) {
		return names[idx];
	}
	
	@Override
	public Iterator<PropertyReader> iterator() {
		return new PropertyIterator(this);
	}

}
