package kr.co.deliverydispatch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class MainController {

    /**
     * 로그인 페이지
     *
     * @return
     */
    @GetMapping("/login")
    public String loginMain() { return "/login/login"; }


    /**
     * 공사중 페이지
     *
     * @return
     */
    @GetMapping("/caution")
    public String caution() {
        return "/caution";
    }

}
