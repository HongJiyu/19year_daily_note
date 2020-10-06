package test.control;

import test.annotation.JYAutowrited;
import test.annotation.JYController;
import test.annotation.JYRequestMapping;
import test.annotation.JYRequestParameter;
import test.service.IServiceTest;

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
