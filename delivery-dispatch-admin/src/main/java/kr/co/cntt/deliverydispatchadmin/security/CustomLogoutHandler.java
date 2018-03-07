package kr.co.cntt.deliverydispatchadmin.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.cntt.deliverydispatchadmin.config.DeliveryDispatchAdminAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;


import kr.co.cntt.core.model.admin.AdminPerformanceHistory;

/**
 * <p>kr.co.burgerking.admin.security
 * <p>CustomLogoutHandler.java
 * <p>로그아웃 핸들러
 * @author JIN
 */
public class CustomLogoutHandler implements LogoutHandler {
    /**
     * <p>관리자 정보
     * @author JIN
     */
    @Autowired private AuthenticationInfo authenticationInfo;
    /**
     * <p>관리자 감사
     * @author JIN
     */
    @Autowired private DeliveryDispatchAdminAudit deliveryDispatchAdminAudit;
    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.logout.LogoutHandler#logout(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        AdminPerformanceHistory adminPerformanceHistory = new AdminPerformanceHistory();
        adminPerformanceHistory.setAdminSeq(authenticationInfo.getAdminSeq());
        adminPerformanceHistory.setAdminId(authenticationInfo.getAdminId());
        deliveryDispatchAdminAudit.insertAdminPerformanceHistoryLog(request, adminPerformanceHistory);
    }
}
