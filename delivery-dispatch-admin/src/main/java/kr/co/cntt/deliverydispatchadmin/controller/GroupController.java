package kr.co.cntt.deliverydispatchadmin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class GroupController {

    /**
     * 그룹관리 페이지
     *
     * @return
     */
    @GetMapping("/group")
    public String group() { return "/group/group"; }

}
