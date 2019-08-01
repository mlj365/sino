package foundation.phone;

import java.util.HashMap;
import java.util.Map;

public class Configer {
	
	private static String serverAddress;
	private static String UserName;
	private static String Password;
	private static Map<String, Template> templateMap;

	static {
		templateMap = new HashMap<String, Template>();
	}
	
	public static String getURL() {
		return serverAddress;
	}

	public static String createContent(String id, String value) {
		if (id == null) {
			return null;
		}
		
		id = id.toLowerCase();
		Template template = templateMap.get(id);
		return template.toContent(value);
	}

	public static String getUserName() {
		return UserName;
	}

	public static String getPassword() {
		return Password;
	}

	public static void setURL(String value) {
		serverAddress = value;
	}

	public static void setUserName(String value) {
		UserName = value;
	}

	public static void setPassword(String value) {
		Password = value;
	}

	public static void addTemplate(String name, String content) {
		if (name == null) {
			return;
		}
		
		name = name.toLowerCase();
		Template template = new Template(content);
		templateMap.put(name, template);
	}
}
