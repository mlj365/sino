package foundation.phone;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import foundation.util.Util;

public class Sendsms {
	
	private static Logger logger;
	private static boolean active = true;
	
	static {
		logger = Logger.getLogger(Sendsms.class);
		active = Util.stringToBoolean(foundation.config.Configer.getParam("SendSMS"));
	}
	
	public static SendResult sendVCode(String phone, String vcode) throws Exception {
		String content = Configer.createContent("vcode", vcode);
		return doSend(phone, content);
	}
	
	public static SendResult sendMessage(String phone, String id, String message) throws Exception {
		String content = Configer.createContent(id, message);
		return doSend(phone, content);
	}
	
	public static SendResult doSend(String phone, String content) throws Exception {
		if (!active) {
			return null;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("send message (" + phone + ") ：" + content);
		}
		
        CloseableHttpClient httpclient = HttpClients.createDefault();
        
        HttpPost httppost = new HttpPost(Configer.getURL());
        try {
            System.out.println("Executing request " + httppost.getRequestLine());
           
    		List<NameValuePair> data = new ArrayList<NameValuePair>();
    		data.add(new BasicNameValuePair("UserName", Configer.getUserName()));
    		data.add(new BasicNameValuePair("Password", Configer.getPassword()));
    		data.add(new BasicNameValuePair("Mobiles", phone)); 
    		data.add(new BasicNameValuePair("Content", content));		
    		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(data, "UTF-8");
    		httppost.setEntity(uefEntity);
     
    		SMSResponseHandler responseHandler = new SMSResponseHandler();
    		String result = httpclient.execute(httppost, responseHandler);

    		SendResult sendResult = new SendResult();
    		sendResult.setValue(result);
    		
			 if(logger.isDebugEnabled()){
				 logger.debug(sendResult.toString());
			}
			 
			 return sendResult;
        } 
        finally {
            httpclient.close();
        }		
	}
	
}