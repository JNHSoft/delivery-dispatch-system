package kr.co.cntt.core.model.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.method.HandlerMethod;

import kr.co.cntt.core.concurrent.task.ILog;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.BaseException;
import kr.co.cntt.core.model.AbstractPagination;
import kr.co.cntt.core.util.AgentUtil;
import kr.co.cntt.core.util.DateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorLog extends AbstractPagination implements ILog {

	private static final long serialVersionUID = -9086202869519606002L;

	/** request ip */
	private String ip;
	/** request channel 구분 */
	private String channel;
	/** request uri */
	private String uri;
	/** 에러발생 클래스명 */
	private String clazz;
	/** 에러발생 메소드명 */
	private String method;
	/** error code */
	private String code;
	/** error message */
	private String message;
	/** request parameter */
	private String request;
	/** 에러발생시간 */
	private String stime;

	public ErrorLog(HttpServletRequest req) {
		this.ip = AgentUtil.getIp(req);
		this.channel = AgentUtil.getChannel(req);
		this.uri = req.getRequestURI();
		this.request = "";
		this.message = "";
	}

	public static ErrorLog create(HttpServletRequest req, Throwable ex, HandlerMethod handler) {
		ErrorLog error = new ErrorLog(req);
		if (ex instanceof BaseException) {
			error.setCode(((BaseException)ex).getErrorCode());
			error.setMessage(ex.getLocalizedMessage() == null ? "" : ex.getLocalizedMessage());
		} else {
			error.setCode(ErrorCodeEnum.S0001.name());
			error.setMessage(ex.getLocalizedMessage() == null ? "" : ex.getLocalizedMessage() + "\n" + stackTraceToString(ex));
		}
		error.setStime(DateUtil.getToday("yyyyMMddHHmmss"));
		if (handler != null) {
			error.setClazz(handler.getBean().getClass().getName());
			error.setMethod(handler.getMethod().getName());
		}
		return error;
	}

	public static ErrorLog create(HttpServletRequest req, Throwable ex, String clazz, String method) {
		ErrorLog error = new ErrorLog(req);
		if (ex instanceof BaseException) {
			error.setCode(((BaseException) ex).getErrorCode());
		} else {
			error.setCode(ErrorCodeEnum.S0001.name());
		}
		error.setMessage(ex.getLocalizedMessage() == null ? "" : ex.getLocalizedMessage());
		error.setStime(DateUtil.getToday("yyyyMMddHHmmss"));
		error.setClazz(clazz);
		error.setMethod(method);
		return error;
	}

	public static String stackTraceToString(Throwable ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
}
