package spring.webmvc;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spring.framework.annotation.JYAutowrited;
import spring.framework.annotation.JYController;
import spring.framework.annotation.JYRequestMapping;
import spring.framework.annotation.JYRequestParameter;
import spring.framework.annotation.JYService;
import spring.framework.context.JYApplicationContext;


@WebServlet(value = "/*", loadOnStartup = 1, initParams = {
		@WebInitParam(name = "setting", value = "application.properties") })
public class DispatcherServlet extends HttpServlet {


	/**
	 * 这是一个地址和方法的映射关系。
	 */
	Map<String, Method> handlerMapping = new HashMap<String, Method>();

	JYApplicationContext jyApplicationContext=null;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//将请求地址和调用的方法进行了关联，并调用方法。
		doDispatcher(request,response);
	

	}

	private void doDispatcher(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getRequestURI();
		String contextUrl = request.getContextPath();
		url = url.replace(contextUrl, "").replaceAll("/+", "/");
		
		if (handlerMapping.containsKey(url)) {
			Method method = handlerMapping.get(url);
			Map<String, String[]> parameterMap = request.getParameterMap();
			
			//对方法所需的参数和传进来的参数进行匹配
			Object[] params=new Object[method.getParameterCount()];
			Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			for (int i = 1; i < parameterAnnotations.length; i++) {
				String paramName=method.getParameters()[i].getName();
				for (int j = 0; j < parameterAnnotations[i].length; j++) {
					if (parameterAnnotations[i][j] instanceof JYRequestParameter  ) {
						String alias= ((JYRequestParameter)parameterAnnotations[i][j]).name();
						if (!alias.equals("")) {
							
							paramName =alias;
							break;
						}

					}
				}
				params[i]=Arrays.toString(parameterMap.get(paramName)).replaceAll("\\[|\\]", "").replaceAll("\\s", ",");
			}
			
			
			//调用
			try {
				// controller,永远只会放首字母小写
				method.invoke(jyApplicationContext.getBean(toLowerCase(method.getDeclaringClass().getSimpleName())),params);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} else {
			try {
				response.getWriter().write("404 Not Found");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void init(ServletConfig config) {

//		ioc 和di
		 jyApplicationContext=new JYApplicationContext(config.getInitParameter("setting"));

		
//		5.初始化handlerMapping
		doMapping();

	}

	private void doMapping() {
		if (jyApplicationContext.getBeanDefinitionCount()==0) {
			return;
		}
		String[] beanNames=jyApplicationContext.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			Object instance=jyApplicationContext.getBean(beanName);
			Class<?> one=instance.getClass();
			if (!one.isAnnotationPresent(JYController.class)) {
				continue;
			}
			String baseUrl = "";
			if (one.isAnnotationPresent(JYRequestMapping.class)) {
				baseUrl = baseUrl + "/" + one.getAnnotation(JYRequestMapping.class).value();
			}
			for (Method method : one.getMethods()) {
				if (method.isAnnotationPresent(JYRequestMapping.class)) {
					String tmpUrl = "/" + method.getAnnotation(JYRequestMapping.class).value();
					tmpUrl = baseUrl + tmpUrl;
					tmpUrl = tmpUrl.replaceAll("/+", "/");
					handlerMapping.put(tmpUrl, method);
					System.out.println("映射关系：" + tmpUrl + "  -->>" + method.getName());

				}
			}

		}

	}

	private String toLowerCase(String string) {
		char[] tmp = string.toCharArray();
		tmp[0] += 32;
		return new String(tmp);
	}
	

	@Override
	public void destroy() {

	}

}
