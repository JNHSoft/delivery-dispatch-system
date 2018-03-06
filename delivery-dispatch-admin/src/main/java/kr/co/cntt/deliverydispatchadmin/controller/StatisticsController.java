package kr.co.cntt.deliverydispatchadmin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class StatisticsController {

    /**
     * 통계 페이지
     *
     * @return
     */
    @GetMapping("/statistics")
    public String statistics() { return "/statistics/statement"; }

}
