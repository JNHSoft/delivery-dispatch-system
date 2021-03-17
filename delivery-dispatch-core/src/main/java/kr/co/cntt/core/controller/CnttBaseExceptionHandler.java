package kr.co.cntt.core.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.HandlerMethod;

//import kr.co.cntt.core.concurrent.service.ServerTaskExecuteService;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.CnttBizException;
import kr.co.cntt.core.exception.InvalidRequestException;
//import kr.co.cntt.core.trace.NotMonitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@NotMonitor
public abstract class CnttBaseExceptionHandler implements ApplicationContextAware, MessageSourceAware{

	private final Logger logger = LoggerFactory.getLogger(CnttBaseExceptionHandler.class);
	
	private MessageSource messageSource;
	public ApplicationContext context;
//	public ServerTaskExecuteService executor;
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
//		this.executor = (ServerTaskExecuteService) context.getBean("serverTaskExecuteService");
	}
	
	public abstract Object handleGenericException(Throwable e, HandlerMethod handlerMethod, HttpServletRequest request, HttpServletResponse response); 
	
	public abstract void insertErrorLog(HttpServletRequest request, HandlerMethod handler, Throwable ex);

	public ResponseEntity<ValidationError> processValidationError(Throwable e) {
		BindingResult result = null;
		if (e instanceof MethodArgumentNotValidException) {
			result = ((MethodArgumentNotValidException) e).getBindingResult();
		}
		if (e instanceof BindException) {
			result = ((BindException) e).getBindingResult();
		}
		List<FieldError> fieldErrors = result.getFieldErrors();
		return response(processFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
	}
	
	/*
	@ExceptionHandler({ Exception.class })
	@ResponseBody
	public ResponseEntity<?> handleAnyException(Exception e) {
		log.debug(e.getMessage());
		return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ SQLException.class, DataAccessException.class, RuntimeException.class })
	@ResponseBody
	public ResponseEntity<?> handleSQLException(Exception e) {
		log.debug(e.getMessage());
		return errorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ IOException.class, JsonParseException.class, JsonMappingException.class })
	@ResponseBody
	public ResponseEntity<?> handleParseException(Exception e) {
		log.debug(e.getMessage());
		return errorResponse(e, HttpStatus.BAD_REQUEST);
	}*/

	@Deprecated
	protected ResponseEntity<ExceptionMessage> errorResponse(Throwable ex, HandlerMethod handlerMethod, HttpStatus status, HttpServletRequest request) {
		insertErrorLog(request, handlerMethod, ex);
		return errorResponse(ex, status);
	}
	
	protected ResponseEntity<ExceptionMessage> errorResponse(Throwable ex, HttpStatus status) {
		if (null != ex) {
			logger.error("CnttBaseExceptionHandler.errorResponse : {}", ex.getMessage(), ex);
			if (ex instanceof InvalidRequestException) {
				InvalidRequestException exception = (InvalidRequestException) ex;
				return response(new ExceptionMessage(exception.getErrorCode(), ex.getLocalizedMessage(), exception.getForwardUrl()), status);
			} else if (ex instanceof CnttBizException) {
				CnttBizException exception = (CnttBizException) ex;
				return response(new ExceptionMessage(exception.getErrorCode(), ex.getLocalizedMessage(), exception.getForwardUrl()), status);
			} else {
				// unhandled exception message는 로깅 후 화면에 보내지 않는다. 보안상 이슈발생 소지가 있음.
				return response(new ExceptionMessage(ErrorCodeEnum.S0001.name(), ""), status);
			}
		} else {
			return response(null, status);
		}
	}

	
	protected <T> ResponseEntity<T> response(T body, HttpStatus status) {
		return new ResponseEntity<T>(body, new HttpHeaders(), status);
	}

	private ValidationError processFieldErrors(List<FieldError> fieldErrors) {
		ValidationError dto = new ValidationError();

		for (FieldError fieldError : fieldErrors) {
			String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
			dto.addFieldError(fieldError.getField(), localizedErrorMessage);
		}
		return dto;
	}

	private String resolveLocalizedErrorMessage(FieldError fieldError) {
		Locale currentLocale = LocaleContextHolder.getLocale();
		String localizedErrorMessage = messageSource.getMessage(fieldError, currentLocale);
		return localizedErrorMessage;
	}

	protected void printStack(Throwable e) {
		if (log.isDebugEnabled()) {
			e.printStackTrace();
		}
	}
	@Getter
	@Setter
	@RequiredArgsConstructor
	@AllArgsConstructor
	class ExceptionMessage {
		private final String errorCode;
		private final String errorMessage;
		private String redirectUrl;
	}

	@Getter
	@Setter
	class ValidationError {
		private List<ErrorDto> fieldErrors = new ArrayList<ErrorDto>();

		public ValidationError() {
		}

		public void addFieldError(String field, String message) {
			ErrorDto error = new ErrorDto(field, message);
			fieldErrors.add(error);
		}
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	class ErrorDto {
		private final String field;
		private final String message;
	}
}
