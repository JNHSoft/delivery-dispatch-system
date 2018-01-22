package kr.co.cntt.core.concurrent.task;

public interface LogService {
	/** 에러 로그 */
	public void insertErrorLog(ILog log) throws Exception;
	/** API 전문 로그 */
	public void insertTrLog(ILog log) throws Exception;
}
