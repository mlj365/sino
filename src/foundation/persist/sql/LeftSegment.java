package foundation.persist.sql;

import bi.AggConstant;
import foundation.util.Util;

import java.text.MessageFormat;

/**
 * @author kimi
 * @description 左联
 * @date 2019-05-06 10:24
 */


public class LeftSegment  {
    
    
    private String leftTable;
    private String leftField;
    private String nowtable;
    private String nowField;
    private String leftTableAcronym;
    public LeftSegment(String leftTable, String leftField, String nowtable, String nowField) {
        this.leftTable = leftTable;
        this.leftField = leftField;
        this.nowtable = nowtable;
        this.nowField = nowField;
    }

    @Override
    public String toString() {
        if (Util.isNull(leftTable) || Util.isNull(nowtable) || Util.isNull(leftField) || Util.isNull(nowField)) {
            return null;
        } else {
            return MessageFormat.format(AggConstant.LeftJoinFactor_Template, nowtable,nowField,leftTable,leftField);
        }

    }

    public String getAcronymString () {
        if (Util.isNull(leftTable) || Util.isNull(nowtable) || Util.isNull(leftField) || Util.isNull(nowField)) {
            return null;
        } else {
            return MessageFormat.format(AggConstant.LeftJoinFactor_Template, nowtable,nowField,leftTableAcronym,leftField);
        }
    }

    @Override
    public int hashCode() {
        if (Util.isNull(nowtable)) {
            return super.hashCode();
        } else {
            return  nowtable.hashCode();
        }
    }

    public String getLeftTable() {
        return leftTable;
    }

    public LeftSegment setLeftTable(String leftTable) {
        this.leftTable = leftTable;
        return this;
    }

    public String getLeftField() {
        return leftField;
    }

    public LeftSegment setLeftField(String leftField) {
        this.leftField = leftField;
        return this;
    }

    public String getNowtable() {
        return nowtable;
    }

    public LeftSegment setNowtable(String nowtable) {
        this.nowtable = nowtable;
        return this;
    }

    public String getNowField() {
        return nowField;
    }

    public LeftSegment setNowField(String nowField) {
        this.nowField = nowField;
        return this;
    }

    public String getLeftTableAcronym() {
        return leftTableAcronym;
    }

    public LeftSegment setLeftTableAcronym(String leftTableAcronym) {
        this.leftTableAcronym = leftTableAcronym;
        return this;
    }
}
