package spring.webmvc;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spring.framework.annotation.JYController;
import spring.framework.annotation.JYRequestMapping;

import spring.framework.context.JYApplicationContext;

@WebServlet(value = "/*", loadOnStartup = 1, initParams = {
		@WebInitParam(name = "setting", value = "application.properties") })
public class DispatcherServlet extends HttpServlet {

	/**
	 * 这是一个地址和方法的映射关系。
	 */
	List<JYHandlerMapping> handlerMappings = new ArrayList<JYHandlerMapping>();
	Map<JYHandlerMapping,JYHandlerAdapter> handlerAdapters = new HashMap<JYHandlerMapping,JYHandlerAdapter>();
	List<JYViewResolver> viewResolvers=new ArrayList<JYViewResolver>();
	JYApplicationContext jyApplicationContext = null;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 将请求地址和调用的方法进行了关联，并调用方法。
		doDispatcher(request, response);

	}

	private void doDispatcher(HttpServletRequest request, HttpServletResponse response) {
		
//		1.根据url拿到对应的handler
		JYHandlerMapping handler=getHandler(request);
		if (handler==null) {
			processDispatchResult(request, response, new JYModelAndView("404.html"));
			return ;
		}
//		2.根据handler拿到对应的handlerAdapter
		JYHandlerAdapter handlerAdapter=getHandlerAdapter(handler);
//		3.根据HandlerAdapter拿到ModelAndView
		JYModelAndView mv=handlerAdapter.handle(request, response, handler);
//		4.将view渲染成能被浏览器接收的结果  html字符串。
		processDispatchResult(request,response,mv);
	}

	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, JYModelAndView mv) {
		if (mv==null) {
			return;
		}
		//如果没有提供模板，直接返回
		if (viewResolvers.isEmpty()) {
			return;
		}
		//这一手for循环，没看懂
		for (JYViewResolver viewResolver : viewResolvers) {

			JYView view=viewResolver.resolveViewName(mv.getViewName(), null);
			try {
				view.render(mv.getModel(), request, response);
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private JYHandlerAdapter getHandlerAdapter(JYHandlerMapping handler) {
		if (handlerAdapters.isEmpty()) {
			return null;
		}
		return handlerAdapters.get(handler);
	}

	private JYHandlerMapping getHandler(HttpServletRequest request) {
		if (handlerMappings.isEmpty()) {
			return null;
		}
		String url=request.getRequestURI();
		String contextUrl=request.getContextPath();
		url=url.replace(contextUrl, "").replaceAll("/+", "/");
		for (JYHandlerMapping jyHandlerMapping : handlerMappings) {
				Matcher matcher=jyHandlerMapping.getPattern().matcher(url);
				if (!matcher.matches()) {
					continue;
				}
				return jyHandlerMapping;
		}
		return  null;
	}

	@Override
	public void init(ServletConfig config) {

		//	ioc 和di
		jyApplicationContext = new JYApplicationContext(config.getInitParameter("setting"));

		// mvc，本应该是初始化九大组件
		initStrategies(jyApplicationContext);
	}

	private void initStrategies(JYApplicationContext jyApplicationContext) {
		initHandlerMappings(jyApplicationContext);
		initHandlerAdapters(jyApplicationContext);
		initViewResolvers(jyApplicationContext);

	}

	/**
	 * 视图转换器
	 * 初始化所有页面模板，保存在全局变量viewResolvers，一个模板对应一个viewResolver
	 * @param jyApplicationContext
	 */
	private void initViewResolvers(JYApplicationContext jyApplicationContext) {
			Properties properties=jyApplicationContext.getConfig();
			String templateRootDir=properties.getProperty("templateRootDir");
			String AbsoluteUrl=this.getClass().getClassLoader().getResource(templateRootDir).getFile();
			File templateFile=new File(AbsoluteUrl);
			for (File one : templateFile.listFiles()) {
				viewResolvers.add(new JYViewResolver(templateRootDir));
			}
	}

	/**
	 * 参数适配器
	 * 一个method和url的关系，对应一个参数适配器
	 * @param jyApplicationContext
	 */
	private void initHandlerAdapters(JYApplicationContext jyApplicationContext) {
		for (JYHandlerMapping jyHandlerMapping : handlerMappings) {
			handlerAdapters.put(jyHandlerMapping, new JYHandlerAdapter());
		}
	}

	/**
	 * 遍历所有类的含有@controller的method和url的映射关系
	 * 
	 * @param jyApplicationContext
	 */
	private void initHandlerMappings(JYApplicationContext jyApplicationContext) {
		if (jyApplicationContext.getBeanDefinitionCount() == 0) {
			return;
		}
		String[] beanNames = jyApplicationContext.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			Object instance = jyApplicationContext.getBean(beanName);
			Class<?> one = instance.getClass();
			if (!one.isAnnotationPresent(JYController.class)) {
				continue;
			}
			String baseUrl = "";
			if (one.isAnnotationPresent(JYRequestMapping.class)) {
				baseUrl =  "/" + one.getAnnotation(JYRequestMapping.class).value();
			}
			for (Method method : one.getMethods()) {
				if (method.isAnnotationPresent(JYRequestMapping.class)) {
					String tmpUrl = "/" + method.getAnnotation(JYRequestMapping.class).value();
					tmpUrl = baseUrl + tmpUrl;
					tmpUrl = tmpUrl.replaceAll("/+", "/").replaceAll("\\s", "").replaceAll("\\*", ".*");
					Pattern pattern=Pattern.compile(tmpUrl);
					this.handlerMappings.add(new JYHandlerMapping(instance, method, pattern));
					System.out.println("映射关系：" + tmpUrl + "  -->>" + method.getName());
				}
			}

		}
	}


}
