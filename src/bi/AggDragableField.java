package bi;

import bi.define.AggDimensionsContainer;
import bi.define.AggMainFieldContainer;
import bi.define.Dimension;
import bi.define.Measurment;
import foundation.util.Util;

/**
 * @author kimi
 * @description 前端可拖拽字段包装类
 * @date 2019-05-05 10:12
 */


public class AggDragableField {
    private Dimension dimension;
    private String field;
    private EDragableFieldType type;
    private String tableName;


    public AggDragableField(String field, boolean isDimension) {

        int index = field.indexOf(Util.Dot);
        String subField;
        if (index != -1) {
            subField = field.substring(index + 1, field.length());
        } else {
            subField = field;
        }

        this.field = subField;

        if (!isDimension) {
            checkIsMainField(field, index);
            type = EDragableFieldType.filter;
            return;
        }

        Dimension dimension = AggDimensionsContainer.getDimensionByCode(subField);

        if (!Util.isNull(dimension)) {
            type = EDragableFieldType.agg;
            this.dimension = dimension;
        } else {
            checkIsMainField(field, index);
            /*if (!checkIsPeroid(field)) {

            }*/
        }
    }

    private boolean checkIsPeroid(String field) {
        Dimension peroidDimension = AggDimensionsContainer.getInstance().getPeroidDimension(field);
        if (Util.isNull(peroidDimension)) {
            return false;
        }
        type = EDragableFieldType.agg;
        this.dimension = peroidDimension;
        return true;
    }

    private void checkIsMainField(String field, int index) {
        String tableName = AggMainFieldContainer.getTableName(field);

        if (index != -1) {
            String preTableName = field.substring(0, index);
            if (!preTableName.equalsIgnoreCase(tableName)) {
                type = EDragableFieldType.unknown;
                return;
            }
        }

        if (Util.isNull(tableName)) {
            Measurment measurmentByCode = AggDimensionsContainer.getMeasurmentByCode(field);
            if (Util.isNull(measurmentByCode)) {
                type = EDragableFieldType.unknown;
            } else {
                type = EDragableFieldType.measurment;
            }

        } else {
            type = EDragableFieldType.main;
            this.tableName = tableName;
        }
    }

    public Dimension getDimension() {
        return dimension;
    }

    public String getField() {
        return field;
    }

    public EDragableFieldType getType() {
        return type;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return field.toLowerCase();
    }

    @Override
    public int hashCode() {
        return field.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AggDragableField) {
            return  this.field.equalsIgnoreCase(((AggDragableField) obj).field);
        }
        return super.equals(obj);
    }
}
