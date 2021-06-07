package kr.co.cntt.deliverydispatchadmin.controller;


import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Slf4j
@Controller
public class DashboardController {
    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    /**
     * 통계 페이지
     *
     * @return
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        log.info("dashboard");
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        return "/dashboard/dashboard";
    }

}
