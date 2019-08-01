package foundation.phone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;

import foundation.config.ConfigFileLoader;
import foundation.util.DTDEntityResolver;


public class PhoneConfigLoader extends ConfigFileLoader {

	protected static final String Node_Param = "param";	
	protected static final String Node_Template = "template";
	
	public PhoneConfigLoader() {
		path = foundation.config.Configer.getPath_Config() + "phone.xml";
	}
	
	@Override
	public void load() {
		File file = new File(path);
		
		if (file.exists()) {
			loadPhoneConfigFile(file);
		}
	}
	
	public void loadPhoneConfigFile(File file) {
		try {
			logger.debug("load phone config file:" + file);
			InputStream inputStream = new FileInputStream(file);
			
	        try {
	    		SAXReader reader = new SAXReader();
	    		EntityResolver entityResolver = getEntityResolver();
	    		reader.setEntityResolver(entityResolver);
	    			
				Document doc = reader.read(inputStream);
				Element root = doc.getRootElement();
					
				loadParams(root);
				loadTemplates(root);
					
			} catch (DocumentException e) {
				logger.error("can not load phone config file: " + file);
				logger.error(e);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
	}
	
	protected void loadParams(Element root) throws Exception {
		Element element = root.element(Node_Param);
		
		String url = element.elementText("url");
		Configer.setURL(url);
		
		String username = element.elementText("username");
		Configer.setUserName(username);
		
		String password = element.elementText("password");
		Configer.setPassword(password);
	}

	private void loadTemplates(Element root) {
		Iterator<?> iterator = root.elementIterator(Node_Template);
		
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();	
			String name = element.attributeValue("name");
			
			String content = element.elementText("content");
			Configer.addTemplate(name, content);
		}
	}

	@Override
	protected EntityResolver getEntityResolver() throws FileNotFoundException {
		return new DTDEntityResolver(foundation.config.Configer.getPath_Config(), "phone.dtd");
	}


}
