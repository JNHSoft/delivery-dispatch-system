package kr.co.cntt.deliverydispatchadmin.controller;

import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class PolicyController {

    @GetMapping("/policy")
    public String policyMain(Model model){
        return "/policy/policy";
    }
}
