package foundation.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class PhoneUtil {

	public static String getTimestemp() {
		return new SimpleDateFormat("MMddHHmmss").format(new Date());
	}

	public static String getKey(String userName, String password,
			String timestemp) {
		String key = "";
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(userName.getBytes());
			mdTemp.update(password.getBytes());
			mdTemp.update(timestemp.getBytes());
			key = bytesToHexString(mdTemp.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;
	}

	private static String bytesToHexString(byte[] src) {
		String resultString = "";
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
			return null;
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 255;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2)
				stringBuilder.append(0);
			stringBuilder.append(hv);
		}

		resultString = stringBuilder.toString();
		stringBuilder = null;
		return resultString;
	}

	public static InputStream SendMessage(byte[] Content, HashedMap _Header,
			String serverAddress) {
		return SendMessage(Content, _Header, serverAddress, null, 0, null, null);
	}
	

	private static InputStream SendMessage(byte[] Content, HashedMap _Header,
			String serverAddress, String proxyHost, int proxyPort, String userName, String password) {
		// TODO Auto-generated method stub
		InputStream _InputStream = null;

		try {
			if (serverAddress == null)
				return null;

			HttpPost _HttpPost = new HttpPost(serverAddress);
			if (_Header != null) {
				Set keySet = _Header.keySet();
				String keyString = "";
				try {
					
						for (Object object : keySet) {
							keyString =	object.toString();
							_HttpPost.addHeader(keyString, (String) _Header.get(keyString));
						}
						
				} catch (NoSuchElementException e) {
					e.printStackTrace();
				}
			}
			_HttpPost.addHeader("Connection", "Keep-Alive");
			_HttpPost.setEntity(new ByteArrayEntity(Content));
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse _HttpResponse = httpclient.execute(_HttpPost);

			if (_Header != null) {
				_Header.put("ResponseContentLength", String.valueOf(_HttpResponse.getEntity().getContentLength()));
				org.apache.http.Header[] _RespHeader = _HttpResponse.getAllHeaders();
				if (_RespHeader != null && _RespHeader.length > 0) {
					for (org.apache.http.Header header : _RespHeader) {
						_Header.put(header.getName(), header.getValue());
					}
				}
			}
			_InputStream = _HttpResponse.getEntity().getContent();
		} catch (Exception e) {
			System.out.println("请求失败，原因：" + e.getMessage() + "请求地址：" + serverAddress);
		}
		return _InputStream;
	}

	public static String GetResponseString(InputStream _InputStream, String Charset) {
		String response = "error:";
		try {
			if (_InputStream != null) {
				StringBuffer buffer = new StringBuffer();
				InputStreamReader isr = new InputStreamReader(_InputStream, Charset);
				Reader in = new BufferedReader(isr);
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				response = buffer.toString();
				buffer = null;
			} else {
				response = response + "timeout";
			}
		} catch (Exception e) {
			System.out.println("获取响应错误，原因：" + e.getMessage());
			response = response + e.getMessage();
		}
		return response;
	}

}
