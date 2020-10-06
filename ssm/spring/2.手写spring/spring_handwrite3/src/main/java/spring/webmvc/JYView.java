package spring.webmvc;

import java.io.File;

import java.io.RandomAccessFile;

import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JYView {

	private File viewFile;

	public JYView(File file) {
		this.viewFile = file;
	}

	public File getViewFile() {
		return viewFile;
	}



//	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
//			throws Exception {
////		把模板文件的内容读取出来。
//		StringBuffer sBuffer = new StringBuffer();
//		RandomAccessFile ra = new RandomAccessFile(this.viewFile, "r");
////		解析，把模板里面写的模板语言替换掉。
//		String line = null;
//		if(model==null)
//		{
//			response.setCharacterEncoding("UTF-8");
//			while (null != (line = ra.readLine())) {
//				line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
//				sBuffer.append(line);
//			}
//			response.getWriter().write(sBuffer.toString());
//			return;
//		}
//		while (null != (line = ra.readLine())) {
//			line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
//			Pattern pattern = Pattern.compile("￥\\{|\\}", Pattern.CASE_INSENSITIVE);
//			Matcher matcher = pattern.matcher(line);
//			while (matcher.find()) {
//				String paramName = matcher.group();
//				paramName = paramName.replaceAll("￥\\{|\\}", "");
//				Object paramValue = model.get("paramName");
//				if (null == paramValue) {
//					continue;
//				}
//				line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
//				matcher = pattern.matcher(line);
//
//			}
//			sBuffer.append(line);
//
//		}
//		response.setCharacterEncoding("UTF-8");
//		response.getWriter().write(sBuffer.toString());
//	}

    public void render(Map<String,?> model, HttpServletRequest request, HttpServletResponse response) throws Exception{

        //第一步，就是把模板文件的内容读出来
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");

        //解析，把模板里面写的模板语言替换掉
        String line = null;
        while (null != (line = ra.readLine())){
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher mather = pattern.matcher(line);
            while (mather.find()){
                String paramName = mather.group();
                paramName = paramName.replaceAll("￥\\{|\\}","");
                Object paramVlue = model.get(paramName);
                if(null == paramVlue){continue;}
                line = mather.replaceFirst(makeStringForRegExp(paramVlue.toString()));
                mather = pattern.matcher(line);
            }
            sb.append(line);
        }

        response.setCharacterEncoding("utf-8");
        response.getWriter().write(sb.toString());
    }
        
//	public static String makeStringForRegExp(String str)
//	{
//		return str.replace("\\","\\\\").replace("*", "\\*")
//				.replace("+","\\+").replace("|", "\\|")
//				.replace("{","\\{").replace("}", "\\}")
//				.replace("(","\\(").replace(")", "\\)")
//				.replace("^","\\^").replace("$", "\\$")
//				.replace("[","\\[").replace("]", "\\]")
//				.replace("?","\\?").replace("&", "\\&")
//				.replace(".","\\.").replace(",", "\\,");
//
//	}
    

    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
