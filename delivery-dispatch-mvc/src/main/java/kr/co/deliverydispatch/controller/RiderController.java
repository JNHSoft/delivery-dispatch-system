package kr.co.deliverydispatch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class RiderController {

    /**
     * 기사현황 페이지
     *
     * @return
     */
    @GetMapping("/rider")
    public String rider() { return "/rider/rider"; }

}
