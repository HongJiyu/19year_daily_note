package spring_handwrite;

import java.lang.reflect.Method;

import test.annotation.JYController;

public class Test {
	public static void main(String[] args) {
		try {
//										  test.control.ControllerTest
			Class<?> clazz=Class.forName("test.control.ControllerTest");
			for (Method one : clazz.getMethods()) {
				System.out.println(one.getName());
			}
			System.out.println(clazz.isAnnotationPresent(JYController.class));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
