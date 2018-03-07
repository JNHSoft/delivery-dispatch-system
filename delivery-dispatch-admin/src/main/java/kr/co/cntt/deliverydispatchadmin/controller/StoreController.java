package kr.co.cntt.deliverydispatchadmin.controller;

import com.google.gson.Gson;
import kr.co.cntt.core.annotation.CnttMethodDescription;
import kr.co.cntt.core.model.admin.Admin;
import kr.co.cntt.core.model.login.User;
import kr.co.cntt.core.model.store.Store;
import kr.co.cntt.core.service.admin.StoreAdminService;
import kr.co.cntt.deliverydispatchadmin.enums.SessionEnum;
import kr.co.cntt.deliverydispatchadmin.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Controller
public class StoreController {


    /**
     * 객체 주입
     */
    StoreAdminService storeAdminService;


    @Autowired
    public StoreController(StoreAdminService storeAdminService){
        this.storeAdminService = storeAdminService;
    }

    /**
     * 매장관리 페이지
     *
     * @return
     */
    @GetMapping("/store")
    @CnttMethodDescription("매장 리스트 조회")
    public String store(Store store, @RequestParam(required=false) String frag, Model model) {
        // ADMIN 정보
        SecurityUser adminInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        log.info("===============> adminInfo.getAdminAccessToken()    : {}", adminInfo.getAdminAccessToken());

        store.setToken(adminInfo.getAdminAccessToken());

        List<Store> storeList = storeAdminService.selectStoreList(store);

        model.addAttribute("storeList", storeList);
        model.addAttribute("jsonList", new Gson().toJson(storeList));

        log.info("json : {}", new Gson().toJson(storeList));

        return "/store/store";
    }

}
