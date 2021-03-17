package kr.co.cntt.deliverydispatchadmin.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.mapper.AdminMapper;
import kr.co.cntt.deliverydispatchadmin.security.AuthenticationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

//import kr.co.cntt.core.concurrent.service.ServerTaskExecuteService;
import kr.co.cntt.core.concurrent.task.ILogSupport;
//import kr.co.cntt.core.concurrent.task.LogTask;

import kr.co.cntt.core.model.admin.AdminPerformanceHistory;
import kr.co.cntt.core.util.AgentUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>kr.co.burgerking.admin.config
 * <p>BurgerkingAdminAudit.java
 * <p>관리자 감사
 */
@Slf4j
@Component
public class DeliveryDispatchAdminAudit {
    /**
     * <p>요청 매핑 핸들러 매핑
     * @author JIN
     */
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    /**
     * <p>관리자 정보
     * @author JIN
     */
    private AuthenticationInfo authenticationInfo;
    /**
     * <p>server task executor working in backgroud mode
     * @author JIN
     */
//    private ServerTaskExecuteService taskExecuteService;
    /**
     * <p>관리자 D A O
     * @author JIN
     */
    private AdminMapper adminMapper;
    /**
     * <p>K:uri, V:desc
     * @author JIN
     */
    public static Map<String, String> REQUEST_MAPPING_DESCRIPTION_MAP = new HashMap<String, String>();
    /**
     * <p>로그아웃 uri
     * @author JIN
     */
    public static final String LOGOUT_URI = "/admin/logoutSuccess";
    /**
     * @author JIN
     */
    public DeliveryDispatchAdminAudit(){}
    /**
     * @param requestMappingHandlerMapping 요청 매핑 핸들러 매핑
     * @author JIN
     */
    @Autowired
    public DeliveryDispatchAdminAudit(RequestMappingHandlerMapping requestMappingHandlerMapping
            , AuthenticationInfo authenticationInfo
//            , ServerTaskExecuteService taskExecuteService
            , AdminMapper adminMapper){
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.authenticationInfo = authenticationInfo;
//        this.taskExecuteService = taskExecuteService;
        this.adminMapper = adminMapper;
    }
    /**
     * <p>handleContextRefresh
     * <p>핸들 컨텍스트 리플레쉬
     * @param event 컨텍스트 리프레시드 이벤트
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            Method method = entry.getValue().getMethod();
            if (method.isAnnotationPresent(CnttMethodDescription.class)) {
                Set<String> patternSet = requestMappingInfo.getPatternsCondition().getPatterns();
                for (String pattern : patternSet) {
                    REQUEST_MAPPING_DESCRIPTION_MAP.put(pattern, method.getAnnotation(CnttMethodDescription.class).value());
                }
            }
        }
    }
    /**
     * <p>insertAdminPerformanceHistoryLog
     * <p>제목
     * <p>내용 (생략 가능)
     * @param request Http Servlet 요청
     * @param adminPerformanceHistory 관리자 수행 이력 로그 모델
     */
    public void insertAdminPerformanceHistoryLog(HttpServletRequest request, AdminPerformanceHistory adminPerformanceHistory) {
        String key = adminPerformanceHistory.getAdminSeq() == null ? request.getRequestURI() : LOGOUT_URI;
        String value = REQUEST_MAPPING_DESCRIPTION_MAP.get(key);
        if (authenticationInfo.isLogin()) {
            adminPerformanceHistory.setAdminSeq(authenticationInfo.getAdminSeq());
            adminPerformanceHistory.setAdminId(authenticationInfo.getAdminId());
        }
        if (!StringUtils.isEmpty(value)) {
            adminPerformanceHistory.setRequestUri(key);
            adminPerformanceHistory.setPerformanceTask(value);
            adminPerformanceHistory.setIp(AgentUtil.getIp(request));
//            taskExecuteService.doTask(new LogTask<AdminPerformanceHistory>(new ILogSupport<AdminPerformanceHistory>(){
//                @Override
//                public void insertLog() {
//                    //adminMapper.insertAdminPerformanceHistoryLog(adminPerformanceHistory);
//                }
//                @Override
//                public void traceLog() {
//                    if (log.isDebugEnabled()) {
//                        log.debug("[AdminPerformanceHistoryFilter][doFilterInternal][K]:{}", key);
//                        log.debug("[AdminPerformanceHistoryFilter][doFilterInternal][V]:{}", value);
//                    }
//                }
//            }));
        }
    }
}
