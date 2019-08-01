package foundation.persist.sql;

import foundation.variant.VariantList;
import foundation.variant.VariantSegment;

import java.util.Set;

public abstract class SQLContext implements ISQLContext {

	public void setParametersTo(NamedSQL namedSQL, Object... args) throws Exception {
		VariantList variantMap = namedSQL.getVariantMap();
		Set<String> names = variantMap.keySet();

		for (String name : names) {
			VariantSegment variant = variantMap.get(name);

			if (variant != null) {
				if (!variant.isEmpty()) {
					continue;
				}

				String value = getSqlString(name, args);
				if (value == null) {
					throw new Exception("can not get param: " + name);
				}
				variant.setValue(value);
			}
		}
	}

	public abstract String getSqlString(String name, Object... args) throws Exception;

	public NamedSQL reassignNamedSQL(NamedSQL namedSQL, Object... args) throws Exception {
		return namedSQL;
	}
}
