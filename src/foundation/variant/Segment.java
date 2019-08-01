package foundation.variant;

import org.apache.log4j.Logger;

public abstract class Segment implements ISegment {
    protected static Logger logger = Logger.getLogger(Segment.class);

    public Segment() {
    }

    public abstract String getValueString();

    public abstract Segment newInstance();
}
