package spring.framework.beans.config;

/**
 * @author Administrator
 * @Description 用来存放每一个类的信息，以防止如果是多例等情况需要再次实例化时，不用再对配置文件进行扫描 
 * @Time 下午5:12:13
 *
 */
/**
 * @author Administrator
 * @Description 
 * @Time 下午5:13:27
 *
 */
public class JYBeanDefinition {
	String factoryBeanName ; //通过这个来注入的，可以是用户自定义、默认类名首字母小写、对接口注入采用它的实现类的 包名.类名
	String beanClassName; //包名.类名，是通过这个来通过反射获取实例的
	public String getFactoryBeanName() {
		return factoryBeanName;
	}
	public void setFactoryBeanName(String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}
	public String getBeanClassName() {
		return beanClassName;
	}
	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}
	
}

