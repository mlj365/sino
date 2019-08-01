package foundation.variant;

import foundation.util.Util;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Expression implements Iterable<VariantSegment>, IVariantParseListener, IExpression {
    protected static Logger logger = Logger.getLogger(Expression.class);
    protected List<ISegment> segments;
    protected VariantList variantList;

    public Expression(String string) throws Exception {
        this(string, 0);
    }

    public Expression(String string, int size) throws Exception {
        this(size);
        VariantParser parser = new VariantParser(this);
        parser.parse(string);
    }

    private Expression(int size) throws Exception {
        if (size <= 0) {
            size = 4;
        }

        this.segments = new ArrayList(size);
        this.variantList = new VariantList();
    }

    public Expression newInstance() throws Exception {
        int size = this.segments.size();
        Expression instance = new Expression(size);

        for(int i = 0; i < size; ++i) {
            ISegment segment = (ISegment)this.segments.get(i);
            if (segment instanceof VariantSegment) {
                VariantSegment param = (VariantSegment)segment;
                String name = param.getName();
                if (instance.variantList.contains(param.getName())) {
                    instance.segments.add((Segment)instance.variantList.get(name));
                } else {
                    segment = segment.newInstance();
                    instance.segments.add(segment);
                    instance.variantList.add(name, (VariantSegment)segment);
                }
            } else {
                segment = segment.newInstance();
                instance.segments.add(segment);
            }
        }

        return instance;
    }

    public String tryGetString() throws Exception {
        Iterator var2 = this.variantList.iterator();

        while(var2.hasNext()) {
            VariantSegment variant = (VariantSegment)var2.next();
            if (variant.isEmpty()) {
                throw new Exception("empty sql param: " + variant.getName());
            }
        }

        return this.getString();
    }

    public String getString() {
        StringBuilder result = new StringBuilder();
        int n = this.segments.size();

        for(int i = 0; i < n; ++i) {
            Segment segment = (Segment)this.segments.get(i);
            String value = segment.getValueString();
            result.append(value);
        }

        return result.toString();
    }

    public void onSegment(String value) {
        if (!Util.isEmptyStr(value)) {
            Segment segment = new StringSegment(value);
            this.segments.add(segment);
        }
    }

    public void addVariant(String name) throws Exception {
        if (!Util.isEmptyStr(name)) {
            VariantSegment segment;
            if (this.variantList.contains(name)) {
                segment = (VariantSegment)this.variantList.get(name);
                this.segments.add(segment);
            } else {
                segment = new VariantSegment(name);
                this.segments.add(segment);
                this.variantList.add(name, segment);
            }

        }
    }

    public VariantSegment getVariant(String name) {
        return Util.isEmptyStr(name) ? null : (VariantSegment)this.variantList.get(name);
    }

    public Iterator<VariantSegment> iterator() {
        return this.variantList.getItemList().iterator();
    }

    public VariantList getVariantList() {
        return this.variantList;
    }

    public boolean isVariantEmpty() {
        return this.variantList.isEmpty();
    }

    public void clearVariantValues() {
        Iterator var2 = this.variantList.iterator();

        while(var2.hasNext()) {
            VariantSegment variant = (VariantSegment)var2.next();
            variant.clearValue();
        }

    }
}
