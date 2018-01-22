package kr.co.cntt.core.concurrent.task;

public interface ILogSupport<T extends ILog> {

	public void insertLog();
	public void traceLog();
}
