package test.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import test.annotation.JYAutowrited;
import test.annotation.JYController;
import test.annotation.JYRequestMapping;
import test.annotation.JYRequestParameter;
import test.annotation.JYService;

@WebServlet(value = "/*", loadOnStartup = 1, initParams = {
		@WebInitParam(name = "setting", value = "application.properties") })
public class DispatcherServlet extends HttpServlet {

	/**
	 * 用来读取配置文件的
	 */
	Properties properties = new Properties();

	/**
	 * 保存所需扫描包下的所有.class文件，存储其文件路径，格式为包名.类名
	 */
	List<String> beanClassNames = new ArrayList<String>();

	/**
	 * ioc容器，用来存放扫描到的实例
	 */
	Map<String, Object> ioc = new HashMap<String, Object>();

	/**
	 * 这是一个地址和方法的映射关系。
	 */
	Map<String, Method> handlerMapping = new HashMap<String, Method>();

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
				method.invoke(ioc.get(toLowerCase(method.getDeclaringClass().getSimpleName())),params);
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
//		1.通过web.xml中配置的servlet信息，读取读取配置文件的路径
		doLoadConfig(config);

//		2.扫描相关类，获取扫描包下得所有类
		doScan(properties.getProperty("scan_package"));

//		3.实例化类.
		doInstance();

//		4.注入
		doDi();

		
//		5.初始化handlerMapping
		doMapping();

	}

	private void doMapping() {
		if (ioc.isEmpty()) {
			return;
		}
		for (Entry<String, Object> entry : ioc.entrySet()) {
			Class<?> one = entry.getValue().getClass();
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

	private void doDi() {
		if (ioc.isEmpty()) {
			return;
		}
		for (Entry<String, Object> entry : ioc.entrySet()) {
			Class<?> one = entry.getValue().getClass();
			Field[] fields = one.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(JYAutowrited.class)) {
					field.setAccessible(true);
					//自定义的命名
					String name = field.getAnnotation(JYAutowrited.class).name();
					// 没有自定义名字，就采用类型注入，可以多一个类名首字母小写，其实，通过类型就可以了（具体看doInstance的代码）。
					if ("".equals(name.trim())) {
						name = field.getType().getName();
					}

					try {
						field.set(entry.getValue(), ioc.get(name));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

	}

	private void doInstance() {
		for (int i = 0; i < beanClassNames.size(); i++) {
			try {
				Class<?> clazz = Class.forName(beanClassNames.get(i));
				// 如果是controller，因为它是不用注入的，不过在调用方法的时候需要用到它。所以直接根据类名数字小写来配置就行。
				if (clazz.isAnnotationPresent(JYController.class)) {
					String name = toLowerCase(clazz.getSimpleName());
					ioc.put(name, clazz.newInstance());
					/*如果是service类，那么它是可能会注入到字段里面去的，有多种注入匹配的机制。每一个service实现类都必须配多个，才能实现类型注入。
					  * 具体个数是： 1+接口个数。
					 *1.因为 一般都是  接口名 变量名 如IServiceTest serviceTest  
					  *要将它的子类注入到这里面来，要通过类型注入。 格式如下：  key:包名.接口名  value：实现类实例  要实现这个得遍历这个实现类得所有接口
					 *2.用户配置通过自定义来注入，则采用自定义的命名，否则采用默认类名加首字母小写
					 */
					
				} else if (clazz.isAnnotationPresent(JYService.class)) {
					
					Object instance = clazz.newInstance();
					String name = clazz.getAnnotation(JYService.class).name();
					if ("".equals(name)) {
						// 2.如果没有自定义beanName的话，那就采用类名加首字母小写。
						name = toLowerCase(clazz.getSimpleName()); 

					}
					ioc.put(name, instance);
					// 1.同时应该考虑service的接口的实例化，因为在注入的时候是用接口类型来接收的。
					Class<?>[] interfaces = clazz.getInterfaces();
					for (Class<?> class1 : interfaces) {
						ioc.put(class1.getName(), instance);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}

	}

	private String toLowerCase(String string) {
		char[] tmp = string.toCharArray();
		tmp[0] += 32;
		return new String(tmp);
	}

	private void doScan(String scanString) {
//		以classloader为根路径，在其后添加其他路径
		URL url = this.getClass().getClassLoader().getResource("/" + scanString.replaceAll("\\.", "/"));
		File scan_file = new File(url.getFile());
		for (File one : scan_file.listFiles()) {
			if (one.isDirectory()) {
				// 使用递归
				doScan(scanString + "." + one.getName());
			}
			if (one.getName().endsWith(".class")) {
				String name = one.getName().replace(".class", "");
				beanClassNames.add(scanString + "." + name);

			}
		}

	}

	private void doLoadConfig(ServletConfig config) {
		String setting = config.getInitParameter("setting"); //在web.xml配置的初始化的参数，也可以通过注解配置
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(setting));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void destroy() {

	}

}
