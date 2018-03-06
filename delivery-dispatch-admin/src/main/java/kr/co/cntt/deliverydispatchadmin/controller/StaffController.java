package kr.co.cntt.deliverydispatchadmin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class StaffController {

    /**
     * 직원관리 페이지
     *
     * @return
     */
    @GetMapping("/staff")
    public String staff() { return "/staff/staff"; }

}
