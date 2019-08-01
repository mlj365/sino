package phonecode;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.collections.map.HashedMap;
import foundation.callable.Callable;
import foundation.util.PhoneUtil;
public class SendVcode extends Callable {

	protected void publishMethod() {
		addMethod("Sendsms");
	}
	
	public boolean SendVcode(String phone,String vcode) throws UnsupportedEncodingException {
		boolean status = false;
		try {
			String UserName = "blr";// 用户名
			String Password = "420455";// 密码
			String Timestemp = PhoneUtil.getTimestemp();// 时间戳
			String Key = PhoneUtil.getKey(UserName, Password, Timestemp);// 加密
			String serverAddress = "http://www.youxinyun.com:3070/Platform_Http_Service/servlet/SendSms";// 请求的URL
			String Content = "【士卓曼报表系统】尊敬的用户您本次验证码为"+vcode+"，五分钟有效请勿泄露";
			String Mobiles = phone;
			int Priority = 5;
			String PackID = "";
			String PacksID = "";
			String ExpandNumber = "";
			Long SMSID = System.currentTimeMillis();
			StringBuffer StringBuffer = new StringBuffer();
			StringBuffer.append("UserName=" + UserName + "&");
			StringBuffer.append("Key=" + Key + "&");
			StringBuffer.append("Timestemp=" + Timestemp + "&");
			StringBuffer.append("Content=" + URLEncoder.encode(Content, "utf-8") + "&");
			StringBuffer.append("CharSet=utf-8&");
			StringBuffer.append("Mobiles=" + Mobiles + "&");
			StringBuffer.append("Priority=" + Priority + "&");
			StringBuffer.append("PackID=" + PackID + "&");
			StringBuffer.append("PacksID=" + PacksID + "&");
			StringBuffer.append("ExpandNumber=" + ExpandNumber + "&");
			StringBuffer.append("SMSID=" + SMSID);
			HashedMap Header = new HashedMap();
			Header.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			InputStream _InputStream = PhoneUtil.SendMessage(StringBuffer.toString().getBytes("utf-8"), Header, serverAddress);
			String response = PhoneUtil.GetResponseString(_InputStream, "utf-8");
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
}
