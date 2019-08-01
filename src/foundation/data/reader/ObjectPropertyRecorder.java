package foundation.data.reader;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import foundation.data.translator.Translator;

public class ObjectPropertyRecorder {
	
	private List<String> nameList;
	private List<String> lowerNameList;
	private Map<String, Integer> nameMap;
	private Map<String, PropertyReader> propertyReaderMap;
	private List<PropertyReader> propertyReaderList;
	
	
	public ObjectPropertyRecorder() {
		nameList = new ArrayList<String>();
		lowerNameList = new ArrayList<String>();
		nameMap = new HashMap<String, Integer>();
		propertyReaderMap = new HashMap<String, PropertyReader>();
		propertyReaderList = new LinkedList<PropertyReader>();
	}

	public void addGetMethod(String name, Method method) throws Exception {
		Class<?> returnType = method.getReturnType();
		String lower = name.toLowerCase();
		
		PropertyReader propertyReader = propertyReaderMap.get(lower);
		
		if (propertyReader == null) {
			nameMap.put(lower, nameList.size());
			
			nameList.add(name);
			lowerNameList.add(lower);
			
			propertyReader = new PropertyReader(name, returnType);
			
			propertyReaderList.add(propertyReader);	
			propertyReaderMap.put(lower, propertyReader);
		}
		
		propertyReader.setGetMethod(method);			
	}
	
	public void addSetMethod(String name, Method method) throws Exception {
		Class<?> paramType = method.getParameterTypes()[0];
		if (!Translator.containsType(paramType)) {
			return;
		}
		
		String lower = name.toLowerCase();
		
		PropertyReader propertyReader = propertyReaderMap.get(lower);
		
		if (propertyReader == null) {
			nameMap.put(lower, nameList.size());
			
			nameList.add(name);
			lowerNameList.add(lower);
			
			propertyReader = new PropertyReader(name, paramType);
			
			propertyReaderList.add(propertyReader);	
			propertyReaderMap.put(lower, propertyReader);
		}
		
		propertyReader.setSetMethod(method);			
	}
	
	public PropertyReader[] getPropertyReaderArray() {
		PropertyReader[] result = new PropertyReader[propertyReaderList.size()];
		result = propertyReaderList.toArray(result);
		
		return result;
	}

	public String[] getNameArray() {
		String[] result = new String[nameList.size()];
		return nameList.toArray(result);
	}

	public String[] getLowerNameArray() {
		String[] result = new String[lowerNameList.size()];
		return lowerNameList.toArray(result);
	}

	public Map<String, Integer> getNameMap() {
		return nameMap;
	}
}
