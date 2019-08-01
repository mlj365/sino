package foundation.callable;

import java.util.ArrayList;
import java.util.List;

public class ResultPool {

	private boolean success;
	private String errorCode;
	private String errorMessage;
	private String code;
	private String message;
	private List<ResultItem> itemList;
	private String json;

	public ResultPool() {
		success = true; // 创建对象时即为true，只要不人为设置false，就一直true
		itemList = new ArrayList<ResultItem>();
	}

	public void success() {
		success = true;
	}

	public void error(String message) {
		error(null, message);
	}

	public void error(String code, String message) {
		success = false;
		errorCode = code;
		errorMessage = message;
	}

	public void setMessage(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getMessage() {
		return message;
	}
	
	public String getCode() {
		return code;
	}

	public void addValue(Object value) {// 添加匿名结果，名字用对象的简单类名
		if (value == null) {
			return;
		}

		String name = value.getClass().getSimpleName();
		addValue(name, value);
	}

	public void addValue(Object name, Object value) {
		if (name == null) {
			addValue(value);
		}

		ResultItem resultItem = createResultItem(name, ObjectItem.class);
		resultItem.setValue(value);
	}
	
	public void addValue(Object name, Object value, IBeanWriter beanWriter) {
		String nameStr = String.valueOf(name);
		
		if (nameStr == null) {
			nameStr = "list";
		}
		
		ResultItem resultItem = createResultItem(name, ObjectItem.class);
		resultItem.setValue(value);
		resultItem.setBeanWriter(beanWriter);
	}
	
	public void addJson(Object name, String json) {
		if (json == null) {
			json = "null";
		}
		
		ResultItem resultItem = createResultItem(name, JsonItem.class);
		resultItem.setValue(json);
	}

	public boolean isJson() {
		return json != null;
	}
	
	public void setJson(String json) {
		this.json = json;
	}
	
	public String getJson() {
		return json;
	}

	public List<ResultItem> getItemList() {
		return itemList;
	}
	
	private ResultItem createResultItem(Object name, Class<? extends ResultItem> clazz) {
		String nameString = String.valueOf(name);
		ResultItem resultItem = null;

		for (ResultItem item : itemList) {
			if (item.getName().equalsIgnoreCase(nameString)) {
				resultItem = item;
				break;
			}
		}

		if (resultItem == null) {
			try {
				resultItem = clazz.newInstance();
				resultItem.setName(nameString);
				itemList.add(resultItem);
			} 
			catch (Exception e) {
			}
		}
		
		return resultItem;
	}

}
