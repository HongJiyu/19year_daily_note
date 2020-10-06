package demo.service;

import spring.framework.annotation.JYService;

@JYService(name = "s1")
public class ServiceTest implements IServiceTest {

	@Override
	public void pringLog() {
		// TODO Auto-generated method stub
		System.out.println("successfully!!");
	}

}
