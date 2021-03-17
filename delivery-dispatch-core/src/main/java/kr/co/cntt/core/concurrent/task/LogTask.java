//package kr.co.cntt.core.concurrent.task;
//
//public class LogTask<T extends ILog> implements IServerTask {
//
//	ILogSupport<T> log;
//
//	public LogTask(ILogSupport<T> log) {
//		this.log = log;
//	}
//
//	@Override
//	public void processTask() {
//		log.insertLog();
//		log.traceLog();
//	}
//}
