package demo.service;

import spring.framework.annotation.JYService;

@JYService(name = "s1")
public class ServiceTest implements IServiceTest {

	@Override
	public void pringLog() {
		System.out.println("successfully!!");
	}

	@Override
	public void throwException() throws Exception {
		System.out.println("throwException is starting...");
		throw new Exception("故意抛出一个异常");
	}

}
