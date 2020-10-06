package spring_handwrite;

import java.io.File;
import java.lang.reflect.Method;
import java.util.regex.Pattern;


public class Test {

	public static void main(String[] args) {
		
		String pointCut = "public .* com.gupaoedu.vip.demo.service..*Service..*(.*)".replaceAll("\\.", "\\\\.").replaceAll("\\\\.\\*", ".*")
				.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");

		// 玩正则
		String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
	Pattern	pointCutClassPattern = Pattern
				.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));
		
	}

}
