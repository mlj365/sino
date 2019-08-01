package foundation.dictionary;

import java.util.ArrayList;
import java.util.List;

public class Dictionary  {

	private String name;
	private boolean dirty;
	private List<DictionaryLine> dataList;
	
	public Dictionary(String name) {
		dataList = new ArrayList<DictionaryLine>();
		this.name = name;
	}

	public void append(String key, String value) {
		if (key == null) {
			return;
		}
		
		DictionaryLine line = new DictionaryLine(key, value);
		dataList.add(line);
	}
	
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void clear() {
		dataList.clear();
	}

	public String getName() {
		return name;
	}

	public Object getDataList() {
		return dataList;
	}
	
}
