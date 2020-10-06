package demo.control;

import java.util.HashMap;
import java.util.Map;

import demo.service.IServiceTest;
import spring.framework.annotation.JYAutowrited;
import spring.framework.annotation.JYController;
import spring.framework.annotation.JYRequestMapping;
import spring.framework.annotation.JYRequestParameter;
import spring.webmvc.JYModelAndView;

@JYController
@JYRequestMapping("/test")
public class ControllerTest {
	
	@JYAutowrited
	IServiceTest serviceTest;
	
	@JYRequestMapping("/test")
	public void myTest(String name,@JYRequestParameter(name="cardId")String id)
	{
		serviceTest.pringLog();
		System.out.println(name +","+id);
	}
	
	@JYRequestMapping("/third")
	public JYModelAndView third(String name,@JYRequestParameter(name="cardId")String id)
	{
		Map<String, Object> model=new HashMap<String, Object>();
		model.put("name",name);
		model.put("cardId", id);
		JYModelAndView modelAndView =new JYModelAndView("test.html",model);
		serviceTest.pringLog();
		return modelAndView;
	  
	}
}
