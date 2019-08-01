package bi.define;

public enum AggDirection {
	Dimension,Measurment,Agg;
	
	public static AggDirection parse(String type) {
		if (AggDirection.Dimension.toString().equalsIgnoreCase(type)) {
			return AggDirection.Dimension;
		}
		else if (AggDirection.Measurment.toString().equalsIgnoreCase(type)) {
			return AggDirection.Measurment;
		}
		else if (AggDirection.Agg.toString().equalsIgnoreCase(type)) {
			return AggDirection.Agg;
		}
		else {
			return null;
		}
	}
}
