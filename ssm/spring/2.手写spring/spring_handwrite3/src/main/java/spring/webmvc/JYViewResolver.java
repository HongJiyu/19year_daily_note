package spring.webmvc;

import java.io.File;
import java.util.Locale;

public class JYViewResolver {
	private File templateRootDir;
	private final String DEFAULT_TEMPLATE_SUFFIX= ".html";
	public JYViewResolver(String templateRoot) {
		String templateRootPath=this.getClass().getClassLoader().getResource(templateRoot).getFile();
		templateRootDir=new File(templateRootPath);
	}
	
	/**
	 * 
	 * @param viewName
	 * @param locale
	 * @return
	 */
	public JYView resolveViewName(String viewName,Locale locale)
	{
		if(viewName==null || "".equals(viewName)) return null;
		viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName :viewName +DEFAULT_TEMPLATE_SUFFIX;
		File templateFile=new File((templateRootDir.getPath()+"/"+viewName).replace("/+","/"));
		return new  JYView(templateFile);
	}
}
