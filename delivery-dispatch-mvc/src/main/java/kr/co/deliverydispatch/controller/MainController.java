package kr.co.deliverydispatch.controller;

import kr.co.cntt.core.model.notice.Notice;
import kr.co.cntt.core.model.order.Order;
import kr.co.cntt.core.model.rider.Rider;
import kr.co.deliverydispatch.security.SecurityUser;
import kr.co.deliverydispatch.service.StoreNoticeService;
import kr.co.deliverydispatch.service.StoreOrderService;
import kr.co.deliverydispatch.service.StoreRiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
public class MainController {

    @Value("${websocket.localhost}")
    private String websocketHost;

    /**
     * 객체 주입
     */
    private StoreOrderService storeOrderService;
    private StoreNoticeService storeNoticeService;
    private StoreRiderService storeRiderService;
    @Autowired
    public MainController(StoreOrderService storeOrderService, StoreNoticeService storeNoticeService, StoreRiderService storeRiderService) {
        this.storeOrderService = storeOrderService;
        this.storeNoticeService = storeNoticeService;
        this.storeRiderService = storeRiderService;
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


    /**
     * 공사중 페이지
     *
     * @return
     */
    @GetMapping("/caution")
    public String caution() {
        return "/caution";
    }

    @RequestMapping("/commonNotice")
    @ResponseBody
    public List<Notice> getNotice() {
        Notice notice = new Notice();
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        notice.setToken(storeInfo.getStoreAccessToken());
        return storeNoticeService.getNoticeList(notice);
    }

    @RequestMapping("/websocketHost")
    @ResponseBody
    public String getWebsocketHost() {
        return websocketHost;
    }

    @RequestMapping("/footerRiderList")
    @ResponseBody
    public List<Rider> getFooterRiderList(){
        Rider rider = new Rider();
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        rider.setToken(storeInfo.getStoreAccessToken());
        return storeRiderService.getRiderFooter(rider);
    }

    @RequestMapping("/footerOrderList")
    @ResponseBody
    public List<Order> getFooterOrderList(){
        Order order = new Order();
        SecurityUser storeInfo = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
        order.setToken(storeInfo.getStoreAccessToken());
        return storeOrderService.getFooterOrders(order);
    }
}
