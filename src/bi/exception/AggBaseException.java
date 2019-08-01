package bi.exception;

import foundation.util.Util;

public class AggBaseException extends RuntimeException {

	/**
	 * 
	 */
	protected String code;
	protected String message;
	private static final long serialVersionUID = 1L;
	
	public AggBaseException(String code , String message) {
		this.message = message;
		this.code = code;
	}
	public AggBaseException(String message) {
		this("-1", message);
	}
	
	@Override
	public String getMessage() {
		if (!Util.isEmptyStr(message) && !Util.isEmptyStr(code)) {
			return "errorcode:" + code + ";message:" + message;
		}
		return super.getMessage();
	}
}
