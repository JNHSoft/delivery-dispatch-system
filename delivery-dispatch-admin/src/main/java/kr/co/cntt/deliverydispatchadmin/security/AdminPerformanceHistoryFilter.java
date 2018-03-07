package kr.co.cntt.deliverydispatchadmin.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.cntt.deliverydispatchadmin.config.DeliveryDispatchAdminAudit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;


import kr.co.cntt.core.model.admin.AdminPerformanceHistory;

public class AdminPerformanceHistoryFilter extends OncePerRequestFilter {
    /**
     * <p>관리자 감사
     */
    @Autowired private DeliveryDispatchAdminAudit deliveryDispatchAdminAudit;
    /* (non-Javadoc)
     * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String key = request.getRequestURI();
        if (DeliveryDispatchAdminAudit.REQUEST_MAPPING_DESCRIPTION_MAP.containsKey(key) && !key.equals(DeliveryDispatchAdminAudit.LOGOUT_URI)) {
            deliveryDispatchAdminAudit.insertAdminPerformanceHistoryLog(request, new AdminPerformanceHistory());
        }
        chain.doFilter(request, response);
    }
}
