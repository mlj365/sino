package foundation.phone;

import org.dom4j.Document;   
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;   
import org.dom4j.Element; 

import foundation.util.Util;

public class SendResult {

	private String code;
	private String msg;
	private String SMSID;
	
	public SendResult() {
		
	}
	
	public void setValue(String value) throws DocumentException {
		if (Util.isEmptyStr(value)) {
			return;
		}
		
		Document doc = DocumentHelper.parseText(value); 
		Element root = doc.getRootElement();

		code = root.elementText("code");	
		msg = root.elementText("msg");	
		SMSID = root.elementText("smsid");	
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public String getSmsid() {
		return SMSID;
	}

	public boolean isSuccess() {
		return "2".equals(code);
	}

	@Override
	public String toString() {
		return code + ": " + msg;
	}
	
}
