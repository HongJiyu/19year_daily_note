package spring.framework.aop.config;

/**
 * @author Administrator
 * @Description 单纯保存aop的配置文件的
 * @Time 下午9:49:57
 *
 */
public class JYAopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAfterThrow;
    private String aspectClass;
    private String aspectAfterThrowingName;
    
    
	public String getPointCut() {
		return pointCut;
	}
	public void setPointCut(String pointCut) {
		this.pointCut = pointCut;
	}
	public String getAspectBefore() {
		return aspectBefore;
	}
	public void setAspectBefore(String aspectBefore) {
		this.aspectBefore = aspectBefore;
	}
	public String getAspectAfter() {
		return aspectAfter;
	}
	public void setAspectAfter(String aspectAfter) {
		this.aspectAfter = aspectAfter;
	}
	public String getAspectClass() {
		return aspectClass;
	}
	public void setAspectClass(String aspectClass) {
		this.aspectClass = aspectClass;
	}
	public String getAspectAfterThrow() {
		return aspectAfterThrow;
	}
	public void setAspectAfterThrow(String aspectAfterThrow) {
		this.aspectAfterThrow = aspectAfterThrow;
	}
	public String getAspectAfterThrowingName() {
		return aspectAfterThrowingName;
	}
	public void setAspectAfterThrowingName(String aspectAfterThrowingName) {
		this.aspectAfterThrowingName = aspectAfterThrowingName;
	}

}
