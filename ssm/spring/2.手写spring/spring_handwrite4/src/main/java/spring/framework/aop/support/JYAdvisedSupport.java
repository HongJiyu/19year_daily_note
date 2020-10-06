package spring.framework.aop.support;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import spring.framework.aop.aspect.JYAdvice;
import spring.framework.aop.config.JYAopConfig;

/**
 * @author Administrator
 * @Description  一个实例就对应一个改对象，保存着原实例，又保存该实例需要增强的方法以及具体增强的通知
 * @Time 下午9:54:15
 *
 */
public class JYAdvisedSupport {

	/**
	 * 目标对象的 包名.类名
	 */
	Class<?> targetClass;
	/**
	 * 目标对象
	 */
	Object target;
	/**
	 * 配置切面的信息
	 */
	private JYAopConfig config;
	private Pattern pointCutClassPattern;
	/**
	 * 一个对象对应一个methodCache，包装着一个对象的所有method，而每个method又有多个通知。 spring用的是list
	 */
	private Map<Method, Map<String, JYAdvice>> methodCache;

	public JYAdvisedSupport(JYAopConfig config) {
		super();
		this.config = config;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	/**
	 * 震惊！！！在这里执行了parse（）方法
	 * @param targetClass
	 */
	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
		parse();
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public boolean pointCutMatch() {

		 return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
	}

	public Map<String, JYAdvice> getAdvices(Method method, Class<?> clazz) throws Exception {
        Map<String,JYAdvice> cached = methodCache.get(method);

        //精髓
        //如果没找到，就返回自己
        if(null == cached){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m,cached);
        }
        return cached;
	}

	/**
	 * 已知aop的配置信息，目标对象，目标类。
	 * 这个方法将解析aop的配置文件，并与当前实例进行匹配，这个实例是否需要增强，将需要增强的信息保存在methodCache中
	 */
	private void parse() {
		//将aop配置信息中的pointCut 转化为正则的形式
		String pointCut = config.getPointCut().replaceAll("\\.", "\\\\.").replaceAll("\\\\.\\*", ".*")
				.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
		Pattern pointCutPattern = Pattern.compile(pointCut);

		// 玩正则
		String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
		pointCutClassPattern = Pattern
				.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));
		
		//上面的操作就是：
		//pointCut ：                 public .* com.gupaoedu.vip.demo.service..*Service..*(.*)
		// pointCut                		public .* com\\.gupaoedu\\.vip\\.demo\\.service\\..*Service\\..*\\(.*\\)
//	pointCutForClassRegex：	public .* com\\.gupaoedu\\.vip\\.demo\\.service\\..*Service
// pointCutClassPattern：    class com\.gupaoedu\.vip\.demo\.service\..*Service
		
		
		   try {
	            methodCache = new HashMap<Method, Map<String, JYAdvice>>();
	            

	            //在配置文件中获取的增强的类，以及方法。
	            Class apectClass = Class.forName(this.config.getAspectClass());
	            Map<String,Method> aspectMethods = new HashMap<String,Method>();
	            for (Method method : apectClass.getMethods()) {
	                aspectMethods.put(method.getName(),method);
	            }

	            //在被增强的类中循环找到所有的方法
	            for (Method method : this.targetClass.getMethods()) {
	                //保存方法名
	                String methodString = method.toString();
	                if(methodString.contains("throws")){
	                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
	                }
	                Matcher matcher = pointCutPattern.matcher(methodString);
	                if(matcher.matches()){
	                    Map<String,JYAdvice> advices = new HashMap<String,JYAdvice>();

	                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))){
	                        advices.put("before",new JYAdvice(apectClass.newInstance(),aspectMethods.get(config.getAspectBefore())));
	                    }

	                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))){
	                        advices.put("after",new JYAdvice(apectClass.newInstance(),aspectMethods.get(config.getAspectAfter())));
	                    }

	                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))){
	                        JYAdvice a =  new JYAdvice(apectClass.newInstance(),aspectMethods.get(config.getAspectAfterThrow()));
	                        a.setThrowName(config.getAspectAfterThrowingName());
	                        advices.put("afterThrow",a);
	                    }

	                    methodCache.put(method,advices);
	                }
	            }

	        }catch (Exception e){
	            e.printStackTrace();
	        }
	}

}
