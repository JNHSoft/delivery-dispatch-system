package kr.co.cntt.api.exporter;

import com.fasterxml.jackson.databind.JavaType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.cntt.api.config.IServiceRouter;
import kr.co.cntt.api.security.RequestWrapper;
import kr.co.cntt.core.api.model.CommonBody;
import kr.co.cntt.core.api.model.GenericRequest;
import kr.co.cntt.core.controller.ControllerSupport;
import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.AppTrException;
import kr.co.cntt.rest.custom.mapper.RestObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public abstract class ExporterSupportor extends ControllerSupport implements ApplicationContextAware {

    protected final static String CODE_SUCCESS = "1";
    protected final static String CODE_ERROR = "0";
    protected final static String CODE_SUCCESS_MESSAGE = "success";
    protected final static String CODE_ERROR_MESSAGE = "failed";
    protected final static String CODE_SYSTEM_ERROR = "System Error";
    protected final static String CODE_ACCESS_DENIED = "Access Denied Error";

    private ApplicationContext context;

    @Autowired
    @Qualifier("objectMapper")
    private RestObjectMapper mapper;
    /**
     * generic service invoker
     * @param router
     * @param jsonStr
     * @return
     * @throws Exception
     */
    protected ResponseEntity<?> trServiceInvoker(IServiceRouter router, String jsonStr, HttpServletRequest servletRequest) {
        if (router == null) {
            return responseError(null, new AppTrException(getMessage(ErrorCodeEnum.S0001), ErrorCodeEnum.S0001.name()));
        }
        RequestWrapper requestWrapper;

        Object service = context.getBean(router.getQualifierName());
        Method[] methods = service.getClass().getMethods();
        GenericRequest<?> requestVo;
        for (Method m : methods) {
            try {
                if (router.getMethod().equals(m.getName())) {
                    // body string to vo object
                    JavaType reqType = mapper.getTypeFactory().constructParametrizedType(GenericRequest.class, GenericRequest.class, router.getIn());
                    requestVo = mapper.readValue(jsonStr, reqType);

//                    RequestHeader requestHeader = requestVo.getHeader();
                    //RequestHeader requestHeader = requestVo.getHeader().get(0);
//                    String token = requestHeader.getToken();
                    requestWrapper  = new RequestWrapper(servletRequest);
//                    String token = requestWrapper.getHeader("token");

                    JsonObject json = new JsonParser().parse(requestWrapper.getJsonBody()).getAsJsonObject();
                    JsonObject jbody = json.getAsJsonObject("body").getAsJsonObject();

                    String token = jbody.get("token").getAsString();

                    return response(() -> {
                        try {
                            return m.invoke(service, requestVo.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e.getCause());
                        }
                    }, token);
                }
            } catch (Exception e) {
                return responseError(null, e);
            }
        }
        return responseError(null, new AppTrException(getMessage(ErrorCodeEnum.S0001), ErrorCodeEnum.S0001.name()));
    }

    /**
     * response success
     * @param command
     * @param token
     * @return
     */
    protected <T> ResponseEntity<?> response(Supplier<T> command, String token) throws Exception {
        kr.co.cntt.core.api.model.GenericResponse<CommonBody<T>> response = new kr.co.cntt.core.api.model.GenericResponse<CommonBody<T>>();
        kr.co.cntt.core.api.model.ResponseHeader header = new kr.co.cntt.core.api.model.ResponseHeader();
        //header.setToken(token);
        header.setDate(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        CommonBody<T> cBody = new CommonBody<T>(CODE_SUCCESS);

        cBody.setCode(command.get());

        response.setBody(cBody);
        response.setHeader(header);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * reponse error
     * @param token
     * @param e
     * @return
     */
    protected ResponseEntity<?> responseError(String token, Throwable e) {
        kr.co.cntt.core.api.model.GenericResponse<CommonBody<Map<String, String>>> response = new kr.co.cntt.core.api.model.GenericResponse<CommonBody<Map<String, String>>>();
        kr.co.cntt.core.api.model.ResponseHeader header = new kr.co.cntt.core.api.model.ResponseHeader();
        //header.setToken(token);
        header.setDate(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        CommonBody<Map<String, String>> cBody = new CommonBody<Map<String, String>>(CODE_ERROR);
        Map<String, String> errorMap = new HashMap<String, String>();
        if (e.getCause() != null) {
            e = e.getCause();
        }
        String errorLocalizedMessage = e.getLocalizedMessage();
        if (e instanceof AppTrException) {
            errorMap.put("error_code", ((AppTrException) e).getErrorCode());
            errorMap.put("error", errorLocalizedMessage);
        } else if (e instanceof AccessDeniedException) {
            log.info("[AppExporterSupportor][response][!AppTrException][errorCause : {}]", errorLocalizedMessage);
            errorMap.put("error", CODE_ACCESS_DENIED);
        } else {
            log.info("[AppExporterSupportor][response][!AppTrException][errorCause : {}]", errorLocalizedMessage);
            errorMap.put("error", CODE_SYSTEM_ERROR);
        }
        cBody.setError_desc(errorMap);
        response.setBody(cBody);
        response.setHeader(header);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
	/*
	protected ResponseEntity<?> responseError(String token, Throwable e) {
		kr.co.cntt.core.api.model.app.GenericResponse<CommonBody<Map<String, String>>> response = new kr.co.cntt.core.api.model.app.GenericResponse<CommonBody<Map<String, String>>>();
		kr.co.cntt.core.api.model.app.GenericResponse<CommonBody<List<Map<String, String>>>> response2 = new kr.co.cntt.core.api.model.app.GenericResponse<CommonBody<List<Map<String, String>>>>();
		kr.co.cntt.core.api.model.app.ResponseHeader header = new kr.co.cntt.core.api.model.app.ResponseHeader();
		header.setToken(token);
		header.setDate(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		CommonBody<Map<String, String>> cBody = new CommonBody<Map<String, String>>(CODE_ERROR);
		CommonBody<List<Map<String, String>>> cBody2 = new CommonBody<List<Map<String, String>>>(CODE_ERROR);
		Map<String, String> errorMap = new HashMap<String, String>();
		List<Map<String, String>> errorMap2 = new ArrayList<Map<String, String>>();
		if (e.getCause() != null) {
			e = e.getCause();
		}
		errorMap.put("error", e.getLocalizedMessage());

		if("A0011".equals(((AppTrException)e).getErrorCode())) {
			errorMap2.add(errorMap);
			cBody2.setCode(errorMap2);
			response2.setBody(cBody2);
			response2.setHeader(header);
			return ResponseEntity.status(HttpStatus.OK).body(response2);
		} else {
			if (e instanceof AppTrException) {
				errorMap.put("error_code", ((AppTrException)e).getErrorCode());
			}
			cBody.setCode(errorMap);
			response.setBody(cBody);
			response.setHeader(header);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}
	}
	*/
    /**
     * empty response
     * @param command
     * @return
     */
    protected ResponseEntity<Void> responseEmpty(Runnable command) {
        command.run();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * application context
     */
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}