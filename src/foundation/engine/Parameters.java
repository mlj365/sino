package foundation.engine;

import foundation.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Parameters {

	private Map<String, String> parameterMap;
	
	public Parameters() {
		parameterMap = new HashMap<String, String>();
	}
	
	public String get(String name) {
		if (Util.isEmptyStr(name)) {
			return null;
		}
		
		name = name.toLowerCase();
		return parameterMap.get(name);
	}
	
	public void putAll(HttpServletRequest repuest) {
		Enumeration<String> names = repuest.getParameterNames();
		
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = repuest.getParameter(name);
			
			if (!Util.isEmptyStr(name)) {
				parameterMap.put(name.toLowerCase(), value);
			}
		}
	}
}
