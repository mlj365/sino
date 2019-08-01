package foundation.dictionary;

import foundation.callable.Callable;

public class DictionaryService extends Callable {

	private static DictionaryContainer dictionaryContainer = DictionaryContainer.getInstance();
	
	public DictionaryService() {
		
	}
	
	@Override
	protected void publishMethod() {
		addMethod("call");
	}
	
	protected void call() throws Exception {
		String name = paths[1];
		Dictionary dictionary = dictionaryContainer.get(name);
		
		if (dictionary != null) {
			resultPool.addValue(dictionary.getDataList());
		}
	}
}
