//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package foundation.persist.sql;

import foundation.config.Configer;
import foundation.persist.DataBaseType;
import foundation.util.Util;
import foundation.variant.Expression;
import foundation.variant.VariantSegment;
import org.apache.log4j.Logger;

public class SQLCreator extends Expression {
    protected static Logger logger = Logger.getLogger(SQLCreator.class);

    public SQLCreator(String sql) throws Exception {
        super(sql, 8);
    }

    public void addVariant(String name) throws Exception {
        if (!Util.isEmptyStr(name)) {
            if (this.variantList.contains(name)) {
                VariantSegment segment = this.variantList.get(name);
                this.segments.add(segment);
            } else {
                VariantSegment segment = new VariantSegment(name);
                this.segments.add(segment);
                this.variantList.add(name, segment);
            }

        }
    }

    public static String deletePageLimit(String sql) {
        DataBaseType dbType = Configer.getDataBaseType();
        if (DataBaseType.MySQL == dbType) {
            return deleteMySQLPageLimit(sql);
        } else if (DataBaseType.Oracle == dbType) {
            return deleteOraclePageLimit(sql);
        } else {
            return DataBaseType.SQLServer == dbType ? deleteSQLServerPageLimit(sql) : null;
        }
    }

    private static String deleteMySQLPageLimit(String sql) {
        for(int pos_beginno = sql.indexOf("@{beginno}"); pos_beginno > 0; pos_beginno = sql.indexOf("@{beginno}", pos_beginno + 1)) {
            int pos_pagesize = sql.indexOf("@{pagesize}", pos_beginno + 1);
            int pos_limit = sql.lastIndexOf("limit ", pos_beginno);
            if (pos_pagesize > 0 && pos_limit > 0) {
                sql = sql.substring(0, pos_limit) + sql.substring(pos_pagesize + "@{pagesize}".length());
            }
        }

        return sql;
    }

    private static String deleteOraclePageLimit(String sql) {
        return null;
    }

    private static String deleteSQLServerPageLimit(String sql) {
        return null;
    }

    public static void main(String[] args) {
        String sql = "select agreement.*, dealer.code, dealer.name,(select count(1) from agreementArea where agreementArea.parentid = agreement.id) as areaCount,(select count(1) from agreementProduct where agreementProduct.parentid = agreement.id) as productCountfrom agreementleft join dealer on agreement.customerid = dealer.idwhere @{filter}limit @{beginno}, @{pagesize}";
        sql = deleteMySQLPageLimit(sql);
        System.out.println(sql);
    }
}
