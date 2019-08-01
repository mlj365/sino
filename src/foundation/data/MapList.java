package foundation.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import foundation.server.Container;
import foundation.util.ContentBuilder;
import foundation.util.Util;

public class MapList<T> implements Collection<T> {

	protected List<T> itemList;
	protected Map<Object, T> itemMap;
	
	public MapList() {
		itemList = new ArrayList<T>();
		itemMap = new HashMap<Object, T>();
	}
	
	public void addAll(Map<Object, T> map) {
		Set<Object> keys = map.keySet();
		
		for (Object key: keys) {
			add(key, map.get(key));
		}
	}
	
	public void add(Object key, T item) {
		itemList.add(item);
		
		if (key == null) {
			key = "empty";
		}
		
		if (key instanceof String) {
			key = ((String) key).toLowerCase();
		}
		
		itemMap.put(key, item);
	}
	
	public T get(String key) {
		if (key == null) {
			key = "empty";
		}
		key = key.toLowerCase();
		
		return itemMap.get(key);
	}
	
	public T remove(String key) {
		if (key == null) {
			return null;
		}
		
		T obj = itemMap.get(key.toLowerCase());
		
		if (obj != null) {
			itemMap.remove(key);
			itemList.remove(obj);
		}
		
		return obj;
	}
	
	public List<T> getItemList() {
		return itemList;
	}

	public boolean isEmpty() {
		return itemList.isEmpty();
	}
	
	public int size() {
		return itemList.size();
	}
	
	public int mapSize() {
		return itemMap.size();
	}
	
	@Override
	public Iterator<T> iterator() {
		return itemList.iterator();
	}

	public void clear() {
		itemList.clear();
		itemMap.clear();
	}
	
	public String toString(String separator) {
		ContentBuilder builder = new ContentBuilder(separator);
		
		for (Object key: itemMap.keySet()) {
			builder.append(key);
		}
		
		return builder.toString();
	}
	
	public void loadFromString(String content, Container<T> container, String separator) {
		clear();
		
		if (Util.isEmptyStr(content)) {
			return;
		}
		
		String[] keyArray = content.trim().replace(";", separator).split(separator);
		
		for (String key: keyArray) {
			T obj = container.get(key);
			
			if (obj == null) {
				continue;
			}
			
			add(key, obj);
		}
	}

	public Set keySet() {
		return itemMap.keySet();
	}

	public boolean equals(MapList<T> another) {
		if (another == null) {
			return false;
		}
		
		if (this == another) {
			return true;
		}
		
		Set<Object> keys = itemMap.keySet();
		for (Object key: keys) {
			if (!another.contains(key)) {
				return false;
			}
		}
		
		Set<Object> anotherKeys = another.itemMap.keySet();
		for (Object anotherKey: anotherKeys) {
			if (!contains(anotherKey)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean contains(Object key) {
		if (key == null) {
			return false;
		}
		
		if (key instanceof String) {
			key = ((String)key).toLowerCase();
		}

		return itemMap.containsKey(key);
	}

	@Override
	public Object[] toArray() {
		return itemList.toArray();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return itemList.toArray(a);
	}

	@Override
	public boolean add(T e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

}
