package kr.co.cntt.core.concurrent.task;

/**
 * <p>kr.co.cntt.core.concurrent.task
 * <p>ISmsSupport.java
 * <p>SMS Support
 * @author JIN
 */
public interface ISmsSupport<T extends ISms>  {
	public void send();
	public void insertLog();
}
