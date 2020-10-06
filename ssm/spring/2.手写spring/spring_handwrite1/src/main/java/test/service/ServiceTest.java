package test.service;

import test.annotation.JYService;

@JYService(name = "s1")
public class ServiceTest implements IServiceTest {

	@Override
	public void pringLog() {
		// TODO Auto-generated method stub
		System.out.println("successfully!!");
	}

}
