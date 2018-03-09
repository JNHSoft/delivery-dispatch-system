package kr.co.deliverydispatch.security;

import kr.co.cntt.core.model.store.StorePerformanceHistory;
import kr.co.deliverydispatch.config.DeliveryDispatchStoreAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StorePerformanceHistoryFilter extends OncePerRequestFilter {
    /**
     * <p>상점 감사
     */
    @Autowired private DeliveryDispatchStoreAudit deliveryDispatchStoreAudit;
    /* (non-Javadoc)
     * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String key = request.getRequestURI();
        if (DeliveryDispatchStoreAudit.REQUEST_MAPPING_DESCRIPTION_MAP.containsKey(key) && !key.equals(DeliveryDispatchStoreAudit.LOGOUT_URI)) {
            deliveryDispatchStoreAudit.insertStorePerformanceHistoryLog(request, new StorePerformanceHistory());
        }
        chain.doFilter(request, response);
    }
}
