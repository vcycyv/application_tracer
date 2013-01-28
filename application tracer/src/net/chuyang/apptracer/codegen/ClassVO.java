package net.chuyang.apptracer.codegen;


/**
 * 
 * The VO is corresponding to RetrunValue.txt template. It has getter and setter 
 * so it can be consumed by BeanUtils.describe().
 * 
 * @author chuyang
 *
 */
public class ClassVO {
	private String clazz;
	private String method;
	private String returnType;
	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
}
