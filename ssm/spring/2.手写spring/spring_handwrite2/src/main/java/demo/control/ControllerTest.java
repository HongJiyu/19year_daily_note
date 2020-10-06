package demo.control;

import demo.service.IServiceTest;
import spring.framework.annotation.JYAutowrited;
import spring.framework.annotation.JYController;
import spring.framework.annotation.JYRequestMapping;
import spring.framework.annotation.JYRequestParameter;

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
	
}
