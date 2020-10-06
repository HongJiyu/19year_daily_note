package spring.webmvc;

import java.util.Map;

public class JYModelAndView {
	private String viewName;
	private Map<String, ?> model;
	
	public JYModelAndView(String viewName,Map<String, ?> model) {
		super();
		this.viewName = viewName;
		this.model=model;
	}
	
	public JYModelAndView(String viewName) {
		super();
		this.viewName = viewName;
	}
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public Map<String, ?> getModel() {
		return model;
	}
	public void setModel(Map<String, ?> model) {
		this.model = model;
	}
	
}
