package bi.exception;


public class AggEntityLoadException extends AggBaseException {

	private static final long serialVersionUID = 1L;
	
	private static String Entity_Error_Msg = "entity is null";
	private static String Field_Error_Msg ="field is null";
	
	private static String Reflect_Field_Error_Msg ="reflect field error";
	
	
	public AggEntityLoadException(String code , String message) {
		super(code, "Entity load error--" + message);
	}
	
	public AggEntityLoadException(String message) {
		super("-2", message);
	}
	

	public AggEntityLoadException(int type) {
		this(null);
		if (type == 1) {
			message = Entity_Error_Msg;
		}
		else if(type == 2){
			message = Field_Error_Msg;
		}
		else if(type == 3){
			message = Reflect_Field_Error_Msg;
		}
	}
	
	public AggEntityLoadException(int type, String field) {
		this(null);
		if (type == 1) {
			message = Entity_Error_Msg;
		}
		else if(type == 2){
			message = Field_Error_Msg + "--field :" + field;
		}
		else if(type == 3){
			message = Reflect_Field_Error_Msg + "--field :" + field;
		}
	}
	
	
}
