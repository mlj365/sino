package foundation.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import foundation.callable.EnvelopWriter;
import foundation.callable.ICallable;
import foundation.callable.IEnvelop;
import foundation.config.Configer;
import foundation.user.OnlineAnymous;
import foundation.user.OnlineUser;
import foundation.user.Statistics;
import foundation.util.Util;

public class MainDispatcher implements Filter {

	private static MainDispatcher instance;
	private static Logger logger;
	private static String contextPath;
	private static boolean AuthorizeActive;
	private static Set<String> freeVisitTypes;
	private static ResourceFilter freeVisitResources;
	private static Set<String> freeVisitCalls;
	private static Map<String, VirtualPath> virtualPaths;
	private static Map<String, Class<? extends ICallable>> callableClassMap;
	private static ExcludeList excludeList;

	static {
		logger = Logger.getLogger(MainDispatcher.class);

		callableClassMap = new HashMap<String, Class<? extends ICallable>>();
		freeVisitTypes = new HashSet<String>();
		freeVisitResources = new ResourceFilter();
		virtualPaths = new HashMap<String, VirtualPath>();
		freeVisitCalls = new HashSet<String>();
		excludeList = new ExcludeList();
	}

	public MainDispatcher() {
		instance = this;
	}

	public synchronized static MainDispatcher getInstance() {
		if (instance == null) {
			instance = new MainDispatcher();
		}

		return instance;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();
		Map<String, Statistics> visitor = new HashMap<String, Statistics>();
		servletContext.setAttribute("visitor", visitor);

		contextPath = servletContext.getContextPath();
		RequestPath.contextLength = contextPath.length();
		logger.debug("contextPath: " + contextPath);

		AuthorizeActive = Util.stringToBoolean(Configer.getParam("AuthorizeActive"));
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		request.setCharacterEncoding("utf-8");
		saveIp(request, getIP(request));

		try {
			RequestPath path = new RequestPath(request);
			RequestType type = path.getType();

			if (logger.isDebugEnabled()) {
				logger.debug("filter request URI:" + path);
			}

			if (RequestType.Resource == type) {
				// 1.1 goto free resource
				if (freeVisitTypes.contains(path.getSuffix())) {
					RequestDispatcher dispatcher = request.getRequestDispatcher(path.getTarget());
					dispatcher.forward(request, response);
					return;
				}
				// 1.2 goto free sub resource
				else if (freeVisitResources.contains(path.getTarget())) {
					RequestDispatcher dispatcher = request.getRequestDispatcher(path.getTarget());
					dispatcher.forward(request, response);
					return;
				}
				// 1.3 goto authorized resources
				else {
					OnlineUser onlineUser = getOnlineUser(path);
					if (onlineUser == null) {
						String target = Configer.getPage_TimeOut();

						PrintWriter out = response.getWriter();
						out.println("<script>");
						out.println("window.top.location.href='" + target + "'");
						out.println("</script>");
						return;
					}

					RequestDispatcher dispatcher = request.getRequestDispatcher(path.getTarget());
					dispatcher.forward(request, response);					
				}
			}
			else {
				VirtualPath virtualPath = virtualPaths.get(path.getTarget());

				if (virtualPath != null) {
					logger.debug("get virtual path: " + virtualPath);

					//2.1 goto root
					if (RequestType.Root == type) {
						response.sendRedirect(virtualPath.getTarget());
						return;
					}
					//2.2 goto resource
					else if (VirtualPathType.Resource == virtualPath.getType()) {
						// 4. goto resource on virtual path
						RequestDispatcher dispatcher = request.getRequestDispatcher(virtualPath.getTarget());
						dispatcher.forward(request, response);
						return;
					}
					//2.3 goto error
					else {
						writeError(request, response, "resource not exists: " + path.getTarget());
						return;
					}
				}

				virtualPath = virtualPaths.get(path.getParent());

				if (virtualPath == null) {
					if (excludeList.contains(path)) {
						logger.debug("dispatch exclude url:" + path);
						chain.doFilter(request, response);
					}
				}
				
				if (virtualPath == null) {
					writeError(request, response, "resource not exists: " + path.getTarget());
					return;
				}

				Class<? extends ICallable> clazz = virtualPath.getCallableClass();
				ICallable callable = clazz.newInstance();
				
				callable.setRequest(request);
				callable.setResponse(response);

				OnlineUser onlineUser = getOnlineUser(path);

				// 3.1 goto free visit object
				if (onlineUser == null) {
					if (freeVisitCalls.contains(path.getTarget())) {
						onlineUser = OnlineAnymous.getInstance();
					}
					else {
						writeSessionTimeout(request, response);
						return;
					}
				}

				// 3.2 goto authorized object
				callable.setOnlineUser(onlineUser);
				callable.receive(path.getShortTarget());
				return;
			}
		}
		catch (Exception e) {
			String error = printStackToString(e);
			logger.error("dispatch error:" + e.getMessage());
			logger.error(error);
			writeError(request, response, error);
		}
	}

	private OnlineUser getOnlineUser(RequestPath path) {
		HttpServletRequest request = path.getRequest();
		HttpSession session = request.getSession();

		// 获取OnlineUser类的简单类名
		String code = OnlineUser.class.getSimpleName();
		OnlineUser onlineUser = (OnlineUser) session.getAttribute(code);

		// 如果session中没有用户信息，但是不要权限验证，责返回一个匿名的用户信息，以便进行下面的操作
		if (onlineUser == null) {
			if (!AuthorizeActive) {
				onlineUser = OnlineAnymous.getInstance();
			}

			return onlineUser;
		}

		// 如果需要权限验证，如果真没有用户信息，将返回null
		return onlineUser;
	}

	public void destroy() {

	}

	private String printStackToString(Exception e) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outStream);
		e.printStackTrace(printStream);

		return outStream.toString();
	}

	private void writeSessionTimeout(HttpServletRequest request, HttpServletResponse response) {
		try {
			EnvelopWriter envelopWriter = new EnvelopWriter(request, response);
			envelopWriter.ReplyError(IEnvelop.Error_Code_Timeout, IEnvelop.Error_Messgae_Timeout);
		}
		catch (Exception ex) {
		}
	}

	private void writeError(HttpServletRequest request, HttpServletResponse response, String error) {
		try {
			EnvelopWriter envelopWriter = new EnvelopWriter(request, response);
			envelopWriter.ReplyError(IEnvelop.Error_Code_ServerError, error);
		}
		catch (Exception ex) {
		}
	}

	public void clear() {
		callableClassMap.clear();
	}

	public void appendFreeVisitType(String type) {
		type = type.toLowerCase();
		freeVisitTypes.add(type);
	}

	public void appendFreeVisitResource(String resource) {
		resource = resource.toLowerCase();
		freeVisitResources.add(resource);
	}

	public void appendFreeVisitCalls(String call) {
		call = call.toLowerCase();

		if ('/' != call.charAt(0)) {
			call = "/" + call;
		}

		freeVisitCalls.add(call);
	}


	public void appendExcludeResources(String exclude) {
		exclude = exclude.toLowerCase();
		excludeList.add(exclude);
	}
	
	public void appendVirtualPaths(String path, String target, String className) throws Exception {
		if ('/' != path.charAt(0)) {
			path = "/" + path;
		}

		VirtualPath virtualPath = new VirtualPath(target, className);
		virtualPaths.put(path, virtualPath);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void appendCallableClass(String path, String classname) throws Exception {
		path = path.toLowerCase();

		Class clazz = Class.forName(classname);
		Class<? extends ICallable> callableClass = (Class<? extends ICallable>) clazz;

		callableClassMap.put(path, callableClass);
	}
	
	private String getIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");

		if (ip == null) {
			ip = request.getRemoteAddr();
			return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
		}

		return ip;
	}

	@SuppressWarnings("deprecation")
	private void saveIp(HttpServletRequest request, String ip) {
		if (!request.getRequestURI().endsWith(".html")) { // 不对网页以外的请求做统计，例如图片等
			return;
		}
		ServletContext servletContext = request.getServletContext();
		@SuppressWarnings("unchecked")
		Map<String, Statistics> visitor = (Map<String, Statistics>) servletContext.getAttribute("visitor");
		if (visitor.keySet().contains(ip)) {
			Statistics s = visitor.get(ip);
			s.setCnt(s.getCnt() + 1);
			s.setDate(new Date().toLocaleString());
			visitor.put(ip, s);
			
			servletContext.setAttribute("visitor", visitor);
		}
		else {
			visitor.put(ip, new Statistics(ip, 1, new Date().toLocaleString()));
			servletContext.setAttribute("visitor", visitor);
		}
	}

}
