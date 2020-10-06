package spring.framework.context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import spring.framework.annotation.JYAutowrited;
import spring.framework.annotation.JYController;
import spring.framework.annotation.JYService;
import spring.framework.beans.JYBeanWrapper;
import spring.framework.beans.config.JYBeanDefinition;
import spring.framework.beans.support.JYBeanDefinitionReader;


/**
 * @author Administrator
 * @Description 入口类
 * @Time 下午5:15:14
 *
 */
public class JYApplicationContext {
	JYBeanDefinitionReader beanDefinitionReader=null;
	List<JYBeanDefinition> beanDefinitions=null;
	private String[] configLocations;
	/**
	 * 一个map，装配的是可能需要实例的类的信息
	 */
	final Map<String,JYBeanDefinition> jyBeanDefinitionMap=new HashMap<String, JYBeanDefinition>();
	/**
	 * 一个map，装配的是包装实例对象。
	 */
	Map<String, JYBeanWrapper> factoryBeanInstanceCache=new HashMap<String, JYBeanWrapper>();
	/**
	 * 一个map，装配的是一个未包装的对象。
	 */
	Map<String, Object> factoryBeanObjectCache=new HashMap<String, Object>();
	
	public JYApplicationContext(String ... configLocations) {
		this.configLocations=configLocations;
		
//		1.在构造方法中，配置文件路径，读配置文件 ,获取所有beanClassName
		beanDefinitionReader=new JYBeanDefinitionReader(configLocations);
//		2.对所有被@service或者是@controller修饰的类进行生成对应的beanDefinition
		beanDefinitions=beanDefinitionReader.loadJYBeanDefinitions();
//		3.存的不再是对象，而是配置
		try {
			doRegisterBeanDefinition(beanDefinitions);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		4.  调用getBean（），才去获取实例
		doAutowired();

	}

	private void doAutowired() {
		for (Entry<String, JYBeanDefinition> one: jyBeanDefinitionMap.entrySet()) {
			getBean(one.getKey());
		}

	}


	/**
	 * 将beanDefinitions（List） 改成  jyBeanDefinitionMap（map） ，方便定位查找
	 * 注意这里要判断是否存在重复的key，因为spring 是不允许存在重复的自定义命名
	 * @param beanDefinitions
	 * @throws Exception 
	 */
	private void doRegisterBeanDefinition(List<JYBeanDefinition> beanDefinitions) throws Exception {
		for (JYBeanDefinition jyBeanDefinition : beanDefinitions) {
			//有一种可能，都是默认命名的情况下，都采用类名首字母小写，但是是 在不同的包下，那这样也会报错。
			if (jyBeanDefinitionMap.containsKey(jyBeanDefinition.getFactoryBeanName())) {
				throw new Exception(jyBeanDefinition.getFactoryBeanName()+" is existing !!");
			}
				jyBeanDefinitionMap.put(jyBeanDefinition.getFactoryBeanName(), jyBeanDefinition);
				//这些应该都不用加了，在处理JYBeanDefinitionReader 中的loadJYBeanDefinitions 处理了。
//				jyBeanDefinitionMap.put(jyBeanDefinition.getBeanClassName(), jyBeanDefinition); 
			
		}
	}

	/**
	 * 获取ioc中所有的beanname
	 * @return 
	 */
	public String[] getBeanDefinitionNames() 
	{
		return jyBeanDefinitionMap.keySet().toArray(new String[this.jyBeanDefinitionMap.size()]);
	}

	public int getBeanDefinitionCount() {
		return jyBeanDefinitionMap.size();
	}

	/**
	 * 通过beanName在配置信息中找到对应的beanClassName并进行实例化，再对该对象的字段进行依赖注入
	 * @param beanName
	 * @return
	 */
	public Object getBean(String beanName) 
	{
			//	1、根据factoryBeanName获取配置信息。
		JYBeanDefinition jyBeanDefinition=jyBeanDefinitionMap.get(beanName);
		
			//	2、根据配置信息并利用反射来获取实例
		Object instance=instantiateBean(beanName,jyBeanDefinition);
		
			//	3、把创建出来的实例包装为beanWrapper
		JYBeanWrapper beanWrapper=new JYBeanWrapper(instance);

		//	4、把BeanWrapper 对象放到真正的ioc容器
		factoryBeanInstanceCache.put(beanName, beanWrapper);
		
		//	5、执行依赖注入
		populateBean(beanName, jyBeanDefinition, beanWrapper);
		
		return factoryBeanInstanceCache.get(beanName).getWrapperInstance();
	}
	
	/**
	 * 对对象的其字段进行注入
	 * @param beanName
	 * @param beanDefinition
	 * @param beanWrapper
	 */
	public void populateBean(String beanName,JYBeanDefinition beanDefinition,JYBeanWrapper beanWrapper)
	{
			Object instance=beanWrapper.getWrapperInstance();
			Class<?> clazz=beanWrapper.getWrappedClass();
			if ( !(clazz.isAnnotationPresent(JYController.class) || clazz.isAnnotationPresent(JYService.class))) {
				return ;
				
			}
				Field[] fields = clazz.getDeclaredFields();
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
							if (factoryBeanInstanceCache.get(name)==null) {
								continue;
							}
							field.set(instance, factoryBeanInstanceCache.get(name).getWrapperInstance());
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
	

	private Object instantiateBean(String beanName, JYBeanDefinition jyBeanDefinition) {
		Class<?> clazz=null;
		Object instance=null;
		try {
		clazz=Class.forName(jyBeanDefinition.getBeanClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			 instance=clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//也保存未封装的对象。
		factoryBeanObjectCache.put(beanName, instance);
		//？？？？？为什么要保存这个？？？？
//		factoryBeanObjectCache.put(jyBeanDefinition.getFactoryBeanName(), instance);
		
		return instance;
	}

	public Object getBean(Class clazz) {
		return getBean(clazz.getName());
	}
	
	public Properties getConfig() {
		
		return beanDefinitionReader.getConfig();
	}
}
