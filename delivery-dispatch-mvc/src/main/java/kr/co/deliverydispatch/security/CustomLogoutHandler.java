package kr.co.deliverydispatch.security;

import kr.co.cntt.core.model.store.StorePerformanceHistory;
import kr.co.deliverydispatch.config.DeliveryDispatchStoreAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>kr.co.burgerking.store.security
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
    @Autowired private DeliveryDispatchStoreAudit deliveryDispatchStoreAudit;
    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.logout.LogoutHandler#logout(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.Authentication)
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        StorePerformanceHistory storePerformanceHistory = new StorePerformanceHistory();
        storePerformanceHistory.setStoreSeq(authenticationInfo.getStoreSeq());
        storePerformanceHistory.setStoreId(authenticationInfo.getStoreId());
        deliveryDispatchStoreAudit.insertStorePerformanceHistoryLog(request, storePerformanceHistory);
    }
}
