package bi.exception;


public class AggDBlLoadException extends AggBaseException {

	private static final long serialVersionUID = 1L;
	
	private static String Entity_Error_Msg = "tablename is empty";
	private static String Field_Error_Msg ="field is null";
	private static String Sql_Error_Msg ="Sql is error";
	
	
	public AggDBlLoadException(String code , String message) {
		super(code, "Entity load error--" + message);
	}
	
	public AggDBlLoadException(String message) {
		super("-3", message);
	}
	
	public AggDBlLoadException(int type) {
		this(null);
		if (type == 1) {
			message = Entity_Error_Msg;
		}
		else if(type == 2){
			message = Field_Error_Msg;
		}
		else if(type == 3){
			message = Sql_Error_Msg;
		}
	}
	
	public AggDBlLoadException(int type, String msg) {
		this(null);
		if (type == 1) {
			message = Entity_Error_Msg + "--tablename :" + msg;
		}
		else if(type == 2){
			message = Field_Error_Msg + "--field :" + msg;
		}
		else if(type == 3){
			message = Sql_Error_Msg + "--sql :" + msg;
		}
	}
	
	
}
