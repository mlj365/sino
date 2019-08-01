package foundation.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;

import foundation.persist.DatasuorceLoader;
import foundation.server.MainDispatcher;
import foundation.user.UserRightContainer;
import foundation.util.DTDEntityResolver;
import foundation.util.Util;

public class MainConfigLoader {

	protected static Logger logger;
	private static MainConfigLoader instance;
	private static ConfigLoaderContainer configLoaderContainer;
	private static UserRightContainer userRightContainer;
	private static String Node_Param = "param";
	private static final String Node_Param_Name = "name";
	private static final String Node_Param_Value = "value";
	private static final String Node_Param_Client = "client";
	private static String Node_FreeVisit = "freeVisit";
	private static String Node_Exclude = "exclude";
	private static String Node_LimitedVisit = "limitedVisit";
	private static String Node_RoleResource = "roleResource";
	private static String Node_RoleResource_Role = "role";
	private static String Node_RoleResource_Resource = "resource";
	private static String Node_Type = "type";
	private static String Node_Resource = "resource";
	private static String Node_Call = "call";
	private static String Node_Callable = "callable";
	private static String Node_Mapping = "mapping";
	private static String Node_Mapping_Path = "path";
	private static String Node_Mapping_ClassName = "classname";
	private static String Node_DataFilter = "dataFilter";
	private static String Node_DataFilter_Filter = "filter";
	private static String Node_DataFilter_Filter_TableName = "tablename";
	private static String Node_DataFilter_Filter_Where = "where";
	private static String Node_ConfigLoader = "configLoader";
	private static String Node_ConfigLoader_Loader = "loader";
	private static String Node_ConfigLoader_Loader_Name = "name";
	private static String Node_ConfigLoader_Loader_Active = "active";
	private static String Node_ConfigLoader_Loader_ClassName = "classname";
	private MainDispatcher mainDispatcher;
	private List<VirtualPathConfig> virtualPathList;
	private List<CallableConfig> callablList;

	static {
		logger = Logger.getLogger(DatasuorceLoader.class);
		configLoaderContainer = ConfigLoaderContainer.getInstance();
		userRightContainer = UserRightContainer.getInstance();
	}

	private MainConfigLoader() {
		mainDispatcher = MainDispatcher.getInstance();
		virtualPathList = new ArrayList<VirtualPathConfig>();
		callablList = new ArrayList<CallableConfig>();
	}

	public synchronized static MainConfigLoader getInstance() {
		if (instance == null) {
			instance = new MainConfigLoader();
		}

		return instance;
	}

	public void load() {
		String path = Configer.getPath_MainConfig();
		File file = new File(path);

		loadOneFile(file);
	}

	private void loadOneFile(File file) {
		try {
			logger.debug("load dispatch file:" + file);
			InputStream inputStream = new FileInputStream(file);

			try {
				SAXReader reader = new SAXReader();

				EntityResolver entityResolver = new DTDEntityResolver(Configer.getPath_Config(), "config.dtd");
				reader.setEntityResolver(entityResolver);
				reader.setValidation(true);

				Document doc = reader.read(inputStream);
				Element root = doc.getRootElement();

				mainDispatcher.clear();

				loadParams(root);
				loadConfigLoader(root);
				loadFreeVisit(root);
				loadLimitedVisit(root);
				loadVirtualPathConfig(root);
				loadCallableConfig(root);
				loadDataRight(root);
			} 
			catch (DocumentException e) {
				logger.error("can not load dispatch file: " + file);
				logger.error(e);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	public void loadClasses() throws Exception {
		//1.
		for (VirtualPathConfig virtualPath: virtualPathList) {
			loadOneVirtualPath(virtualPath);
		}
		
		//2.
		for (CallableConfig callableConfig: callablList) {
			loadOneDispatch(callableConfig);
		}
	}

	private void loadParams(Element root) throws Exception {
		Iterator<?> iterator = root.elementIterator(Node_Param);

		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();

			String name = element.attributeValue(Node_Param_Name);
			String value = element.attributeValue(Node_Param_Value);
			boolean client = Util.stringToBoolean(element.attributeValue(Node_Param_Client));

			Configer.addParam(name, value, client);
		}

		Configer.afterLoadParams();
	}

	private void loadFreeVisit(Element root) throws Exception {
		Iterator<?> iterator = root.elementIterator(Node_FreeVisit);

		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();

			Iterator<?> typeIterator = element.elementIterator(Node_Type);
			while (typeIterator.hasNext()) {
				Element elemnet = (Element) typeIterator.next();
				String type = elemnet.getTextTrim();
				loadOneFreeVisitType(type);
			}

			Iterator<?> resourceIterator = element.elementIterator(Node_Resource);
			while (resourceIterator.hasNext()) {
				Element elemnet = (Element) resourceIterator.next();
				String resource = elemnet.getTextTrim();
				loadOneFreeVisitResource(resource);
			}

			Iterator<?> callIterator = element.elementIterator(Node_Call);
			while (callIterator.hasNext()) {
				Element elemnet = (Element) callIterator.next();
				String call = elemnet.getTextTrim();
				loadOneFreeVisitCall(call);
			}
			
			Iterator<?> excludeIterator = element.elementIterator(Node_Exclude);
			while (excludeIterator.hasNext()) {
				Element elemnet = (Element) excludeIterator.next();
				String exclude = elemnet.getTextTrim();
				loadOneExcludeResource(exclude);
			}
		}
	}
	
	private void loadLimitedVisit(Element root) {
		Iterator<?> iterator = root.elementIterator(Node_LimitedVisit);

		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();

			Iterator<?> typeIterator = element.elementIterator(Node_RoleResource);
			while (typeIterator.hasNext()) {
				Element elemnet = (Element) typeIterator.next();
				String role = elemnet.attributeValue(Node_RoleResource_Role);
				String resource = elemnet.attributeValue(Node_RoleResource_Resource);
				userRightContainer.addRoleResource(role, resource);
			}
		}
	}

	private void loadVirtualPathConfig(Element root) throws Exception {
		Iterator<?> iterator = root.elementIterator("virtualPaths");

		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();

			Iterator<?> mappingIterator = element.elementIterator("virtualPath");
			while (mappingIterator.hasNext()) {
				Element elemnet = (Element) mappingIterator.next();

				String path = elemnet.attributeValue("path");
				String target = elemnet.attributeValue("target");
				String className = elemnet.attributeValue("classname");

				VirtualPathConfig virtualPath = new VirtualPathConfig(path, target, className);
				virtualPathList.add(virtualPath);
			}
		}
	}

	private void loadCallableConfig(Element root) throws Exception {
		Iterator<?> iterator = root.elementIterator(Node_Callable);
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();

			Iterator<?> mappingIterator = element.elementIterator(Node_Mapping);
			while (mappingIterator.hasNext()) {
				Element elemnet = (Element) mappingIterator.next();

				String path = elemnet.attributeValue(Node_Mapping_Path);
				String classname = elemnet.attributeValue(Node_Mapping_ClassName);

				CallableConfig callableConfig = new CallableConfig(path, classname);
				callablList.add(callableConfig);
			}
		}
	}

	private void loadConfigLoader(Element root) throws Exception {
		Iterator<?> iterator = root.elementIterator(Node_ConfigLoader);

		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();
			Iterator<?> configIterator = element.elementIterator(Node_ConfigLoader_Loader);

			while (configIterator.hasNext()) {
				Element loaderEl = (Element) configIterator.next();

				String name = loaderEl.attributeValue(Node_ConfigLoader_Loader_Name);
				String active = loaderEl.attributeValue(Node_ConfigLoader_Loader_Active);
				String classname = loaderEl.attributeValue(Node_ConfigLoader_Loader_ClassName);
				Class<?> clazz = Class.forName(classname);

				IPreloader configLoader;
				Method getInstance = null;
				try {
					getInstance = clazz.getDeclaredMethod("getInstance");
				} catch (NoSuchMethodException e) {
				}

				if (getInstance != null) {
					configLoader = (IPreloader) getInstance.invoke(null);
				} else {
					configLoader = (IPreloader) clazz.newInstance();
				}

				configLoader.setName(name);
				configLoader.setActive(Util.stringToBoolean(active));

				configLoaderContainer.add(configLoader);
			}
		}
	}

	private void loadDataRight(Element root) throws Exception {
		Iterator<?> iterator = root.elementIterator(Node_DataFilter);

		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();
			Iterator<?> filterIterator = element.elementIterator(Node_DataFilter_Filter);

			while (filterIterator.hasNext()) {
				Element filterEl = (Element) filterIterator.next();

				String tablename = filterEl.attributeValue(Node_DataFilter_Filter_TableName);
				String where = filterEl.attributeValue(Node_DataFilter_Filter_Where);

				userRightContainer.addDataRight(tablename, where);
			}
		}
	}

	private void loadOneFreeVisitType(String type) {
		if (Util.isEmptyStr(type)) {
			return;
		}

		mainDispatcher.appendFreeVisitType(type);
	}

	private void loadOneFreeVisitResource(String resource) {
		if (Util.isEmptyStr(resource)) {
			return;
		}

		mainDispatcher.appendFreeVisitResource(resource);
	}

	private void loadOneFreeVisitCall(String call) {
		if (Util.isEmptyStr(call)) {
			return;
		}

		mainDispatcher.appendFreeVisitCalls(call);
	}
	
	private void loadOneExcludeResource(String exclude) {
		if (Util.isEmptyStr(exclude)) {
			return;
		}

		mainDispatcher.appendExcludeResources(exclude);
	}
	
	private void loadOneVirtualPath(VirtualPathConfig virtualPath) throws Exception {
		if (virtualPath.invalid()) {
			return;
		}

		mainDispatcher.appendVirtualPaths(virtualPath.getPath(), virtualPath.getTarget(), virtualPath.getClassName());
	}

	private void loadOneDispatch(CallableConfig callableConfig) throws Exception {
		if (callableConfig.invalid()) {
			return;
		}

		mainDispatcher.appendCallableClass(callableConfig.getPath(), callableConfig.getClassName());
	}
}
