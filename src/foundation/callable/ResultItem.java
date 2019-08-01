package foundation.callable;

public class ResultItem {

	private String name;
	private Object value;
	private IBeanWriter beanWriter;
	
	public ResultItem() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setBeanWriter(IBeanWriter beanWriter) {
		this.beanWriter = beanWriter;
	}

	public IBeanWriter getBeanWriter() {
		return beanWriter;
	}
	
}
