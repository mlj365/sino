package foundation.shedule;

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
import foundation.config.Configer;
import foundation.util.DTDEntityResolver;

public class ScheduleJobLoader extends ConfigFileLoader {

	public ScheduleJobLoader() {
		path = Configer.getPath_Config() + "/job.xml";
	}
	
	@Override
	public void load() throws Exception {
		File file = new File(path);
		
		if (!file.exists()) {
			return;
		}
			
		loadOneFile(file);
		
		Schedule.start();
	}

	private void loadOneFile(File file) {
		try {
			logger.debug("load schedule file:" + file);
			InputStream inputStream = new FileInputStream(file);
			
	        try {
	    		SAXReader reader = new SAXReader();
	    		reader.setEntityResolver(new DTDEntityResolver(Configer.getPath_Config(), "job.dtd"));
	    		reader.setValidation(false);
	    		
				Document doc = reader.read(inputStream);
				Element root = doc.getRootElement();
					
				loadJobs(root);
					
			} catch (DocumentException e) {
				logger.error("can not load schedule file: " + file);
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

	private void loadJobs(Element root) {
		Iterator<?> iterator = root.elementIterator("job");
		
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();	
			loadOneJob(element);
		}
	}

	private void loadOneJob(Element element) {
		try {
			String id = element.attributeValue("id");
			String name = element.attributeValue("name");
			String runtime = element.attributeValue("runtime");
			String classname = element.attributeValue("classname");
			
			Class<?> clazz = Class.forName(classname);
			Job job = (Job)clazz.newInstance();
			
			job.setId(id);
			job.setGroup("schedule-group");
			job.setName(name);
			job.setRuntime(runtime);
			
			Schedule.appendJob(job);
		}
		catch (Exception e) {
			logger.error("load schedule job error, " + e.getClass().getSimpleName() + ":" + e.getMessage());
		}
	}

	@Override
	protected EntityResolver getEntityResolver() throws FileNotFoundException {
		return new DTDEntityResolver(Configer.getPath_Config(), "job.dtd");
	}
}
