package bi.work;

import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;

import java.sql.Connection;

public class DataCenter {

	public static void emptyTable(String tableName, Connection conn, String filter) throws Exception {
		NamedSQL namedSQL = NamedSQL.getInstance("deleteByCriteria");
		namedSQL.setTableName(tableName);

		if (filter != null) {
			namedSQL.setFilter(filter);
		}

		SQLRunner.execSQL(conn, namedSQL);
	}

}
