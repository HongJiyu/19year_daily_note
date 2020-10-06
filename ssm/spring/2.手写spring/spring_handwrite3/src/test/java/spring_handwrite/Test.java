package spring_handwrite;

import java.io.File;
import java.lang.reflect.Method;


public class Test {

	public static void main(String[] args) {
		File one=new  File(Test.class.getClassLoader().getResource("layouts/404.html").getFile());
		File templateFile=new File((one.getPath()+"/"+"404.html").replace("/+","/"));
		System.out.println(templateFile);
		
	}

}
