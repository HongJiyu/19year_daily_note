package spring.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class JYHandlerMapping {
/**
 * 调用方法的实例
 */
public Object controller;
/**
 * 方法
 */
public Method method;
/**
 * url 占位符
 */
public Pattern pattern;

public JYHandlerMapping(Object controller, Method method, Pattern pattern) {
	super();
	this.controller = controller;
	this.method = method;
	this.pattern = pattern;
}
public Object getController() {
	return controller;
}
public void setController(Object controller) {
	this.controller = controller;
}
public Method getMethod() {
	return method;
}
public void setMethod(Method method) {
	this.method = method;
}
public Pattern getPattern() {
	return pattern;
}
public void setPattern(Pattern pattern) {
	this.pattern = pattern;
}
}
