package spring.webmvc;

import java.lang.annotation.Annotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spring.framework.annotation.JYRequestMapping;
import spring.framework.annotation.JYRequestParameter;


public class JYHandlerAdapter {

	/**
	 * @param request
	 * @param response
	 * @param handlerMapping
	 * @return
	 */
	public JYModelAndView handle(HttpServletRequest request,HttpServletResponse response,JYHandlerMapping handlerMapping) {
		//形参的拼接
		//paramIndexMapping方法的参数列表中的第几位是用户自定的参数命名，或者第几位是request或者是response 类型，
			Map<String ,Integer> paramIndexMapping=new HashMap<String, Integer>();
			Method method=handlerMapping.getMethod();
			Annotation[][] parameterAnnotations = method.getParameterAnnotations();
			for (int i = 0; i < parameterAnnotations.length; i++) {
				paramIndexMapping.put(method.getParameters()[i].getName(),i);//先同一加上参数名
				for (Annotation annotations : parameterAnnotations[i]) {
					if (annotations instanceof JYRequestParameter) {
						if(!((JYRequestParameter)annotations).name().equals(""))
						{
							paramIndexMapping.put(((JYRequestParameter)annotations).name(), i);//有自定义再用自定义的
						}
					}
				}
			}
			
			//这一步加，因为实参是没有这两个的，形参可能有，要靠内部处理加上去。
			Class<?> []paramsType=method.getParameterTypes();
			for (int i = 0; i <paramsType.length ; i++) {
				if (paramsType[i]==request.getClass()  || paramsType[i]==response.getClass() ) {
					paramIndexMapping.put(paramsType[i].getName(), i);
				}
			}
			
			//保存真正要传入方法的参数
			Object[] paramObjects=new Object[paramsType.length];
			
			Map<String, String[]> parameterMap = request.getParameterMap();
			for (Entry<String, String[]> entry : parameterMap.entrySet()) {
				if (paramIndexMapping.containsKey(entry.getKey())) {
					String value=Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", ","); 
					Integer index=paramIndexMapping.get(entry.getKey());
					paramObjects[index]=caseStringValue(value, paramsType[index]);
				}
			}
			
			//形参需要的话，存入这两个值
			if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
				Integer index=paramIndexMapping.get(HttpServletRequest.class.getName());
				paramObjects[index]=request;
			}else if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
				Integer index=paramIndexMapping.get(HttpServletResponse.class.getName());
				paramObjects[index]=response;
			}
			
			//调用方法
			Object result=null;
			try {
				 result= method.invoke(handlerMapping.getController(), paramObjects);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result==null || result instanceof Void ) {
				return null;
			}
			
			boolean isModelAndView =method.getReturnType() ==JYModelAndView.class;
			if (isModelAndView) {
				return (JYModelAndView) result;
			}
			
			return null;
		


}
	
	private Object caseStringValue(String param,Class<?> paramType)
	{
		if (String.class==paramType) {
			return param;
		}
		else if (Integer.class==paramType) {
			return Integer.valueOf(param);
		}
		else if (Double.class==paramType) {
			return Double.valueOf(param);
		}
		// =====
		else {
			return param;
		}
	}
}
