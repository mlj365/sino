package foundation.dictionary;

import foundation.server.Container;

public class DictionaryContainer extends Container<Dictionary> {

	private static DictionaryContainer instance;
	private static Object lock = new Object();
	
	private DictionaryContainer() {
		
	}
	
	public static DictionaryContainer getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new DictionaryContainer();
				}
			}
		}
		
		return instance;
	}

	public Dictionary append(String code) {
		Dictionary dictionary = new Dictionary(code);
		add(code, dictionary);
		
		return dictionary;
	}

	public void add(String group, String key, String value) {
		if (group == null) {
			return;
		}
		
		Dictionary dictionary = get(group);
		
		if (dictionary == null) {
			dictionary = new Dictionary(group);
			add(group, dictionary);
		}
		
		dictionary.append(key, value);
	}
	
}
