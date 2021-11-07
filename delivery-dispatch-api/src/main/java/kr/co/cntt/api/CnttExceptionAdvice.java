package kr.co.cntt.api;

import kr.co.cntt.core.controller.CnttBaseExceptionHandler;
import kr.co.cntt.core.model.web.ErrorLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * exception resolver
 * @author su
 *
 */
@ControllerAdvice
//@NotMonitor
@Slf4j
public class CnttExceptionAdvice extends CnttBaseExceptionHandler {

	@Override
	@ExceptionHandler({ Throwable.class })
	@ResponseBody
	public Object handleGenericException(Throwable e, HandlerMethod handlerMethod, HttpServletRequest request, HttpServletResponse response) { 
		ResponseBody methodAnnotation = handlerMethod.getMethodAnnotation(ResponseBody.class);
		insertErrorLog(request, handlerMethod, e);
		if (methodAnnotation != null) {
			if (e instanceof MethodArgumentNotValidException || e instanceof BindException) {
				return processValidationError(e);
			}
			return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return null;
		}
	}
	
	@Override
	public void insertErrorLog(HttpServletRequest request, HandlerMethod handler, Throwable ex) {
		ErrorLog error = ErrorLog.create(request, ex, handler);
//		if (this.executor != null) {
//			executor.doTask(new LogTask<ErrorLog>(new ILogSupport<ErrorLog>() {
//				@Override
//				public void insertLog() {
////					LogService logService = (LogService) context.getBean("logService");
////					try {
////						logService.insertErrorLog(error);
////					} catch (Exception e) {
////						// 에러 무시
////						if (log.isDebugEnabled()) {
////							log.debug("insertErrorLog error occured");
////						}
////					}
//				}
//				@Override
//				public void traceLog() {
//					if (log.isDebugEnabled()) {
//
//						log.debug("#################################################### CNT ERROR TRACE ####################################################");
//						log.debug("ErrorLog : {}", new Gson().toJson(error, ErrorLog.class));
//						log.debug("error.getMessage : {} ", error.getMessage());
//						log.debug("ex : {}",ex.toString());
//						log.debug("#########################################################################################################################");
//					}
//				}
//			}));
//		}
	}
}
