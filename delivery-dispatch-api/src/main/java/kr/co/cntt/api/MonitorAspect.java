package kr.co.cntt.api;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;

import kr.co.cntt.core.concurrent.service.ServerTaskExecuteService;
import kr.co.cntt.core.concurrent.task.ILogSupport;
import kr.co.cntt.core.concurrent.task.LogService;
import kr.co.cntt.core.concurrent.task.LogTask;
import kr.co.cntt.core.model.web.ErrorLog;
import kr.co.cntt.core.model.web.TrLog;
import kr.co.cntt.core.trace.NotMonitor;
import kr.co.cntt.core.util.DateUtil;

@Aspect
public class MonitorAspect {

	private Logger log = LoggerFactory.getLogger(MonitorAspect.class);
	
	private static boolean isEnabled = false;
	private static String basepackage = "";
	private ServerTaskExecuteService executor;
	
	
	@PostConstruct
	public void initialize() {
		this.executor = (ServerTaskExecuteService) SpringApplicationContext.getBean("serverTaskExecuteService");
	}
	
	@Value("${cntt.monitor.enable}")
	public void setEnable(boolean isEnable) {
		//isEnabled = isEnable && log.isDebugEnabled();
		isEnabled = true;	
	}
	@Value("${cntt.monitor.basepackage}")
	public void setBasepackage(String base) {
		basepackage = "kr.co.cntt";
	}
	// || @annotation(kr.co.cntt.core.trace.Monitor)
	@Pointcut("(within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *))"
			+ " && (@annotation(org.springframework.web.bind.annotation.GetMapping) "
			+ "|| @annotation(org.springframework.web.bind.annotation.PostMapping) "
			+ "|| @annotation(org.springframework.web.bind.annotation.RequestMapping))")
	public void controller() {
	}

	@Around("controller()")
	public Object logPrint(ProceedingJoinPoint joinPoint) throws Throwable {
		if (joinPoint.getTarget().getClass().isAnnotationPresent(NotMonitor.class) || !isEnabled) {
			return joinPoint.proceed();
		}
		log.debug("############################### Cntt Monitor Start ################################");
		Object result;
		StopWatch watch = new StopWatch(joinPoint.getTarget().getClass().getName());
		watch.start();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		TrLog trLog = new TrLog(request);
		try {
			trLog.setStime(DateUtil.getToday("yyyyMMddHHmmss"));
			trLog.setClazz(joinPoint.getSignature().getDeclaringTypeName());
			trLog.setMethod(joinPoint.getSignature().getName());
			for (Object o : joinPoint.getArgs()) {
				if (o != null) {
					Class<?> argClazz = o.getClass();
					if (o instanceof HttpServletRequest || o instanceof MultipartHttpServletRequest) {
						HttpServletRequest req = (HttpServletRequest) o;
						Map<String, String[]> m = req.getParameterMap();
						trLog.setRequestMessage(new Gson().toJson(m, Map.class));
					} else if (argClazz == String.class || ClassUtils.isPrimitiveOrWrapper(argClazz) ||  ClassUtils.isPrimitiveWrapperArray(argClazz)) {
						trLog.setRequestMessage(o.toString());
					} else if (argClazz.getName().startsWith(basepackage) && o instanceof Serializable) {
						trLog.setRequestMessage(new Gson().toJson(o, argClazz));
					}
				}
			}
		} catch (Exception e) {
			log.debug("############ Cntt Monitor Aspect Exception Occured ############");
			return joinPoint.proceed();
		} finally {
		}
		result = joinPoint.proceed();
		try {
			if (result != null) {
				Class<? extends Object> retClazz = result.getClass();
				// primitive type과 serializable한 object만 로깅
				if (retClazz instanceof Serializable) {
					trLog.setResponseMessage(new Gson().toJson(result, retClazz));
				} else {
					trLog.setResponseMessage(result.toString());
				}
			}
		} catch (Exception e) {
			// exception 무시
			log.debug("############ Cntt Monitor Aspect Exception Occured ############");
		} finally {
			watch.stop();
			trLog.setEtime(DateUtil.getToday("yyyyMMddHHmmss"));
			trLog.setElapsed(watch.getTotalTimeMillis());
			insertTrLog(trLog);
			//log.info(trLog.getLogInfo());
		}
		log.debug("############################### Cntt Monitor Aspect End ################################");
		return result;
	}
	
	@AfterThrowing(pointcut="controller()", throwing="ex")
	public void afterThrowing(JoinPoint joinPoint, Throwable ex){
		log.debug("############ Cntt Monitor Aspect afterThrowing Task Start ############");
		boolean isMonitorAllowed = !joinPoint.getTarget().getClass().isAnnotationPresent(NotMonitor.class);
		if (isMonitorAllowed) {
			try {
				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
				ErrorLog errorLog = ErrorLog.create(request, ex, joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
				insertErrorLog(errorLog);
			} catch (Exception e) {
				e.printStackTrace();
				log.debug("############ Cntt Monitor Aspect afterThrowing Exception Occured############");
			} finally {
				log.debug("############ Cntt Monitor Aspect afterThrowing Task End ############");
			}
		}
	}
	
	private void insertTrLog(TrLog trLog) {
		executor.doTask(new LogTask<TrLog>(new ILogSupport<TrLog>() {
			@Override
			public void insertLog() {
//				LogService logService = (LogService) SpringApplicationContext.getBean("logService");
//				try {
//					logService.insertTrLog(trLog);
//				} catch (Exception e) {
//					// 에러 무시
//					if (log.isDebugEnabled()) {
//						log.debug("insertErrorLog error occured");
//					}
//				}
			}
			@Override
			public void traceLog() {
				if (log.isDebugEnabled()) {
					log.debug("#################################################### TRLOG TRACE ####################################################");
					log.debug("request : {}", new Gson().toJson(trLog, TrLog.class));
					log.debug(trLog.toString());
					log.debug("#########################################################################################################################");
				}
			}
		}));
	}
	
	private void insertErrorLog(ErrorLog errorLog) {
		executor.doTask(new LogTask<ErrorLog>(new ILogSupport<ErrorLog>() {
			@Override
			public void insertLog() {
//				LogService logService = (LogService) SpringApplicationContext.getBean("logService");
//				try {
//					logService.insertErrorLog(errorLog);
//				} catch (Exception e) {
//					// 에러 무시
//					if (log.isDebugEnabled()) {
//						log.debug("insertErrorLog error occured");
//					}
//				}
			}
			@Override
			public void traceLog() {
				if (log.isDebugEnabled()) {
					log.debug("#################################################### TRLOG ERROR TRACE ####################################################");
					log.debug("request : {}", new Gson().toJson(errorLog, ErrorLog.class));
					log.debug(errorLog.toString());
					log.debug("#########################################################################################################################");
				}
			}
		}));
	}
	
	
	/*@Before("controller(requestMapping)")
	public void before(JoinPoint joinPoint, RequestMapping requestMapping) {
		boolean isMonitorAllowed = !joinPoint.getTarget().getClass().isAnnotationPresent(NotMonitor.class);
		if (isMonitorAllowed) {
			monitor.increaseRequestCount();
			Object[] args = joinPoint.getArgs();
			for (Object o : args) {
				if (o != null) {
				}
			}
		}
	}
	@After("controller(requestMapping)")
	public void after(JoinPoint joinPoint, RequestMapping requestMapping) {
		boolean isMonitorAllowed = !joinPoint.getTarget().getClass().isAnnotationPresent(NotMonitor.class);
		if (isMonitorAllowed) {
			monitor.increaseTotalSuccessCount();
		}
	}*/
}
