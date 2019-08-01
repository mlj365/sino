package foundation.callable;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import foundation.data.Entity;
import foundation.data.Variant;
import foundation.persist.sql.NamedSQL;
import foundation.util.Util;

public class DataPool {

	private Map<String, Variant> params;

	public DataPool(HttpServletRequest request) throws Exception {
		params = new HashMap<String, Variant>();
		load(request);
	}

	private void load(HttpServletRequest request) throws Exception {
		Enumeration<String> paramNames = request.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String value = request.getParameter(paramName);
			addParameter(paramName, value);
		}
	}
	
	public void addParameter(String name, String value) { 
		if (name == null) {
			return;
		}
		
		name = name.toLowerCase();
		Variant variant = new Variant(value);
		params.put(name, variant);
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

	public void setParameterTo(NamedSQL namedSQL) {
		Set<String> names = params.keySet();

		for (String name : names) {
			namedSQL.setParam(name, params.get(name).getStringValue());
		}
	}

	public Entity getEntity(String tableName) throws Exception {
		Entity entity = new Entity(tableName);
		String id = params.get("id").getStringValue();

		if (Util.isEmptyStr(id)) {
			id = Util.newShortGUID();
		}

		entity.set("id", id);
		Set<String> names = params.keySet();

		for (String name : names) {
			if ("id".equalsIgnoreCase(name)) {
				continue;
			}
			entity.set(name, params.get(name).getStringValue());
		}

		return entity;
	}
}
