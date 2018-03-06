package kr.co.cntt.deliverydispatchadmin.config;

import com.google.gson.Gson;
import kr.co.cntt.core.concurrent.task.ILogSupport;
import kr.co.cntt.core.concurrent.task.LogService;
import kr.co.cntt.core.concurrent.task.LogTask;
import kr.co.cntt.core.controller.CnttBaseExceptionHandler;
import kr.co.cntt.core.model.web.ErrorLog;
import kr.co.cntt.core.trace.NotMonitor;
import kr.co.cntt.deliverydispatchadmin.controller.ErrorController;
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
import java.io.IOException;

/**
 * exception resolver
 * @author su
 *
 */
@ControllerAdvice
@NotMonitor
@Slf4j
public class CnttExceptionAdvice extends CnttBaseExceptionHandler {

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
			//model.addAttribute("error", new ExceptionMessage(ErrorCodeEnum.S0001.name(), e.getCause().getLocalizedMessage()));
			try {
				response.sendRedirect(ErrorController.ERROR_DEFAULT);
			} catch (IOException e1) {
				// 에러 무시
				if (log.isDebugEnabled()) {
					log.debug("insertErrorLog error occured");
				}
			}
			return null;
		}
	}
	
	/*@ExceptionHandler({ CnttBizException.class })
	@ResponseBody
	public ResponseEntity<?> handleCnttBizExcpetion(Exception e, HandlerMethod handlerMethod, HttpServletRequest request) {
		return errorResponse(e, handlerMethod, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler({ InvalidRequestException.class })
	@ResponseBody
	public ResponseEntity<?> handleInvalidRequestExcpetion(Exception e, HandlerMethod handlerMethod, HttpServletRequest request) {
		printStack(e);
		return errorResponse(e, handlerMethod, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}*/
	
	@Override
	public void insertErrorLog(HttpServletRequest request, HandlerMethod handler, Throwable ex) {
		ErrorLog error = ErrorLog.create(request, ex, handler);
		if (this.executor != null) {
			executor.doTask(new LogTask<ErrorLog>(new ILogSupport<ErrorLog>() {
				@Override
				public void insertLog() {
					LogService logService = (LogService) context.getBean("logService");
					try {
						logService.insertErrorLog(error);
					} catch (Exception e) {
						// 에러 무시
						if (log.isDebugEnabled()) {
							log.debug("insertErrorLog error occured");
						}
					}
				}
				@Override
				public void traceLog() {
					if (log.isDebugEnabled()) {
						log.debug("#################################################### CNT ERROR TRACE ####################################################");
						log.debug("ErrorLog : {}", new Gson().toJson(error, ErrorLog.class));
						log.debug("#########################################################################################################################");
					}
				}
			}));
		}
	}
}
