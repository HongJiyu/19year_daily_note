package spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author Administrator
 * @Description  这是个通知类，也就是加强的功能，保存实例和方法 
 * @Time 下午9:50:20
 *
 */
public class JYAdvice {
	private Object aspect;
	private Method adviceMethod;
	private String throwName;
	public JYAdvice(Object newInstance, Method method) {
			this.aspect=newInstance;
			this.adviceMethod=method;
	}
	public Object getAspect() {
		return aspect;
	}
	public void setAspect(Object aspect) {
		this.aspect = aspect;
	}
	public Method getAdviceMethod() {
		return adviceMethod;
	}
	public void setAdviceMethod(Method adviceMethod) {
		this.adviceMethod = adviceMethod;
	}
	public String getThrowName() {
		return throwName;
	}
	public void setThrowName(String throwName) {
		this.throwName = throwName;
	}
	
}
