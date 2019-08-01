package phonecode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.collections.map.HashedMap;

public class Utils {

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
		return SendMessage(Content, _Header, serverAddress);
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
