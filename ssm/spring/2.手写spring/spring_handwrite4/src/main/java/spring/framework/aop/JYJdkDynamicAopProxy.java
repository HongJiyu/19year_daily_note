package spring.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import spring.framework.aop.aspect.JYAdvice;
import spring.framework.aop.support.JYAdvisedSupport;

/**
 * @author Administrator
 * @Description 通过获取JYAdvisedSupport，生成原对象的代理对象并返回结果
 * @Time 下午9:56:05
 *
 */
public class JYJdkDynamicAopProxy implements InvocationHandler {
	private JYAdvisedSupport config;

	public JYJdkDynamicAopProxy(JYAdvisedSupport config) {
		this.config = config;
	}

	public Object getProxy() {
		return Proxy.newProxyInstance(this.getClass().getClassLoader(),this.config.getTargetClass().getInterfaces() , this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Map<String, JYAdvice> advices=this.config.getAdvices(method, this.config.getTargetClass());
		invokeAdvice(advices.get("before"));
		Object returnValue=null;
		try {
			
			 returnValue=method.invoke(this.config.getTarget(), args);
		}
		catch(Exception e)
		{
			invokeAdvice(advices.get("afterThrow"));
			e.printStackTrace();
		}
		invokeAdvice(advices.get("after"));
		return null;
	}

	private void invokeAdvice(JYAdvice jyAdvice) {
			try {
				jyAdvice.getAdviceMethod().invoke(jyAdvice.getAspect());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
