package bi.define;

/**
 * @author kimi
 * @description
 * @date 2019-05-16 13:54
 */


public class MainField {
    private String field;
    private EMainFieldType type;

    public MainField(String field, EMainFieldType type) {
        this.field = field;
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public MainField setField(String field) {
        this.field = field;
        return this;
    }

    public EMainFieldType getType() {
        return type;
    }

    public MainField setType(EMainFieldType type) {
        this.type = type;
        return this;
    }

    @Override
    public int hashCode() {
        return field.hashCode() + type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  MainField) {
            return  this.field.equalsIgnoreCase(((MainField) obj).getField()) && this.type.equals(((MainField) obj).getType());
        }
        return super.equals(obj);

    }
}
