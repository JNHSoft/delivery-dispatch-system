package kr.co.cntt.api.config;

public interface IServiceRouter {
	public String getQualifierName();
	public String getMethod();
	public Class<?> getIn();
	//public Class<?> getOut();
}
