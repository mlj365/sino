package bi.exception;


public class AggCalculateException extends AggBaseException {

	private static final long serialVersionUID = 1L;
	
	private static String Field_Error_Msg = "element not map real value";
	
	public AggCalculateException(String code , String message) {
		super(code, "Calculate error--" + message);
	}
	
	public AggCalculateException(String message) {
		super("-1", message);
	}
	

	public AggCalculateException(int type) {
		this(null);
		if (type == 1) {
			message = Field_Error_Msg;
		}
	}
	
	public AggCalculateException(int type, String msg) {
		this(null);
		if (type == 1) {
			message = Field_Error_Msg + "--tablename :" + msg;
		}
		
	}
	
	
}
