package kr.co.cntt.core.concurrent.task;

/**
 * <p>kr.co.cntt.core.concurrent.task
 * <p>SmsTask.java
 * <p>SMS 발송
 * <p>Task
 * @author JIN
 */
public class SmsTask<T extends ISms> implements IServerTask {
	/**
	 * <p>SMS 도우미
	 * @author JIN
	 */
	private ISmsSupport<T> sms;
	/**
	 * @param sms SMS 도우미
	 * @author JIN
	 */
	public SmsTask(ISmsSupport<T> sms) {
		this.sms = sms;
	}
	/* (non-Javadoc)
	 * @see kr.co.cntt.core.concurrent.task.IServerTask#processTask()
	 */
	@Override
	public void processTask() {
		sms.send();
		sms.insertLog();
	}
}
