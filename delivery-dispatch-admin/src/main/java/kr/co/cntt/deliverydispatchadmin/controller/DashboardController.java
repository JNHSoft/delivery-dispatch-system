package kr.co.cntt.deliverydispatchadmin.controller;


import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Controller
public class DashboardController {
    @Value("${spring.mvc.locale}")
    private Locale regionLocale;

    /**
     * 대시보드 페이지
     */
    @GetMapping("/dashboard")
    @CnttMethodDescription("대시보드 메인페이지")
    public String dashboard() {
        return "/dashboard/dashboard";
    }

    /**
     * 대시보드 메인 페이지 통계 추출
     * */
    @ResponseBody
    //@ GetMapping("/totalStatistsc")
    @PostMapping(value = "/totalStatistsc")
    @CnttMethodDescription("대시보드 메인 통계 자료")
    public Map<String, Object> totalStatistsc(@RequestParam("groupId") Object groupID){
        Map<String, Object> resultMap = new HashMap<>();

        System.out.println("###########################");
        System.out.println(groupID);
        System.out.println("###########################");

        resultMap.put("result", "OK");

        return resultMap;
    }
    
    
    
    
    
    /**
     * 대시 보드 상세 페이지
     * */
    @GetMapping("/dashboardDetail")
    @CnttMethodDescription("대시보드 상세페이지")
    public String dashboardDetail(){

        return "/dashboard/dashboardDetail";
    }
}
