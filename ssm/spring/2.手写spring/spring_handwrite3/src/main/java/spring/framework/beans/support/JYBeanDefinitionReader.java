package spring.framework.beans.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import spring.framework.annotation.JYController;
import spring.framework.annotation.JYService;
import spring.framework.beans.config.JYBeanDefinition;

/**
 * @author Administrator
 * @Description 用来读取配置文件的
 * @Time 下午5:13:49
 *
 */
public class JYBeanDefinitionReader {
	String[] config;
	Properties properties = new Properties();

	private List<String> registerBeanName = new ArrayList<String>();

	public JYBeanDefinitionReader(String[] config) {
		this.config = config;

		// 1.在web.xml配置的初始化的参数，也可以通过注解配置
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream(config[0]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String scanString = properties.getProperty("scan_package");

//		2.扫描获取所有beanClassName
		doScan(scanString);

	}

	/**
	 * 对所有的beanName，生成对应的bean配置信息
	 * 
	 * @return
	 */
	public List<JYBeanDefinition> loadJYBeanDefinitions() {
		List<JYBeanDefinition> beanDefinitions = new ArrayList<JYBeanDefinition>();
		for (String one : registerBeanName) {
			Class<?> clazz;
			try {
				clazz = Class.forName(one);
				if (clazz.isInterface()) {
					continue;
				}
//				beanDefinitions.add(doCreateJYBeanDefinition(toLowerCase(clazz.getSimpleName()), one));
				if (clazz.isAnnotationPresent(JYController.class) ) {
					// controller的话,采用类名首字母小写
					JYBeanDefinition beanDefinition = doCreateJYBeanDefinition(toLowerCase(clazz.getSimpleName()), one);
					beanDefinitions.add(beanDefinition);
				}
				else if (clazz.isAnnotationPresent(JYService.class)) {
					//service要考虑多的多,如果是自定义，采用自定义，否则采用默认
					String factoryBeanName=toLowerCase(clazz.getSimpleName());
					if (!clazz.getAnnotation(JYService.class).name().equals("")) {
						factoryBeanName=clazz.getAnnotation(JYService.class).name();
					}
					JYBeanDefinition beanDefinition = doCreateJYBeanDefinition(factoryBeanName, one);
					beanDefinitions.add(beanDefinition);
					//同时对接口进行注入
					for(Class<?> tmp : clazz.getInterfaces())
					{
						 beanDefinition = doCreateJYBeanDefinition(tmp.getName(), one);
						 beanDefinitions.add(beanDefinition);
					}
				}

				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return beanDefinitions;

	}

	/**
	 * 生成beandefinition对象
	 * 
	 * @param factoryBeanName
	 * @param beanClassName
	 * @return
	 */
	public JYBeanDefinition doCreateJYBeanDefinition(String factoryBeanName, String beanClassName) {
		JYBeanDefinition beanDefinition = new JYBeanDefinition();
		beanDefinition.setBeanClassName(beanClassName);
		beanDefinition.setFactoryBeanName(factoryBeanName);
		return beanDefinition;
	}

	/**
	 * 递归扫描指定路径下的所有类
	 * 
	 * @param scanString
	 */
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
				registerBeanName.add(scanString + "." + name);

			}
		}

	}

	private String toLowerCase(String string) {
		char[] tmp = string.toCharArray();
		tmp[0] += 32;
		return new String(tmp);
	}

	public Properties getConfig() {
		return properties;
	}

}
