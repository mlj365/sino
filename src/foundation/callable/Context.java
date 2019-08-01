package foundation.callable;

import foundation.data.Variant;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Context {

	private static Map<String, Variant> params;

	public Context(HttpServletRequest request) {
		params = new HashMap<String, Variant>();
		if (request != null) {
			load(request);
		}

	}

	private void load(HttpServletRequest request) {
		Enumeration<String> paramNames = request.getParameterNames();
		
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String value = request.getParameter(paramName);
			paramName = paramName.toLowerCase();
			
			Variant variant = new Variant(value);
			params.put(paramName, variant);
		}
	}

	public Variant getParameter(String name) {
		if (name == null) {
			return new Variant();
		}
		
		name = name.toLowerCase();
		Variant variant = params.get(name);
		
		if (variant == null) {
			variant = new Variant();
		}
		
		return variant;
	}

}
