package spring.framework.beans;

/**
 * @author Administrator
 * @Description 一个包装类，是为了兼容aop？？
 * @Time 下午5:14:05
 *
 */
public class JYBeanWrapper {
	Object wrapperInstance;
	Class<?> wrappedClass;
	public JYBeanWrapper(Object instance) {
		this.wrappedClass=instance.getClass();
		this.wrapperInstance=instance;
	}
	public Object getWrapperInstance() {
		return wrapperInstance;
	}
	public void setWrapperInstance(Object wrapperInstance) {
		this.wrapperInstance = wrapperInstance;
	}
	public Class<?> getWrappedClass() {
		return wrappedClass;
	}
	public void setWrappedClass(Class<?> wrappedClass) {
		this.wrappedClass = wrappedClass;
	}
	
}
