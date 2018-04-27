package kr.co.cntt.deliverydispatchadmin.controller;

import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.service.admin.AccountAdminService;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class MainController {

    private AccountAdminService accountAdminService;

    @Autowired
    public MainController(AccountAdminService accountAdminService){
        this.accountAdminService = accountAdminService;
    }

    /**
     * 로그인 페이지
     *
     * @return
     */
    @GetMapping("/login")
    public String login() { return "/login/login"; }


    /**
     * 로그아웃
     *
     * @return
     */
    @GetMapping("/logout")
    public String logout() { return "redirect:/login"; }

    @RequestMapping("/adminInfo")
    @ResponseBody
    public Admin getAdminInfo(){
        Admin admin = new Admin();
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        admin.setToken(adminInfo.getAdminAccessToken());

        return accountAdminService.getAdminAccount(admin);
    }
}
