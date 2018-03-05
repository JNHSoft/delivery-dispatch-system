package kr.co.deliverydispatch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class OrderController {

    /**
     * 주문 페이지
     *
     * @return
     */
    @GetMapping("/order")
    public String orderMain() { return "/order/order"; }

}
