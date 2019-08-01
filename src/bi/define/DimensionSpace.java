package bi.define;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DimensionSpace implements Iterable<Dimension> {

	private List<Dimension> dimensionList; 
	
	public DimensionSpace() {
		dimensionList = new ArrayList<Dimension>();
	}
	
	public void addDimension(Dimension dimension) {
		if (dimensionList == null) {
			dimensionList = new ArrayList<Dimension>();
		}
		
		dimensionList.add(dimension);
	}

	public void addDimension(List<Dimension> subDimensionList) {
		if (dimensionList == null) {
			dimensionList = new ArrayList<Dimension>();
		}

		dimensionList.addAll(subDimensionList);
	}
	
	public List<Dimension> getDimensionList() {
		return dimensionList;
	}
	
	@Override
	public Iterator<Dimension> iterator() {
		return dimensionList.iterator();
	}

}
